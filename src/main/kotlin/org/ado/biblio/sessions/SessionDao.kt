package org.ado.biblio.sessions

import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.util.*

interface SessionDao {

    @SqlQuery("select * from sessions where id=:id")
    @RegisterMapper(SessionMapper::class)
    fun get(@Bind("id") id: String): Session?

    @SqlUpdate("insert into sessions(id,username,created_at,expires_at) values (:id,:username,:createdAt,:expiresAt)")
    @RegisterMapper(SessionMapper::class)
    fun add(@BindBean session: Session)

    class SessionMapper : ResultSetMapper<Session> {
        override fun map(index: Int, r: ResultSet, ctx: StatementContext): Session {
            return Session((UUID.fromString(r.getString("id"))),
                    r.getString("username"),
                    r.getTimestamp("created_at").toInstant(),
                    r.getTimestamp("expires_at").toInstant())
        }
    }
}