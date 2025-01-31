# Practitioner Service

## Overview
The **Practitioner Service** manages healthcare professionals, including doctors and staff.

## Features
- CRUD operations for doctors, nurses, and medical staff
- Role-based authorization
- Integration with patient encounters

## Technologies Used
- **Spring Boot**
- **JPA & Hibernate**
- **PostgreSQL/MySQL**
- **Docker & Kubernetes**

## Installation & Setup
```sh
git clone https://github.com/yourusername/practitioner-service.git
cd practitioner-service
mvn clean install
docker build -t practitioner-service .
docker run -p 8083:8083 practitioner-service
Other Services
Works with Patient Service to manage practitioner-patient interactions.
```

## Other Services
- Works with Patient Service to manage practitioner-patient interactions.
- Works with User Service to manage practitioner creation
