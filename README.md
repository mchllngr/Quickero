![Quickero](./assets/logo_name.svg)

[![Build Status](https://github.com/mchllngr/Quickero/workflows/deploy/badge.svg)](https://github.com/mchllngr/Quickero/actions)

An android app to quickly open your favorite apps through a notification.

<a href='https://play.google.com/store/apps/details?id=de.mchllngr.quickero'><img width='300' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Building

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

## License

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
