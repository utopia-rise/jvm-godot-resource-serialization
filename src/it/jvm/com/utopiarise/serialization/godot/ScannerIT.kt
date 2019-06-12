package com.utopiarise.serialization.godot

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@DisplayName("Scanner integration tests")
class ScannerIT {

    private val tokens: List<Token> = Scanner(File(javaClass.classLoader.getResource("data/common/item_db.tres").file).readText())
            .scanTokens()

    @Test
    @DisplayName("Has Only one GdResource Token")
    fun hasOneGdResource() = Assertions.assertEquals(1, tokens.filter { it is GdResourceToken }.size)

    @Test
    @DisplayName("Has Only one resource Token")
    fun hasOneResourceTag() = Assertions.assertEquals(1, tokens.filter { it is ResourceToken }.size)

    @Test
    @DisplayName("Has two external resource Token")
    fun hasTwoExtResource() = Assertions.assertEquals(2, tokens.filter { it is ExtResourceToken }.size)

    @Test
    @DisplayName("Check token count")
    fun checkTokenSize() = Assertions.assertEquals(56, tokens.size)
}