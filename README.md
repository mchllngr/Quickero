# QuickOpen [![Build Status](https://travis-ci.org/mchllngr/QuickOpen.svg?branch=master)](https://travis-ci.org/mchllngr/QuickOpen)
An android app to quickly open your favorite apps through a notification.

# KNOWN BUGS
- NightMode starting automatically?
- splashscreen is broken (only api 27?)
- white status bar color on release (happened on Nexus7 API 27 Emulator)
- AboutPage "Rate us on the Play Store" seems to be non functional

# TODO
- add empty-view for recyclerview
- what to do when app-list is empty ?
- disable MainActivity when notification_enabled = false
- new notification icon
- check customNotification-design on all api-levels
- other app-name
- add responsive icon + new icon design
- add licence to every file ?
- improve proguard
- add screenshots + real descriptions to google-play-page (and about-page)

# TODO a little bit later
- rebuild app completely in kotlin
- show installed apps on another page
- add tutorial
- add feedback
- add "Donate a beer"
- add search-bar to application-list ?

# FEATURES-IDEAS (for later)
- profiles (e.g. work/home)
    - automatic change between profiles decided by time/location(/connected wifi?)

# Crashlytics
This app uses Crashlytics by Fabric. For it to work you must declare your own `app/fabric.properties` file containing your `apiKey` and `apiSecret`. Otherwise you first need to remove Crashlytics completely.

# License

```
Copyright 2016 Michael Langer (mchllngr)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
