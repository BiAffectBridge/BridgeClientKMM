import SwiftUI
import shared

struct ContentView: View {
    
    @ObservedObject private(set) var viewModel: ViewModel
    @State var userName: String = ""
    @State var password: String = ""
    
    var body: some View {
        VStack {
            if (AccountDAO().hasSignedIn) {
                Text(viewModel.configString)
            } else {
                TextField("Username", text: $userName)
                TextField("Password", text: $password)
                Button("Sign In", action: login)
            }
        }
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView()
//    }
//}

extension ContentView {
    // ...

    class ViewModel: ObservableObject {
        let dbDriverFactory: DatabaseDriverFactory
        var nvm: NativeAssessmentViewModel? = nil
        @Published var configString = "Loading..."

        init(dbDriverFactory: DatabaseDriverFactory) {
            self.dbDriverFactory = dbDriverFactory
            self.loadNativeViewModel()
        }
        
        func loadNativeViewModel() {
            self.nvm = NativeAssessmentViewModel(databaseDriverFactory: dbDriverFactory, viewUpdate: { [weak self] summary in
                self?.configString = summary ?? ""
            })
        }

        func loadConfig() {
            self.nvm?.observeAssessmentConfig(identifier: "eGhiQTT2a6SCCmjTod6CDb0t")
        }
        
    }
    
    func login() {
        self.viewModel.nvm?.signIn(userName: userName, password: password, callBack: {_ in self.viewModel.loadConfig()})
        
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        /*@START_MENU_TOKEN@*/Text("Hello, World!")/*@END_MENU_TOKEN@*/
    }
}