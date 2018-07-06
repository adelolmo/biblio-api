package org.ado.biblio.books.core

import org.ado.biblio.books.BookDao
import org.ado.biblio.lend.Lend
import org.ado.biblio.lend.LendDao
import org.ado.biblio.users.UserDao
import java.time.Clock
import java.time.Instant
import javax.ws.rs.NotFoundException

class Library(
        val bookDao: BookDao,
        val userDao: UserDao,
        val lendDao: LendDao,
        private val clock: Clock
) {

    fun lend(bookId: Long, person: String) {
        if (someoneHasIt(bookId)) {
            throw NotFoundException("someone has the book")
        }
        lendDao.add(Lend(null, bookId, person, Instant.now(clock), null))
    }

    private fun someoneHasIt(bookId: Long): Boolean {
        return lendDao.someoneHasIt(bookId)
    }
}

