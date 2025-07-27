#!/usr/bin/env bash
#
# create_db.sh - comprehensive-case-flink
#
# Copyright 2023 Jinsong Zhang
#
# This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
#

if [ "$HOSTNAME" != "db01" ]; then
    echo "Must execute from the database node db01!"
    exit 1
fi

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PSQL_FILE_NAME="log_analysis_db.sql"

cp "$SCRIPT_DIR/$PSQL_FILE_NAME" /tmp/

pushd /tmp/

sudo -u postgres psql -f "./$PSQL_FILE_NAME"
rm "./$PSQL_FILE_NAME"

popd
