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
### android bundleDebug
```
fastlane android bundleDebug
```
Assemble a new debug aab version
### android bundleRelease
```
fastlane android bundleRelease
```
Assemble a new release aab version
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
### android deploy_aab_beta
```
fastlane android deploy_aab_beta
```
Deploy a new beta aab version to the Google Play
### android deploy_aab_production
```
fastlane android deploy_aab_production
```
Deploy a new production aab version to the Google Play

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
