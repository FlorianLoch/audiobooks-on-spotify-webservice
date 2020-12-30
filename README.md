# Audiobooks on Spotify
This is the webservice/frontend component, for the backend see the [crawler's repository](https://github.com/FlorianLoch/audiobooks-on-spotify-crawler).

The component is built with the awesome Jooby microframework, Hibernate, Jackson and QueryDSL.

## API
Three routes are available, 

### Albums a.k.a. audiobooks
```/albums```

### Artists a.k.a. authors
```/artists```

### Crawling metadata
```/stats```

## Running & building with Maven
This is a maven-based project, it can be run with ```mvn clean jobby:run``` and build with ```mvn clean package```.

