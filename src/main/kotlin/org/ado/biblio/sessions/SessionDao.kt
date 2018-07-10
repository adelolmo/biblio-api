package org.ado.biblio.sessions

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet
import java.util.*

interface SessionDao {

    @SqlQuery("select * from sessions where id=:id")
    @RegisterRowMapper(SessionMapper::class)
    fun get(@Bind("id") id: String): Session?

    @SqlUpdate("insert into sessions(id,username,created_at,expires_at) values (:id,:username,:createdAt,:expiresAt)")
    @RegisterRowMapper(SessionMapper::class)
    fun add(@BindBean session: Session)

    class SessionMapper : RowMapper<Session> {
        override fun map(rs: ResultSet, ctx: StatementContext): Session {
            return Session((UUID.fromString(rs.getString("id"))),
                    rs.getString("username"),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("expires_at").toInstant())
        }
    }
}