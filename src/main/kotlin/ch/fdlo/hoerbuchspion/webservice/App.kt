package ch.fdlo.hoerbuchspion.webservice

import io.jooby.Kooby
import io.jooby.hibernate.HibernateModule
import io.jooby.hikari.HikariModule
import io.jooby.json.JacksonModule
import io.jooby.runApp
import io.jooby.whoops.WhoopsModule




class App: Kooby({
  install(JacksonModule())

  install(HikariModule())

  install(HibernateModule())

  install(WhoopsModule())

  mvc(AlbumController())
})

fun main(args: Array<String>) {
  runApp(args, App::class)
}
