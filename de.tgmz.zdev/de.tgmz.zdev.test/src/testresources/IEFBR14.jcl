//* Copyright (c) 09.10.2023 Thomas Zierer                      
//*                                                             
//* This program and the accompanying materials are made        
//* available under the terms of the Eclipse Public License 2.0 
//* which is available at https://www.eclipse.org/legal/epl-2.0/
//*                                                             
//* SPDX-License-Identifier: EPL-2.0                            
//*
//STEP01   EXEC PGM=IEFBR14
//SYSPRINT DD SYSOUT=*
//SYSOUT   DD SYSOUT=*
//SYSDUMP  DD SYSOUT=*
//DD1      DD DSN=TGMZ.IBMMF.PSFILE,
//            DISP=(NEW,CATLG,DELETE),VOLUME=SER=DEVL,
//            SPACE=(TRK,(1,1),RLSE),UNIT=SYSDA,
//            DCB=(DSORG=PS,RECFM=FB,LRECL=80,BLKSIZE=800)
//*