# QBE for Graph Database

This repository contains a prototype of QBE query language for my Master's Thesis.
The implementation uses Java and Neo4j graph database.

The syntax is based on the original version [_Query by Example_](https://dl.acm.org/doi/10.1145/1499949.1500034) query language suggested by Moshé M. Zloof.

## Installation

_Java 11_ is needed as [_Neo4j embedded_](https://neo4j.com/docs/java-reference/current/java-embedded/) does not support other versions.
The project was developed using Amazon Corretto.

```shell
# Install the dependencies
mvn dependecy:resolve

# Compile the code
mvn compile

# Run tests
mvn test

# Run the interpreter
mvn exec:java

# Seed the database with example graph
seed

# Run queries
query
```

## Example Queries

Here are some example queries you can run after the test database is seeded.

```
| Course | id* | name* |
|--------+-----+-------|
| QUERY  |     |       |
```

```
| Course | id* | name*                |
|--------+-----+----------------------|
| QUERY  |     | /Introduction to .*/ |
```

```
| recommends          | id* | period* |
|---------------------+-----+---------|
| QUERY Course.Course |     |         |
```

```
| contains           | contains         | Topic | name* |
|--------------------+------------------+-------+-------|
| QUERY Course.Topic | QUERY Book.Topic | QUERY |       |
```

```
| Course | name* | contains           | primary as has_primary_topic* |
|--------+-------+--------------------+-------------------------------|
| QUERY  |       | QUERY Course.Topic |                               | 
```

```
| Book  | title* | year                 |
|-------+--------+----------------------|
| QUERY |        | > 2000 |
```

```
| Book           |
|----------------|
| COUNT AS books |
```

## Troubleshooting

### Problem 1: Invalid Java version

```
java.lang.LinkageError: Cannot to link java.nio.DirectByteBuffer`
```

You are likely using wrong Java version. In PowerShell, change the version:

```ps1
$env:JAVA_HOME = "C:\...\.jdks\corretto-11.0.12"
```
