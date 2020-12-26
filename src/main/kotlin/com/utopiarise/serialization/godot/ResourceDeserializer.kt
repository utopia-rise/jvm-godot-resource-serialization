package com.utopiarise.serialization.godot

import java.io.File

internal val pathMap = HashMap<String, Any>()

inline fun <reified T> fromGodotResource(resourcePath: String, resPathReplacement: String) =
    getOrPutResourceFromMap(resourcePath, ResourceDeserializer(T::class.java, resPathReplacement) deserialize File(resourcePath)) as T

fun <T> fromGodotResource(type: Class<T>, resourcePath: String, resPathReplacement: String): T {
    val resource = ResourceDeserializer(type, resPathReplacement) deserialize File(resourcePath)
    if (resource::class.java == type) return getOrPutResourceFromMap(resourcePath, resource) as T else TODO("Error not implemented")
}

fun getOrPutResourceFromMap(resourcePath: String, any: Any): Any {
    if (!pathMap.containsKey(resourcePath)) pathMap[resourcePath] = any
    return pathMap[resourcePath]!!
}

class ResourceDeserializer(private val clazz: Class<*>, private val resPathReplacement: String) {
    infix fun deserialize(file: File) = DataInjecter(clazz, resPathReplacement).inject(Parser(Scanner(file.readText()).scanTokens()).parse())
}
