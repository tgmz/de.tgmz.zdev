/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.cpp;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.tgmz.zdev.editor.ZdevCompletionProposal;

/**
 * C key words. <code>enum</code> to identify doubles quickly.
 */
public enum ReservedWords {
	ALIGNAS
	, ALIGNOF
	, AND
	, AND_EQ
	, ASM
	, ATOMIC_CANCEL
	, ATOMIC_COMMIT
	, ATOMIC_NOEXCEPT
	, AUTO
	, BITAND
	, BITOR
	, BOOL
	, BREAK
	, CASE
	, CATCH
	, CHAR
	, CHAR8_T
	, CHAR16_T
	, CHAR32_T
	, CLASS
	, COMPL
	, CONCEPT
	, CONST
	, CONSTEVAL
	, CONSTEXPR
	, CONSTINIT
	, CONST_CAST
	, CONTINUE
	, CO_AWAIT
	, CO_RETURN
	, CO_YIELD
	, DECLTYPE
	, DEFAULT
	, DELETE
	, DO
	, DOUBLE
	, DYNAMIC_CAST
	, ELSE
	, ENUM
	, EXPLICIT
	, EXPORT
	, EXTERN
	, FALSE
	, FLOAT
	, FOR
	, FRIEND
	, GOTO
	, IF
	, INLINE
	, INT
	, LONG
	, MUTABLE
	, NAMESPACE
	, NEW
	, NOEXCEPT
	, NOT
	, NOT_EQ
	, NULLPTR
	, OPERATOR
	, OR
	, OR_EQ
	, PRIVATE
	, PROTECTED
	, PUBLIC
	, REFLEXPR
	, REGISTER
	, REINTERPRET_CAST
	, REQUIRES
	, RETURN
	, SHORT
	, SIGNED
	, SIZEOF
	, STATIC
	, STATIC_ASSERT
	, STATIC_CAST
	, STRUCT
	, SWITCH
	, SYNCHRONIZED
	, TEMPLATE
	, THIS
	, THREAD_LOCAL
	, THROW
	, TRUE
	, TRY
	, TYPEDEF
	, TYPEID
	, TYPENAME
	, UNION
	, UNSIGNED
	, USING
	, VIRTUAL
	, VOID
	, VOLATILE
	, WCHAR_T
	, WHILE
	, XOR
	, XOR_EQ;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.getDefault());
	}

	public static ZdevCompletionProposal[] getCompletionProposals() {
		List<ZdevCompletionProposal> l = new LinkedList<>();
		
		for (ReservedWords rw : values()) {
			l.add(new ZdevCompletionProposal(rw.toString(),  rw.toString(),  null));
		}
		
		return l.toArray(new ZdevCompletionProposal[l.size()]);
	}
}
