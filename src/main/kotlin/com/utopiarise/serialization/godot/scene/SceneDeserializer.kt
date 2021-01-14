package com.utopiarise.serialization.godot.scene

import com.utopiarise.serialization.godot.core.*
import com.utopiarise.serialization.godot.model.ExternalResource
import com.utopiarise.serialization.godot.model.Node
import com.utopiarise.serialization.godot.model.SceneModel
import com.utopiarise.serialization.godot.model.SignalConnection
import java.io.File


fun sceneFromTscn(tscnFilePath: String): SceneModel {
    return SceneDeserializer().deserialize(File(tscnFilePath))
}

@PublishedApi
internal class SceneDeserializer {
    fun deserialize(file: File): SceneModel {
        val declarations = Tokenizer(file.readText()).tokenize()
        val sceneDeclaration = requireNotNull(declarations.firstOrNull { it is GdSceneDeclaration }) {
            "Malformed scene ${file.path}: Got no \"gd_scene\" declaration"
        }
        val loadSteps = requireNotNull(sceneDeclaration.getValue<Int>("load_steps")) {
            "Malformed scene ${file.path}: Got no \"load_steps\" in \"gd_scene\" declaration"
        }
        val format = requireNotNull(sceneDeclaration.getValue<Int>("format")) {
            "Malformed scene ${file.path}: Got no \"load_steps\" in \"gd_scene\" declaration"
        }
        val externalResources = deserializeExternalResources(declarations, file)
        val nodes = deserializeNodes(declarations, file)
        val signalConnections = deserializeSignalConnections(declarations, file, nodes)

        return SceneModel(
            loadSteps,
            format,
            externalResources,
            nodes,
            signalConnections
        )
    }

    private fun deserializeExternalResources(declarations: List<Declaration>, file: File) = declarations
        .filterIsInstance<ExternalResourceDeclaration>()
        .map { externalResourceDeclaration ->
            val path = requireNotNull(externalResourceDeclaration.getValue<String>("path")) {
                "Malformed \"ext_resource\" declaration in ${file.path}: Got no \"path\" value"
            }
            val type = requireNotNull(externalResourceDeclaration.getValue<String>("type")) {
                "Malformed \"ext_resource\" declaration in ${file.path}: Got no \"type\" value"
            }
            val id = requireNotNull(externalResourceDeclaration.getValue<Int>("id")) {
                "Malformed \"ext_resource\" declaration in ${file.path}: Got no \"id\" value"
            }
            ExternalResource(
                id,
                path,
                type
            )
        }

    private fun deserializeNodes(declarations: List<Declaration>, file: File): List<Node> {
        val declarationToChildDeclarations: MutableMap<Declaration, List<Declaration>> = mutableMapOf()
        val tmpNonStandaloneDeclarations = mutableListOf<Declaration>()
        declarations
            .reversed()
            .forEach {
                if (it is StandaloneDeclaration) {
                    declarationToChildDeclarations[it] = tmpNonStandaloneDeclarations.toList()
                    tmpNonStandaloneDeclarations.clear()
                } else {
                    tmpNonStandaloneDeclarations.add(it)
                }
            }

        return declarationToChildDeclarations
            .filterKeys { it is NodeDeclaration }
            .map { (node, childDeclarations) ->
                val name = requireNotNull(node.getValue<String>("name")) {
                    "Malformed \"node\" declaration in ${file.path}: Got no \"name\" value"
                }
                val type = requireNotNull(node.getValue<String>("type")) {
                    "Malformed \"node\" declaration in ${file.path}: Got no \"type\" value"
                }
                val parent = node.getValue<String>("parent")

                val scriptPath = childDeclarations
                    .filterIsInstance<ScriptDeclaration>()
                    .map { scriptDeclaration ->
                        scriptDeclaration
                            .values
                            .filterIsInstance<CallToResourceDeclaration>()
                            .first()
                            .values
                            .first()
                            .let { it as Int }
                    }
                    .firstOrNull()
                    ?.let { scriptId ->
                        declarations
                            .filterIsInstance<ExternalResourceDeclaration>()
                            .firstOrNull { externalResourceDeclaration ->
                                externalResourceDeclaration.getValue<Int>("id") == scriptId
                            }
                            ?.getValue<String>("path")
                    }


                Node(
                    name,
                    type,
                    parent,
                    scriptPath
                )
            }
            .reversed()
    }

    private fun deserializeSignalConnections(declarations: List<Declaration>, file: File, nodes: List<Node>): List<SignalConnection> {
        return declarations
            .filterIsInstance<SignalConnectionDeclaration>()
            .map { signalConnectionDeclaration ->
                val signal = requireNotNull(signalConnectionDeclaration.getValue<String>("signal")) {
                    "Malformed \"connection\" declaration in ${file.path}: Got no \"signal\" value"
                }
                val from = requireNotNull(signalConnectionDeclaration.getValue<String>("from")) {
                    "Malformed \"connection\" declaration in ${file.path}: Got no \"from\" value"
                }
                    .let { nodeName ->
                        getNodeBySignalConnection(nodeName, nodes)
                    }

                val to = requireNotNull(signalConnectionDeclaration.getValue<String>("to")) {
                    "Malformed \"connection\" declaration in ${file.path}: Got no \"to\" value"
                }
                    .let { nodeName ->
                        getNodeBySignalConnection(nodeName, nodes)
                    }

                val method = requireNotNull(signalConnectionDeclaration.getValue<String>("method")) {
                    "Malformed \"connection\" declaration in ${file.path}: Got no \"method\" value"
                }

                SignalConnection(
                    signal,
                    from,
                    to,
                    method
                )
            }
    }

    private fun getNodeBySignalConnection(nodeName: String, nodes: List<Node>) = if (nodeName == ".") {
        nodes.first { it.parent == null }
    } else {
        val nodePathSegments = nodeName.split("/")
        val node = nodePathSegments.last()
        val parent = nodePathSegments.getOrNull(nodePathSegments.size - 2) ?: "."
        nodes.first {
            it.parent == parent && it.name == node
        }
    }

    private inline fun <reified T> Declaration.getValue(lexeme: String): T? = values
        .filterIsInstance<Declaration>()
        .firstOrNull { stringDeclaration ->
            stringDeclaration.identifierToken?.lexeme == lexeme
        }
        ?.let { declaration ->
            if (declaration is NumberDeclaration) {
                listOf(declaration.getValueToType(T::class.java))
            } else {
                declaration.values.toList()
            }
        }
        ?.firstOrNull()
        ?.let {
            if (it is NumberDeclaration) {
                it.getValueToType(T::class.java)
            } else {
                it
            } as? T
        }
}
