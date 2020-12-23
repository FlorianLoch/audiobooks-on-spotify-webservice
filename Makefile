DB_NAME="JOOBY_H2_TEST_DB"

integration-test:
	@# We need no persistence as this is run for testing only. Assigning a random port does not work as it takes
	@# some until the container is running and before that the port cannot be fetched (a stale one might be fetched, causing issues).
	@# Running it in the background (-d)
	docker container rm --force $(DB_NAME); true
	docker run -p 127.0.0.1:1521:1521 -p 127.0.0.1:81:81 -d -e H2_OPTIONS="-ifNotExists" --name=$(DB_NAME) oscarfonts/h2:alpine
	mvn test