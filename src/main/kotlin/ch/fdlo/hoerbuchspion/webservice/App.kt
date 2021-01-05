package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.db.QueryBuilder
import ch.fdlo.hoerbuchspion.webservice.db.queryCrawlStats
import io.jooby.Kooby
import io.jooby.hibernate.HibernateModule
import io.jooby.hikari.HikariModule
import io.jooby.json.JacksonModule
import io.jooby.runApp
import io.jooby.whoops.WhoopsModule
import io.jooby.OpenAPIModule





class App : Kooby({
    // The implicit route "/swagger/" provides OpenAPI documentation
    install(OpenAPIModule())

    install(JacksonModule())

    install(HikariModule())

    // We need to explicitly state the package, otherwise Hibernate won't be initialized correctly (only when running tests)
    install(HibernateModule().scan("ch.fdlo.hoerbuchspion.webservice.data"))

    install(WhoopsModule())

    get("/albums") { it ->
        QueryBuilder.fetchAlbums(it)
    }

    get("/artists") { it ->
        QueryBuilder.fetchArtists(it)
    }

    get("/stats") { it ->
        queryCrawlStats(it)
    }
})

fun main(args: Array<String>) {
    runApp(args, App::class)
}
