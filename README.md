# README for BooknPlay Microservices

#### Overview

BooknPlay is a microservices-based system for turf discovery, booking, and payments. It uses an API Gateway for a unified entry point and Eureka for service discovery. JWT-based security is applied across services, with tokens issued by the User Service and validated by downstream services.

* API Gateway (Spring Cloud Gateway)
* Eureka Server (Service discovery)
* User Service (registration, login, profile, roles)
* Turf Service (turf management, owner validation)
* Booking Service (slots and booking )
* Payment Service (payments)
* Notification Service (notifications)

#### Tech Stack

* Java 17, Spring Boot 3.5.x
* Spring Cloud 2025.0.x (Gateway, Netflix Eureka, OpenFeign)
* Spring Security, JWT (JJWT and NimbusJwtDecoder)
* JPA/Hibernate, MySQL
* OpenAPI/Swagger (springdoc)

#### Ports and Service IDs

* Eureka Server: 8761 (spring.application.name=EUREKA-SERVER)
* API Gateway: 8080 (spring.application.name=API-GATEWAY)
* User Service: 8081 (spring.application.name=USER-SERVICE)
* Booking Service: 8082 (spring.application.name=BOOKING-SERVICE)
* Payment Service: 8083 (spring.application.name=PAYMENT-SERVICE) [placeholder]
* Notification Service: 8084 (spring.application.name=NOTIFICATION-SERVICE) [placeholder]
* Turf Service: 8085 (spring.application.name=TURF-SERVICE)

Note: Clients only call the gateway on 8080.

#### Security Model


* User Service issues JWTs with roles claim using a shared symmetric key:
* Turf and Booking are JWT resource servers and validate tokens with the same key.
* Authorization header is propagated across Feign clients.
* Role conventions: ROLE_USER, ROLE_OWNER, ROLE_ADMIN.

### Running Locally

#### Prerequisites

* Java 17, Maven 3.9+
* MySQL (schemas created on first run with ddl-auto=update)
* Optional: Docker for DB
* Start Order
* Start Eureka Server
* Start API Gateway
* Start User, Turf, Booking, Payment, Notification services
* Verify
* Eureka Dashboard: http://localhost:8761
* Gateway health (if enabled): http://localhost:8080/actuator/health
* Swagger per service:
* http://localhost:<service-port>/swagger-ui.html

#### API Gateway Routing (example)

* USER-SERVICE: /api/users/, /api/test/
* TURF-SERVICE: /api/turfs/**
* BOOKING-SERVICE: /api/bookings/, /api/bookings/slots/
* PAYMENT-SERVICE: /api/payments/** [placeholder]
* NOTIFICATION-SERVICE: /api/notifications/** [placeholder]

### Service APIs (through Gateway)

#### User Service

Purpose: Registration, login, profile, roles, password updates.

* POST /api/users/register
* POST /api/users/login
* GET /api/users/me
* GET /api/users/{id}
* GET /api/users/email/{email}
* PUT /api/users/me
* PUT /api/users/me/password
* DELETE /api/users/me

Common responses

* 400 validations (DTO)
* 401 unauthenticated
* 403 forbidden for privileged role assignment without owner
* 404 user not found
* 409 duplicate email

Example calls

Register: POST /api/users/register { name, email, password, phone?, roles? }
Login: POST /api/users/login { email, password } -> returns token string

### Turf Service

Purpose: Manage turfs and owner-validated operations.

* POST /api/turfs
* GET /api/turfs/{id}
* PUT /api/turfs/{turfId}
* DELETE /api/turfs/{turfId}
* GET /api/turfs
* GET /api/turfs/me

##### Notes

Owner validated by calling User Service via Feign to ensure ROLE_OWNER.
DTOs include Address and a list of sport options {sportType, isIndoor}.

### Booking Service

Purpose: Slot creation and bookings.

* POST /api/bookings
* GET /api/bookings/{bookingId}
* GET /api/bookings/user/{userId}
* GET /api/bookings
* POST /api/bookings/slots


### Payment Service 

Purpose: Booking payments.
Typical endpoints

* POST /api/payments
* GET /api/payments/{id}
* GET /api/payments?bookingId=&userId=&status=
* POST /api/payments/{id}/capture
* POST /api/payments/{id}/refund

### Notification Service

Purpose: In-app notifications on booking/payment events.

Typical endpoints
* POST /api/notifications/booking-success
* POST /api/notifications/payment-success
* GET /api/notifications/me
* GET /api/notifications/owner
* GET /api/notifications

### Inter-Service Communication

* Turf and Booking use OpenFeign clients and propagate Authorization header.
* Typical patterns:
* Turf -> User: validate owner role, fetch by email/id.
* Booking -> Turf: fetch turf availability and price; -> User: user context.

### Error Handling

* User: Structured ApiError { timestamp, status, error, message, path }
* Turf: Structured ApiError with 403/400/500
* Booking: Map-based error payload for 400/500
* Suggestion: Normalize to a common ApiError across services.

### Example End-to-End Flow

* Register and login (User):
* POST /api/users/register -> 200
* POST /api/users/login -> 200 (token string)
* Owner creates turf and slots:
* POST /api/turfs (ROLE_OWNER) -> 200
* POST /api/bookings/slots (ROLE_OWNER) -> 200 list of slots
* User books:
* POST /api/bookings with { userId, turfId, slotIds } -> 201 booking
* Payment:
* POST /api/payments -> 201 payment record [finalize with your implementation]
* Notifications:
* POST /api/notifications/payment-success -> 200 [finalize with your implementation]
* GET /api/notifications/me -> 200 list