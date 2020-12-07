#!/bin/sh

[ -d "$HOME"/secrets ] || mkdir "$HOME"/secrets

# Create the encrypted api.json
echo "$API_JSON" | base64 -di > api.json.gpg

# Decrypt the api.json
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg \
  --quiet \
  --batch\
  --yes \
  --decrypt \
  --passphrase="$API_JSON_ENCRYPTION_PASSPHRASE" \
  --output "$HOME"/secrets/api.json api.json.gpg

# Add api.json to Appfile
echo "json_key_file(\"$(realpath "$HOME"/secrets/api.json)\")" >> fastlane/Appfile
