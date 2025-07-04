![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-valideringskomponent/workflows/CICD/badge.svg)
# klausuleret-tilskud-valideringskomponent

Template repository showing how to be a good Java Spring Boot citizen in a k8s cluster.

## A good citizen

Below is a set of recommendations for being a good service. The recommendations are not tied to a specific language or 
framework.

1. Configuration through environment variables.
2. Expose readiness endpoint
3. Expose endpoint that Prometheus can scrape
4. Be stateless
5. Support multiple instances
6. Always be in a releasable state
7. Automate build and deployment
8. Application log to stdout
9. Set a user in the docker file (non-root)
10. Readonly filesystem
11. Use least linux kernel capabilities

Some of above recommendations are heavily inspired by [https://12factor.net/](https://12factor.net/). It is recommended 
read [https://12factor.net/](https://12factor.net/) for more inspiration and further details. Some points go 
further than just being a good service and also touches areas like operations.

Point 9 to 11 are from [OWASP Docker Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Docker_Security_Cheat_Sheet.html)

## Building and testing

To run test with working debugging:\
`mvn clean install`

To run (same) tests against docker-compose:\
`mvn clean install -Pdocker-test`\
Debugging won't work because the service runs inside a container that is created on-demand by the tests.

## Endpoints

### Service

The service is listening for connections on port 8080.

Spring boot actuator is listening for connections on port 8081. This is used as prometheus scrape endpoint and health monitoring. 

Prometheus scrape endpoint: http://localhost:8081/actuator/prometheus  
Health URL that can be used for readiness probe: http://localhost:8081/actuator/health

### Documentation

Documentation of the API is build as a separate Docker image. Documentation is build using Swagger. The documentation 
image is post-fixed with `-documentation`. The file `compose/development/docker-compose.yml` contains a  setup 
that starts both the service and documentation image. The documentation can be accessed at http://localhost/test
and the service can be called through the Swagger UI. 

In the docker-compose setup is also an example on how to set custom endpoints for the Swagger documentation service.

## Dependency updates

Out of the box we use GitHub Actions as our CI/CD platform and that can also handle dependency updates. We utilize 
GitHubs [Dependabot](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuring-dependabot-version-updates) 
to create PR's with dependency updates. Further we have a job that automatically approves and merges dependencies. By 
default, it is only enabled in the template repository. You can enable this by removing ` && github.repository == 'KvalitetsIT/klausuleret-tilskud-valideringskomponent'`
from [dependabot-auto-merge.yml](.github/workflows/dependabot-auto-merge.yml). Before enabling it please consider below. 

- If no branch protection rule is configured dependency udpates that fails the automatic build and test will get merged. 
- You will not have a chance to review the changes in the dependency updates before it gets merged.  
- Enable auto-merge must be enabled in the repository.

## Configuration

| Environment variable          | Description                                                                                                                  | Required |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------|----------|
| JDBC_URL                      | JDBC connection URL                                                                                                          | Yes      |
| JDBC_USER                     | JDBC user                                                                                                                    | Yes      |
| JDBC_PASS                     | JDBC password                                                                                                                | Yes      |
| JDBC_CONNECTION_TEST_QUERY    | Query for testing the JDBC connection. Defaults to using the JDBC driver for validating connections.                         | No       |
| JDBC_CONNECTION_MAX_AGE       | Maximum amount of time (ISO 8601 Duration) a connection is allowed to be in the JDBC connection pool. Defaults to 30 minutes | No       |
| JDBC_CONNECTION_MAX_IDLE_TIME | Maximum amount of time (ISO 8601 Duration) a connection is allowed to sit idle in the JDBC connection pool                   | No       |
| LOG_LEVEL                     | Log Level for applikation  log. Defaults to INFO.                                                                            | No       |
| LOG_LEVEL_FRAMEWORK           | Log level for framework. Defaults to INFO.                                                                                   | No       |
| CORRELATION_ID                | HTTP header to take correlation id from. Used to correlate log messages. Defaults to "x-request-id".                         | No       |

The database connection pool is set up using HikariCP, and uses its default settings that are documented at https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#frequently-used. 
