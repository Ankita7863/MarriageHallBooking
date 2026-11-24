# Marriage Hall Booking Management System

Spring Boot 3.3.5, Java 17, Spring Web, Spring Data JPA, Validation, and an embedded H2 file database.

## Import into Eclipse

1. Extract `marriage-hall-booking-fixed.zip` to a normal folder.
2. In Eclipse, choose **File > Import > Maven > Existing Maven Projects**.
3. For **Root Directory**, choose the extracted `marriage-hall-booking` folder — the folder that directly contains `pom.xml`.
4. Ensure `pom.xml` appears in the Projects list, select it, and click **Finish**.
5. Wait for Maven dependency downloads to finish. If prompted, choose **Maven > Update Project**.
6. In Package Explorer, expand `src/main/java`. The base package is `com.example.hallbooking`.
7. Open `MarriageHallApplication.java` and choose **Run As > Java Application** or **Run As > Spring Boot App**.

If Eclipse shows a collapsed package such as `com.example.hallbooking`, that is normal. Expand it using the small arrow beside the package.

## Run from a terminal

Requires JDK 17+ and Maven installed:

```bash
mvn spring-boot:run
```

## URLs

- Web interface: http://localhost:8080/
- REST API: http://localhost:8080/api/v1/hall-bookings
- H2 console: http://localhost:8080/h2-console

H2 console JDBC settings:

- JDBC URL: `jdbc:h2:file:./data/hallbooking`
- User Name: `sa`
- Password: leave blank

No MySQL installation or MySQL password is needed for this version.
