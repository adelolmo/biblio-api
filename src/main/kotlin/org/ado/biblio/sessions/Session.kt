package org.ado.biblio.sessions

import org.hibernate.validator.constraints.NotEmpty
import java.time.Instant
import java.util.*
import javax.validation.constraints.NotNull

data class Session(@field:NotNull @field:NotEmpty val id: UUID,
                   @field:NotNull @field:NotEmpty val username: String,
                   @field:NotNull val createdAt: Instant,
                   @field:NotNull val expiresAt: Instant)