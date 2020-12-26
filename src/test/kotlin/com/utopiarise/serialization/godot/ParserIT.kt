package com.utopiarise.serialization.godot

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class ParserIT {

    private val declarations = Parser(
        Scanner(
            File(
                javaClass.classLoader
                    .getResource("data/common/item_db.tres").file
            ).readText()
        )
            .scanTokens()
    ).parse()

    @Test
    fun hasOneGdResource() = assertEquals(1, declarations.filter { it is GdResourceDeclaration }.size)

    @Test
    fun hasOneResourceTag() = assertEquals(1, declarations.filter { it is ResourceDeclaration }.size)

    @Test
    fun hasTwoExtResource() = assertEquals(2, declarations.filter { it is ExternalResourceDeclaration }.size)

    @Test
    fun dictionary() {
        assertEquals(1, declarations.filter { it is DictionaryDeclaration }.size)
        assertTrue(
            (declarations.first { it is DictionaryDeclaration }.values[0] as Pair<*, *>).second is CallExternalResourceDeclaration
        )
    }

    @Test
    fun checkTokenSize() = assertEquals(6, declarations.size)
}
