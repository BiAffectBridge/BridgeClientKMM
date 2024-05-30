# BridgeClientKMM
A Kotlin Multiplatform Mobile [Bridge](https://developer.sagebridge.org/index.html) client based on a repository design pattern, built using Kotlin Serialization, Ktor, and Sqldelight.

#### iOS

For iOS applications, directly add the [Swift Package](https://github.com/BiAffectBridge/BridgeClient-Swift.git) which contains the binary built by this project as well as the native Swift code used to support access to Bridge on iOS.

To build the binaries, you can run the script:

```
./xcframework_build.sh
```

This script assumes that the [Swift Package](https://github.com/BiAffectBridge/BridgeClient-Swift.git) repo is in a parallel directory and will copy the files to that directories's binary path. The script intentionally does not tag, commit, create branches, etc. You should build the swift package and run unit tests locally before pushing changes to the remote repo.

#### Android

BridgeClientKMM uses Koin for dependency injection and should be initialized as shown below:
```kotlin
class ExampleApplication : MultiDexApplication(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        initKoin (enableNetworkLogs = BuildConfig.DEBUG){
            androidLogger(Level.ERROR)
            androidContext(this@ExampleApplication)
            workManagerFactory()
            //Add any additional Koin modules here
            //modules(appModule)
        }
    }
}
```
To support encrypting uploads, place your study_public_key.pem in your app's assets directory.

