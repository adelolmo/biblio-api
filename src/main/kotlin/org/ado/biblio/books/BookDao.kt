package org.ado.biblio.books

import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.*
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.time.Instant

interface BookDao {

    @SqlQuery("select * from books where username=:username and id=:id")
    @RegisterMapper(BookMapper::class)
    fun get(@Bind("username") username: String, @Bind("id") id: Long): Book?

    @SqlQuery("select * from books where username=:username")
    @RegisterMapper(BookMapper::class)
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

    class BookMapper : ResultSetMapper<Book> {
        override fun map(index: Int, r: ResultSet, ctx: StatementContext): Book {
            return Book(r.getLong("id"),
                    r.getString("username"),
                    r.getString("title"),
                    r.getString("author"),
                    r.getString("isbn"),
                    r.getString("tags"),
                    r.getTimestamp("created_at").toInstant(),
                    r.getString("image_url"))
        }

    }
}
