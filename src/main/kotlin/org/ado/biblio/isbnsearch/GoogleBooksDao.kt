package org.ado.biblio.isbnsearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksDao {

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
                          val publisher: String = "",
                          val publishedDate: String = "",
                          val description: String = "",
                          val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
                          val pageCount: Int,
                          val printType: String,
                          val categories: List<String> = emptyList(),
                          val averageRating: Int,
                          val ratingsCount: Int,
                          val contentVersion: String,
                          val imageLinks: ImageLinks,
                          val language: String,
                          val previewLink: String,
                          val infoLink: String,
                          val canonicalVolumeLink: String)

    data class IndustryIdentifier(val type: IndustryIdentifierTypeEnum,
                                  val identifier: String)

    data class ImageLinks(val smallThumbnail: String,
                          val thumbnail: String)

    enum class IndustryIdentifierTypeEnum {
        ISBN_10, ISBN_13, OTHER
    }
}