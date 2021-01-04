package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.*
import com.querydsl.jpa.impl.JPAQuery
import io.jooby.Context
import io.jooby.require
import javax.persistence.EntityManager

import ch.fdlo.hoerbuchspion.webservice.data.QAlbum.album
import ch.fdlo.hoerbuchspion.webservice.data.QArtist.artist
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.EntityPathBase
import kotlin.math.min

class QueryBuilder {
    companion object {
        const val DEFAULT_ROW_LIMIT_PER_REQUEST = 50L

        fun fetchAlbums(ctx: Context): PaginationWrapper<Album> {
            return fetchFromRequest(ctx, album)
        }

        fun fetchArtists(ctx: Context): PaginationWrapper<Artist> {
            return fetchFromRequest(ctx, artist)
        }

        private fun <T> fetchFromRequest(
            ctx: Context,
            from: EntityPathBase<T>
        ): PaginationWrapper<T> {
            val offset = ctx.query("offset").longValue(0)
            val limit = min(ctx.query("limit").longValue(DEFAULT_ROW_LIMIT_PER_REQUEST), DEFAULT_ROW_LIMIT_PER_REQUEST)

            val em = ctx.require(EntityManager::class)

            val criteria = buildFilter(ctx, from)
            val count = JPAQuery<Long>(em).from(from).where(criteria).fetchCount()

            val records = if (count > 0 && offset < count)
                JPAQuery<T>(em).select(
                    from
                ).from(from).where(criteria).offset(offset).limit(limit).fetch()
            else mutableListOf()

            return PaginationWrapper(count, offset, limit, records)
        }

        private fun <T> buildFilter(
            ctx: Context,
            from: EntityPathBase<T>
        ): Predicate {
            val searchString = "%${ctx.query("s").value("%").trim()}%"
            val unabridgedOnly = ctx.query("unabridged_only").booleanValue(false)

            // TODO: Implement filter by language

            when (from) {
                is QAlbum -> {
                    val predicate = from.name.likeIgnoreCase(searchString)

                    if (unabridgedOnly) {
                        return predicate.and(from.storyType.eq(Album.StoryType.UNABRIDGED))
                    }

                    return predicate
                }
                is QArtist -> {
                    return from.name.likeIgnoreCase(searchString)
                }
                else -> {
                    throw Throwable("Entity needs to be of either QAlbum or QArtist.")
                }
            }
        }

    }
}

