# Loggerator APP
This Application has 2 major components 
1. LoggeratorListener for fetching logs from loggerator instance running on a specified host:port
2. Spring web API interface to lookup logs with provided filters

### Development
* clone git repo workspace directory of choice
* import in IDE of choice (IntelliJ preferred)
* Application builds a bootable jar using maven

### Build Environment
* mvn
* java17
* docker

### Scripts
1. init-containers.sh
Builds and Starts containers. Upon success, Application will be running at port forward http://localhost:8080
Supports loggerator service overrides COUNT for --count and SEED for --seed e.g. 
    ```bash
       COUNT=100 SEED=3 ./init-containers.sh
    ```
   * build project with maven
   * docker build image
   * docker compose spawns containers
2. destroy-containers.sh
   * docker compose destroy containers

### Deployment
Docker Image can be published to the artifactory of choice and then pulled with Containerized deployment utils like HELM
against kubernetes or amazon ECS clusters

#### Local
For Local development and testing, `docker compose` is used

1. docker-compose.yml
   * containers
     * db (cassandra)
     * source db keyspace and schema
     * loggerator (source)
     * loggerator-app (this application)
   * network: loggerator_network
    > Network is shared between all the container so that each container can resolve dns of others in the same network using container_name docker compose attribute
2. DockerFile
   * Using base ubuntu:20.04 image
   * Installs corretto-jdk17 as java runtime
   * Copies the maven generated Application JAR file to image

Run the Application with all necessary component containers
```shell
# in the project root
./init-containers.sh
# ctrl+c to exit
```

Cleanup
```shell
# in the project root
./destroy-containers.sh
```

### Database
Cassandra is the choice of the db since this is heavy write and tolerable yet speedy read requirement
cassandra provides compelling support for clustering key order_by and types like timestamp that is compatible with java datetime
Instants making it easy to ser/deser to/from db and sort. Huge advantage with cassandra is it supports
TTL which allows us to retain only desired period's data keeping our performance and data requirements
in check. Optimizations can be done to further introduce pagination on fetched records (preliminary 
support is already added). Unbounded queries are optimized using MaterializedView with primaryKey
on proto (transport i.e. HTTP/1.1) which does not change between the log data and thus provides
a mean to get sort order implemented at database layer (avoids application side sorting by timestamp 
overhead)
* Table: log (pk: user ...) with clustering order_by timestamp DESC.
* MatView: logByTS_mv (pk: proto ...) with clustering order_by timestamp DESC.

### Web Framework
SpringBoot: provides a lot of boilerplate features and implementations that simplifies application
development and clearly increased the focus on business logic making it quickly deliverable.
#### components
* spring-boot-starter-data-cassandra
    > provides seamless integrations with cassandra datastax deiver and spring-data framework
* spring-integration-ip
    > provides easy setup for a TCP socket connection listener while leveraging producer/consumer style interface to loggerator instance

### RESTAPI (Spring MVC)
* GET /log
  * auth: none
  * query parameters:
    1. user (username optional)
    2. code (http status optional)
    3. method (http method optional)
  * produces: application/json
  * response: [ "&lt;logLine&gt;" ... limit 1000]
    * ordered by Timestamps in descending order (latest first)

```shell
❯ curl 'http://localhost:8080/logs?user=garymitchell' | json_pp
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   194    0   194    0     0   2591      0 --:--:-- --:--:-- --:--:--  2939
[
   "33.15.199.187 - garymitchell [21/Jul/2000 08:23:15 +0000] \"POST /likes/218 HTTP/1.0\" 403 239",
   "33.15.199.187 - garymitchell [11/Jul/2000 06:29:40 +0000] \"GET /posts/174 HTTP/1.0\" 200 552"
]
```

### Code TestCoverage

| package  | class % | method % | line %  |
|----------| ------- |----------|---------|
| channel  | 100 %   | 100%     | 100%    |
| config   | 100 %   | 100%     | 100%    |
| data     | 100 %   | 100%     | 100%    |
| service  | 100 %   | 100%     | 93%     |
| web      | 100 %   | 100%     | 100%    |





