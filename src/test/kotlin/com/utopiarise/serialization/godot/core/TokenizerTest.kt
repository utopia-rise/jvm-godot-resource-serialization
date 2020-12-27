package com.utopiarise.serialization.godot.core

import com.utopiarise.serialization.godot.util.getResourceUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TokenizerTest {

    private val resourceDeclarations = Tokenizer(
            File(
                getResourceUrl("data/common/item_db.tres").file
            ).readText()
    ).tokenize()

    private val sceneDeclarations = Tokenizer(
            File(
                getResourceUrl("scenes/TestScene.tscn").file
            ).readText()
    ).tokenize()

    @Test
    fun `resource item_db has 1 GdResource when parsed`() = assertEquals(1, resourceDeclarations.filterIsInstance<GdResourceDeclaration>().size)

    @Test
    fun `resource item_db has 1 ResourceTag when parsed`() = assertEquals(1, resourceDeclarations.filterIsInstance<ResourceDeclaration>().size)

    @Test
    fun `resource item_db has 2 ExtResources when parsed`() = assertEquals(2, resourceDeclarations.filterIsInstance<ExternalResourceDeclaration>().size)

    @Test
    fun `resource item_db dictionary has external resource declaration when parsed`() {
        assertEquals(1, resourceDeclarations.filterIsInstance<DictionaryDeclaration>().size)
        assertTrue(
            (resourceDeclarations.first { it is DictionaryDeclaration }.values[0] as Pair<*, *>).second is CallExternalResourceDeclaration
        )
    }

    @Test
    fun `resource item_db contains 6 tokens when parsed`() = assertEquals(6, resourceDeclarations.size)

    @Test
    fun `scene TestScene contains 9 tokens when parsed`() = assertEquals(9, sceneDeclarations.size)

    @Test
    fun `scene TestScene has 1 GdScene when parsed`() = assertEquals(1, sceneDeclarations.filterIsInstance<GdSceneDeclaration>().size)

    @Test
    fun `scene TestScene has 2 ExtResources when parsed`() = assertEquals(2, sceneDeclarations.filterIsInstance<ExternalResourceDeclaration>().size)

    @Test
    fun `scene TestScene has 2 Nodes when parsed`() = assertEquals(2, sceneDeclarations.filterIsInstance<NodeDeclaration>().size)

    @Test
    fun `scene TestScene has 2 Signals when parsed`() = assertEquals(2, sceneDeclarations.filterIsInstance<SignalConnectionDeclaration>().size)
}
