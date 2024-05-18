#!/bin/bash
# 
# This script is meant as supportive start script for
# UNIX-like systems, e.g., Linux or Mac OS X
#

# change to directory where the danceinterpreter is installed
script_path=$(readlink -f $0)
installation_path="${script_path%$(basename "${script_path}")}"

# start danceinterpreter
java -jar "${installation_path}danceinterpreter.jar" "$@"
