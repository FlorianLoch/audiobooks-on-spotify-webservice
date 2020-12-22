package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.Album
import ch.fdlo.hoerbuchspion.webservice.data.PaginationWrapper
import ch.fdlo.hoerbuchspion.webservice.db.fetchAlbums
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path


@Path("/album")
class AlbumController {

  @GET
  fun getAlbumFromDB(ctx: Context): PaginationWrapper<Album> {
    return fetchAlbums(ctx)
  }
}
