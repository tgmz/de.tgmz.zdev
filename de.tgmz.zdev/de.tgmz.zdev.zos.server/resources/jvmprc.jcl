//******************************************************************** 
//* Licensed Materials - Property of IBM                               
//* 5655-W44                                                           
//* Copyright IBM Corp. 1997, 2011                                     
//* STATUS = HJVB700                                                   
//*                                                                    
//* Stored procedure for executing the JZOS Java Batch Launcher        
//*                                                                    
//* Tailor the proc your installation:                                 
//* If the PDSE containing the JVMLDMxx module is not in your          
//* LNKLST, uncomment the STEPLIB statement and update the DSN to      
//* refer to the PDSE                                                  
//*                                                                    
//*******************************************************************  
//JVMPRC   PROC JAVACLS=,                < Fully Qfied Java class..RQD 
//   ARGS=,                              < Args to Java class          
//   VERSION='76',                       < DEFAULT: Java 1.7.0, 64 BIT          
//   LOGLVL='I',                         < Debug LVL: +I(info) +T(trc) 
//   REGSIZE='0M',                       < EXECUTION REGION SIZE       
//   LEPARM=''                                                         
//JAVAJVM  EXEC PGM=JVMLDM&VERSION,REGION=&REGSIZE,                    
//   PARM='&LEPARM/&LOGLVL &JAVACLS &ARGS'                             
//SYSPRINT DD SYSOUT=*          < System stdout                        
//SYSOUT   DD SYSOUT=*          < System stderr                        
//STDOUT   DD SYSOUT=*          < Java System.out                      
//STDERR   DD SYSOUT=*          < Java System.err                      
//CEEDUMP  DD SYSOUT=*                                                 
//ABNLIGNR DD DUMMY                                                    
//*                                                                    
//*The following DDs can/should be present in the calling JCL          
//*                                                                    
//*STDIN   DD                   < OPTIONAL - Java System.in            
//*STDENV  DD                   < REQUIRED - JVM Environment script    
//*MAINARGS DD                  < OPTIONAL - Alt. method to supply args
// PEND                                                                
