{0}
//*
//* Copyright (c) 06.10.2023 Thomas Zierer
//*
//* This program and the accompanying materials are made
//* available under the terms of the Eclipse Public License 2.0
//* which is available at https://www.eclipse.org/legal/epl-2.0/
//*
//* SPDX-License-Identifier: EPL-2.0
//*    
//* Execute the script
//*
//XSDOSRG EXEC  PGM=BPXBATCH,
//        PARM=''sh cd {1}; chmod +x {2}; ./{2}''
//STDOUT  DD SYSOUT=*
//STDERR  DD SYSOUT=*
