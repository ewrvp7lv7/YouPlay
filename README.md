# YouPlay
**An open-source YouTube audio & radio player for Android.**

[![IO shield](https://img.shields.io/github/v/release/Stipess1/YouPlay)](https://github.com/Stipess1/YouPlay/releases) [![IO shield](https://img.shields.io/github/license/Stipess1/YouPlay)](https://www.gnu.org/licenses/gpl.html)

## Description
YouTube client for playing music in background. Application reproduces only audio stream without video, which significantly reduces network load. Uses [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor) for extracting information from YouTube.

## Features
* Search songs
* Download audio only
* Show next/related songs
* Enqueue songs
* Show comments
* Make your own playlists
* Search over 20,000 radio stations
* Background player
* No ads

More features will be added in the near future.

## Requirements
Minimum Android version is 5.0 Lollipop (API 21). The project targets the latest Android release (API 35) while maintaining backward compatibility.

## ExoPlayer migration
The project now uses the AndroidX Media3 implementation of ExoPlayer. All
player-related classes were switched from the deprecated
`com.google.android.exoplayer2` package to their `androidx.media3`
counterparts.
This resolves the deprecation warnings shown during compilation.

## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl-3.0.en.html)

YouPlay is Free Software: You can use, study share and improve it at your will. Specifically you can redistribute and/or modify it under the terms of the [GNU General Public](https://www.gnu.org/licenses/gpl.html) License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
