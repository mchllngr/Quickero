# QuickOpen [![Build Status](https://travis-ci.org/mchllngr/QuickOpen.svg?branch=master)](https://travis-ci.org/mchllngr/QuickOpen)
An android app to quickly open your favorite apps through a notification.

# KNOWN BUGS
- splashscreen is broken
- the automatic start of NotificationService is pretty inconsistent
- settings-text eventually too long (-> multiline)
- item slide shows a red background, which doesn't look right (no trash-box for deleting, no fading, etc.)

# TODO
- remove notification priority options, because this can now be handled over the channel (even by the user)
    - also the visiblity option ?
- add licence to every file ?
- remove saving restart-time in NotificationServiceStarter
- add better texts and explanations to settings-page / style settings with images
- add empty-view for recyclerview
- check customNotification-design on all api-levels
- check activity leak on api 19 ? (open and close settings)
- what to do when app-list is empty ?
- rebuild MainPresenter#openApplicationList() with better rxjava-integration
- rebuild MainPresenter#addDummyItemsIfFirstStart() with better rxjava-integration
- add Crashlytics

# TODO a little bit later
- add tutorial
- add screenshots + real descriptions to google-play-page
- add delete icon on remove-swipe
- add feedback
- disable MainActivity when notification_enabled = false
- check for uninstalled applications every start (BroadcastReceiver)
- add "Donate a beer"
- check night-mode on about-page
    - https://github.com/medyo/android-about-page/issues/62
- other app-name
- add search-bar to application-list ?

# FEATURES-IDEAS (for later)
- profiles (e.g. work/home)
    - automatic change between profiles decided by time/location(/connected wifi?)

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
