# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.1/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.1/reference/web/servlet.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.1/reference/using/devtools.html)
* [Validation](https://docs.spring.io/spring-boot/3.4.1/reference/io/validation.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

1. Invalid or Missing Query Parameters

   Location: Controller
   Reason: Query parameters are part of the HTTP layer, so the controller should validate them and ensure they meet basic requirements (e.g., presence, correct types, and ranges).

2. Delivery Distance Exceeds Range

   Location: Service
   Reason: This is part of the business logic because the range is determined by the venue's dynamic data. The service can calculate the distance and validate it against the range.

3. Empty or Missing distance_ranges

   Location: Service
   Reason: The logic for handling and validating venue data is business-specific. The service should verify that the required fields from the API response are present and correct.

4. Small Cart Value (Zero or Negative Surcharge)

   Location: Service
   Reason: Calculating the small_order_surcharge is business logic. The controller should pass the cart value, and the service determines the surcharge.

5. Negative or Zero Delivery Fee

   Location: Service
   Reason: Calculating the delivery fee is business logic, as it depends on venue-specific data. The service should ensure it computes the fee correctly.

6. Venue Not Found

   Location: Controller
   Reason: If the Home Assignment API returns a 404, the controller can map this HTTP response to a 404 Not Found response in the DOPC API.

7. Unreachable Home Assignment API

   Location: Controller
   Reason: Network issues and external service availability are HTTP-related concerns. The controller should catch these exceptions and return appropriate HTTP responses.

8. Latitude and Longitude Out of Bounds

   Location: Controller
   Reason: Validating latitude and longitude ranges is input validation, which belongs in the controller.

9. Extremely High Cart Value

   Location: Controller
   Reason: Input validation for the cart value is an HTTP concern and should be checked in the controller.

10. Unexpected or Malformed API Response

    Location: Service
    Reason: Handling malformed or unexpected API responses requires validating the data used in business logic, so this belongs in the service.