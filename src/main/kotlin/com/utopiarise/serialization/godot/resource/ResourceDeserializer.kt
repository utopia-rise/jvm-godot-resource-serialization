package com.utopiarise.serialization.godot.resource

import com.utopiarise.serialization.godot.core.Tokenizer
import java.io.File

internal val pathMap = HashMap<String, Any>()

inline fun <reified T> fromGodotResource(resourcePath: String, resPathReplacement: String) =
    getOrPutResourceFromMap(resourcePath, ResourceDeserializer(T::class.java, resPathReplacement).deserialize(File(resourcePath))) as T

fun <T> fromGodotResource(type: Class<T>, resourcePath: String, resPathReplacement: String): T {
    val resource = ResourceDeserializer(type, resPathReplacement).deserialize(File(resourcePath))
    return if (resource::class.java == type) {
        getOrPutResourceFromMap(resourcePath, resource) as T
    } else{
        throw IllegalArgumentException("$resource is not equal to $type")
    }
}

fun getOrPutResourceFromMap(resourcePath: String, any: Any): Any {
    if (!pathMap.containsKey(resourcePath)) pathMap[resourcePath] = any
    return pathMap[resourcePath]!!
}

@PublishedApi
internal class ResourceDeserializer(private val clazz: Class<*>, private val resPathReplacement: String) {
    fun deserialize(file: File): Any = DataInjector(clazz, resPathReplacement).inject(Tokenizer(file.readText()).tokenize())
}
