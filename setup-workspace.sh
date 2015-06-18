#!/bin/bash

if [ -d $(pwd)/build ]; then
    export CLASSPATH=$CLASSPATH:$(pwd)/build
fi

