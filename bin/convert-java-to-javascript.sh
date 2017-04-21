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
ParseQuit \
ParseStatus \
ParseNumberElevatorFloors"

SRC_DIR=java/src/main/java/com/glbrimhall/elevator
DST_DIR=javascript/lib

for SRC in $FILES; do
    echo "COPYING: $SRC_DIR/$SRC.java $DST_DIR/$SRC.js"
    cp -fv $SRC_DIR/$SRC.java $DST_DIR/$SRC.js
done
