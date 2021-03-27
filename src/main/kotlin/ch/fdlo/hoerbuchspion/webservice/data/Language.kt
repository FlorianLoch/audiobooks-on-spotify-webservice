package ch.fdlo.hoerbuchspion.webservice.data

enum class Language {
    DE, EN, FR, ES, IT, UNKNOWN;

    companion object {
        fun fromISO_639_1(lang: String): Language {
            return when (lang) {
                "de" -> DE
                "en" -> EN
                "es" -> ES
                "it" -> IT
                "fr" -> FR
                else -> UNKNOWN
            }
        }
    }
}