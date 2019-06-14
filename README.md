# JVM Serialization API for godot resources (WIP)

This project is designed to make some serialization operations for Godot resources, on the JVM.  
It is written in Kotlin, and no Java source should go in it !

![kotlin-approval]

This project is currently a work in progress, and only support deserialization. For now it does
not support internal resources (it will be added).

We thank Bob Nystrom for his book [crafting interpreters](https://craftinginterpreters.com/), which has inspired our code.

## Project's goal

If you are a java user and a Godot game developer, this project might feet some of your needs.  
Imagine you build a game with godot, with some static data, registered as Godot resource (tres). On an other side, you
have a java application (multi-player server, spring rest service, ...) and want this java application to use those
resource as JVM objects. This project is made for you !  
It provide a deserializer for godot resource data format, using reflexion to inject data into your class instance, like
you can do with json using Jackson.

## Usage

The only thing you have to is to insure your data models between Java/Kotlin and GDScript are the same.
Let admit you have an Item Godot resource like this :

```
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

You should have the JVM equivalent :

```
data class Item(var id: Int,
                    var attributes: Array<Int>,
                    var category: Int,
                    var description: String,
                    var item_name: String,
                    var price: Int,
                    var shop: Int,
                    var width: Int,
                    var height: Int) {
    constructor(): this(-1, arrayOf(), -1, "", "", -1, -1, -1, -1)

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
```

If I have an ItemDatabase that refers to Item, you should have in GDScript:

```
extends Resource

class_name ItemDatabase

export(Dictionary) var database: Dictionary
```

And on JVM side :

```
data class ItemDB(var database: Map<Int, Item>) {
    constructor(): this(HashMap())
}
```

Notice that you should have setters accessible and provide a NoArg constructor. AllArg constructor initialization will
be added later.

In this case you have an itemdatabase resource, that refers to item resources. You can check the example
[here](src/it/resources/data/common/item_db.tres).

To create a JVM ItemDB instance from [item_db.tres](src/it/resources/data/common/item_db.tres) call static
`fromGodotResource` method, like this:

- Kotlin :
```
val itemDB = fromGodotResource<ItemDB>(file, resPathReplacement)
```

- Java: 
```
ResourceDeserializerKt.fromGodotResource(ItemDB.class, file, resPathReplacement);
```

The difference between those method is that the kotlin's one is inlined with reified parameter, which cannot be called
from Java code. The content of the method is copied at compile time, so that compiler get rid of type parameter.

## TODO

This project is a work in progress. Feel free to fork us and propose pull request !

Here are a bunch of features we plan to add:

- Internal resources support
- Serialization from JVM
- AllArgs constructor deserialization



[kotlin-approval]: https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKBdfk60YSF47gE7XfiN7h9raTwhQsdbcF1PMxk3VG2pl3QyydiA