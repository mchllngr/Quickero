fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android deploy_apk_internal
```
fastlane android deploy_apk_internal
```
Deploy a new internal apk version to the Google Play
### android deploy_apk_alpha
```
fastlane android deploy_apk_alpha
```
Deploy a new alpha apk version to the Google Play
### android deploy_aab_internal
```
fastlane android deploy_aab_internal
```
Deploy a new internal aab version to the Google Play
### android deploy_aab_alpha
```
fastlane android deploy_aab_alpha
```
Deploy a new alpha aab version to the Google Play
### android assembleRelease
```
fastlane android assembleRelease
```
Assemble a new release apk version
### android bundleRelease
```
fastlane android bundleRelease
```
Assemble a new release aab version

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
