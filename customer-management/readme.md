# **Spring Boot CRUD Application with Java 17 and Unit Testing**

This project is a **Spring Boot application** built with **Java 17**, demonstrating **CRUD operations** for managing **Users and Vehicles**, along with **file upload (image storage)** and **unit testing with JUnit and Mockito**. This serves as a **test practice** for adapting to tools that will be used during the **6-month internship for Backend - IT Core Development at FIFGroup Astra**.

## **Entities & Relationships**
- **User**
  - Fields: `id`, `name`, `address`, `birthDate`, `ktpNumber`
- **Vehicle**
  - Fields: `id`, `licensePlate`, `registrationId`, `brand`, `model`, `vehicleImage`
- **Relationships**
  - **One-to-Many**: A **User** can have multiple **Vehicles**.
  - **Many-to-One**: Each **Vehicle** belongs to one **User**.

## **Features**
- **CRUD Operations**: Create, read, update, and delete Users and Vehicles.
- **File Upload**: Vehicles can have **one image** (JPEG, PNG, or WEBP) that can be uploaded, retrieved, updated, and deleted.
- **Testing**:
  - **Postman** for API testing.
  - **JUnit & Mockito** for automated unit testing.

## **Testing Tools**
- **Postman** for CRUD operation testing.
- **JUnit 5 & Mockito** for unit testing.
