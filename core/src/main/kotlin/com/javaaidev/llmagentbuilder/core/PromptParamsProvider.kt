package com.javaaidev.llmagentbuilder.core

interface PromptParamsProvider {
    fun provideSystemParams(): Map<String, Any>? {
        return mapOf()
    }

    fun provideUserParams(): Map<String, Any>? {
        return mapOf()
    }
}