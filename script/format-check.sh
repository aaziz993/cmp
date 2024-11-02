#!/bin/bash

echo Spotless code format check

./gradlew spotlessCheck --no-configuration-cache
