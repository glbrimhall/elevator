#!/bin/sh

FILES="\
Elevator \
ElevatorQueue \
ElevatorSystem \
FloorRequest \
IParseCommand \
Movement \
ParseCommand \
ParseElevator \
ParseFloor \
ParseHelp \
ParseList \
ParseNumberElevatorFloors \
ParseQuit \
ParseStatus"

for F in $FILES; do
   echo " mv $F.js $F.py"
   mv $F.js $F.py
done
