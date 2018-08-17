package org.ado.biblio.isbnsearch

import java.util.*

interface GoogleBooksDao {
    fun get(q: String): Optional<GoogleBooksApi.Volumes>
}

class DefaultGoogleBooksDao(
        private val googleBooksApi: GoogleBooksApi,
        private val country: String) : GoogleBooksDao {

    override fun get(isbn: String): Optional<GoogleBooksApi.Volumes> {
        val response = googleBooksApi.get("isbn:$isbn", country).execute()
        if (response.isSuccessful) {
            return Optional.ofNullable(response.body())
        }
        return Optional.empty()
    }
}