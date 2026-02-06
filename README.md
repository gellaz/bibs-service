<h1 align="center">BIBS Service</h1>

<div align="center">

![Java Badge](https://img.shields.io/badge/Java-21-555?logo=openjdk&logoColor=white&labelColor=blue&style=plastic)
![Spring Boot Badge](https://img.shields.io/badge/Spring%20Boot-4-555?logo=springboot&logoColor=fff&labelColor=6DB33F&style=plastic)
![Apache Maven Badge](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=fff&style=plastic)
![Hibernate Badge](https://img.shields.io/badge/Hibernate-59666C?logo=hibernate&logoColor=fff&style=plastic)
![Liquibase Badge](https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=fff&style=plastic)
![JUnit5 Badge](https://img.shields.io/badge/JUnit5-25A162?logo=junit5&logoColor=fff&style=plastic)
![OpenAPI Initiative Badge](https://img.shields.io/badge/OpenAPI-3.1-555?logo=openapiinitiative&logoColor=fff&labelColor=6BA539&style=plastic)
![Swagger Badge](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=000&style=plastic)
<br/>
![PostgreSQL Badge](https://img.shields.io/badge/PostgreSQL-18-555?logo=postgresql&logoColor=fff&labelColor=4169E1&style=plastic)
![MinIO Badge](https://img.shields.io/badge/MinIO-C72E49?logo=minio&logoColor=fff&style=plastic)
<br/>
![GitHub Actions Badge](https://img.shields.io/badge/GitHub%20Actions-000?logo=githubactions&logoColor=fff&style=plastic)
![Docker Badge](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff&style=plastic)

</div>

---

## Overview

This is the backend service for the BIBS e-commerce platform.

## Getting started

Before starting the application, make sure you have:

- Java 21 installed. You can use [SDKMAN!](https://sdkman.io/) to install it.
- [Docker](https://www.docker.com/get-started/) installed.

To get started:

1. Clone the repository
   ```shell
   git clone https://github.com/gellaz/bibs-service.git
   ```
2. During development it is recommended to use the profile `local`. In IntelliJ `-Dspring.profiles.active=local` can be
   added in the VM options of the Run Configuration after enabling this property in "Modify options". Create your own
   `application-local.yml` file to override settings for development.
3. Run the application with `local` profile using IntelliJ or Maven
   ```shell
   mvnw spring-boot:run -Dspring.profiles.active=local
   ```

When the application is running you can access the following endpoints:

- Service http://localhost:8080
- OpenAPI documentation http://localhost:8080/api-docs
- Swagger UI http://localhost:8080/swagger-ui
- Keycloak Admin Console http://localhost:8085 (username: `pgadmin`, password: `P4ssword!`)
- MinIO Admin Console http://localhost:9001 (username: `minioadmin`, password: `P4ssword!`)

Keycloak is configured to use the `bibs` realm. There are four pre-created users:

| User          | Email                 | Password  | IAM Role | App Role      |
|---------------|-----------------------|-----------|----------|---------------|
| Admin         | admin@bibs.it         | P4ssword! | ADMIN    | ADMIN         |
| User          | user@bibs.it          | P4ssword! | USER     | CUSTOMER      |
| Store Owner   | store.owner@bibs.it   | P4ssword! | USER     | STORE_OWNER   |
| Store Manager | store.manager@bibs.it | P4ssword! | USER     | STORE_MANAGER |
| Store Clerk   | store.clerk@bibs.it   | P4ssword! | USER     | STORE_CLERK   |

## Build

The application can be built using the following command:

```shell
mvnw clean package
```

Start your application with the following command - here with the profile `production`:

```shell
java -Dspring.profiles.active=production -jar ./target/bibs-service-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=production` as
environment variable when running the container.

```shell
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=it.bibs/bibs-service
```

## Auth

In the context of OAuth, _resource server_ means that our application provides a resource - our REST API. The user has
already been identified by the authorization server, so that the client possesses a token that is sent along to the
resource server. Our application therefore no longer has to issue a new token, but only validates the token provided. In
simplified form, the process looks as follows:

![Accessing the resource server with a token](auth.png "Auth Flow")

This approach makes a lot of sense with an SPA (Single Page Application) like React: the client authenticates itself
directly with Keycloak via OAuth, and our Spring Boot application is then only provided with the final token in the
`Authorization: Bearer ...` header. The validity of the token is then checked directly with Keycloak.

### Kecloak Realm Export

```shell
docker exec -u root -it bibs-keycloak sh -c "rm -rf /opt/keycloak/bin/keycloak-realm-export.json"
docker exec -u root -it bibs-keycloak sh -c "./opt/keycloak/bin/kc.sh export --file=keycloak-realm-export.json --realm=bibs --optimized"
docker cp bibs-keycloak:keycloak-realm-export.json ./keycloak-realm-export.json
```

## References

* [Building a Location-Based REST API with OpenStreetMap, PostGIS, and Spring Boot](https://www.antanaskovic.com/en/blog/building-location-based-rest-api-with-osm-postgis-and-spring-boot)
* [Maven docs](https://maven.apache.org/guides/index.html)
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
