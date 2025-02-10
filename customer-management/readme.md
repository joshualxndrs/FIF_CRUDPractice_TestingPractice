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

---

## **Features & Implemented Operations**
### **CRUD Operations**
- **Create** (`POST`)
  - Users and Vehicles can be created using API requests.
  - Vehicles are assigned to a specific user upon creation.
- **Read** (`GET`)
  - Retrieve all users or a specific user by ID.
  - Fetch all vehicles associated with a user or get details of a specific vehicle.
- **Update** (`PUT`)
  - Modify details of a user or vehicle.
  - Vehicle images can also be updated.
- **Delete** (`DELETE`)
  - Remove users and their associated vehicles.
  - Delete a specific vehicle without affecting the user.

### **File Upload & Management**
- Vehicles can have **one** associated image.
- **Allowed formats**: `JPEG`, `PNG`, `WEBP` (restricted to prevent invalid file uploads).
- Operations:
  - Upload a new image (`POST /upload-image`).
  - Retrieve an image (`GET /image`).
  - Update an image (`PUT /update-image`).
  - Delete an image (`DELETE /delete-image`).

### **Testing**
- **Postman** used for manual API testing.
- **JUnit 5 & Mockito** used for unit testing of:
  - **UserController**
  - **VehicleController**
  - **VehicleService**
- Tests included:
  - Checking valid and invalid data inputs.
  - Ensuring correct behavior for CRUD operations.
  - Simulating real-world scenarios where images are uploaded and retrieved.

---

## **What Went Well**
✔ Successfully integrated **Spring Boot with Java 17** for backend development.  
✔ CRUD operations for **Users and Vehicles** worked as expected.  
✔ **File upload & retrieval system** implemented with **strict validation** for allowed file types.  
✔ **Mockito and JUnit** were used to isolate and test **controllers & services** separately.  
✔ **Logging with Log4j** helped in debugging and tracking operations efficiently.  

---

## **Challenges & How They Were Solved**
**Issue with File Upload & Content-Type Detection**
- Problem: Some image files were not detected properly due to incorrect MIME types.
- Solution: Used `Files.probeContentType()` to dynamically detect MIME types before serving images.

**Mocking Dependencies in Unit Tests**
- Problem: Had difficulties **mocking services properly** in `VehicleControllerTest`.
- Solution: Used **`@Mock` and `@InjectMocks`** correctly to isolate dependencies.

**Testing Image Uploads in Unit Tests**
- Problem: Simulating **multipart file uploads** in tests was tricky.
- Solution: Used **MockMultipartFile** in **JUnit tests** to handle this scenario.

**Handling Missing Data in GET Requests**
- Problem: Some `GET` requests returned `null` when data was missing.
- Solution: Used **`Optional` with `.orElseThrow()`** and handled **404 errors** properly.

---

## **Key Learnings**
**Spring Boot CRUD operations** are the foundation for building RESTful APIs.  
🔹 **JUnit & Mockito** are essential for ensuring application reliability.  
🔹 **Postman testing** is crucial for verifying real API behavior before deployment.  
🔹 **Proper logging** (`Log4j`) helps debug issues efficiently.  
🔹 **File handling in Spring Boot** requires strict validation for security & consistency.  
