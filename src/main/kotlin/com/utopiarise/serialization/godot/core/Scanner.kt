package com.utopiarise.serialization.godot.core

class Scanner(private val source: String) {

    private val tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    private val isAtEnd: Boolean
        get() = current == source.length
    private val advance: Char
        get() {
            current++
            return source[current - 1]
        }
    private val previous: Char
        get() = source[current -1]
    private val peek: Char
        get() = if (isAtEnd) '\u0000' else source[current]
    private val peekNext: Char
        get() = if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private val identifier: Token
        get() {
            while (peek.isAlphaNumeric() || peek == '.' || peek == '/' || peek == ':' || peek == '\\') advance
            val text = source.substring(start, current)
            return Keywords.tokenFromText(text, line) ?: IdentifierToken(text, null, line)
        }
    private val number: Token
        get() {
            while (
                peek.isDigit() ||
                (peek == '.' && peekNext.isDigit()) ||
                (previous.isDigit() && peek == 'e' && peekNext == '-') ||
                (previous == 'e' && peek == '-')
            ) {
                advance
            }

            return NumberToken(source.substring(start, current), source.substring(start, current).toDouble(), line)
        }
    private val string: Token
        get() {
            while (!(peek == '"' && previous != '\\') && !isAtEnd) {
                if (peek == '\n') line++
                advance
            }

            // Unterminated string.
            if (isAtEnd) {
                TODO("Error not implemented")
            }
            advance
            return StringToken(source.substring(start, current), source.substring(start + 1, current - 1), line)
        }
    private val token: Token?
        get() {
            return when (val c = advance) {
                '(' -> LeftParenthesisToken(source.substring(start, current), null, line)
                ')' -> RightParenthesisToken(source.substring(start, current), null, line)
                '{' -> LeftBraceToken(source.substring(start, current), null, line)
                '}' -> RightBraceToken(source.substring(start, current), null, line)
                '[' -> LeftBracketToken(source.substring(start, current), null, line)
                ']' -> RightBracketToken(source.substring(start, current), null, line)
                ',' -> CommaToken(source.substring(start, current), null, line)
                '=' -> EqualToken(source.substring(start, current), null, line)
                ':' -> SemiColonToken(source.substring(start, current), null, line)
                '-' -> if (peekNext.isDigit() && !previous.isDigit()) {
                    number
                } else {
                    MinusToken(source.substring(start, current), null, line)
                }

                //Spaces are ignored
                ' ' -> null
                '\r' -> null
                '\t' -> null

                '\n' -> {
                    line++
                    null
                }

                '"' -> string
                else -> {
                    when {
                        c.isDigit() -> number
                        c.isAlpha() -> identifier
                        else -> {
                            val blubb = ""
                            TODO("Error not implemented")
                        }
                    }
                }
            }
        }

    fun scanTokens(): List<Token> {
        while (!isAtEnd) {
            start = current
            tokens add token
        }
        tokens.add(EofToken(line))
        return tokens
    }

    private fun Char.isDigit() = this in '0'..'9'
    private fun Char.isAlpha() = this in 'a'..'z' || this in 'A'..'Z' || this == '_'
    private fun Char.isAlphaNumeric() = this.isDigit() || this.isAlpha()
    private infix fun ArrayList<Token>.add(token: Token?) = if (token == null) false else this.add(token)
}
