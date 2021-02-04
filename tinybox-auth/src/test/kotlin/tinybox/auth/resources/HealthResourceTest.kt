package tinybox.auth.resources

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
class HealthResourceTest {

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun testGetHealthEndpoint() {
        given()
            .`when`().get("/api/health")
            .then()
            .statusCode(200)
            .body(`is`(objectMapper.writeValueAsString(HealthResource.HealthResponse())))
    }
}
