package org.ado.biblio.lend

import org.hibernate.validator.constraints.NotEmpty
import java.time.Instant
import javax.validation.constraints.NotNull

data class Lend(
        val id: Long?,
        @field:NotNull val bookId: Long,
        @field:NotNull @field:NotEmpty val person: String,
        @field:NotNull val createdAt: Instant,
        val returnedAt: Instant?
)
