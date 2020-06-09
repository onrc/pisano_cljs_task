# Pisano ClojureScript Task

## Run Project

This project uses docker-compose. To run project;

```
docker-compose up -d --build
```

To setup database;

```
docker-compose exec api rails:db:setup
```

Then visit `http://localhost:3001`.

## Run Tests

For api tests use;
```
docker-compose exec api rspec
```

For app tests;
```
lein karma
```
