# Set and export LIBPATH so xsdosrg finds the DLLs.
# Export is mandatory! 

# Copyright (c) 06.10.2023 Thomas Zierer
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

export LIBPATH=/usr/lib:/usr/lpp/java/J8.0/lib/s390/classic/:/usr/lib/java_runtime

# Generate the OSR files
xsdosrg -v -o "{0}" {1}
