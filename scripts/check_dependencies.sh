#!/bin/bash
set -e
# Ensure ARCore dependency is declared
if ! grep -R "libs.arcore" -n --include=build.gradle.kts | grep -q . ; then
  echo "ARCore dependency missing" >&2
  exit 1
fi
# Ensure TensorFlow Lite dependency is declared
if ! grep -R "libs.tensorflow.lite" -n --include=build.gradle.kts | grep -q . ; then
  echo "TensorFlow Lite dependency missing" >&2
  exit 1
fi