package org.ado.biblio.books.core

import org.ado.biblio.books.BookDao
import org.ado.biblio.lend.Lend
import org.ado.biblio.lend.LendDao
import java.time.Clock
import java.time.Instant
import javax.ws.rs.NotFoundException

class Library(
        private val lendDao: LendDao,
        private val bookDao: BookDao,
        private val clock: Clock
) {

    fun lend(username: String, bookId: Long, person: String) {
        if (!bookDao.exists(username, bookId)) {
            throw NotFoundException("book not found")
        }
        if (lendDao.someoneHasIt(bookId)) {
            throw NotFoundException("someone has the book")
        }
        lendDao.add(Lend.create(bookId, person, Instant.now(clock)))
    }

    fun giveBack(username: String, bookId: Long) {
        if (!bookDao.exists(username, bookId)) {
            throw NotFoundException("book not found")
        }
        if (!lendDao.someoneHasIt(bookId)) {
            throw NotFoundException("the book is not lent")
        }
        lendDao.updateReturnedAt(bookId, Instant.now(clock))
    }
}