package org.ado.biblio.isbnsearch

import java.util.*

interface GoogleBooksDao {
    fun get(q: String): Optional<GoogleBooksApi.Volumes>
}

class DefaultGoogleBooksDao(private val googleBooksApi: GoogleBooksApi) : GoogleBooksDao {
    override fun get(q: String): Optional<GoogleBooksApi.Volumes> {
        val response = googleBooksApi.get(q).execute()
        if (!response.isSuccessful || response.body() == null) {
            return Optional.empty()
        }
        return Optional.ofNullable(response.body())
    }
}