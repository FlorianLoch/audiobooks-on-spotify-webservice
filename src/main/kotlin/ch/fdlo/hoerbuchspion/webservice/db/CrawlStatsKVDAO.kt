package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.CrawlStatsKV
import ch.fdlo.hoerbuchspion.webservice.data.QCrawlStatsKV
import com.querydsl.jpa.impl.JPAQuery
import io.jooby.Context
import javax.persistence.EntityManager

fun queryCrawlStats(ctx: Context) : MutableMap<CrawlStatsKV.KVKey, String> {
    val em = ctx.require(EntityManager::class.java)
    val map = mutableMapOf<CrawlStatsKV.KVKey, String>()

    JPAQuery<CrawlStatsKV>(em).from(QCrawlStatsKV.crawlStatsKV).fetch().forEach() {
        map[it.key!!] = it.value!!
    }

    return map
}