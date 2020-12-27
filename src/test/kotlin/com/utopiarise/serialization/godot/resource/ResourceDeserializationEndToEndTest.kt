package com.utopiarise.serialization.godot.resource

import com.utopiarise.serialization.godot.model.TestItemDB
import com.utopiarise.serialization.godot.util.getResourceUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class ResourceDeserializationEndToEndTest {

    @Test
    fun endToEnd() {
        val itemDB = fromGodotResource<TestItemDB>(
            getResourceUrl("data/common/item_db.tres").file,
            getResourceUrl("data").path.removeSuffix("data")
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
