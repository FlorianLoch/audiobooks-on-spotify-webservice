package ch.fdlo.hoerbuchspion.webservice.db

import ch.fdlo.hoerbuchspion.webservice.data.CrawlStatsKV
import ch.fdlo.hoerbuchspion.webservice.data.QCrawlStatsKV
import com.querydsl.jpa.impl.JPAQuery
import javax.persistence.EntityManager

fun queryCrawlStats(em: EntityManager) : MutableMap<CrawlStatsKV.KVKey, String> {
    val map = mutableMapOf<CrawlStatsKV.KVKey, String>()

    JPAQuery<CrawlStatsKV>(em).from(QCrawlStatsKV.crawlStatsKV).fetch().forEach() {
        map[it.key!!] = it.value!!
    }

    return map
}