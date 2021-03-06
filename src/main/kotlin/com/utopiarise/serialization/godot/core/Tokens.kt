package com.utopiarise.serialization.godot.core

sealed class Token(val lexeme: String, val literal: Any?, val line: Int) {
    fun isPrimitive() = this is NumberToken || this is StringToken
}

class ResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class GdResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class GdSceneToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class ExtResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class SubResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class ConnectionToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class NodeToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)

open class IdentifierToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class CallExtResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class CallSubResourceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)

class ScriptToken(lexeme: String, literal: Any?, line: Int) : IdentifierToken(lexeme, literal, line)

class EqualToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class SemiColonToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class CommaToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class LeftBracketToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class RightBracketToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class LeftBraceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class RightBraceToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class LeftParenthesisToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class RightParenthesisToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class MinusToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)

class StringToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class NumberToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class BooleanToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)
class NullToken(lexeme: String, literal: Any?, line: Int) : Token(lexeme, literal, line)

class EofToken(line: Int) : Token("", null, line)
