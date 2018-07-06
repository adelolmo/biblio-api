package org.ado.biblio.users

import org.hibernate.validator.constraints.NotEmpty
import java.time.Instant
import javax.validation.constraints.NotNull

data class User(@field:NotNull @field:NotEmpty val username: String,
                @field:NotNull @field:NotEmpty val password: String,
                @field:NotNull @field:NotEmpty val salt: String,
                @field:NotNull @field:NotEmpty val role: String,
                @field:NotNull val createdAt: Instant)