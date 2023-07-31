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
  - A valid m3u/m3u8/xspf file
- For Local-MP3-Mode
  - Windows -> file locking which is not available on UNIX needed by this mode
  - A Folder containing mp3 files / subfolders which contain mp3 files

## **How to Use**

- [ ] Download Danceinterpreter.jar and logback.xml
- [ ] Save them to the Directory you want the Software to run in
- [ ] See [Spotify](README.md#spotify) / [Local mp3 Files](README.md#local-mp3-Files)


## Spotify

1. Run `Danceinterpreter.jar`
2. Select `Spotify` in the drop-down
3. Authorize Spotify Account access in your browser
9. Setup Finished!

## Local mp3 Files

1. Run `Danceinterpreter.jar`
5. Select `LocalMP3` in the drop-down
6. Select the Directory the App should check for playing .mp3 files
7. Finished!

## Playlists

1. Run `Danceinterpreter.jar`
5. Select `Playlist` in the drop-down
6. Select your m3u/m3u8/xspf file
7. Finished!

## _Usage_
1. Run `DanceInterpreter.jar`
2. Select your appmode in the drop-down
3. Have Fun!


## Self compile and building
1. Clone the project `git clone --recursive https://github.com/klassenserver7b/Danceinterpreter.git`
2. `cd Danceinterpreter`
3. Make sure you have java-jdk-17 and maven installed
4. run 'maven clean package'
5. you can now find your jar at ./target/Danceinterpreter-$VERSION-jar-with-dependencies.jar
6. run it with `java -jar YOUR_JAR_FILE_NAME`
