package com.utopiarise.serialization.godot

class UnknownKeywordException(message: String) : RuntimeException(message)
class SyntaxException(message: String) : RuntimeException(message)
class NoSetterException(operand: String) : RuntimeException("Cannot find setter for field $operand !")
class WrongTypeException(message: String) : RuntimeException(message)
