# JVM Serialization API for godot resources (WIP)

## Overview
This project allows one to deserialize Godot resources into kotlin classes.  
It allows you to define your static item data as Godot resources (`tres` files) and be able to use those resources not
only on the client, but also on your jvm based application (Server, Tool, ...).  
It uses reflection to inject the resource data into your kotlin classes (like Jackson for json and xml based data).

## Status
This project is currently a work in progress, and only supports deserialization. For now, it does
not support internal resources.

## Roadmap / Todo:
- Internal resources support
- Serialization from JVM to Godot resources
- AllArgs constructor deserialization

## Usage
The base requirement for this project to work is, that you keep your models between GDScript and Kotlin in sync.  
Let's assume you have an Item Godot resource like this :

### Model layout
```gdscript
extends Resource

class_name Item

enum ItemAttributes {
	BATTLE, CRAFT, FIELD, HOLD, MOVESLOT, OTHER
}

enum ItemCategory {
	OTHER, POTIONS
}

export(int) var id: int
export(Array, ItemAttributes) var attributes: Array
export(ItemCategory) var category: int
export(String) var description: String
export(String) var item_name: String
export(int) var price: int = 0
export(int) var shop: int = 0

export(int) var width: int
export(int) var height: int
```

You should have a equivalent JVM model:
```kotlin
data class Item(
    var id: Int,
    var attributes: List<Int>,
    var category: Int,
    var description: String,
    var item_name: String,
    var price: Int,
    var shop: Int,
    var width: Int,
    var height: Int
) {
    constructor() : this(-1, arrayOf(), -1, "", "", -1, -1, -1, -1)
}
```

If you have an ItemDatabase that refers to Item, your GDScript model should look like this:
```gdscript
extends Resource

class_name ItemDatabase

export(Dictionary) var database: Dictionary
```

And on JVM side:
```kotlin
data class ItemDB(var database: Map<Int, Item>) {
    constructor(): this(mapOf())
}
```

The above example can be found in the tests [here](src/it/resources/data/common/item_db.tres).  
**Note:** You should have setters accessible and provide a NoArg constructor. AllArg constructors are not supported for now.

### Deserialization
To create a JVM ItemDB instance from [item_db.tres](src/it/resources/data/common/item_db.tres) call the static
`fromGodotResource` method, like this:

- Kotlin :
```kotlin
val itemDB = fromGodotResource<ItemDB>(file, resPathReplacement)
```

- Java: 
```java
ResourceDeserializerKt.fromGodotResource(ItemDB.class, file, resPathReplacement);
```

## Contribution
Contributions are very welcome!



## Credits
We thank Bob Nystrom for his book [crafting interpreters](https://craftinginterpreters.com/), which has inspired our code.
