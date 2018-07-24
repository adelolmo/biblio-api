package org.ado.biblio

import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.ValidatableResponse
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import java.util.*

class UseCaseIT {

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
    }

    @Test
    fun creatingBook() {
        val username = UUID.randomUUID()
        createUser(username)
        val sessionResponse = createSession(username)
        val session = sessionResponse.extract().header("Authorization")

        val response = createBook(session)
        response.statusCode(`is`(201))
        response.header("Location", startsWith("http://localhost:18090/books"))
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

    private fun createUser(username: UUID): ValidatableResponse {
        val post = given()
                .request()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username":"$username",
                            "password":"test"
                        }
                    """.trimIndent())
                .`when`()
                .post("http://localhost:18090/users")
        return post.then()
    }

    private fun createSession(username: UUID): ValidatableResponse {
        val post = given()
                .request()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username":"$username",
                            "password":"test"
                        }
                    """.trimIndent())
                .`when`()
                .post("http://localhost:18090/sessions")
        return post.then()
    }

    private fun createBook(session: String): ValidatableResponse {
        val addBook = given()
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
                .post("http://localhost:18090/books")
        return addBook.then()
    }
}