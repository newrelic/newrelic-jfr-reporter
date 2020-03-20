#!/usr/bin/env bash

set -ex

# gpg2 doesn't support "long" directory or file names (the limit is something like 100 characters).
# The default home directory that gpg uses is "long" by this definition. We force it to something
# short to make sure we don't exceed the limit.
export SSH_HOME=/tmp/foo

# gpg2 by default tries to launch pinentry in a curses mode, which hangs up the terminal in the
# container. By saving the passphrase to a file, we can pass the filename on the command-line
# instead of passing the passphrase on the command-line.
export SSH_PASSPHRASE_FILE=$PWD/my-passphrase
export GPG_TTY=

# New Relic's Vault + Grand Central doesn't handle binary secrets well. So, vault has the
# keyring stored as a base64-encoded file with 65-char lines. This decodes it. Using
# openssl means that the short lines are handled correctly.
echo -n "$SIGNING_SECRETKEYRINGFILE" | openssl base64 -d > $PWD/secring.gpg

# We have to create the gpg homedir with appropriate permissions. Otherwise, it'll WARN us.
rm -fR $SSH_HOME
mkdir $SSH_HOME
chmod 700 $SSH_HOME

# Write the passphrase to the passphrase file for later usage. Ideally, this minimizes the length
# of time which the passphrase is exposed as a command-line argument.
rm -f $SSH_PASSPHRASE_FILE
touch $SSH_PASSPHRASE_FILE
chmod 600 $SSH_PASSPHRASE_FILE
echo $SIGNING_PASSWORD >>$SSH_PASSPHRASE_FILE

# The secret keyring stored in vault is a gpg 1.0 keyring. The gpg installed in the container is 2.2 by default.
# 2.2 can't use the keyring by default, so we have to convert it for gpg 2.2.
#   --homedir: tells gpg where to store the imported keys
#   --passphrase-file: the passphrase that applies to the secring.
#   --pinentry-mode: gpg forks pinentry (or something). "loopback" tells it to prompt the caller. Then gpg will use the passphrase-file.
#   --batch: never ever attempt to prompt for anything (this script is completely detached from any input terminal).
gpg --homedir $SSH_HOME --passphrase-file $SSH_PASSPHRASE_FILE --pinentry-mode loopback --batch --import $PWD/secring.gpg
rm $PWD/secring.gpg

# At this point, $SSH_HOME has the necessary gpg 2.2 public and secret keys. Now we need to configure the Maven gpg plugin with the information
# necessary to sign the jars.
# We use -e and -X (similar to set -x above in the script) in order to gather as much troubleshooting info if the process fails.
#    gpg.homedir: same as --homedir above; tells gpg where to find the keys to use
#    gpg.keyname: the id of the key to use. There's only one key.
# IMPORTANT: These two settings are only part of the settings that get sent to gpg. See pom.xml for additional settings.
mvn -e -X -s settings.xml clean deploy -Dgpg.homedir=$SSH_HOME -Dgpg.keyname=$SIGNING_KEYID

# delete our secrets, since we don't need them anymore.
rm -fR $SSH_HOME
rm -f $SSH_PASSPHRASE_FILE
