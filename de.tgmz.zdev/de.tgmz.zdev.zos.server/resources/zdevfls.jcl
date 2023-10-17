//*
//* Copyright (c) 06.10.2023 Thomas Zierer
//*
//* This program and the accompanying materials are made
//* available under the terms of the Eclipse Public License 2.0
//* which is available at https://www.eclipse.org/legal/epl-2.0/
//*
//* SPDX-License-Identifier: EPL-2.0
//*
//* PROC for running the filelock server
//*    
//ZDEVFLS  EXEC PROC=JVMPRC,LOGLVL='+I',
//       JAVACLS='de.tgmz.zdev.filelock.FileLockServer',
//       ARGS='3141',
//       VERSION='70',   <- JAVA 1.7.0, 32 BIT
//       LEPARM='HEAP(150M),ANYHEAP(1M),RPTOPTS(ON),RPTSTG(ON)'
//STDENV   DD DISP=SHR,DSN=HLQ.ZDEV.FLSOPTS
//SYSPRINT DD SYSOUT=*                        
//SYSOUT   DD SYSOUT=*                        
//STDOUT   DD SYSOUT=*                      
//STDERR   DD SYSOUT=*                      
//CEEDUMP  DD SYSOUT=*                                                 
//