# Danceinterpreter
[![CodeFactor](https://www.codefactor.io/repository/github/klassenserver7b/danceinterpreter/badge)](https://www.codefactor.io/repository/github/klassenserver7b/danceinterpreter)
[![License](https://img.shields.io/github/license/klassenserver7b/Danceinterpreter.svg)](https://github.com//klassenserver7b/Danceinterpreter/blob/master/LICENSE)
[![Build Status](https://jitci.com/gh/klassenserver7b/Danceinterpreter/svg)](https://jitci.com/gh/klassenserver7b/Danceinterpreter)
[![Latest Release](https://jitpack.io/v/Klassenserver7b/Danceinterpreter.svg)](https://jitpack.io/#Klassenserver7b/Danceinterpreter)


This tool provides information about a currently playing song and displays them.
-> These information are "Title", "Author", "Cover" and "Dance"
It's meant to be used on e.g. a Prom or other Dance events

## Requirements
- Java 17 or newer
- For Spotify-use:
  -  Spotify Account
  -  Internet Connection
- For Playlist-Mode
  - A valid m3u/m3u8/xspf file referencing **LOCAL** mp3 files
- For Local-MP3-Mode
  - Windows only -> file locking which is not available on UNIX is needed by this mode
  - A Folder containing mp3 files / subfolders which contain mp3 files

## **How to Use**

- [ ] Download `danceinterpreter-x.x.x-full.jar` from the [Release page](https://github.com/klassenserver7b/Danceinterpreter/releases/latest)
- [ ] Save them to the Directory you want the Software to run in
- [ ] See [Spotify](README.md#spotify) / [Local mp3 Files](README.md#local-mp3-Files) / [Playlist](README.md#playlists)


## Spotify - UNMAINTANIED

1. Run `danceinterpreter-x.x.x-full.jar`
2. Select `Spotify` in the drop-down
3. Authorize Spotify Account access in your browser
9. Setup Finished!

## Local mp3 Files

**WINDOWS ONLY** -> UNMAINTANIED since I don't use Windows anymore
1. Run `danceinterpreter-x.x.x-full.jar`
5. Select `LocalMP3` in the drop-down
6. Select the Directory the App should check for playing .mp3 files
7. Finished!

## Playlists

1. Run `danceinterpreter-x.x.x-full.jar`
5. Select `Playlist` in the drop-down
6. Select your m3u/m3u8/xspf file
7. Finished!

## _Usage_
1. Run `danceinterpreter-x.x.x-full.jar`
2. Select your appmode in the drop-down
3. Have Fun!


## Self compile and building
1. Clone the project `git clone https://github.com/klassenserver7b/Danceinterpreter.git`
2. `cd Danceinterpreter`
3. Make sure you have java-jdk-17 and maven installed
4. run 'maven -B package'
5. you can now find your runnable jar at ./target/Danceinterpreter-$VERSION-full.jar
