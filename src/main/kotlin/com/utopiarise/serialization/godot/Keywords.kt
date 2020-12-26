package com.utopiarise.serialization.godot

enum class Keywords(private val text: String) {
    RESOURCE("resource") {
        override fun token(text: String, line: Int): Token = ResourceToken(text, null, line)
    },
    GD_RESOURCE("gd_resource") {
        override fun token(text: String, line: Int): Token = GdResourceToken(text, null, line)
    },
    EXT_RESOURCE("ext_resource") {
        override fun token(text: String, line: Int): Token = ExtResourceToken(text, null, line)
    },
    EXTRESOURCE("ExtResource") {
        override fun token(text: String, line: Int): Token = CallExtResourceToken(text, null, line)
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
