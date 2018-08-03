# Collections
This is an api wrapper on top of a [DOS Server](https://github.com/ekeilty17/GA4GH-DOS-Server), whose goal is to be more inuitive while still conforming to the GA4GH standard api. The topology of the api can be found in a swaggerhub specification [here](https://app.swaggerhub.com/apis/ekeilty/Collections/1.0.0#/).

## Run

First run a DOS Server on [port 8080](http://localhost:8080/) and make sure you turn off keycloak security (instructions on how to do that is found in the README.md in the DOS Server github repo). Then execute the following

```

mvn clean install
mvn clean spring-boot:run

```

The Collections wrapper will run on [port 5050](http://localhost:5050/)
