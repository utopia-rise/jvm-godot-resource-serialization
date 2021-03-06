package com.utopiarise.serialization.godot.model

data class TestItem(
    var id: Int,
    var attributes: Array<Int>,
    var category: Int,
    var description: String,
    var item_name: String,
    var price: Int,
    var shop: Int,
    var width: Int,
    var height: Int
) {
    constructor() : this(-1, arrayOf(), -1, "", "", -1, -1, -1, -1)

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestItem

        if (id != other.id) return false

        return true
    }
}
