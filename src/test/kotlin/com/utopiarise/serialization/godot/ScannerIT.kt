package com.utopiarise.serialization.godot

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.File

class ScannerIT {

    private val tokens: List<Token> = Scanner(File(javaClass.classLoader.getResource("data/common/item_db.tres").file).readText())
        .scanTokens()

    @Test
    fun hasOneGdResource() = assertEquals(1, tokens.filter { it is GdResourceToken }.size)

    @Test
    fun hasOneResourceTag() = assertEquals(1, tokens.filter { it is ResourceToken }.size)

    @Test
    fun hasTwoExtResource() = assertEquals(2, tokens.filter { it is ExtResourceToken }.size)

    @Test
    fun checkTokenSize() = assertEquals(56, tokens.size)
}
