![Quickero](./assets/logo_name.svg)

[![Build Status](https://github.com/mchllngr/Quickero/workflows/deploy/badge.svg)](https://github.com/mchllngr/Quickero/actions)

An android app to quickly open your favorite apps through a notification.

<a href='https://play.google.com/store/apps/details?id=de.mchllngr.quickero'><img width='300' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Building

> **IMPORTANT**  
> **Because of new retrictions introduced in Android 12 this app can't function correctly anymore,** because it depends heavily on starting other activities from a notification started by a foreground service and that is forbidden as of now. Maybe I'll try to find a way around those new restrictions if I have the time, but **because of this I'm currently not working on this app anymore.**
>
> See:
> * https://developer.android.com/about/versions/12/behavior-changes-12#foreground-service-launch-restrictions
> * https://developer.android.com/about/versions/12/behavior-changes-12#notification-trampolines

### Debug
To build the debug variant you need to create an empty `keystore.properties` file in the root project directory.

### Release
To build the release variant you need to create a `keystore.properties` file in the root project directory defining the following variables:
```
storeFile=/path/to/keystore.jks
storePassword=STORE_PASSWORD
keyAlias=KEY_ALIAS
keyPassword=KEY_PASSWORD
```

## Branching strategy
This repository uses Three-Flow as described [here](https://blog.danlew.net/2020/11/11/trello-androids-git-branching-strategy/).

## License

```
Copyright 2016, 2020 Michael Langer (mchllngr)

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
