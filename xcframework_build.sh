#!/bin/sh

#  xcframework_build.sh
#  iosApp
#

./gradlew :bridge-client:assembleBridgeClientXCFramework
cp -rf bridge-client/build/XCFrameworks/* ../BridgeClient-Swift/Binaries

