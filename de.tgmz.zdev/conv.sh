#!/bin/bash

# Copyright (c) 06.10.2023 Thomas Zierer
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

FROM=iso-8859-1
TO=UTF-8
ICONV="iconv -f $FROM -t $TO"
# Convert
find . -type f -name \*.txt -o -name \*.bat -o -name \*.xml -o -name \*.md -o -name \*.MF  -o -name \*.ini -o -name \*.java -o -name \*.properties | while read fn; do
cp ${fn} ${fn}.bak
$ICONV < ${fn}.bak > ${fn}
rm ${fn}.bak
done
