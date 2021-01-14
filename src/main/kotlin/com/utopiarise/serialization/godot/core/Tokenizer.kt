package com.utopiarise.serialization.godot.core

import kotlin.collections.ArrayList

class Tokenizer(private val sourceAsText: String) {
    private var tokens = Scanner(sourceAsText).scanTokens()

    private var current = 0

    private val peek: Token
        get() = tokens[current]
    private val peekNext: Token
        get() {
            return if (!isAtEnd) {
                tokens[current + 1]
            } else peek
        }
    private val peekPrevious: Token
        get() {
            return if (current != 0) {
                tokens[current - 1]
            } else peek
        }
    private val isAtEnd: Boolean
        get() = peek is EofToken
    private val next: Token
        get() {
            if (!isAtEnd) current++
            return peek
        }

    private val declaration: Declaration
        get() {
            return when (val token = peek) {
                is ScriptToken -> {
                    if (next is EqualToken) {
                        val nextToken = next
                        when(nextToken) {
                            is CallExtResourceToken -> ScriptDeclaration(token, tokenizeCallToExternalResource(token))
                            is CallSubResourceToken -> ScriptDeclaration(token, tokenizeCallToSubResource(token))
                            else -> {
                                val blubb = ""
                                TODO("Error not implemented")
                            }
                        }
                    } else {
                        val blubb = ""
                        TODO("Error not implemented")
                    }
                }
                is IdentifierToken -> tokenizeAssignment(token)
                is LeftBracketToken -> {
                    when (next) {
                        is ResourceToken -> if (next is RightBracketToken) ResourceDeclaration() else TODO("Error not implemented")
                        is GdResourceToken -> GdResourceDeclaration(*tokenizeInnerAssignment())
                        is GdSceneToken -> GdSceneDeclaration(*tokenizeInnerAssignment())
                        is ExtResourceToken -> ExternalResourceDeclaration(*tokenizeInnerAssignment())
                        is SubResourceToken -> SubResourceDeclaration(*tokenizeInnerAssignment())
                        is NodeToken -> NodeDeclaration(*tokenizeInnerAssignment())
                        is ConnectionToken -> SignalConnectionDeclaration(*tokenizeInnerAssignment())
                        else -> {
                            throw NotImplementedError("${next.lexeme} after ${token.lexeme} not yet implemented")
                        }
                    }
                }
                else -> {
                    val blubb = ""
                    TODO("Error not implemented")
                }
            }
        }

    fun tokenize(): List<Declaration> {
        val declarations = ArrayList<Declaration>()
        while (!isAtEnd) {
            declarations.add(declaration)
            next
        }
        return declarations
    }

    private fun tokenizeInnerAssignment(): Array<Declaration> {
        next
        val declarationParameters = ArrayList<Declaration>()
        if (peek is IdentifierToken) {
            while (peek is IdentifierToken) {
                declarationParameters.add(tokenizeAssignment(peek as IdentifierToken))
                next
            }
        } else TODO("Error not implemented")
        if (peek !is RightBracketToken) TODO("Error not implemented")
        return declarationParameters.toTypedArray()
    }

    private fun tokenizeAssignment(token: IdentifierToken): Declaration {
        return if (next is EqualToken){
            tokenizeElement(token, next)
        } else {
            val blubb = ""
            TODO("Error not implemented")
        }
    }

    private fun tokenizeDictionary(identifierToken: IdentifierToken?): Declaration {
        return DictionaryDeclaration(identifierToken, *tokenizeDictionaryElement(next).toList().toTypedArray())
    }

    private fun tokenizeCallToExternalResource(token: IdentifierToken?): CallExternalResourceDeclaration {
        if (next is LeftParenthesisToken) {
            val idParameterToken = next
            val idParameterValue = if (idParameterToken is NumberToken) (idParameterToken.literal
                as Double).toInt() else TODO("Error not implemented")
            if (next !is RightParenthesisToken) TODO("Error not implemented")
            return CallExternalResourceDeclaration(token, idParameterValue)
        } else TODO("Error not implemented")
    }

    private fun tokenizeCallToSubResource(token: IdentifierToken?): CallSubResourceDeclaration {
        if (next is LeftParenthesisToken) {
            val idParameterToken = next
            val idParameterValue = if (idParameterToken is NumberToken) (idParameterToken.literal
                as Double).toInt() else TODO("Error not implemented")
            if (next !is RightParenthesisToken) TODO("Error not implemented")
            return CallSubResourceDeclaration(token, idParameterValue)
        } else TODO("Error not implemented")
    }

    private fun tokenizeArray(token: IdentifierToken?) = ArrayDeclaration(token, *tokenizeArrayElement(next).toTypedArray())

    private fun tokenizeArrayElement(elementToken: Token): List<Declaration> {
        val arrayValues = ArrayList<Declaration>()
        arrayValues.add(tokenizeElement(null, elementToken))
        val nextToken = next
        if (nextToken is CommaToken) arrayValues.addAll(tokenizeArrayElement(next))
        else if (nextToken !is RightBracketToken) TODO("Error not implemented")
        return arrayValues
    }

    private fun tokenizeDictionaryElement(elementToken: Token): Map<Declaration, Declaration> {
        val map = HashMap<Declaration, Declaration>()
        val keyDeclaration = tokenizeElement(null, elementToken)
        if (next !is SemiColonToken) TODO("Error not implemented")
        val valueDeclaration = tokenizeElement(null, next)
        map[keyDeclaration] = valueDeclaration
        val token = next
        if (token !is RightBraceToken) {
            map.putAll(tokenizeDictionaryElement(token))
        }
        return map
    }

    private fun tokenizeConstructor(identifierToken: IdentifierToken?, elementToken: IdentifierToken): ConstructorDeclaration {
        val className = elementToken.lexeme
        val constructorValues = mutableListOf<Declaration>()
        if (next !is LeftParenthesisToken) {
            val blubb = ""
            TODO("Error not implemented")
        }

        var nextToken = next
        while (nextToken !is RightParenthesisToken) {
            if (nextToken is CommaToken) {
                nextToken = next
                continue
            }
            constructorValues.add(tokenizeElement(null, nextToken))
            nextToken = next
        }
        return ConstructorDeclaration(identifierToken, className, *constructorValues.toTypedArray())
    }

    private fun tokenizeElement(identifierToken: IdentifierToken?, elementToken: Token): Declaration {
        return when (elementToken) {
            is NumberToken -> NumberDeclaration(identifierToken, elementToken.literal as Double)
            is StringToken -> StringDeclaration(identifierToken, elementToken.literal as String)
            is BooleanToken -> BooleanDeclaration(identifierToken, elementToken.literal as Boolean)
            is CallExtResourceToken -> tokenizeCallToExternalResource(identifierToken)
            is CallSubResourceToken -> tokenizeCallToSubResource(identifierToken)
            is LeftBracketToken -> tokenizeArray(identifierToken)
            is LeftBraceToken -> tokenizeDictionary(identifierToken)
            is IdentifierToken -> if (elementToken.lexeme.first().isUpperCase()) {
                tokenizeConstructor(identifierToken, elementToken)
            } else {
                val blubb = ""
                TODO("Error not implemented")
            }
            is MinusToken -> if (peekNext is NumberToken && peekPrevious !is NumberToken) {
                val numberToken = next
                NumberDeclaration(identifierToken, "${elementToken.lexeme}${numberToken.literal.toString()}".toDouble())
            } else {
                val blubb = ""
                TODO("Error not implemented")
            }
            else -> {
                val blubb = ""
                TODO("Error not implemented")
            }
        }
    }
}
