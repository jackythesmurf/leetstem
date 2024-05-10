# backend
Springboot backend server of LeetSTEM.

## Getting Started

### Dependencies
#### System
* Any distribution of JDK 17.
* For a list of dependent libraries, refer to `/leetstem/pom.xml`

#### External APIs
* Profanity checking: [PurgoMalum](https://www.purgomalum.com/)

### Executing
```sh
cd leetstem
./mvnw spring-boot:run
```
This will take care of dependency downloading and backend server booting. It may take a short while if you haven't run this before.

The server is up and running when you have something similar to the following log line:
```
Started LeetstemApplication in 12.123 seconds (process running for 12.591)
```

The server runs on port `5000`.

### Database
#### Remote DB (default)
This app uses an Amazon AWS RDS instance as the database. To view the database's metadata and usage info,
1. [Click here](https://939094703787.signin.aws.amazon.com/console) to open the AWS sign-in page
2. Login with username `elec5619-dev` and password `wnKS3X$%`

#### Local DB
To use a locally hosted DB,
1. Create an empty database schema
2. Use `/leetstem-db/init.sql` to create/reset all tables used by the app
3. In `/leetstem/src/main/resources/application.properties`, replace `spring.datasource.*` with local connection configs
