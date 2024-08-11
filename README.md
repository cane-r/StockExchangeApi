# IngHubs assessment
Stock and exchange API assessment project

## Tech Stack
* Java17
* Spring Boot ( including devtools )
* Spring Web
* Spring Security(Basic Auth with Roles)
* Spring Test
* Maven
* Junit
* Mockito
* Jacoco Code Coverage
* H2/JPA
* Flyway
* Lombok
* Awaility (for asynchronous operations asserting/testing)
* Swagger for API docs

## How To Run
	mvn clean package spring-boot:run  

## Tests
    Run only tests : mvn clean test 
    
    With coverage and coverage report : mvn clean jacoco:prepare-agent test jacoco:report 
    
    Coverage report will be in /target/site/jacoco/index.html and as a rule of thumb,model and config classes are excluded from coverage report
    
    Coverage is %85,which is between 80-90 optimal range

##  API Docs
You can see the api docs link below :

* [Swagger Docs Link](http://localhost:8081/swagger-ui/index.html)

## Security 
    There is two types of user , which are admin and user.

    Users can only do HTTP GETs.Admins can do whatever they please(add,delete,update etc)

    So supply user:user or admin:admin HTTP Basic credentials while making requests.

    Security is disabled during tests,as security testing is not a concern here.(@MockUser annotation can be user for supplying mock users while testing)