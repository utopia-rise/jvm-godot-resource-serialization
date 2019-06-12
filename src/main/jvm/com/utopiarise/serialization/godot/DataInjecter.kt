package com.utopiarise.serialization.godot

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.io.File
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.Type

private fun getClass(type: Type): Class<*>? {
    var className = type.toString()
    if (className.startsWith("class ")) {
        className = className.substring("class ".length)
    }
    return if (className.isEmpty()) null else Class.forName(className)
}

class DataInjecter(private val clazz: Class<*>, private val resPathReplacement: String) {

    private val instance = clazz.getConstructor().newInstance()
    private val resourceMap = HashMap<Int, String>()

    fun inject(declarations: List<Declaration>): Any {
        declarations.forEach {
            if (it is ExternalResourceDeclaration) {
                externalResource(it)
            }
            else {
                val triple = it getReflectionValue null
                if (triple != null) clazz.getDeclaredMethod("set${triple.first?.capitalize()}", triple.third)
                        .invoke(instance, triple.second)
            }
        }
        return instance
    }

    private infix fun Declaration.getReflectionValue(type: Class<*>?): Triple<String?, Any, Class<*>>? {
        return when(this) {
            is DictionaryDeclaration -> value(this, type) { t, f ->
                if (Map::class.java.isAssignableFrom(t) && f != null) {
                    val keyType = (f.genericType as ParameterizedTypeImpl).actualTypeArguments[0]
                    val valueType = (f.genericType as ParameterizedTypeImpl).actualTypeArguments[1]
                    val map = (if (t.isInterface || Modifier.isAbstract(t.modifiers)) HashMap<Any, Any>()
                    else t.getConstructor().newInstance() as Map<*, *>).toMutableMap()
                    this.values.forEach {
                        it as Pair<Declaration, Declaration>
                        val keyTriple = it.first getReflectionValue getClass(keyType)
                        val valueTriple = it.second getReflectionValue getClass(valueType)
                        map[keyTriple?.second] = valueTriple?.second
                    }
                    map
                }
                else TODO("Error not implemented")
            }
            is ArrayDeclaration -> value(this, type) { t, _ ->
                if (t.isArray) {
                    val array = Array.newInstance(t.componentType, this.values.size)
                    for (i in 0 until this.values.size) {
                        val triple = this.values[i] as Declaration getReflectionValue t.componentType
                        if (triple != null) {
                            Array.set(array, i, triple.second)
                        }
                    }
                    array
                }
                else TODO("Error not implemented")
            }
            is CallExternalResourceDeclaration -> value(this, type) { t, _ ->
                val resPath = resourceMap[this.values[0] as Int]
                if (resPath != null) {
                    val extResPath = resPath.replace("res://", resPathReplacement)
                    getOrPutResourceFromMap(resPath, ResourceDeserializer(t, resPathReplacement) deserialize File(extResPath))
                }
                else TODO("Error not implemented")
            }
            is NumberDeclaration -> value(this, type) { t, _ -> this getValueToType t }
            is StringDeclaration -> value(this, type) { _, _ -> this.values[0] }
            is BooleanDeclaration -> value(this, type) { _, _ -> this.values[0] }
            else -> return null
        }
    }

    private inline fun value(declaration: Declaration, type: Class<*>?, block: (Class<*>, Field?) -> Any): Triple<String?, Any, Class<*>> {
        val lexeme = declaration.identifierToken?.lexeme
        val declaredField = if (type == null) clazz.getDeclaredField(lexeme) else null
        val selectedType = type ?: declaredField!!.type
        val value = block(selectedType, declaredField)
        return Triple(lexeme, value, selectedType)
    }

    private fun externalResource(externalResourceDeclaration: ExternalResourceDeclaration) {
        var resId: Int? = null
        var resPath: String? = null
        externalResourceDeclaration.values.forEach {
            if (it is Declaration && it.identifierToken?.lexeme == "id") {
                 resId = (it.values[0] as Double).toInt()
            }
            if (it is Declaration && it.identifierToken?.lexeme == "path") {
                resPath = it.values[0] as String
            }
        }
        if (resId != null && resPath != null) resourceMap[resId!!] = resPath!!
    }
}