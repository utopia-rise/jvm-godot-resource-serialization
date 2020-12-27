package com.utopiarise.serialization.godot.scene

import com.utopiarise.serialization.godot.util.getResourceUrl
import org.junit.Assert.*
import org.junit.Test

class SceneDeserializerTest {

    @Test
    fun `scene TestScene deserialized`() {
        val sceneModel = fromTscn(getResourceUrl("scenes/TestScene.tscn").file)

        assertEquals("load_steps is supposed to be 3", 3, sceneModel.loadSteps)
        assertEquals("format is supposed to be 2", 2, sceneModel.format)

        //ext resources
        assertEquals("there should be 2 external resources", 2, sceneModel.externalResource.size)

        assertEquals("first external resource path should be \"res://Spatial.cs\"", "res://Spatial.cs", sceneModel.externalResource.first().path)
        assertEquals("first external resource type should be \"Script\"", "res://Spatial.cs", sceneModel.externalResource.first().path)
        assertEquals("first external resource id should be \"1\"", 1, sceneModel.externalResource.first().id)
        assertEquals("second external resource path should be \"res://GdScript.gd\"", "Script", sceneModel.externalResource[1].type)
        assertEquals("second external resource type should be \"Script\"", "Script", sceneModel.externalResource[1].type)
        assertEquals("second external resource id should be \"2\"", 2, sceneModel.externalResource[1].id)


        //nodes
        assertEquals("there should be 9 nodes", 9, sceneModel.nodes.size)

        assertEquals("cSharp", sceneModel.nodes[0].name)
        assertEquals("Spatial", sceneModel.nodes[0].type)
        assertEquals(null, sceneModel.nodes[0].parent)
        assertEquals("res://Spatial.cs", sceneModel.nodes[0].script)

        assertEquals("GdScript", sceneModel.nodes[1].name)
        assertEquals("Spatial", sceneModel.nodes[1].type)
        assertEquals(".", sceneModel.nodes[1].parent)
        assertEquals("res://GdScript.gd", sceneModel.nodes[1].script)

        assertEquals("Node", sceneModel.nodes[2].name)
        assertEquals("Node", sceneModel.nodes[2].type)
        assertEquals(".", sceneModel.nodes[2].parent)
        assertEquals(null, sceneModel.nodes[2].script)

        assertEquals("Node", sceneModel.nodes[3].name)
        assertEquals("Node", sceneModel.nodes[3].type)
        assertEquals("Node", sceneModel.nodes[3].parent)
        assertEquals(null, sceneModel.nodes[3].script)

        assertEquals("TestParent", sceneModel.nodes[4].name)
        assertEquals("Node", sceneModel.nodes[4].type)
        assertEquals("Node/Node", sceneModel.nodes[4].parent)
        assertEquals(null, sceneModel.nodes[4].script)

        assertEquals("TestChild", sceneModel.nodes[5].name)
        assertEquals("Node", sceneModel.nodes[5].type)
        assertEquals("Node/Node/TestParent", sceneModel.nodes[5].parent)
        assertEquals(null, sceneModel.nodes[5].script)

        assertEquals("Node2", sceneModel.nodes[6].name)
        assertEquals("Node", sceneModel.nodes[6].type)
        assertEquals("Node", sceneModel.nodes[6].parent)
        assertEquals(null, sceneModel.nodes[6].script)


        assertEquals("TestParent", sceneModel.nodes[7].name)
        assertEquals("Node", sceneModel.nodes[7].type)
        assertEquals("Node/Node2", sceneModel.nodes[7].parent)
        assertEquals(null, sceneModel.nodes[7].script)

        assertEquals("TestChild", sceneModel.nodes[8].name)
        assertEquals("Node", sceneModel.nodes[8].type)
        assertEquals("Node/Node2/TestParent", sceneModel.nodes[8].parent)
        assertEquals(null, sceneModel.nodes[8].script)


        //signals
        assertEquals("there should be 2 signal connections", 2, sceneModel.signalConnections.size)

        assertEquals("tree_entered", sceneModel.signalConnections[0].signal)
        assertEquals(sceneModel.nodes[0], sceneModel.signalConnections[0].from)
        assertEquals(sceneModel.nodes[0], sceneModel.signalConnections[0].to)
        assertEquals("_on_Spatial_tree_entered", sceneModel.signalConnections[0].method)

        assertEquals("tree_entered", sceneModel.signalConnections[1].signal)
        assertEquals(sceneModel.nodes[1], sceneModel.signalConnections[1].from)
        assertEquals(sceneModel.nodes[1], sceneModel.signalConnections[1].to)
        assertEquals("_on_GdScript_tree_entered", sceneModel.signalConnections[1].method)
    }
}
