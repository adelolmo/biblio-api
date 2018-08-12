package org.ado.biblio.books

import org.ado.biblio.shared.IdHasher
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet
import java.time.Instant
import java.util.*

interface BookDao {

    @SqlQuery("select * from books where username=:username and id=:id")
    fun get(@Bind("username") username: String, @Bind("id") id: Long): Optional<Book>

    @SqlQuery("select * from books where username=:username")
    fun getAll(@Bind("username") username: String): List<Book>

    @SqlUpdate(
            """
            insert into books (username,title,author,created_at,isbn,tags,image_url)
            values (:user,:title,:author,:created,:isbn,:tags,:imageUrl)
            """)
    @GetGeneratedKeys
    fun add(@Bind("user") user: String, @Bind("created") createdAt: Instant, @BindBean book: Book): Long

    @SqlQuery("select exists(select 1 from books where username=:username and id=:id)")
    fun exists(@Bind("username") username: String, @Bind("id") id: Long): Boolean

    class BookMapper(val idHasher: IdHasher) : RowMapper<Book> {
        override fun map(rs: ResultSet, ctx: StatementContext): Book {
            return Book(idHasher.encode(rs.getLong("id")),
                    rs.getString("username"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("tags"),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getString("image_url"))
        }
    }
}
