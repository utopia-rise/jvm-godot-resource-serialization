package com.utopiarise.serialization.godot.util

import java.net.URL

fun Any.getResourceUrl(path: String): URL {
    return requireNotNull(javaClass.classLoader.getResource(path)) {
        "Could not find resource at path $path"
    }
}
