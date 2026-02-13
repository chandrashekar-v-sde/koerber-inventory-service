# Inventory Service

Inventory Service manages product stock at batch level with expiry tracking.

---

# 1️⃣ Overview

Responsibilities:
- Maintain product inventory batches
- Return batches sorted by expiry date
- Reserve stock for orders
- Track reservation details

Tech Stack:
- Spring Boot
- Spring Data JPA
- H2 Database
- Liquibase
- Log4j2
- JUnit 5 & Mockito
- Factory Design Pattern

Runs on:
http://localhost:8081

---

# 2️⃣ Setup Instructions

## Prerequisites
- Java 17+
- Maven 3.9+

## Start Application

mvn clean install
mvn spring-boot:run

H2 Console:
http://localhost:8081/h2-console

JDBC URL:
jdbc:h2:mem:inventorydb

---

# 3️⃣ API Documentation

---------------------------------------
GET /inventory/{productId}
---------------------------------------

Description:
Returns inventory batches sorted by expiry date (FEFO).

Example Request:
GET http://localhost:8081/inventory/1002

Success Response:

{
"productId": 1002,
"productName": "Smartphone",
"batches": [
{
"batchId": 9,
"quantity": 29,
"expiryDate": "2026-05-31"
},
{
"batchId": 10,
"quantity": 83,
"expiryDate": "2026-11-15"
}
]
}

Error Response:

{
"status": 404,
"message": "Product not found"
}

---------------------------------------
POST /inventory/update
---------------------------------------

Description:
Reserves inventory for an order.

Request Body:

{
"orderId": 5012,
"productId": 1002,
"quantity": 3
}

Success Response:

{
"orderId": 5012,
"productId": 1002,
"productName": "Smartphone",
"quantity": 3,
"status": "PLACED",
"reservedFromBatchIds": [9],
"message": "Order placed. Inventory reserved"
}

Insufficient Inventory Response:

{
"status": 400,
"message": "Insufficient inventory available"
}

---------------------------------------
GET /inventory/reservation/{orderId}
---------------------------------------

Description:
Returns reserved batch IDs for given order.

Example:
GET http://localhost:8081/inventory/reservation/5012

Response:

[9]

If no reservation:

{
"status": 404,
"message": "No reservations found for orderId: 5012"
}
