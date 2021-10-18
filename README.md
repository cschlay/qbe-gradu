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

## Connecting Neo4j Desktop

1. Run QBE CLI once so that database files get created
2. In _Neo4j Desktop_ Create a new project from directory
3. Click "Add" and select "Local DBMS". Use any password
4. Start the DBMS
5. Visualization tool _Neo4j Bloom_ can then be opened

## Troubleshooting

### Problem 1: `java.lang.LinkageError: Cannot to link java.nio.DirectByteBuffer`

Set CLI to use Java 11 e.g. in PowerShell

```
$env:JAVA_HOME = "C:\...\.jdks\corretto-11.0.12"
```

