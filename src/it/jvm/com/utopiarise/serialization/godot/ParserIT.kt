package com.utopiarise.serialization.godot

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@DisplayName("Parser integration tests")
class ParserIT {

    private val declarations = Parser(Scanner(File(javaClass.classLoader
            .getResource("data/common/item_db.tres").file).readText())
            .scanTokens()).parse()

    @Test
    @DisplayName("Has Only one GdResource declaration")
    fun hasOneGdResource() = Assertions.assertEquals(1, declarations.filter { it is GdResourceDeclaration }.size)

    @Test
    @DisplayName("Has Only one resource declaration")
    fun hasOneResourceTag() = Assertions.assertEquals(1, declarations.filter { it is ResourceDeclaration }.size)

    @Test
    @DisplayName("Has two external resource declaration")
    fun hasTwoExtResource() = Assertions.assertEquals(2, declarations.filter { it is ExternalResourceDeclaration }.size)

    @Test
    @DisplayName("Check dictionary declaration")
    fun dictionary() {
        Assertions.assertEquals(1, declarations.filter { it is DictionaryDeclaration }.size)
        Assertions.assertTrue(
                (declarations.first { it is DictionaryDeclaration }.values[0] as Pair<*, *>).second is CallExternalResourceDeclaration
        )
    }

    @Test
    @DisplayName("Check declaration count")
    fun checkTokenSize() = Assertions.assertEquals(6, declarations.size)
}