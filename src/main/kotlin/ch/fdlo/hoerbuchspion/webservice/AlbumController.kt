package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.Album
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.require
import javax.persistence.EntityManager


@Path("/album")
class AlbumController {

  @GET
  fun getAlbumFromDB(ctx: Context): List<Album>? {
    val em = ctx.require(EntityManager::class)
    val query = em.createQuery("FROM Album", Album::class.java)
    query.firstResult = 0
    query.maxResults = 5

    for (res in query.resultList) {
      print(res)
    }

    return query.resultList
  }
}
