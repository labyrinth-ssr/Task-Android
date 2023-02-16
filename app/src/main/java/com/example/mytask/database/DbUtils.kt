package com.example.mytask.database

object DbUtils {
    const val MAX_SQLITE_ARGS = 990

    fun <T> Iterable<T>.dbchunk(): List<List<T>> = chunked(MAX_SQLITE_ARGS)
}
