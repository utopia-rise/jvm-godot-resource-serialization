package com.utopiarise.serialization.godot.core

import com.utopiarise.serialization.godot.util.getResourceUrl
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ScannerTest {

    private val resourceTokens: List<Token> = Scanner(File(getResourceUrl("data/common/item_db.tres").file).readText()).scanTokens()
    private val sceneTokens: List<Token> = Scanner(File(getResourceUrl("scenes/TestScene.tscn").file).readText()).scanTokens()

    @Test
    fun `resource item_db has 1 GdResource when scanned`() = assertEquals(1, resourceTokens.filterIsInstance<GdResourceToken>().size)

    @Test
    fun `resource item_db has 1 ResourceTag when scanned`() = assertEquals(1, resourceTokens.filterIsInstance<ResourceToken>().size)

    @Test
    fun `resource item_db has 2 ExtResources when scanned`() = assertEquals(2, resourceTokens.filterIsInstance<ExtResourceToken>().size)

    @Test
    fun `resource item_db has 56 tokens when scanned`() = assertEquals(56, resourceTokens.size)

    @Test
    fun `scene TestScene has 181 tokens when scanned`() = assertEquals(181, sceneTokens.size)

    @Test
    fun `scene TestScene has 1 GdScene when scanned`() = assertEquals(1, sceneTokens.filterIsInstance<GdSceneToken>().size)

    @Test
    fun `scene TestScene has 2 ExtResourceTokens when scanned`() = assertEquals(2, sceneTokens.filterIsInstance<ExtResourceToken>().size)

    @Test
    fun `scene TestScene has 9 NodeTokens when scanned`() = assertEquals(9, sceneTokens.filterIsInstance<NodeToken>().size)

    @Test
    fun `scene TestScene has 2 ConnectionTokens when scanned`() = assertEquals(2, sceneTokens.filterIsInstance<ConnectionToken>().size)

    @Test
    fun `scene TestScene has 2 ScriptTokens when scanned`() = assertEquals(2, sceneTokens.filterIsInstance<ScriptToken>().size)
}
