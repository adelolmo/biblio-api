package org.ado.biblio.users

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet

@RegisterRowMapper(UserDao.UserMapper::class)
interface UserDao {

    @SqlQuery("select * from users where username=:username")
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

    class UserMapper : RowMapper<User> {
        override fun map(rs: ResultSet, ctx: StatementContext): User {
            return User(rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("salt"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at").toInstant())
        }
    }
}