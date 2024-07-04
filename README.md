# fedex-aggregate-api

## Useful commands
When running your application locally on port 8080 you can use the following URL's out of the box. When
deployed on other machines you must adjust host and port accordingly.

### Health and other actuator info

http://localhost:8080/actuator/health 

To enable other actuator info you can read Spring Boot documentation for enabling the various other options.

### Api swagger (OpenApi 3.0)

http://localhost:8080/webjars/swagger-ui/index.html

For the specification:

http://localhost:8080/v3/api-docs (json)

http://localhost:8080/v3/api-docs.yaml (yaml)

## External runtime dependencies

The aggregate service connects to three different REST API's on the same endpoint. 
The default port is 8080. Get the Docker image here:

https://hub.docker.com/r/xyzassessment/backend-services

After getting the Docker image you can run it in your Docker environment on port 9090 as follows:
```
docker container run -p 9090:8080 xyzassessment/backend-services:latest
```
Now you can access (with example query's):

http://localhost:9090/shipments?q=109347263,123456891

http://localhost:9090/track?q=109347263,123456891

http://localhost:9090/pricing?q=NL,CN