package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.*
import com.querydsl.jpa.impl.JPAQuery
import io.jooby.Context
import javax.persistence.EntityManager

import ch.fdlo.hoerbuchspion.webservice.data.QAlbum.album
import ch.fdlo.hoerbuchspion.webservice.data.QArtist.artist
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
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
            languages: Set<AlbumDetails.Language>
        ): PaginationWrapper<Album> {
            var criteria = buildSearchTermPredicate(album.name, searchTerm)

            if (unabridgedOnly) {
                criteria = criteria.and(album.storyType.eq(Album.StoryType.UNABRIDGED))
            }

            if (languages.isEmpty().not()) {
                criteria = criteria.and(album.albumDetails.assumedLanguage.`in`(languages))
            }

            return fetchFromRequest(em, album, criteria, offset, limit)
        }

        fun fetchArtists(
            em: EntityManager,
            offset: Long,
            limit: Long,
            searchTerm: String,
        ): PaginationWrapper<Artist> {
            val criteria = buildSearchTermPredicate(artist.name, searchTerm)

            return fetchFromRequest(em, artist, criteria, offset, limit)
        }

        private fun <T> fetchFromRequest(
            em: EntityManager,
            from: EntityPathBase<T>,
            criteria: Predicate,
            offset: Long,
            limit: Long
        ): PaginationWrapper<T> {
            val limit = min(limit, DEFAULT_ROW_LIMIT_PER_REQUEST).coerceAtLeast(MIN_ROWS_REQUIRED_TO_BE_REQUESTED)

            val count = JPAQuery<Long>(em).from(from).where(criteria).fetchCount()

            val records = if (count > 0 && offset < count)
                JPAQuery<T>(em).select(
                    from
                ).from(from).where(criteria).offset(offset).limit(limit).fetch()
            else mutableListOf()

            return PaginationWrapper(count, offset, limit, records)
        }

        private fun buildSearchTermPredicate(nameField: StringPath, searchTerm: String): BooleanExpression {
            return nameField.likeIgnoreCase("%${searchTerm.trim()}%")
        }

        private fun toLanguageSet(stringSet: Set<String>): Set<AlbumDetails.Language> {
            val langSet = mutableSetOf<AlbumDetails.Language>()

            for (langStr in stringSet) {
                val lang = AlbumDetails.Language.fromISO_639_1(langStr.toLowerCase())

                if (lang == AlbumDetails.Language.UNKNOWN) {
                    throw IllegalArgumentException("'$langStr' is not an ISO 639-1 identifier or does not denote a supported language.")
                }

                langSet.add(lang)
            }

            return langSet
        }
    }
}

