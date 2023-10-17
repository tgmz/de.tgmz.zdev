# Set LIBPATH so xsdosrg finds the DLLs

# Copyright (c) 06.10.2023 Thomas Zierer
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

LIBPATH=/lib:/usr/lib:/usr/lpp/java/J8.0/lib/s390/classic/
LIBPATH=$LIBPATH:/usr/lib/java_runtime

# Generate the OSR files
xsdosrg -v -o "{0}" {1}
