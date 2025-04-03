# Delivery Order Price Calculator (DOPC)

## Overview

The **Delivery Order Price Calculator (DOPC)** is a backend service designed to calculate the total price and price breakdown of a delivery order.

---

## Features

- **Endpoint**: Provides a RESTful API endpoint at `/api/v1/delivery-order-price`.
- **Query Parameters**:
    - `venue_slug`: Unique identifier of the venue (required).
    - `cart_value`: Total value of the shopping cart in cents (required).
    - `user_lat`: Latitude of the user's location (required).
    - `user_lon`: Longitude of the user's location (required).
- **Response**: Returns the total price, small order surcharge, delivery fee, and delivery distance in JSON format.
- **Integration**: Fetches venue-specific data from the provided **Home Assignment API** to calculate delivery details.

---

## Technologies Used

- **Programming Language**: Kotlin
- **Framework**: Spring Boot
- **Testing**:
    - Unit Tests: JUnit 5, MockK
    - End-to-End Tests: Spring Boot Test with `TestRestTemplate`
- **Build Tool**: Gradle
- **External APIs**: Wolt Home Assignment API

---

## Installation and Running Instructions

### Prerequisites

Ensure the following are installed on your system:
- **JDK 17 or higher:** The project requires a compatible Java Development Kit to run.
- The project uses the Gradle Wrapper (`gradlew`).

### Building the Application
1. Make the Gradle Wrapper executable:
   ```bash
   chmod +x gradlew
   ```
2. Build the project:
   ```bash
   ./gradlew build
   ```

### Running the Application
1. Start the application:
   ```bash
   ./gradlew bootRun
   ```
2. The application will start at `http://localhost:8000`.
3. Access the API:
    - Open a web browser or use a REST client like Postman.
    - Send a GET request to `http://localhost:8000/api/v1/delivery-order-price` with the required query parameters.
    - Example Terminal Command: 
        ```bash
        curl "http://localhost:8000/api/v1/delivery-order-price?venue_slug=home-assignment-venue-helsinki&cart_value=1000&user_lat=60.17094&user_lon=24.93087"
        ```

### Running Tests
1. Run all tests:
    ```bash
    ./gradlew test
    ```
2. View test reports:
    - Reports are generated in the `build/reports/tests/test/index.html.` directory.

## Design and Code Quality

### Architectural Choices
- **Controller-Service Separation:** Business logic is encapsulated in services to ensure the controller remains lightweight.
- **Modular Design:** Each responsibility is handled by a dedicated module, ensuring maintainability.
- **Error Handling:** Comprehensive error handling for bad requests, missing parameters, and external API failures.

## Additional Edge Cases

### 1. Duplicate Distance Ranges in `distance_ranges`
- **Description**: Overlapping or duplicate ranges in `distance_ranges` can lead to ambiguous fee calculations.
- **Resolution**: Implement validation logic to ensure ranges are properly defined and non-overlapping.

### 2. Invalid Coordinates (Out of Range)
- **Description**: `user_lat` and `user_lon` may exceed acceptable bounds (e.g., `latitude > 90` or `longitude > 180`).
- **Resolution**: Validate these parameters and return a `400 Bad Request` with appropriate error messages.

### 3. Invalid `distance_ranges` Values
- **Description**: If `min` is greater than `max` or `b` is negative in `distance_ranges`, the input from the Home Assignment API is invalid.
- **Resolution**: Validate the received data and return a `502 Bad Gateway` with a descriptive error message.

### 4. Empty or Missing `distance_ranges`
- **Description**: If the Home Assignment API returns an empty `distance_ranges` or the field is missing, delivery fee calculation is impossible.
- **Resolution**: Return a `502 Bad Gateway` indicating an issue with the upstream service.

### 5. Extremely Large or Small `cart_value`
- **Description**: Extremely large (e.g., `Integer.MAX_VALUE`) or zero `cart_value` may lead to unexpected results.
- **Resolution**: Enforce an upper limit for `cart_value` (e.g., `1,000,000`) and ensure it is strictly greater than zero.

### 6. API Timeout or Failure
- **Description**: The Home Assignment API may fail to respond or take too long to respond.
- **Resolution**: Set a timeout for API calls and return a `503 Service Unavailable` error if the timeout is exceeded.

### 7. Invalid `venue_slug`
- **Description**: An invalid `venue_slug` may result in a `404 Not Found` response from the Home Assignment API.
- **Resolution**: Return a `404 Not Found` with a user-friendly error message.


## Developer Notes

- **Spring WebFlux Consideration**:
    - The current implementation uses Spring MVC, which works well for this use case.
    - However, if this service were to handle a high volume of concurrent requests (e.g., thousands of simultaneous calls), **Spring WebFlux** could be a better choice due to its non-blocking, reactive nature.


- **Why Stick to MVC for Now**:
    - Simplicity: Spring MVC is easier to understand and debug for synchronous, REST-based workflows.
    - Maintenance: MVC aligns with most teams' familiarity, reducing the overhead for onboarding and codebase understanding.


- **Future Improvements**:
    - If scaling becomes a priority, transitioning to WebFlux or introducing asynchronous processing for API calls could enhance performance.
