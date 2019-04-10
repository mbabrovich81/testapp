# Query Performance Problem
Check Performance Query

Task Definition
=================

In company ABC we run quite a few queries against our databases, and the queries have different performance characteristics. We frequently need to work on optimizing them. We want to build a system that allow us to compare different versions of the same query and be able to benchmark the performance of its versions.

Build a system to perform this benchmarking with the following characteristics:

- it is a restful service built with Spring (and optionally Spring Boot)
- it can execute a performance test
- it measures the time for each query to complete (work time)
- exactly one of the performance tests can execute at any point of time against a database installation.
- tests against different database installations can execute in parallel. So if a user starts a test for query Q, this test can execute in parallel against databases A,B,C and collect the results in a "report"

Define a data model, define the rest api and write the code for the service.

Build, run
=================

build service: `./mvn clean install`

run service: `./mvn spring-boot:run`

*NOTES*: 
- Java 8
- Maven >=3.6.0
- PostgreSQL 9.6

API Documentation
=================

- Start to test for query 'SQL_query':

[../query/performance/check?q=SQL_query](http://localhost:8080/query/performance/check?q='select 1')

The result of the test is the JSON with the report UID.

- Getting a report on the result of the test by report UID:

[../query/performance/report?r=uid](http://localhost:8080/query/performance/report?r='299PS5LEAA')

The the result is a JSON with test data

Developer Notes
=================

Need to create `quartz` database and create tables by file `quartz_tables.sql`.

`application.properties` has control values:

*turvo.preload.database=false*

- FALSE (default value). Do not create and do not fill the table

- TRUE. Create table  `tbl_test_performance_report` in `turvotest_stats` database. Create tables `tbl_test_task` in `turvotest`, `turvotest2`, `turvotest3` databases. After that these table filling by default values 

---
*turvo.quartz.jobs.remove=true*

- TRUE (default value). Remove jobs of scheduler which started before after restart server.

- FALSE. Don't remove jobs of scheduler. Scheduler continues to execute jobs after restart server.

