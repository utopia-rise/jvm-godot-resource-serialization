package com.utopiarise.serialization.godot.core

sealed class Declaration(val identifierToken: IdentifierToken?, vararg val values: Any)

class NumberDeclaration(token: IdentifierToken?, value: Double) : Declaration(token, value) {
    infix fun getValueToType(type: Class<*>): Any {
        return when (type) {
            Double::class.java -> values[0]
            Long::class.java -> (values[0] as Double).toLong()
            Float::class.java -> (values[0] as Double).toFloat()
            Int::class.java -> (values[0] as Double).toInt()
            Integer::class.java -> (values[0] as Double).toInt()
            Short::class.java -> (values[0] as Double).toShort()
            Byte::class.java -> (values[0] as Double).toByte()
            else -> TODO("Error not implemented")
        }
    }
}

class StringDeclaration(token: IdentifierToken?, value: String) : Declaration(token, value)
class BooleanDeclaration(token: IdentifierToken?, value: Boolean) : Declaration(token, value)
class ArrayDeclaration(token: IdentifierToken?, vararg values: Declaration) : Declaration(token, *values)
class DictionaryDeclaration(token: IdentifierToken?, vararg values: Pair<Declaration, Declaration>) : Declaration(token, *values)

class ResourceDeclaration : Declaration(null), StandaloneDeclaration
class GdResourceDeclaration(vararg values: Declaration) : Declaration(null, *values), StandaloneDeclaration
class GdSceneDeclaration(vararg values: Declaration) : Declaration(null, *values), StandaloneDeclaration
class ExternalResourceDeclaration(vararg values: Declaration) : Declaration(null, *values), StandaloneDeclaration
class NodeDeclaration(vararg values: Declaration) : Declaration(null, *values), StandaloneDeclaration
class SignalConnectionDeclaration(vararg values: Declaration) : Declaration(null, *values), StandaloneDeclaration

class CallExternalResourceDeclaration(token: IdentifierToken?, id: Int) : Declaration(token, id)

class ScriptDeclaration(token: ScriptToken, callExternalResourceDeclaration: CallExternalResourceDeclaration) : Declaration(token, callExternalResourceDeclaration)


interface StandaloneDeclaration
