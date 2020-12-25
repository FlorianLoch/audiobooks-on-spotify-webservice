package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.Album
import io.jooby.Jooby
import io.jooby.JoobyTest
import io.jooby.StatusCode
import io.jooby.require
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import javax.persistence.EntityManager
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths


// TODO: Add a tag to explicitly include running this test

@JoobyTest(App::class)
class IntegrationTest {

    companion object {
        val client = OkHttpClient()

        @BeforeAll
        @JvmStatic
        fun setup(app: Jooby) {
            val em = app.require(EntityManager::class)

            println("Going to run initialization queries:")

            em.transaction.begin()

            Files.lines(Paths.get(this::class.java.classLoader.getResource("queries.sql").toURI())).forEach() {
                println("-> Going to run query: $it")
                em.createNativeQuery(it).executeUpdate()
            }

            em.transaction.commit()
        }
    }

    @Test
    fun shouldProvideAlbumsViaRESTAPI(serverPort: Int) {
        val req = Request.Builder()
            .url("http://localhost:$serverPort/albums")
            .build()

        client.newCall(req).execute().use { rsp ->
            assertEquals("{\"total\":0,\"offset\":0,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideArtistsViaRESTAPI(serverPort: Int) {
        val req = Request.Builder()
            .url("http://localhost:$serverPort/artists")
            .build()

        client.newCall(req).execute().use { rsp ->
            assertEquals("{\"total\":0,\"offset\":0,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun apiShouldSupportPagination(serverPort: Int) {
        val req = Request.Builder()
            .url("http://localhost:$serverPort/albums")
            .build()

        client.newCall(req).execute().use { rsp ->
            assertEquals("{\"total\":0,\"offset\":0,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }
}
