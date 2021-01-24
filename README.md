# Audiobooks on Spotify - Web App
Spotify is great and has a lot to offer when it comes to audiobooks - at least content-wise. Function-wise audiobooks are treated as "normal" music and therefore functionality like pause and resume later (see [Cassette](https://github.com/FlorianLoch/Cassette), another project of mine utilizing the Spotify Web API) and browsing them in a seperate view suiting audiobooks better is not provided.

This projects aims at providing users a better place to find audiobooks they want to listen to with their Spotify subscription.

This is the webservice/frontend component, for the backend see the [crawler's repository](https://github.com/FlorianLoch/audiobooks-on-spotify-crawler).
It serves a REST API supporting pagination and filtering. The service is written in Kotlin, utilizing the awesome Jooby microframework, Hibernate, Jackson and QueryDSL. Persistence used is an H2 database.
The served frontend uses Vue.js and the Bulma CSS framework.

## Current state
The project is still under active development and not yet ready to be used. Hopefully it will be soon.

## API
Three main endpoints are available. More information about them can be retrieved via the OpenAPI definition respectively the Swagger UI at ```/swagger```.

### Albums a.k.a. audiobooks
```/api/albums```

### Artists a.k.a. authors
```/api/artists```

### Crawling metadata
```/api/stats```

## Running & building with Maven
This is a maven-based project, it can be run with ```mvn clean jobby:run``` and build with ```mvn clean package```.

## Disclaimer
The authors of this project are not related to Spotify in any way beside being happy users of their platform. This service is not related to Spotify except using their API and content.