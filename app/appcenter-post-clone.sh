#!/usr/bin/env bash

# fail if any command fails
set -e
# debug log
set -x


git clone https://github.com/Giphy/giphy-android-sdk-analytics.git ../../giphy-android-sdk-analytics/
git clone https://github.com/Giphy/giphy-android-sdk.git ../../giphy-android-sdk/ --single-branch
