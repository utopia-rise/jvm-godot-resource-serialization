package com.utopiarise.serialization.godot

import kotlin.collections.ArrayList

class Parser(private val tokens: List<Token>) {

    private var current = 0

    private val peek: Token
        get() = tokens[current]
    private val isAtEnd: Boolean
        get() = peek is EofToken
    private val next: Token
        get() {
            if (!isAtEnd) current++
            return peek
        }

    private val declaration: Declaration
        get() {
            return when(val token = peek) {
                is ScriptToken -> {
                    if (next is EqualToken && next is CallExtResourceToken)
                        /* Encapsulate ExternalResource declaration in script declaration as script will be ignored,
                        * thus this external resource will be ignored too */
                        ScriptDeclaration(token, parseCallToExternalResource(token)) else TODO("Error not implemented")
                }
                is IdentifierToken -> parseAssignement(token)
                is LeftBracketToken -> {
                    when(next) {
                        is ResourceToken -> if (next is RightBracketToken) ResourceDeclaration() else TODO("Error not implemented")
                        is GdResourceToken -> GdResourceDeclaration(*parseInnerAssignement())
                        is ExtResourceToken -> ExternalResourceDeclaration(*parseInnerAssignement())
                        else -> TODO("Error not implemented")
                    }
                }
                else -> TODO("Error not implemented")
            }
        }

    fun parse(): List<Declaration> {
        val declarations = ArrayList<Declaration>()
        while (!isAtEnd) {
            declarations.add(declaration)
            next
        }
        return declarations
    }

    private fun parseInnerAssignement(): Array<Declaration> {
        var innerToken = next
        val declarationParameters = ArrayList<Declaration>()
        if (innerToken is IdentifierToken) {
            while (innerToken is IdentifierToken) {
                declarationParameters.add(parseAssignement(innerToken))
                innerToken = next
            }
        }
        else TODO("Error not implemented")
        if (innerToken !is RightBracketToken) TODO("Error not implemented")
        return declarationParameters.toTypedArray()
    }

    private fun parseAssignement(token: IdentifierToken): Declaration {
        return if (next is EqualToken) {
            parseElement(token, next)
        }
        else TODO("Error not implemented")
    }

    private fun parseDisctionnary(identifierToken: IdentifierToken?): Declaration {
        return DictionaryDeclaration(identifierToken, *parseDictionnaryElement(next).toList().toTypedArray())
    }

    private fun parseCallToExternalResource(token: IdentifierToken?): CallExternalResourceDeclaration {
        if (next is LeftParenthesisToken) {
            val idParameterToken = next
            val idParameterValue = if (idParameterToken is NumberToken) (idParameterToken.literal
                    as Double).toInt() else TODO("Error not implemented")
            if (next !is RightParenthesisToken) TODO("Error not implemented")
            return CallExternalResourceDeclaration(token, idParameterValue)
        }
        else TODO("Error not implemented")
    }

    private fun parseArray(token: IdentifierToken?) = ArrayDeclaration(token, *parseArrayElement(next).toTypedArray())

    private fun parseArrayElement(elementToken: Token): List<Declaration> {
        val arrayValues = ArrayList<Declaration>()
        arrayValues.add(parseElement(null, elementToken))
        val nextToken = next
        if (nextToken is CommaToken) arrayValues.addAll(parseArrayElement(next))
        else if (nextToken !is RightBracketToken) TODO("Error not implemented")
        return arrayValues
    }

    private fun parseDictionnaryElement(elementToken: Token): Map<Declaration, Declaration> {
        val map = HashMap<Declaration, Declaration>()
        val keyDeclaration = parseElement(null, elementToken)
        if (next !is SemiCollonToken) TODO("Error not implemented")
        val valueDeclaration = parseElement(null, next)
        map[keyDeclaration] = valueDeclaration
        val token = next
        if (token !is RightBraceToken) map.putAll(parseDictionnaryElement(token))
        else next
        return map
    }

    private fun parseElement(identifierToken: IdentifierToken?, elementToken: Token): Declaration {
        return when(elementToken) {
            is NumberToken -> NumberDeclaration(identifierToken, elementToken.literal as Double)
            is StringToken -> StringDeclaration(identifierToken, elementToken.literal as String)
            is BooleanToken -> BooleanDeclaration(identifierToken, elementToken.literal as Boolean)
            is CallExtResourceToken -> parseCallToExternalResource(identifierToken)
            is LeftBracketToken -> parseArray(identifierToken)
            is LeftBraceToken -> parseDisctionnary(identifierToken)
            else -> TODO("Error not implemented")
        }
    }
}
