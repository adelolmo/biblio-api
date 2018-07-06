package org.ado.biblio.books

import org.hibernate.validator.constraints.NotEmpty
import java.time.Instant
import javax.validation.constraints.NotNull

data class Book(val id: Long?,
                val username: String?,
                @field:NotNull @field:NotEmpty val title: String,
                @field:NotNull @field:NotEmpty val author: String,
                @field:NotNull @field:NotEmpty val isbn: String,
                val tags: String?,
                val createdAt: Instant?,
                @field:NotNull @field:NotEmpty val imageUrl: String
)