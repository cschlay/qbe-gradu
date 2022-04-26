# QBE for Graph Database

QBE query language for my Master's Thesis.

## Installation

You must use _Java 11_ as Neo4j embedded doesn't allow other versions.
The project is developed using Amazon Corretto.

```shell
# Install the dependencies
mvn dependecy:resolve

# Compile the code
mvn compile

# Run the interpreter
mvn exec:java
```
## Troubleshooting

### Problem 1: `java.lang.LinkageError: Cannot to link java.nio.DirectByteBuffer`

Set CLI to use Java 11 e.g. in PowerShell

```
$env:JAVA_HOME = "C:\...\.jdks\corretto-11.0.12"
```

## Example Queries

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
