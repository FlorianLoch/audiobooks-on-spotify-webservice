package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.Album
import io.jooby.Jooby
import io.jooby.JoobyTest
import io.jooby.StatusCode
import io.jooby.require
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import javax.persistence.EntityManager
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

@JoobyTest(App::class)
class IntegrationTest {

    companion object {
        val client = OkHttpClient()

        @BeforeAll
        @JvmStatic
        fun setup(app: Jooby) {
            val em = app.require(EntityManager::class)

            println("Going to run initialization queries. Check Hibernate's log.")

            em.transaction.begin()

            Files.lines(Paths.get(this::class.java.classLoader.getResource("queries.sql").toURI())).forEach() {
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
            assertEquals(
                "{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtUrl\":\"http://albumart.de/image1.png\",\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"},{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"1992-08-03\",\"albumArtUrl\":\"http://albumart.de/image2.png\",\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideArtistsViaRESTAPI(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/artists")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun apiShouldSupportPagination(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/albums?limit=1")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":2,\"offset\":0,\"limit\":1,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtUrl\":\"http://albumart.de/image1.png\",\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/albums?limit=1&offset=1")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":2,\"offset\":1,\"limit\":1,\"items\":[{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"1992-08-03\",\"albumArtUrl\":\"http://albumart.de/image2.png\",\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/albums?limit=1000")
                .build()
        ).execute().use { rsp ->
            assertTrue(rsp.body!!.string().startsWith("{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{"))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/albums?offset=2")
                .build()
        ).execute().use { rsp ->
            assertEquals("{\"total\":2,\"offset\":2,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }
}
