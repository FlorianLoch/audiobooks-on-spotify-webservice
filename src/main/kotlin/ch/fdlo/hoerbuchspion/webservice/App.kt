package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.*
import ch.fdlo.hoerbuchspion.webservice.db.QueryBuilder
import ch.fdlo.hoerbuchspion.webservice.db.queryCrawlStats
import io.jooby.*
import io.jooby.hibernate.HibernateModule
import io.jooby.hikari.HikariModule
import io.jooby.json.JacksonModule
import io.jooby.whoops.WhoopsModule
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.persistence.EntityManager

@OpenAPIDefinition(
    info = Info(
        title = "Audiobooks-On-Spotify",
        description = "Providing information on audiobooks available on Spotify to make it easier for you to find good books to listen to.",
    )
)
class App : Kooby({
    // The implicit route "/swagger/" provides OpenAPI documentation
    install(OpenAPIModule())

    install(JacksonModule())

    install(HikariModule())

    decorator(CorsHandler(Cors.from(config)))

    // We need to explicitly state the package, otherwise Hibernate won't be initialized correctly (only when running tests)
    install(HibernateModule().scan("ch.fdlo.hoerbuchspion.webservice.data"))

    install(WhoopsModule())

    val webappAsset = AssetSource.create(classLoader, "webapp")
    assets("/?*", AssetHandler("index.html", webappAsset))

    get("/api/albums", ::getAlbums)
    get("/api/albums/{id}", ::getSingleAlbum)

    get("/api/artists", ::getArtists)
    get("/api/artists/{id}", ::getSingleArtist)

    get("/api/stats", ::getStats)
})

@Operation(
    summary = "Provides albums/audiobooks matching given filters. Information is wrapped inside a PaginationWrapper object.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "400",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\n" +
                            "  \"message\": \"Error description\",\n" +
                            "  \"status\": 400,\n" +
                            "  \"reason\": \"Some details on the error.\"\n" +
                            "}"
                )]
            )]
        ),
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artists\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist1.com/image_lg.png\",\"medium\":\"http://artist1.com/image_md.png\",\"small\":\"http://artist1.com/image_sm.png\"},\"popularity\":90}],\"releaseDate\":\"1992-08-03\",\"albumArt\":{\"large\":\"http://albumart.de/image2_lg.png\",\"medium\":\"http://albumart.de/image2_md.png\",\"small\":\"http://albumart.de/image2_sm.png\"},\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"preview\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"EN\"},{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artists\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist1.com/image_lg.png\",\"medium\":\"http://artist1.com/image_md.png\",\"small\":\"http://artist1.com/image_sm.png\"},\"popularity\":90},{\"id\":\"93hdqcda39ds\",\"name\":\"Another Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist2.com/image_lg.png\",\"medium\":\"http://artist2.com/image_md.png\",\"small\":\"http://artist2.com/image_sm.png\"},\"popularity\":93}],\"releaseDate\":\"2020-08-01\",\"albumArt\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"preview\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}"
                )]
            )]
        )],
    parameters = [
        Parameter(
            name = "offset",
            description = "Required for pagination. Position from which to start returning records.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "0",
                type = "integer",
                minimum = "0"
            )
        ),
        Parameter(
            name = "limit",
            description = "Required for pagination. Number of records to return starting from 'offset'.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString(),
                type = "integer",
                minimum = QueryBuilder.MIN_ROWS_REQUIRED_TO_BE_REQUESTED.toString(),
                maximum = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString()
            )
        ),
        Parameter(
            name = "s",
            description = "Search term records need to match.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "%",
                type = "string"
            )
        ),
        Parameter(
            name = "unabridged_only",
            description = "Flag whether to provide only unabridged albums/audiobooks.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "false",
                type = "boolean"
            )
        ),
        Parameter(
            name = "language",
            description = "Only provide albums/audiobooks matching the given language. Parameter can be set multiple times.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                implementation = Language::class
            )
        ),
        Parameter(
            name = "artist_id",
            description = "Only provide albums/audiobooks by the given artist (matched by the artist's ID).",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "",
                type = "string"
            )
        )]
)
fun getAlbums(ctx: Context): PaginationWrapper<Album> {
    val offset = ctx.query("offset").longValue(0)
    val limit = ctx.query("limit").longValue(QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST)
    val searchTerm = ctx.query("s").value("%")
    val unabridgedOnly = ctx.query("unabridged_only").booleanValue(false)
    val languages = ctx.query("language").map {
        val langStr = it.value().toLowerCase()
        val lang = Language.fromISO_639_1(langStr)

        if (lang == Language.UNKNOWN) {
            throw IllegalArgumentException("'$langStr' is not an ISO 639-1 identifier or does not denote a supported language.")
        }

        lang
    }.toSet()
    val artistID = ctx.query("artist_id").value("")
    val em = ctx.require(EntityManager::class.java)

    return QueryBuilder.fetchAlbums(em, offset, limit, searchTerm, unabridgedOnly, languages, artistID)
}

@Operation(
    summary = "Provides a single album/audiobook matching the given id.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "400",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\n" +
                            "  \"message\": \"Error description\",\n" +
                            "  \"status\": 400,\n" +
                            "  \"reason\": \"Some details on the error.\"\n" +
                            "}"
                )]
            )]
        ),
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artists\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist1.com/image_lg.png\",\"medium\":\"http://artist1.com/image_md.png\",\"small\":\"http://artist1.com/image_sm.png\"},\"popularity\":90}],\"releaseDate\":\"1992-08-03\",\"albumArt\":{\"large\":\"http://albumart.de/image2_lg.png\",\"medium\":\"http://albumart.de/image2_md.png\",\"small\":\"http://albumart.de/image2_sm.png\"},\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"preview\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"EN\"}"
                )]
            )]
        )],
    parameters = [
        Parameter(
            name = "id",
            description = "Describes which record to return.",
            `in` = ParameterIn.PATH,
            required = false,
            schema = Schema(
                type = "string",
            )
        )]
)
fun getSingleAlbum(ctx: Context): Album {
    val id = ctx.path("id").value()

    val em = ctx.require(EntityManager::class.java)

    val album = QueryBuilder.fetchSingleAlbum(em, id)
    if (album == null) {
        throw NoSuchElementException("Identifier '$id' cannot be resolved to an album.")
    } else {
        return album
    }
}

@Operation(
    summary = "Provides artists/authors matching given filters. Information is wrapped inside a PaginationWrapper object.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"93hdqcda39ds\",\"name\":\"Another Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist2.com/image_lg.png\",\"medium\":\"http://artist2.com/image_md.png\",\"small\":\"http://artist2.com/image_sm.png\"},\"popularity\":93},{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist1.com/image_lg.png\",\"medium\":\"http://artist1.com/image_md.png\",\"small\":\"http://artist1.com/image_sm.png\"},\"popularity\":90}]}"
                )]
            )]
        )],
    parameters = [
        Parameter(
            name = "offset",
            description = "Required for pagination. Position from which to start returning records.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "0",
                type = "integer",
                minimum = "0"
            )
        ),
        Parameter(
            name = "limit",
            description = "Required for pagination. Number of records to return starting from 'offset'.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString(),
                type = "integer",
                minimum = QueryBuilder.MIN_ROWS_REQUIRED_TO_BE_REQUESTED.toString(),
                maximum = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString()
            )
        ),
        Parameter(
            name = "s",
            description = "Search term records need to match.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "%",
                type = "string"
            )
        )]
)
fun getArtists(ctx: Context): PaginationWrapper<Artist> {
    val offset = ctx.query("offset").longValue(0)
    val limit = ctx.query("limit").longValue(QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST)
    val searchTerm = ctx.query("s").value("%")
    val em = ctx.require(EntityManager::class.java)

    return QueryBuilder.fetchArtists(em, offset, limit, searchTerm)
}

@Operation(
    summary = "Provides a single artist/author matching the given id.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "400",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\n" +
                            "  \"message\": \"Error description\",\n" +
                            "  \"status\": 400,\n" +
                            "  \"reason\": \"Some details on the error.\"\n" +
                            "}"
                )]
            )]
        ),
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"id\":\"93hdqcda39ds\",\"name\":\"Another Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist2.com/image_lg.png\",\"medium\":\"http://artist2.com/image_md.png\",\"small\":\"http://artist2.com/image_sm.png\"},\"popularity\":93},{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":{\"large\":\"http://artist1.com/image_lg.png\",\"medium\":\"http://artist1.com/image_md.png\",\"small\":\"http://artist1.com/image_sm.png\"},\"popularity\":90}"
                )]
            )]
        )],
    parameters = [
        Parameter(
            name = "id",
            description = "Describes which record to return.",
            `in` = ParameterIn.PATH,
            required = false,
            schema = Schema(
                type = "string",
            )
        )]
)
fun getSingleArtist(ctx: Context): Artist {
    val id = ctx.path("id").value()

    val em = ctx.require(EntityManager::class.java)

    val artist = QueryBuilder.fetchSingleArtist(em, id)
    if (artist == null) {
        throw NoSuchElementException("Identifier '$id' cannot be resolved to an artist.")
    } else {
        return artist
    }
}

@Operation(
    summary = "Provides some statistical values about the last run of the crawler.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"PLAYLISTS_CONSIDERED_COUNT\":\"3\",\"PROFILES_CONSIDERED_COUNT\":\"2\",\"ARTISTS_CONSIDERED_COUNT\":\"1\",\"ALBUMS_FOUND_COUNT\":\"0\"}"
                )]
            )]
        )]
)
fun getStats(ctx: Context): MutableMap<CrawlStatsKV.KVKey, String> {
    val em = ctx.require(EntityManager::class.java)

    return queryCrawlStats(em)
}

fun main(args: Array<String>) {
    runApp(args, App::class)
}
