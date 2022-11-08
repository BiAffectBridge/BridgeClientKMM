//
//  HistoryView.swift
//

import SwiftUI
import BridgeClient
import SharedMobileUI

public struct HistoryView: View {
    @EnvironmentObject private var bridgeManager: SingleStudyAppManager
    @StateObject private var viewModel: HistoryViewModel = .init()
    @SwiftUI.Environment(\.assessmentInfoMap) private var assessmentInfoMap: AssessmentInfoMap

    private let previewRecords: [AssessmentRecord]
    
    public init(_ previewRecords: [AssessmentRecord] = []) {
        self.previewRecords = previewRecords
    }
    
    public var body: some View {
        ScreenBackground {
            VStack(spacing: 21) {
                if viewModel.minutes == 0 {
                    heroImage()
                    Text("Your completed activities will be shown here.", bundle: .module)
                        .font(DesignSystem.fontRules.headerFont(at: 2))
                        .multilineTextAlignment(.center)
                        .foregroundColor(.textForeground)
                        .padding(.horizontal, 24)
                }
                else {
                    header()
                    CustomScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(viewModel.records) { record in
                                card(record)
                            }
                        }
                        .padding(.bottom, 20)
                    }// end scrollview
                }
            }
        }
        .onAppear {
            viewModel.onAppear(studyId: bridgeManager.studyId!, previewRecords: previewRecords)
        }
    }
    
    @ViewBuilder
    private func header() -> some View {
        VStack(spacing: 10) {
            ZStack(alignment: .top) {
                heroImage()
                    .padding(.top, 12)
                VStack(spacing: -6) {
                    Text("\(viewModel.minutes)")
                        // Use fixed size b/c otherwise real-estate will not fit with image.
                        .font(.latoFont(fixedSize: 24, weight: .bold))
                        .foregroundColor(.textForeground)
                    Text("minutes", bundle: .module)
                        // Use fixed size b/c otherwise real-estate will not fit with image.
                        .font(.poppinsFont(fixedSize: 12))
                        .foregroundColor(.textForeground)
                }
            }
            Text("Thank you for your contributions!", bundle: .module)
                .font(DesignSystem.fontRules.headerFont(at: 2))
                .foregroundColor(.textForeground)
        }
        .padding(.top, 16)
    }
    
    @ViewBuilder
    private func heroImage() -> some View {
        CompositeTintedImage("History.tinted", background: "History", bundle: .module)
    }
    
    @ViewBuilder
    private func card(_ record: AssessmentRecord) -> some View {
        HStack(alignment: .top, spacing: 0) {
            rightSide(record)
            leftSide(record)
        }
        .padding(.horizontal, 31)
    }
    
    @ViewBuilder
    private func rightSide(_ record: AssessmentRecord) -> some View {
        assessmentInfoMap.color(for: record.info)
            .overlay(assessmentInfoMap.icon(for: record.info))
            .frame(width: 114)
            .frame(minHeight: 112)
    }
    
    let detailHeaderFont = DesignSystem.fontRules.headerFont(at: 7)
    let titleHeaderFont = DesignSystem.fontRules.headerFont(at: 5)
    
    @ViewBuilder
    private func leftSide(_ record: AssessmentRecord) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            CompletedDotView(color: assessmentInfoMap.color(for: record.info))
                .padding(.bottom, 2)
            Text(DateFormatter.string(from: record.finishedOn, timeZone: record.timeZone, dateStyle: .medium, timeStyle: .none))
                .foregroundColor(.textForeground)
                .font(detailHeaderFont)
            assessmentInfoMap.title(for: record.info)
                .font(titleHeaderFont)
                .foregroundColor(.textForeground)
                .frame(maxWidth: .infinity, alignment: .leading)
            Text(durationFormatter.string(from: Double(record.minutes * 60)) ?? "")
                .foregroundColor(.textForeground)
                .font(detailHeaderFont)
        }
        .padding(.leading, 16)
        .padding(.top, 8)
    }
    
    private let durationFormatter: DateComponentsFormatter = {
        let formatter = DateComponentsFormatter()
        formatter.unitsStyle = .short
        formatter.allowedUnits = .minute
        return formatter
    }()
}

extension DateFormatter {
    static func string(from date: Date, timeZone: TimeZone, dateStyle: Style, timeStyle: Style) -> String {
        let formatter = DateFormatter()
        formatter.timeStyle = timeStyle
        formatter.dateStyle = dateStyle
        formatter.timeZone = timeZone
        return formatter.string(from: date)
    }
}

// syoung 08/16/2021 SwiftUI fails to build complex UI in a shared framework. Therefore, the preview
// for this element is in iosApp.
struct HistoryView_Previews: PreviewProvider {
    static var previews: some View {
        Text("Hello, World!")
    }
}
 

