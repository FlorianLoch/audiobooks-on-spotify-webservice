package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.Album
import ch.fdlo.hoerbuchspion.webservice.data.Artist
import ch.fdlo.hoerbuchspion.webservice.data.PaginationWrapper
import com.querydsl.jpa.impl.JPAQuery
import io.jooby.Context
import io.jooby.require
import javax.persistence.EntityManager

import ch.fdlo.hoerbuchspion.webservice.data.QAlbum.album
import ch.fdlo.hoerbuchspion.webservice.data.QArtist.artist
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import kotlin.math.min

class QueryBuilder {
    companion object {
        const val DEFAULT_ROW_LIMIT_PER_REQUEST = 50L

        fun fetchAlbums(ctx: Context): PaginationWrapper<Album> {
            return fetchFromRequest(ctx, {
                album.name.likeIgnoreCase(it)
            }, album)
        }

        fun fetchArtists(ctx: Context): PaginationWrapper<Artist> {
            return fetchFromRequest(ctx, {
                artist.name.likeIgnoreCase(it)
            }, artist)
        }

        private fun <T> fetchFromRequest(
            ctx: Context,
            searchPredicateBuilder: (String) -> Predicate,
            from: EntityPath<T>
        ): PaginationWrapper<T> {
            val searchString = ctx.query("s").value("%")
            val offset = ctx.query("offset").longValue(0)
            val limit = min(ctx.query("limit").longValue(DEFAULT_ROW_LIMIT_PER_REQUEST), DEFAULT_ROW_LIMIT_PER_REQUEST)

            val em = ctx.require(EntityManager::class)

            val criteria = searchPredicateBuilder("%$searchString%")
            val count = JPAQuery<Long>(em).from(from).where(criteria).fetchCount()

            val records = if (count > 0 && offset < count)
                JPAQuery<T>(em).select(
                    from
                ).from(from).where(criteria).offset(offset).limit(limit).fetch()
            else mutableListOf()

            return PaginationWrapper(count, offset, limit, records)
        }
    }
}

