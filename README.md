# Devtools web server

<!-- TOC -->
* [About](#about)
* [Usage](#usage)
  * [Quick start](#quick-start)
  * [Documentation](#documentation)
  * [Swagger](#swagger)
  * [Generate encrypted text for jasypt](#generate-encrypted-text-for-jasypt)
* [Plugins](#plugins)
* [References](#references)
<!-- TOC -->

## About
- Web-based personal development tool 

### TODO
- Stabilization
- Licensing in an offline environment 
- Hot load/unload plugins(jar). aka, Jenkins

## Usage
> **Warning** Some own libraries are not provided

### Quick start
#### Docker
- Command line
```shell
docker run -d -p 8080:8080 -p 8081:8081 devkirin666/toys:devtools-latest
```

- docker-compose.yml (with default environment)
```yml
version: "3"
services:
  devtools:
    image: devkirin666/devtools
    ports:
      - "8080:8080" # admin web, api
      - "8081:8081" # for mock server
    environment:
      CONTEXT_PATH: "/"
      DATABASE_URL: "jdbc:h2:mem:devtool"
      DATABASE_USERNAME: "sa"
      DATABASE_PASSWORD: ""
      DATABASE_PLATFORM: "H2Dialect"
      DATASOURCE_MAX_POOL_SIZE: "10"
      LOGGING_ROOT_LEVEL: "warn"
      LOGGING_APP_LEVEL: "info"
      ENABLE_API_DOCS: "false"
      ENCRYPT_KEY_FILE: "classpath:default.key"
      ADMIN_USER: "admin"
      ADMIN_PASSWORD: "admin"
```


### Documentation
#### Repositories
- Github : [devkirin666/package-repo](https://github.com/devkirin666/package-repo)
- DockerHub: [devkirin666/devtools](https://hub.docker.com/repository/docker/devkirin666/devtools/general)

#### System Environment

| Variable                 | Default Value         | docker | Description                                                                                                                          |
|--------------------------|-----------------------|:------:|--------------------------------------------------------------------------------------------------------------------------------------|
| SERVER_PORT              | 8080                  |   X    | Tomcat service port (api, admin-web)                                                                                                 |
| MOCK_SERVER_PORT         | 8081                  |   X    | Mock request server port                                                                                                             |
| CONTEXT_PATH             | /                     |   O    | Servlet context Path                                                                                                                 |
| DATABASE_URL             | jdbc:h2:mem:devtool   |   O    | Database JDBC URL                                                                                                                    |
| DATABASE_USERNAME        | sa                    |   O    | Database user name                                                                                                                   |
| DATABASE_PASSWORD        |                       |   O    | Database user password                                                                                                               |
| DATABASE_PLATFORM        | H2Dialect             |   O    | Hibernate Dialect(name only) [JavaDoc](https://docs.jboss.org/hibernate/orm/5.6/javadocs/org/hibernate/dialect/package-summary.html) |
| DATASOURCE_MAX_POOL_SIZE | 10                    |   O    | Database connection pool size                                                                                                        |
| LOGGING_ROOT_LEVEL       | warn                  |   O    | Application root logging level                                                                                                       |
| LOGGING_APP_LEVEL        | info                  |   O    | Application logging level                                                                                                            |
| ENABLE_API_DOCS          | false                 |   O    | Enable Swagger api Documents                                                                                                         |
| ENCRYPT_KEY_FILE         | classpath:default.key |   O    | Encryption key file [About](https://www.baeldung.com/spring-boot-jasypt)                                                             |
| ADMIN_USER               | admin                 |   O    | Default administrator username                                                                                                       |
| ADMIN_PASSWORD           | admin                 |   O    | Default administrator password                                                                                                       |

### Swagger
- [http://localhost:[PORT]/docs/api/ui](http://localhost:8080/docs/api/ui)

### Generate encrypted text for jasypt
```shell
./gradlew --build-file ./encrypt.gradle -Dkey-file=[keyFile] -Dvalue=[TEXT]
```

## Plugins
### [Mock-Server](manual/mock-server/README.md)

## References
- [Standard REST API Error response](https://www.rfc-editor.org/rfc/rfc7807)

### JS Plugins
- AdminLTE 3.2.0
  - [Demo](https://ajaxorg.github.io/ace-api-docs/index.html)
  - [Documents](https://adminlte.io/docs/3.2/)
- Highlight-js
  - [Quick Start](https://highlightjs.org/usage/)
  - [Language & Themes](https://highlightjs.org/static/demo/)
  - [API Docs](https://highlightjs.readthedocs.io/en/latest/api.html)
- Ace Editor
  - [Quick Start](https://ace.c9.io/#nav=embedding)
  - [Simple usages](https://ace.c9.io/#nav=howto)
  - [API Docs](https://ajaxorg.github.io/ace-api-docs/index.html)
  - [Preview](https://ace.c9.io/build/kitchen-sink.html)
  - [Pre-Build](https://github.com/ajaxorg/ace-builds/tree/v1.15.1)
- Font-awesome v5
  - [Icons](https://fontawesome.com/v5/search?o=r)
- Flag Icons
  - [lipis/flag-icons](https://github.com/lipis/flag-icons)