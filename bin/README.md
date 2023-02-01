# Danceinterpreter
[![CodeFactor](https://www.codefactor.io/repository/github/klassenserver7b/danceinterpreter/badge)](https://www.codefactor.io/repository/github/klassenserver7b/danceinterpreter)
[![License](https://img.shields.io/github/license/klassenserver7b/Danceinterpreter.svg)](https://github.com//klassenserver7b/Danceinterpreter/blob/master/LICENSE)
[![Build Status](https://jitci.com/gh/klassenserver7b/Danceinterpreter/svg)](https://jitci.com/gh/klassenserver7b/Danceinterpreter)
[![Latest Release](https://jitpack.io/v/Klassenserver7b/Danceinterpreter.svg)](https://jitpack.io/#Klassenserver7b/Danceinterpreter)


This tool provides information about a currently playing song and displays them.
-> These information are "Title", "Author", "Cover" and "Dance"
It's meant to be used on e.g. a Prom or other Dance events

## Requirements
- Java 15 or newer
- For Spotify-use:
  -  Spotify Account
  - Internet Connection

## **How to Use**

- [ ] Download Danceinterpreter.jar and logback.xml
- [ ] Save them to the Directory you want the Software to run in
- [ ] See [Spotify](README.md#spotify) / [Local mp3 Files](README.md#local-mp3-Files)


## Spotify

_IF YOU ALREADY HAVE A VALID CONFIG FILE YOU CAN GO TO_ [**`Usage`**](README.md#usage)

### _First Startup_
1. Run `Danceinterpreter.jar`
2. Select `Spotify` in the drop-down
3. Do step 1 and 2 again
4. Head to `CURRENT_DIRECTORY\logs`
5. Open the newest Log and open the given URL
6. Authorize the App and copy the code from the URL
7. Example-code : <img width="359" alt="185384118-c16f5fae-0a0f-4b0c-81c7-01b2359ee32e" src="https://user-images.githubusercontent.com/79657220/185385213-13cc7660-f8a5-483e-ba94-f580be4c3919.png">
8.  Insert the code in `CURRENT_DIRECTORY\resources\config.properties`  -> `authorization_token=YOUR_TOKEN` 
9. Setup Finished!

### _Usage_
1. Make sure there is a valid config-file in `CURRENT_DIRECTORY\resources\config.properties`
2. Run `DanceInterpreter.jar`
3. Select "Spotify" in the drop-down
4. Finished!

## Local mp3 Files

1. Run `Danceinterpreter.jar`
5. Select `local .mp3 files` in the drop-down
6. Select the Directory the App should check for playing .mp3 files
7. Finished!
