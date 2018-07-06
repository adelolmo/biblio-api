package org.ado.biblio.users

import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet

interface UserDao {

    @SqlQuery("select * from users where username=:username")
    @RegisterMapper(UserMapper::class)
    fun get(@Bind("username") username: String): User?

    @SqlQuery("select exists(select 1 from users where username=:username)")
    fun exists(@Bind("username") username: String): Boolean

    @SqlQuery("select exists(select 1 from users where username=:username and password=:password)")
    fun validate(@Bind("username") username: String, @Bind("password") password: String): Boolean

    @SqlUpdate("""
        insert into users(username,password,salt,role,created_at)
        values (:username,:password,:salt,:role,:createdAt)
    """)
    fun add(@BindBean user: User)

    class UserMapper : ResultSetMapper<User> {
        override fun map(index: Int, r: ResultSet, ctx: StatementContext): User {
            return User(r.getString("username"),
                    r.getString("password"),
                    r.getString("salt"),
                    r.getString("role"),
                    r.getTimestamp("created_at").toInstant())
        }
    }
}