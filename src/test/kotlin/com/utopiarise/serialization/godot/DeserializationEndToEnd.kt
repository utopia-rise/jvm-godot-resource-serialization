package com.utopiarise.serialization.godot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class DeserializationEndToEnd {

    @Test
    fun endToEnd() {
        val itemDB = fromGodotResource<TestItemDB>(
            javaClass.classLoader.getResource("data/common/item_db.tres").file,
            javaClass.classLoader.getResource("data").path.removeSuffix("data")
        )
        assertEquals(1, itemDB.database.size)
        assertTrue(itemDB.database[0]?.item_name == "Potion")
        assertEquals(300, itemDB.database[0]?.price)
        assertEquals(1, itemDB.database[0]?.width)
        assertEquals(1, itemDB.database[0]?.height)
        assertEquals(1, itemDB.database[0]?.category)
        assertEquals(0, itemDB.database[0]?.shop)
        assertEquals(3, itemDB.database[0]?.attributes?.size)
        assertEquals(1, itemDB.database[0]?.width)
        assertEquals(0, itemDB.database[0]?.id)
    }
}
