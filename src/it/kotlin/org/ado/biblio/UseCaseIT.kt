package org.ado.biblio

import com.jayway.restassured.RestAssured
import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.ValidatableResponse
import org.ado.biblio.vagrant.Vagrant
import org.hamcrest.CoreMatchers.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.util.*

class UseCaseIT {

    private val baseUrl: String = "https://localhost:18090"

    companion object {
        private val vagrant = Vagrant()

        init {
            RestAssured.useRelaxedHTTPSValidation()
        }

        @BeforeClass
        @JvmStatic
        fun setup() {
            vagrant.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            vagrant.stop()
        }
    }

    @Test
    fun creatingUser() {
        val response = createUser(UUID.randomUUID())

        response.statusCode(`is`(202))
    }

    @Test
    fun creatingSession() {
        val username = UUID.randomUUID()
        createUser(username)

        val response = createSession(username)
        response.statusCode(`is`(201))
        response.header("Authorization", notNullValue())
        response.header("Location", startsWith("$baseUrl/sessions/"))
    }

    @Test
    fun creatingBook() {
        val username = UUID.randomUUID()
        createUser(username)
        val sessionResponse = createSession(username)
        val session = sessionResponse.extract().header("Authorization")

        val response = createBook(session)
        response.statusCode(`is`(201))
        response.header("Location", startsWith("$baseUrl/books/"))
    }

    @Test
    fun gettingBook() {
        val username = UUID.randomUUID()
        createUser(username)
        val session = createSession(username).extract().header("Authorization")
        val createBookResponse = createBook(session)
        val bookLocation = createBookResponse.extract().header("Location")

        val bookId = bookLocation.split("/")[4]
        given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .`when`()
                .get(bookLocation)
                .then()
                .statusCode(`is`(200))
                .body("id", equalTo(bookId))
                .body("username", equalTo("$username"))
                .body("title", equalTo("The Book"))
                .body("isbn", equalTo("12341234"))
                .body("imageUrl", equalTo("http://image"))
    }

    @Test
    fun lendingBook() {
        val username = UUID.randomUUID()
        createUser(username)
        val sessionResponse = createSession(username)
        val session = sessionResponse.extract().header("Authorization")
        val bookLocation = createBook(session).extract().header("Location")
        val bookId = bookLocation.split("/")[4]

        lendBook(bookId, session).statusCode(`is`(202))
    }

    @Test
    fun givingBackBook() {
        val username = UUID.randomUUID()
        createUser(username)
        val sessionResponse = createSession(username)
        val session = sessionResponse.extract().header("Authorization")
        val bookLocation = createBook(session).extract().header("Location")
        val bookId = bookLocation.split("/")[4]
        lendBook(bookId, session)

        given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .`when`()
                .delete("$baseUrl/books/$bookId/lends")
                .then()
                .statusCode(202)
    }

    @Test
    fun shouldFindBookSearchingByIsbn() {
        val username = UUID.randomUUID()
        createUser(username)
        val sessionResponse = createSession(username)
        val session = sessionResponse.extract().header("Authorization")

        given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .`when`()
                .get("$baseUrl/isbnsearch?q=9783836958189")
                .then()
                .statusCode(200)
                .body("books[0].title", equalTo("Aschenputtel"))
                .body("books[0].author", equalTo("Charles Perrault,Roberto Innocenti"))
                .body("books[0].isbn", equalTo("9783836958189"))
                .body("books[0].imageUrl",
                        equalTo("http://books.google.com/books/content?id=lF91oAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"))
    }

    private fun createUser(username: UUID): ValidatableResponse {
        return given()
                .request()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username":"$username",
                            "password":"test"
                        }
                    """.trimIndent())
                .`when`()
                .post("$baseUrl/users")
                .then()
    }

    private fun createSession(username: UUID): ValidatableResponse {
        return given()
                .request()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username":"$username",
                            "password":"test"
                        }
                    """.trimIndent())
                .`when`()
                .post("$baseUrl/sessions")
                .then()
    }

    private fun createBook(session: String): ValidatableResponse {
        return given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .body("""
                        {
                            "title":"The Book",
                            "author":"The Book Foundation",
                            "isbn":"12341234",
                            "imageUrl":"http://image"
                        }
                    """.trimIndent())
                .`when`()
                .post("$baseUrl/books")
                .then()
    }

    private fun lendBook(bookId: String, session: String): ValidatableResponse {
        return given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .body("""
                            {
                                "name":"John"
                            }
                    """.trimIndent())
                .`when`()
                .post("$baseUrl/books/$bookId/lends")
                .then()
    }
}