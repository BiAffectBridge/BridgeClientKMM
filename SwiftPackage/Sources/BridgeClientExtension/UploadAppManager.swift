//
//  UploadAppManager.swift
//
//

import Foundation
import BridgeClient
import JsonModel

public let kPreviewStudyId = "xcode_preview"
public let kStudyIdKey = "studyId"
fileprivate let kLoginStateKey = "hasLoggedIn"

open class UploadAppManager : ObservableObject {

    /// Is this manager used for previewing the app? (Unit tests, SwiftUI Preview, etc.)
    public let isPreview: Bool
    
    /// The configuration bits that need to be set on the 'BridgeClient.xcframework' in order to connect to
    /// Bridge services.
    public let platformConfig: IOSPlatformConfig
    
    /// The path to the pem file that is used to encrypt data being uploaded to Bridge.
    ///
    /// - Note: Data should be encrypted so that it is not stored insecurely on the phone (while waiting
    ///         for an upload connection).
    public var pemPath: String? { uploadProcessor.pemPath }
    private let uploadProcessor: ArchiveUploadProcessor
        
    /// The title of the app to display. By default, this is the localized display name of the app that is shown
    /// to the participant in their phone home screen.
    @Published public var title: String
    
    /// Is the app currently uploading results or user files? This status is maintained and updated by BridgeFileUploadManager.
    @Published public var isUploading: Bool = false
    
    public var sharedUserDefaults: UserDefaults
    
    /// A threadsafe observer for the `BridgeClient.UserSessionInfo` for the current user.
    public let appConfig: AppConfigObserver = .init()
    
    // Do not expose publicly. This class is not threadsafe.
    var config: AppConfig? {
        didSet {
            appConfig.appConfig = config
            didUpdateAppConfig()
            updateAppState()
        }
    }
    
    /// Called when the `BridgeClient.AppConfig` is updated.
    open func didUpdateAppConfig() {
        // Do nothing. Allows subclass override of `config.didSet`.
    }
    
    /// A threadsafe observer for the `BridgeClient.UserSessionInfo` for the current user.
    public let userSessionInfo: UserSessionInfoObserver = .init()
    
    // Do not expose publicly. This class is not threadsafe.
    var session: UserSessionInfo? {
        didSet {
            userSessionInfo.userSessionInfo = session
            uploadProcessor.isTestUser = userSessionInfo.dataGroups?.contains("test_user") ?? false
            didUpdateUserSessionInfo()
            updateAppState()
        }
    }
    
    /// Called when the `BridgeClient.UserSessionInfo` is updated.
    open func didUpdateUserSessionInfo() {
        // Do nothing. Allows subclass override of `session.didSet`.
    }
    
    @MainActor
    public final func setUserSessionInfo(_ session: UserSessionInfo?) {
        self.session = session
        if session?.authenticated ?? false {
            self.hasLoggedIn = true
        }
    }
    
    /// Has the participant previously logged in successfully?
    lazy public private(set) var hasLoggedIn: Bool = sharedUserDefaults.bool(forKey: kLoginStateKey) {
        didSet {
            sharedUserDefaults.set(hasLoggedIn, forKey: kLoginStateKey)
        }
    }
    
    private var appConfigManager: NativeAppConfigManager!
    public private(set) var authManager: NativeAuthenticationManager!
    
    /// Convenience initializer for intializing a bridge manager with just an app id and pem file.
    ///
    /// Both the APP ID and the CMS Public Key (ie. pem file) are accessible through the [Bridge Study Manager](https://research.sagebridge.org).
    /// Once logged into an "app" (which can host multiple "studies"), select "Server Config -> Settings" from the menu.
    ///
    /// - Parameters:
    ///   - appId: The "APP ID" is shown in the upper right corner of the [Bridge Study Manager](https://research.sagebridge.org).
    ///   - pemPath: The path to the pem file (as an embedded resource) to use for encrypting uploads. The pem file can be downloaded
    ///     from the [Bridge Study Manager](https://research.sagebridge.org) by going "Server Settings -> Settings"
    ///     and tapping on the button labeled "Download CMS Public Key..." and saving the file to a secure location.
    public convenience init(appId: String, appGroupId: String? = nil, pemPath: String? = nil) {
        self.init(platformConfig: PlatformConfigImpl(appId: appId, appGroupIdentifier: appGroupId), pemPath: pemPath)
    }
    
    /// Initialize the bridge manager with a custom platform config.
    ///
    /// - Parameters:
    ///   - platformConfig: The platform config to use to set up the connection to Bridge.
    ///   - pemPath: The path to the location of the pem file to use when encrypting uploads.
    public init(platformConfig: IOSPlatformConfig, pemPath: String? = nil) {
        let pemPath = pemPath ?? Bundle.main.path(forResource: platformConfig.appId, ofType: "pem")
        uploadProcessor = .init(pemPath: pemPath)
        self.platformConfig = platformConfig
        self.title = self.platformConfig.localizedAppName
        self.isPreview = (platformConfig.appId == kPreviewStudyId)
        if !self.isPreview {
            IOSBridgeConfig().initialize(platformConfig: self.platformConfig)
            self.sharedUserDefaults = self.platformConfig.appGroupIdentifier.flatMap { .init(suiteName: $0) } ?? .standard
        }
        else {
            self.sharedUserDefaults = UserDefaults.standard
        }

        // Set up the background network manager singleton and make us its app manager
        let bnm = BackgroundNetworkManager.shared
        bnm.appManager = self
        
        // Register the file upload APIs so that retries can happen
        let _ = ParticipantFileUploadAPI.shared
        let _ = StudyDataUploadAPI.shared
    }
    
    /// **Required:** This method should be called by the app delegate when the app is launching in either `willLaunch` or `didLaunch`.
    public func setup() {

        // Initialize koin
        #if DEBUG
            let enableNetworkLogs = true
        #else
            let enableNetworkLogs = false
        #endif
        KoinKt.doInitKoin(enableNetworkLogs: enableNetworkLogs)
        
        // Hook up app config
        self.appConfigManager = NativeAppConfigManager() { appConfig, _ in
            self.config = appConfig ?? self.config
        }
        self.appConfigManager.observeAppConfig()
        
        // Hook up user session info
        self.authManager = NativeAuthenticationManager() { userSessionInfo in
            guard userSessionInfo == nil || !userSessionInfo!.isEqual(userSessionInfo) else { return }
            self.session = userSessionInfo
        }
        self.session = self.authManager.session()
        self.authManager.observeUserSessionInfo()
        
        // Update the app state
        updateAppState()
    }
    
    /// **Required:** This method should be called by the app delegate in the implementation of
    /// `application(:, handleEventsForBackgroundURLSession:, completionHandler:)` and is used to
    /// restore a background upload session.
    public final func handleEvents(for backgroundSession: String, completionHandler: @escaping () -> Void) {
        BackgroundNetworkManager.shared.restore(backgroundSession: backgroundSession, completionHandler: completionHandler)
    }
    
    /// Sign out the current user.
    @MainActor
    open func signOut() {
        authManager.signOut()
        self.session = nil
        self.hasLoggedIn = true
    }
    
    // @Protected - Only this class should call this method and only subclasses should implement.
    open func updateAppState() {
        // Do nothing. Allows subclass override setup and app state changes.
    }
    
    /// Encrypt and upload an archive created with the given builder.
    ///
    /// - Parameter builder: The archive builder.
    public final func encryptAndUpload(using builder: ArchiveBuilder) {
        OperationQueue.main.addOperation {
            self.isUploading = true
        }
        uploadProcessor.encryptAndUpload(using: builder)
    }
    
    /// Encrypt and upload the given data archives.
    ///
    /// - Parameter archives: The archives to encrypt and upload.
    public final func encryptAndUpload(_ archives: [DataArchive]) {
        OperationQueue.main.addOperation {
            self.isUploading = true
        }
        uploadProcessor.encryptAndUpload(archives)
    }
}

final class ArchiveUploadProcessor {
    
    let pemPath: String?
    var isTestUser: Bool = false
    
    init(pemPath: String?) {
        self.pemPath = pemPath
    }
    
    func encryptAndUpload(using builder: ArchiveBuilder) {
        Task.detached(priority: .medium) {
            await self._encryptAndUpload(using: builder)
        }
    }
    
    @MainActor
    private func _encryptAndUpload(using builder: ArchiveBuilder) async {
        do {
            let archive = try await builder.buildArchive()
            await _copyTest(archive: archive)
            let encrypted = await _encrypt(archive: archive)
            await _upload(archive: archive, encrypted: encrypted)
        } catch {
            debugPrint("ERROR Failed to archive and upload \(builder.identifier): \(error)")
        }
    }
    
    func encryptAndUpload(_ archives: [DataArchive]) {
        Task.detached(priority: .medium) {
            await self._encryptAndUpload(archives)
        }
    }

    @MainActor
    private func _encryptAndUpload(_ archives: [DataArchive]) async {
        await withTaskGroup(of: Void.self) { group in
            archives.forEach { archive in
                group.addTask {
                    await self._copyTest(archive: archive)
                    let encrypted = await self._encrypt(archive: archive)
                    await self._upload(archive: archive, encrypted: encrypted)
                }
            }
        }
    }
    
    private func _copyTest(archive: DataArchive) async {
        #if DEBUG
        if isTestUser {
            archive.copyTestArchive()
        }
        #endif
    }
    
    private func _encrypt(archive: DataArchive) async -> Bool {
        guard let path = self.pemPath else { return false }
        do {
            try archive.encryptArchive(using: path)
            return true
        } catch {
            debugPrint("ERROR: Failed to encrypt \(archive.identifier). \(error)")
            return false
        }
    }
    
    @MainActor
    private func _upload(archive: DataArchive, encrypted: Bool) async {
        let exporterV3Metadata: JsonElement? = (archive as? AbstractResultArchive)?.schedule.map { schedule in
            .object([
                "instanceGuid": schedule.instanceGuid,
                "eventTimestamp": schedule.session.eventTimestamp
            ])
        }
        let extras = StudyDataUploadExtras(encrypted: encrypted, metadata: exporterV3Metadata, zipped: true)
        let id = archive.identifier
        guard let url = archive.encryptedURL else {
            debugPrint("WARNING! Cannot upload \(id)")
            return
        }
        StudyDataUploadAPI.shared.upload(fileId: id, fileUrl: url, contentType: "application/zip", extras: extras)
        do {
            try FileManager.default.removeItem(at: url)
        } catch let err {
            debugPrint("WARNING! Failed to delete encrypted archive \(id) at \(url). \(err)")
        }
    }
}

/// A convenience method for ensuring that a given result is processed on the main thread.
func processResultOnMainThread<T>(_ process: () -> T?) -> T? {
    var ret: T? = nil
    if Thread.isMainThread {
        ret = process()
    } else {
        DispatchQueue.main.sync {
            ret = process()
        }
    }
    return ret
}

/// A convenience method for ensuring that a given result is processed on the main thread.
func processOnMainThread(_ process: () -> Void) {
    if Thread.isMainThread {
        process()
    } else {
        DispatchQueue.main.sync {
            process()
        }
    }
}
