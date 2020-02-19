#!/usr/bin/env bash

echo -n "$SIGNING_SECRETKEYRINGFILE" | openssl base64 -d > secring.gpg

./gradlew clean test publish \
  -PsonatypeUsername=$SONATYPE_USERNAME \
  -PsonatypePassword=$SONATYPE_PASSWORD \
  -Psigning.keyId=$SIGNING_KEYID \
  -Psigning.password=$SIGNING_PASSWORD \
  -Psigning.secretKeyRingFile=secring.gpg \
  --info
