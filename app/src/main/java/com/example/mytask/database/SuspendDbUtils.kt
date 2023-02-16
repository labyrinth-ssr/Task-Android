package com.example.mytask.database

import com.example.mytask.database.DbUtils.MAX_SQLITE_ARGS
import com.example.mytask.database.DbUtils.dbchunk

object SuspendDbUtils {
    suspend fun <T> Iterable<T>.eachChunk(action: suspend (List<T>) -> Unit) =
        eachChunk(MAX_SQLITE_ARGS, action)

    suspend fun <T> Iterable<T>.eachChunk(size: Int, action: suspend (List<T>) -> Unit) =
        chunked(size).forEach { action(it) }

    suspend fun <T, R> Iterable<T>.chunkedMap(transform: suspend (List<T>) -> Iterable<R>): List<R> =
        dbchunk().flatMap { transform(it) }
}