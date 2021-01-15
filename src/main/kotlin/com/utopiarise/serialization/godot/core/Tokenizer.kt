package com.utopiarise.serialization.godot.core

import kotlin.collections.ArrayList

class Tokenizer(sourceAsText: String) {
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
                        when(next) {
                            is CallExtResourceToken -> ScriptDeclaration(token, tokenizeCallToExternalResource(token))
                            is CallSubResourceToken -> ScriptDeclaration(token, tokenizeCallToSubResource(token))
                            is NullToken -> NullDeclaration(token)
                            else -> throw MalformedSceneException("Received a script identifier but the assignment is of unknown type. Line: ${token.line}")
                        }
                    } else {
                        throw MalformedSceneException("Received a script identifier but the following token is not of type ${EqualToken::class}. Line: ${token.line}")
                    }
                }
                is IdentifierToken -> tokenizeAssignment(token)
                is LeftBracketToken -> {
                    when (next) {
                        is ResourceToken -> if (next is RightBracketToken) {
                            ResourceDeclaration()
                        } else {
                            throw IllegalArgumentException("Received a resource token but it is not terminated by token of type ${RightBracketToken::class}")
                        }
                        is GdResourceToken -> GdResourceDeclaration(*tokenizeInnerAssignment())
                        is GdSceneToken -> GdSceneDeclaration(*tokenizeInnerAssignment())
                        is ExtResourceToken -> ExternalResourceDeclaration(*tokenizeInnerAssignment())
                        is SubResourceToken -> SubResourceDeclaration(*tokenizeInnerAssignment())
                        is NodeToken -> NodeDeclaration(*tokenizeInnerAssignment())
                        is ConnectionToken -> SignalConnectionDeclaration(*tokenizeInnerAssignment())
                        else -> throw NotImplementedError("${next.lexeme} after ${token.lexeme} not yet implemented")
                    }
                }
                else -> throw IllegalArgumentException("Received unexpected token $token for a start of a declaration")
            }
        }

    fun tokenize(): List<Declaration> {
        val declarations = mutableListOf<Declaration>()
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
        } else {
            throw IllegalArgumentException("Received a inner assignment without any identifier tokens. Line: ${peek.line}")
        }
        if (peek !is RightBracketToken){
            throw MalformedSceneException("Inner assignment is not terminated by ${RightBracketToken::class}. Token received: $peek. Line: ${peek.line}")
        }
        return declarationParameters.toTypedArray()
    }

    private fun tokenizeAssignment(token: IdentifierToken): Declaration {
        return if (next is EqualToken){
            tokenizeElement(token, next)
        } else {
            throw IllegalArgumentException("Received ${IdentifierToken::class} as assignment but not followed by ${EqualToken::class}. Line: ${token.line}")
        }
    }

    private fun tokenizeDictionary(identifierToken: IdentifierToken?): Declaration {
        return DictionaryDeclaration(identifierToken, *tokenizeDictionaryElement(next).toList().toTypedArray())
    }

    private fun tokenizeCallToExternalResource(token: IdentifierToken?): CallExternalResourceDeclaration {
        if (next is LeftParenthesisToken) {
            val idParameterToken = next
            val idParameterValue = if (idParameterToken is NumberToken) {
                (idParameterToken.literal as Double).toInt()
            } else {
                throw MalformedSceneException("Received call to external resource without id. Line: ${peek.line}")
            }
            if (next !is RightParenthesisToken){
                throw MalformedSceneException("Call to external resource not terminated by ${RightParenthesisToken::class} after id. Line: ${peek.line}")
            }
            return CallExternalResourceDeclaration(token, idParameterValue)
        } else {
            throw MalformedSceneException("Call to external resource is missing ${LeftParenthesisToken::class}. Line: ${peek.line}")
        }
    }

    private fun tokenizeCallToSubResource(token: IdentifierToken?): CallSubResourceDeclaration {
        if (next is LeftParenthesisToken) {
            val idParameterToken = next
            val idParameterValue = if (idParameterToken is NumberToken) {
                (idParameterToken.literal as Double).toInt()
            } else {
                throw MalformedSceneException("Received call to sub resource without id. Line: ${peek.line}")
            }
            if (next !is RightParenthesisToken) {
                throw MalformedSceneException("Call to sub resource not terminated by ${RightParenthesisToken::class} after id. Line: ${peek.line}")
            }
            return CallSubResourceDeclaration(token, idParameterValue)
        } else {
            throw MalformedSceneException("Call to sub resource is missing ${LeftParenthesisToken::class}. Line: ${peek.line}")
        }
    }

    private fun tokenizeArray(token: IdentifierToken?, elementToken: LeftBracketToken) = ArrayDeclaration(token, *tokenizeArrayElement(elementToken).toTypedArray())

    private fun tokenizeArrayElement(elementToken: Token): List<Declaration> {
        val arrayValues = mutableListOf<Declaration>()
        if (elementToken !is LeftBracketToken) {
            throw IllegalArgumentException("Array is not starting with ${LeftBracketToken::class}. Line: ${peek.line}")
        }

        var nextToken = next
        while (nextToken !is RightBracketToken) {
            if (nextToken is CommaToken) {
                nextToken = next
                continue
            }
            arrayValues.add(tokenizeElement(null, nextToken))
            nextToken = next
        }
        return arrayValues
    }

    private fun tokenizeDictionaryElement(elementToken: Token): Map<Declaration, Declaration> {
        val map = HashMap<Declaration, Declaration>()
        val keyDeclaration = tokenizeElement(null, elementToken)
        if (next !is SemiColonToken) {
            throw MalformedSceneException("No ${SemiColonToken::class} between key and value found for dictionary. Line ${peek.line}")
        }
        val valueDeclaration = tokenizeElement(null, next)
        map[keyDeclaration] = valueDeclaration
        var token = next
        if (token is CommaToken) {
            token = next
        }
        if (token !is RightBraceToken) {
            map.putAll(tokenizeDictionaryElement(token))
        }
        return map
    }

    private fun tokenizeConstructor(identifierToken: IdentifierToken?, elementToken: IdentifierToken): ConstructorDeclaration {
        val className = elementToken.lexeme
        val constructorValues = mutableListOf<Declaration>()
        if (next !is LeftParenthesisToken) {
            throw MalformedSceneException("Constructor is missing ${LeftParenthesisToken::class}. Line: ${peek.line}")
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
            is LeftBracketToken -> tokenizeArray(identifierToken, elementToken)
            is LeftBraceToken -> tokenizeDictionary(identifierToken)
            is NullToken -> NullDeclaration(identifierToken)
            is IdentifierToken -> if (elementToken.lexeme.first().isUpperCase()) {
                tokenizeConstructor(identifierToken, elementToken)
            } else {
                throw IllegalArgumentException("Did not expect an identifier token: $elementToken")
            }
            is MinusToken -> if (peekNext is NumberToken && peekPrevious !is NumberToken) {
                val numberToken = next
                NumberDeclaration(identifierToken, "${elementToken.lexeme}${numberToken.literal.toString()}".toDouble())
            } else {
                throw IllegalArgumentException("Did not expect a minus token: $elementToken")
            }
            else -> {
                throw IllegalArgumentException("Did not expect token: $elementToken")
            }
        }
    }
}
