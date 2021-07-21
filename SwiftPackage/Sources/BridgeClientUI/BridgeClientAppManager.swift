//
//  BridgeClientAppManager.swift
//
//  Copyright © 2021 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

import SwiftUI
import BridgeClient

fileprivate let kOnboardingStateKey = "isOnboardingFinished"
fileprivate let kStudyIdKey = "studyId"

public let kPreviewStudyId = "xcode_preview"

public final class BridgeClientAppManager : ObservableObject {
    
    public enum AppState : String {
        case launching, login, onboarding, main
    }
    
    public let isPreview: Bool
    public let platformConfig: PlatformConfig
        
    @Published public var title: String
    @Published public var appState: AppState = .launching
    
    @Published public var isUploadingResults: Bool = false
    @Published public var isStudyComplete: Bool = false
    
    @Published public var studyId: String? {
        didSet {
            UserDefaults.standard.set(studyId, forKey: kStudyIdKey)
        }
    }
    
    @Published public var appConfig: AppConfig? {
        didSet {
            updateAppState()
        }
    }
    
    @Published public var userSessionInfo: UserSessionInfo? {
        didSet {
            updateStudy()
            updateAppState()
        }
    }
    
    @Published public var study: Study?
    
    @Published public var isOnboardingFinished: Bool = UserDefaults.standard.bool(forKey: kOnboardingStateKey) {
        didSet {
            UserDefaults.standard.set(isOnboardingFinished, forKey: kOnboardingStateKey)
            updateAppState()
        }
    }
    
    private var studyManager: NativeStudyManager!
    private var appConfigManager: NativeAppConfigManager!
    public private(set) var authManager: NativeAuthenticationManager!
    
    /// The local notification manager is a singleton that can be set up as the notification delegate (to handle snoozing)
    lazy public var localNotificationManager : LocalNotificationManager = LocalNotificationManager()
    
    public convenience init(appId: String) {
        self.init(platformConfig: PlatformConfigImpl(appId: appId))
        self.studyId = UserDefaults.standard.string(forKey: kStudyIdKey)
    }
    
    public init(platformConfig: PlatformConfig) {
        self.platformConfig = platformConfig
        self.title = self.platformConfig.localizedAppName
        self.isPreview = (platformConfig.appId == kPreviewStudyId)
        self.studyId = self.isPreview ? kPreviewStudyId : nil
        if !self.isPreview {
            IOSBridgeConfig().initialize(platformConfig: self.platformConfig)
        }
    }
    
    public func appWillFinishLaunching(_ launchOptions: [UIApplication.LaunchOptionsKey : Any]? ) {

        // Initialize koin
        #if DEBUG
            let enableNetworkLogs = true
        #else
            let enableNetworkLogs = false
        #endif
        KoinKt.doInitKoin(enableNetworkLogs: enableNetworkLogs)
        
        // Hook up app config
        self.appConfigManager = NativeAppConfigManager() { appConfig, _ in
            self.appConfig = appConfig ?? self.appConfig
        }
        self.appConfigManager.observeAppConfig()
        
        // Hook up user session info
        self.authManager = NativeAuthenticationManager() { userSessionInfo in
            guard userSessionInfo == nil || !userSessionInfo!.isEqual(userSessionInfo) else { return }
            self.userSessionInfo = userSessionInfo
        }
        self.userSessionInfo = self.authManager.session()
        self.authManager.observeUserSessionInfo()
        
        // Update the app state
        updateAppState()
    }
    
    public func loginWithExternalId(_ externalId: String, completion: @escaping ((BridgeClient.ResourceStatus) -> Void)) {
        self.authManager.signInExternalId(externalId: externalId, password: externalId) { (userSessionInfo, status) in
            guard status == ResourceStatus.success || status == ResourceStatus.failed else { return }
            self.userSessionInfo = userSessionInfo
            completion(status)
        }
    }
    
    public func signOut() {
        userSessionInfo = nil
        isOnboardingFinished = false
        try? studyManager?.onCleared()
        studyManager = nil
        study = nil
        authManager.signOut()
    }
    
    private func updateStudy() {
        guard (authManager?.isAuthenticated() ?? false),
              let studyIds = userSessionInfo?.studyIds, studyIds.count > 0
        else {
            return
        }
        let previousStudyId = studyId
        if studyId == nil || !studyIds.contains(studyId!) {
            studyId = studyIds.first
            print("Setting studyId=\(studyId!)")
        }
        if studyManager == nil || studyId != previousStudyId {
            try? studyManager?.onCleared()
            self.studyManager = NativeStudyManager(studyId: studyId!) { [weak self] study in
                self?.study = study
            }
            self.studyManager.observeStudy()
        }
    }
    
    private func updateAppState() {
        if appConfig == nil {
            appState = .launching
        }
        else if userSessionInfo == nil {
            appState = .login
        }
        else if !isOnboardingFinished {
            appState = .onboarding
        }
        else {
            appState = .main
        }
    }
    
    public func encryptAndUpload(_ archives: [DataArchive]) {
        DispatchQueue.global().async {

            // Encrypt the files.
            if let path = Bundle.main.path(forResource: self.platformConfig.appId, ofType: "pem"),
               let pemFile = try? String(contentsOfFile: path, encoding: .utf8) {
                archives.forEach { archive in
                    do {
                        try archive.encryptArchive(using: pemFile)
                    } catch let err {
                        print("Failed to encrypt archive. \(err)")
                    }
                }
            }

            DispatchQueue.main.async {
                self.isUploadingResults = true
                // TODO: syoung 06/17/2021 Figure out what needs to happen to allow uploading files to S3.
                // Note: This will have to dispatch to the main queue before accessing the Kotlin framework.
            }
        }
    }
}

