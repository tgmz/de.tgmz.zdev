//<JOBCARD>       
//*
//* Copyright (c) 06.10.2023 Thomas Zierer
//*
//* This program and the accompanying materials are made
//* available under the terms of the Eclipse Public License 2.0
//* which is available at https://www.eclipse.org/legal/epl-2.0/
//*
//* SPDX-License-Identifier: EPL-2.0
//*    
//* Allocate ZFS file system to hold the filelock server jars
//*
//DEFINE EXEC PGM=IDCAMS                            
//SYSPRINT DD SYSOUT=*                              
//SYSIN DD *                                        
   DEFINE CLUSTER (NAME(HLQ.ZDEV.ZFS)          -    
   LINEAR MEGABYTES(80 10) SHAREOPTIONS(2))         
/*                                                  
//FORMAT EXEC PGM=IOEAGFMT,REGION=0M,               
// PARM=('-aggregate hlq.zdev.zfs -compat')      
//SYSPRINT DD SYSOUT=*                              
//STDOUT DD SYSOUT=*                                
//STDERR DD SYSOUT=*                                
//SYSUDUMP DD SYSOUT=*                              
//CEEDUMP DD SYSOUT=*                               
//*                                                 