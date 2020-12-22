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

// TODO: Move to another package, this one does not really fit

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

fun <T> fetchFromRequest(
    ctx: Context,
    searchPredicateBuilder: (String) -> Predicate,
    from: EntityPath<T>
): PaginationWrapper<T> {
    val searchString = ctx.query("s").value("%")
    val offset = ctx.query("offset").longValue(0)
    val limit = min(ctx.query("limit").longValue(DEFAULT_ROW_LIMIT_PER_REQUEST), DEFAULT_ROW_LIMIT_PER_REQUEST)

//    // Get entity name of the referred class. When such annotations are used JPA implementation
//    // should stick to it and we can use the name in JPQL queries. If the annotation is not present then
//    // this method is not able to operate on this type.
//    val entityAnnotation = clazz.getAnnotation(Entity::class.java)
//    assert(entityAnnotation != null)
//    val entityName = entityAnnotation.name

    // TODO: Add count of total items
    // TODO: Wrap result in pagination object
    // TODO: Try to improve this with QueryDSL
    // TODO: Check whether matching is case sensitive
    // TODO: Wrap search term in wildcards to actually find something

    val em = ctx.require(EntityManager::class)

    val criteria = searchPredicateBuilder(searchString)
    val count = JPAQuery<Long>(em).from(from).where(criteria).fetchCount()

    val records = if (count > 0)
        JPAQuery<T>(em).select(
            from
        ).from(from).where(criteria).offset(offset).limit(limit).fetch()
    else mutableListOf()

    return PaginationWrapper(count, offset, limit, records)

//    val query = em.createQuery("FROM $entityName WHERE ", clazz)
//    val cBuilder = em.criteriaBuilder
//    val selectCQ = cBuilder.createQuery(clazz)
//    val root = selectCQ.from(clazz)
//    val root2 = root.join<Album, Artist>("Artist", JoinType.INNER)
//    val searchLiteral = cBuilder.literal("%$searchString%")
//    val criteria = cBuilder.like(cBuilder.lower(root2.get<String>("name")), cBuilder.lower(searchLiteral))
//    selectCQ.where(criteria)
//
//    val selectQ = em.createQuery(selectCQ)
//    selectQ.firstResult = offset
//    selectQ.maxResults = limit
//
//    val countCQ = cBuilder.createQuery(Long::class.java)
//    countCQ.select(cBuilder.count(countCQ.from(clazz)))
//    countCQ.where(criteria)
//    val countQ = em.createQuery(countCQ)
//
//    println(countQ.singleResult)
//
//
//
//    return selectQ.resultList
}

