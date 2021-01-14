package com.utopiarise.serialization.godot.core

enum class Keywords(private val text: String) {
    RESOURCE("resource") {
        override fun token(text: String, line: Int): Token = ResourceToken(text, null, line)
    },
    GD_RESOURCE("gd_resource") {
        override fun token(text: String, line: Int): Token = GdResourceToken(text, null, line)
    },
    GD_SCENE("gd_scene") {
        override fun token(text: String, line: Int): Token = GdSceneToken(text, null, line)
    },
    EXT_RESOURCE("ext_resource") {
        override fun token(text: String, line: Int): Token = ExtResourceToken(text, null, line)
    },
    SUB_RESOURCE("sub_resource") {
        override fun token(text: String, line: Int): Token = SubResourceToken(text, null, line)
    },
    CONNECTION("connection") {
        override fun token(text: String, line: Int): Token = ConnectionToken(text, null, line)
    },
    EXTRESOURCE("ExtResource") {
        override fun token(text: String, line: Int): Token = CallExtResourceToken(text, null, line)
    },
    SUBRESOURCE("SubResource") {
        override fun token(text: String, line: Int): Token = CallSubResourceToken(text, null, line)
    },
    NODE("node") {
        override fun token(text: String, line: Int): Token = NodeToken(text, null, line)
    },
    SCRIPT("script") {
        override fun token(text: String, line: Int): Token = ScriptToken(text, null, line)
    },
    TRUE("true") {
        override fun token(text: String, line: Int): Token = BooleanToken(text, true, line)
    },
    FALSE("false") {
        override fun token(text: String, line: Int): Token = BooleanToken(text, false, line)
    };

    companion object {
        fun tokenFromText(text: String, line: Int): Token? {
            values().forEach {
                if (it.text == text) return it.token(text, line)
            }
            return null
        }
    }

    abstract fun token(text: String, line: Int): Token
}
