# PostgreSQL PGXN and PL/Java with TestContainer Support

This repo contains a maven project able to:

- Build docker images that extend the official PostgreSQL with PGXNClient and PL/Java extensions
- Run TestContainer tests using those docker images
- Build a jar file that can be used by PL/Java
- Run TestContainer tests using the PL/java docker image + that jar (soon)

This repo does not (yet) support running [pgTAP](https://pgtap.org/) tests as part of the docker build.

## Configuration

The configuration properties are located in **configuration.properties**.

The upstream docker image is identified with typical values like

- postgresql.version - 15.3
- postgresql.major - 15 _(for now this needs to be explicitly specified)_

The generated docker images are specified with typical values like

- _EXT_.docker.target - `pgxnclient` or `pljava`
- _EXT_.docker.image.name - `postgresql-pgxn-docker/postgresql-{{ EXT }}`
- _EXT_.docker.image.tag - usually same as `postgresql.version` 

## Building the application software

Use maven as usual to build and test the application software. You can perform
integration testing using [Testcontainers](https://java.testcontainers.com).

## Building a custom PL/Java extension

Use maven as usual to build the PL/Java extension, but add a custom
[maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin) to
build the jar prior to running any integration tests.

**Note**: all of your DDL should be written in a
[SQL Deployment Descriptor](https://github.com/tada/pljava/wiki/Sql-deployment-descriptor).
This ensures that the extension and DDL are always in sync. The DDR will only be
triggered when the custom extension is added.

## Building the Docker images

To build the docker images run

```shell
$ mvn install -Pbuild-docker-images
```

This will build the docker images during the maven **pre-integration-test** phase.

## Installing PL/Java-ready jars

If you have built the jar elsewhere you have two options. The best approach will depend on
your specific needs.

### Initialization script approach

This approach is to create an installation script and append `withInitScript()` when you
create the PostgreSQL Container.

```java
    @Container
    protected MyPostgreSQLContainer db = new MyPostgreSQLContainer(myImage);
```

becomes

```java
    @Container
    protected MyPostgreSQLContainer db = new MyPostgreSQLContainer(myImage).withInitScript(initScriptPath);
```

where the initialization script looks something like this:

```sql
SELECT sqlj.install_jar(
  'file:'
  '/buildroot/pljava-examples/target/pljava-examples-1.6.4.jar',
  'examples', true);
SELECT sqlj.set_classpath('javatest', 'examples');
```

Note: you should perform all of your DDL via a
[SQL Deployment Descriptor](https://github.com/tada/pljava/wiki/Sql-deployment-descriptor)
instead of providing it in the initialization script. This ensures your jar will always contain
the appropriate DDL for the code.

### Modified Dockerfile approach

This approach modifies the existing Dockerfile to contain an additional 'target'. You
can use the relationship between the **pgxnclient** and **pljava** targets as a model
for the changes you should make to the Dockerfile, pom.xml, and container.properties files.

## Installing co-developed jars

You can also test PL/Java-aware extensions by triggering the
[maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin) during
the maven **pre-integration-test** step and then installing the jar as above.

**(Note: this has not been tested yet)**

## Testing

### Custom extension testing

Any custom extension should be tested using [pgTAP](https://pgtap.org/) since it's
fully aware of the server's environment.

Integration tests are still extremely useful as code examples.

### Application integration testing

Any software that uses PL/Java extension(s) can perform integration testing using
[TestContainers](https://java.testcontainers.com).

## Remaining Work

- Implement a simple extension that demonstrates basic functionality
- Build it with [maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin/) and use it in some integration tests
- Add documentation on how to use [pgTAP](https://pgtap.org/)
