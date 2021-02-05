package com.utopiarise.serialization.godot.model

data class TestItemDB(var database: Map<Int, TestItem>) {
    constructor() : this(HashMap())
}
