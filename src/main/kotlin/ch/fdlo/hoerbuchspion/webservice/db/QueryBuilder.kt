package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.*
import com.querydsl.jpa.impl.JPAQuery
import javax.persistence.EntityManager

import ch.fdlo.hoerbuchspion.webservice.data.QAlbum.album
import ch.fdlo.hoerbuchspion.webservice.data.QArtist.artist
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import java.lang.IllegalArgumentException
import kotlin.math.min

class QueryBuilder {
    companion object {
        const val DEFAULT_ROW_LIMIT_PER_REQUEST = 50L
        const val MIN_ROWS_REQUIRED_TO_BE_REQUESTED = 1L

        fun fetchAlbums(
            em: EntityManager,
            offset: Long,
            limit: Long,
            searchTerm: String,
            unabridgedOnly: Boolean,
            languages: Set<Language>,
            artistID: String
        ): PaginationWrapper<Album> {
            var criteria = buildSearchTermPredicate(album.name, searchTerm)

            if (unabridgedOnly) {
                criteria = criteria.and(album.storyType.eq(Album.StoryType.UNABRIDGED))
            }

            if (languages.isNotEmpty()) {
                criteria = criteria.and(album.assumedLanguage.`in`(languages))
            }

            if (artistID.isNotEmpty()) {
                criteria = criteria.and(album.artists.contains(Artist(artistID)))
            }

            return fetchRecordsFromRequest(em, album, criteria, album.popularity, offset, limit)
        }

        fun fetchArtists(
            em: EntityManager,
            offset: Long,
            limit: Long,
            searchTerm: String,
        ): PaginationWrapper<Artist> {
            val criteria = buildSearchTermPredicate(artist.name, searchTerm)

            return fetchRecordsFromRequest(em, artist, criteria, artist.popularity, offset, limit)
        }

        private fun <T> fetchRecordsFromRequest(
            em: EntityManager,
            from: EntityPathBase<T>,
            criteria: Predicate,
            orderBy: NumberPath<Int>,
            offset: Long,
            limit: Long
        ): PaginationWrapper<T> {
            val limit = min(limit, DEFAULT_ROW_LIMIT_PER_REQUEST).coerceAtLeast(MIN_ROWS_REQUIRED_TO_BE_REQUESTED)

            val count = JPAQuery<Long>(em).from(from).where(criteria).fetchCount()

            val records = if (count > 0 && offset < count)
                JPAQuery<T>(em).select(
                    from
                ).from(from).where(criteria).offset(offset).limit(limit).orderBy(orderBy.desc()).fetch()
            else mutableListOf()

            return PaginationWrapper(count, offset, limit, records)
        }

        private fun buildSearchTermPredicate(nameField: StringPath, searchTerm: String): BooleanExpression {
            return nameField.likeIgnoreCase("%${searchTerm.trim()}%")
        }

        fun fetchSingleAlbum(em: EntityManager, id: String): Album? {
            return fetchSingleRecordFromRequest(em, album, album.id.eq(id))
        }

        fun fetchSingleArtist(em: EntityManager, id: String): Artist? {
            return fetchSingleRecordFromRequest(em, artist, artist.id.eq(id))
        }

        private fun <T> fetchSingleRecordFromRequest(
            em: EntityManager,
            from: EntityPathBase<T>,
            criteria: Predicate,
        ): T? {
            return JPAQuery<T>(em).select(
                from
            ).from(from).where(criteria).fetchOne()
        }
    }
}

