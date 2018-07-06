package org.ado.biblio.lend

import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate

interface LendDao {

    @SqlQuery("select exists(select 1 from lends where book_id=:bookId and returned_at is null)")
    fun someoneHasIt(@Bind("bookId") bookId: Long): Boolean

    @SqlUpdate("insert into lends (book_id,person,created_at) values (:bookId,:person,:createdAt)")
    fun add(@BindBean lend: Lend)
}
