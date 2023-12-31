/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.pli;

/**
 * PL/I builtins.
 */
public enum PLIBuiltin {
	ABS,
	ACOS,
	ADD,
	ADDR,
	ADDRDATA,
	ALL,
	ALLOCATE,
	ALLOCATION,
	ALLOCSIZE,
	ANY,
	ASIN,
	ATAN,
	ATAND,
	ATANH,
	AUTOMATIC,
	AVAILABLEAREA,
	BINARY,
	BINARYVALUE,
	BIT,
	BITLOCATION,
	BOOL,
	BYTE,
	CDS,
	CEIL,
	CENTERLEFT,
	CENTRELEFT,
	CENTERRIGHT,
	CENTRERIGHT,
	CHARACTER,
	CHARGRAPHIC,
	CHARVAL,
	CHECKSTG,
	COLLATE,
	COMPARE,
	COMPLEX,
	CONJG,
	COPY,
	COS,
	COSD,
	COSH,
	COUNT,
	CS,
	CURRENTSIZE,
	CURRENTSTORAGE,
	DATAFIELD,
	DATE,
	DATETIME,
	DAYS,
	DAYSTODATE,
	DAYSTOSECS,
	DECIMAL,
	DIMENSION,
	DIVIDE,
	EDIT,
	EMPTY,
	ENDFILE,
	ENTRYADDR,
	EPSILON,
	ERF,
	ERFC,
	EXP,
	EXPONENT,
	FILEDDINT,
	FILEDDTEST,
	FILEDDWORD,
	FILEID,
	FILEOPEN,
	FILEREAD,
	FILESEEK,
	FILETELL,
	FILEWRITE,
	FIXED,
	FIXEDBIN,
	FIXEDDEC,
	FLOAT,
	FLOATBIN,
	FLOATDEC,
	FLOOR,
	GAMMA,
	GETENV,
	GRAPHIC,
	HANDLE,
	HBOUND,
	HBOUNDACROSS,
	HEX,
	HEXIMAGE,
	HIGH,
	HUGE,
	IAND,
	IEOR,
	IMAG,
	INDEX,
	INDICATORS,
	INOT,
	IOR,
	ISFINITE,
	ISIGNED,
	ISINF,
	ISLL,
	ISMAIN,
	ISNAN,
	ISNORMAL,
	ISRL,
	ISZERO,
	IUNSIGNED,
	LBOUND,
	LBOUNDACROSS,
	LEFT,
	LENGTH,
	LINENO,
	LOCATION,
	LOG,
	LOGGAMMA,
	LOG2,
	LOG10,
	LOW,
	LOWERCASE,
	LOWER2,
	MAX,
	MAXEXP,
	MAXLENGTH,
	MEMCONVERT,
	MEMCU12,
	MEMCU14,
	MEMCU21,
	MEMCU24,
	MEMCU41,
	MEMCU42,
	MEMINDEX,
	MEMSEARCH,
	MEMSEARCHR,
	MEMVERIFY,
	MEMVERIFYR,
	MIN,
	MINEXP,
	MOD,
	MPSTR,
	MULTIPLY,
	NULL,
	OFFSET,
	OFFSETADD,
	OFFSETDIFF,
	OFFSETSUBTRACT,
	OFFSETVALUE,
	OMITTED,
	ONAREA,
	ONCHAR,
	ONCODE,
	ONCONDCOND,
	ONCONDID,
	ONCOUNT,
	ONFILE,
	ONGSOURCE,
	ONKEY,
	ONLINE,
	ONLOC,
	ONOFFSET,
	ONSOURCE,
	ONSUBCODE,
	ONWCHAR,
	ONWSOURCE,
	ORDINALNAME,
	ORDINALPRED,
	ORDINALSUCC,
	PACKAGENAME,
	PAGENO,
	PICSPEC,
	PLACES,
	PLIASCII,
	PLICANC,
	PLICKPT,
	PLIDELETE,
	PLIDUMP,
	PLIEBCDIC,
	PLIFILL,
	PLIFREE,
	PLIMOVE,
	PLIOVER,
	PLIREST,
	PLIRETC,
	PLIRETV,
	PLISAXA,
	PLISAXB,
	PLISAXC,
	PLISAXD,
	PLISRTA,
	PLISRTB,
	PLISRTC,
	PLISRTD,
	PLITRAN11,
	PLITRAN12,
	PLITRAN21,
	PLITRAN22,
	POINTER,
	POINTERADD,
	POINTERDIFF,
	POINTERSUBTRACT,
	POINTERVALUE,
	POLY,
	POPCNT,
	PRECISION,
	PRED,
	PRESENT,
	PROCEDURENAME,
	PROCNAME,
	PROD,
	PUTENV,
	RADIX,
	RAISE2,
	RANDOM,
	RANK,
	REAL,
	REG12,
	REM,
	REPATTERN,
	REPEAT,
	REPLACEBY2,
	REVERSE,
	RIGHT,
	ROUND,
	ROUNDDEC,
	SAMEKEY,
	SCALE,
	SEARCH,
	SEARCHR,
	SECS,
	SECSTODATE,
	SECSTODAYS,
	SIGN,
	SIGNED,
	SIN,
	SIND,
	SINH,
	SIZE,
	SOURCEFILE,
	SOURCELINE,
	SQRT,
	SQRTF,
	STORAGE,
	STRING,
	SUBSTR,
	SUBTRACT,
	SUCC,
	SUM,
	SYSNULL,
	SYSTEM,
	TALLY,
	TAN,
	TAND,
	TANH,
	THREADID,
	TIME,
	TINY,
	TRANSLATE,
	TRIM,
	TRUNC,
	TYPE,
	ULENGTH,
	ULENGTH8,
	ULENGTH16,
	UNALLOCATED,
	UNSIGNED,
	UNSPEC,
	UPOS,
	UPPERCASE,
	USUBSTR,
	USURROGATE,
	UVALID,
	UWIDTH,
	VALID,
	VALIDDATE,
	VARGLIST,
	VARGSIZE,
	VERIFY,
	VERIFYR,
	WCHARVAL,
	WEEKDAY,
	WHIGH,
	WIDECHAR,
	WLOW,
	XMLCHAR,
	Y4DATE,
	Y4JULIAN,
	Y4YEAR, 
	;
}
