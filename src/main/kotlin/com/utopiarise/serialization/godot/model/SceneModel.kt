package com.utopiarise.serialization.godot.model

data class SceneModel(
    val loadSteps: Int,
    val format: Int,
    val externalResource: List<ExternalResource> = emptyList(),
    val nodes: List<Node> = emptyList(),
    val signalConnections: List<SignalConnection> = emptyList()
)

data class ExternalResource(
    val id: Int,
    val path: String,
    val type: String
)

data class Node(
    val name: String,
    val type: String?,
    val inheritedScenePath: String? = null,
    val parent: String? = null,
    val script: String? = null
)

data class SignalConnection(
    val signal: String,
    val from: Node,
    val to: Node,
    val method: String
)
