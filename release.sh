#!/usr/bin/env bash

set -ex

export SSH_HOME=/tmp/foo
export SSH_PASSPHRASE_FILE=$PWD/my-passphrase
export GPG_TTY=

echo -n "$SIGNING_SECRETKEYRINGFILE" | openssl base64 -d > $PWD/secring.gpg

rm -fR $SSH_HOME
mkdir $SSH_HOME
chmod 700 $SSH_HOME

rm -f $SSH_PASSPHRASE_FILE
touch $SSH_PASSPHRASE_FILE
chmod 600 $SSH_PASSPHRASE_FILE
echo $SIGNING_PASSWORD >>$SSH_PASSPHRASE_FILE

gpg --homedir $SSH_HOME --passphrase-file $SSH_PASSPHRASE_FILE --pinentry-mode loopback --batch --import $PWD/secring.gpg
rm $PWD/secring.gpg

mvn -e -X -s settings.xml clean deploy -Dgpg.homedir=$SSH_HOME -Dgpg.keyname=$SIGNING_KEYID

rm -fR $SSH_HOME
rm -f $SSH_PASSPHRASE_FILE
