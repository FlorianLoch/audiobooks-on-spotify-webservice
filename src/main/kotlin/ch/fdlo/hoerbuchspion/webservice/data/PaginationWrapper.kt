package ch.fdlo.hoerbuchspion.webservice.data

data class PaginationWrapper<T>(val total: Long, val offset: Long, val limit: Long, val items: List<T>)