package com.utopiarise.serialization.godot

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("End to end deserialization from godot resource test")
class DeserializationEndToEnd {

    @Test
    @DisplayName("deserialize")
    fun endToEnd() {
        val itemDB = fromGodotResource<TestItemDB>(
                javaClass.classLoader.getResource("data/common/item_db.tres").file,
                javaClass.classLoader.getResource("data").path.removeSuffix("data")
        )
        Assertions.assertEquals(1, itemDB.database.size)
        Assertions.assertTrue(itemDB.database[0]?.item_name == "Potion")
        Assertions.assertEquals(300, itemDB.database[0]?.price)
        Assertions.assertEquals(1, itemDB.database[0]?.width)
        Assertions.assertEquals(1, itemDB.database[0]?.height)
        Assertions.assertEquals(1, itemDB.database[0]?.category)
        Assertions.assertEquals(0, itemDB.database[0]?.shop)
        Assertions.assertEquals(3, itemDB.database[0]?.attributes?.size)
        Assertions.assertEquals(1, itemDB.database[0]?.width)
        Assertions.assertEquals(0, itemDB.database[0]?.id)
    }
}