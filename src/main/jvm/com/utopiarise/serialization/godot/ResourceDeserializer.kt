package com.utopiarise.serialization.godot

import java.io.File

internal val pathMap = HashMap<String, Any>()

inline fun <reified T> fromGodotResource(resourcePath: String, resPathReplacement: String): T =
        getOrPutResourceFromMap(resourcePath, ResourceDeserializer(T::class.java, resPathReplacement) deserialize File(resourcePath)) as T

fun getOrPutResourceFromMap(resourcePath: String, any: Any): Any {
    if (!pathMap.containsKey(resourcePath)) pathMap[resourcePath] = any
    return pathMap[resourcePath]!!
}

class ResourceDeserializer(private val clazz: Class<*>, private val resPathReplacement: String) {
    infix fun deserialize(file: File) = DataInjecter(clazz, resPathReplacement).inject(Parser(Scanner(file.readText()).scanTokens()).parse())
}