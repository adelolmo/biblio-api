package org.ado.biblio.isbnsearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    fun get(@Query("q") q: String): Call<Volumes>

    data class Volumes(val kind: String,
                       val totalItems: Int,
                       val items: List<Item> = emptyList())

    data class Item(val kind: String,
                    val id: String,
                    val etag: String,
                    val selfLink: String,
                    val volumeInfo: VolumeInfo)

    data class VolumeInfo(val title: String,
                          val authors: List<String> = emptyList(),
                          val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
                          val imageLinks: ImageLinks)

    data class IndustryIdentifier(val type: IndustryIdentifierTypeEnum,
                                  val identifier: String)

    data class ImageLinks(val smallThumbnail: String,
                          val thumbnail: String)

    enum class IndustryIdentifierTypeEnum {
        ISBN_10, ISBN_13, OTHER
    }
}