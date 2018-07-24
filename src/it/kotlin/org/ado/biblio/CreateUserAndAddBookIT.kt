package org.ado.biblio

import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import java.util.*

class CreateUserAndAddBookIT {

    @Test
    fun user() {
        val username = UUID.randomUUID()

        given()
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
                .then()
                .statusCode(`is`(202))

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
        post.then()
                .statusCode(`is`(201))
                .header("Authorization", notNullValue())
        val session = post.then().extract().header("Authorization")


        val addBook = given()
                .request()
                .contentType(ContentType.JSON)
                .headers("Authorization", session)
                .body("""
                    {
                        "title":"The Book",
                        "author":"The Book Fundation",
                        "isbn":"12341234",
                        "imageUrl":"http://image"
                    }
                """.trimIndent())
                .`when`()
                .post("http://localhost:18090/books")
        addBook
                .then()
                .statusCode(`is`(201))
                .header("Location", startsWith("http://localhost:18090/books"))
        val bookLocation = addBook.then().extract().header("Location")

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
}