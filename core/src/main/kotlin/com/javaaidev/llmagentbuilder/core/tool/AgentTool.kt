package com.javaaidev.llmagentbuilder.core.tool

import com.javaaidev.easyllmtools.llmtoolspec.Tool
import java.lang.reflect.Type

class ExceptionTool : Tool<String, String> {
    override fun getName(): String {
        return "_Exception"
    }

    override fun getDescription(): String {
        return "Exception tool"
    }

    override fun getRequestType(): Type {
        return String::class.java
    }

    override fun call(t: String): String {
        return t
    }

}