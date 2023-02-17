package com.example.mytask
object Strings {
    @JvmStatic
    fun isNullOrEmpty(string: String?) = string?.isEmpty() ?: true
}