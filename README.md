# Store management tool API

## Description
The Store Management API provides a backend solution for managing store operations, including products and orders.
It handles basic store needs such as adding, updating, finding, and 
deleting products, as well as creating orders with multiple items. 
This RESTful API is built using Spring Boot, leveraging the power of Spring Data JPA for database interactions and 
Hibernate for ORM.

## Features
* Product Management: Allows for adding new products,
updating existing ones (and patching them), finding products by ID, deleting products,
listing all products in the database, and retrieving all products having a price higher
than a given threshold and being in lower quantity than a specified limit.

* Order Management: Supports creating orders with multiple items, each linking to existing products 
by their IDs, and specifying the quantity of each product. Also supports deleting retrieving orders
by ID, deleting them, and deleting all orders in the database. Every order is associated with the user who placed it, 
ensuring that only the respective user can retrieve and delete their orders.

## API Endpoints
### Products
* POST /products/admin/add - Add a new product. Requires a JSON body with name, price, and quantity. Description can also be added,
but it is optional
* GET /products/public/{productId} - Retrieve a product by its ID.
* PUT /products/admin/{productId} - Update an existing product by its ID. Full
updated product must be provided as a JSON.
* PATCH /products/admin/{productId} - Update an existing product by its ID. Supports partial updates.
* DELETE /products/admin/{productId} - Delete a product by its ID.
* GET /products/public/all - Retrieve all products.
* GET /products/public/expensive-low-stock?minPrice=x&maxQuantity=y - Retrieve all products more expensive than "x"
and in lower quantity than "y".

### Orders
* POST /orders - Create a new order. Requires a JSON body specifying an array of items, 
with each item containing a productId and quantity. Request body example:
```json
{
  "items": [
    {
      "productId": 8,
      "quantity": 1
    },
    {
      "productId": 9,
      "quantity": 1
    }
  ]
}
```
* GET /orders - Retrieve all orders belonging to currently logged-in user.
* GET /orders/{orderId} - Retrieve order by its ID. 
Only applicable if the order belongs to the user.
* DELETE /orders/{orderId} - Delete order by its ID. 
Only applicable if the order belongs to the user.
* DELETE /orders/admin/all - Delete all orders in the database. Can only be
performed by the admin user.

## Details
### Prerequisites
* JDK 17
* Maven
* Spring Boot
* Project Lombok (version 1.18.32)
* An IDE of your choice (IntelliJ IDEA, Eclipse, etc.). For development, I used IntelliJ IDEA.
* Postman or any API testing tool

##  Running the Application
* Clone the repository to your local machine.
* Open the project in your IDE and wait for Maven to download the dependencies.
* Run the application by executing the main class StoreManagementApplication.java.
* The application starts on http://localhost:8080. Use Postman or a similar tool to interact with the API endpoints.

## Database Configuration
This project uses an H2 in-memory database for simplicity and quick setup. 
The database is auto-configured by Spring Boot and contains two tables, one for products and
the other for orders.

## Testing
Unit tests are provided under _src/test/java_. 
They can be run directly from the IDE or via Maven using the command **mvn test**.

For testing purposes, unit tests have been designed only for the ProductController. 
These tests focus on several critical aspects:

* Status Code Validation: They ensure that the correct HTTP status codes are returned for various operations.
* Function Call Verification: The tests confirm that specific functions are called the correct number of times, 
ensuring that the application logic behaves as expected.
* Access Control: They verify that only users with ADMIN privileges have the ability 
to perform delete and update operations on products.

## Security
Security configuration can be found in **WebSecurityConfig.java** class. Default user roles USER and ADMIN are predefined with basic authorities.

Basic authentication is configured for API access. Two users have been designated with the USER role, 
and one user has been granted ADMIN privileges.

For product management, operations such as removing and updating products are exclusively reserved for the user with ADMIN rights, 
ensuring a controlled and secure management environment.

For order management, only a user with ADMIN rights can delete all the orders in the database.
Furthermore, a user has access exclusively to the details of the orders they have placed, 
with no ability to view or access the order information of other users.

## Logging
For logging within the application, **logback-spring.xml** is utilized for configuration, 
ensuring comprehensive and efficient log management.

Throughout the application, logging is employed to capture key information, warnings, and debug messages (_logger.info_, _logger.warn_, _logger.debug_).

### Configuration Highlights
* Console Appender: Configured to output log messages to the console. The log pattern is set to display the date, thread name, log level, logger name, and message content.

* File Appender: Establishes a mechanism for writing log messages to a file, named **store-management-tool-log.txt**. This ensures that logs are not only visible in real-time but are also archived for historical analysis. 
The file appender uses a rolling policy based on time and size, 
creating a new log file each day (**logs/store-management.%d{dd-MM-yyyy}.log.zip**), 
with each file being up to 10MB in size.

* Asynchronous Appender: To enhance performance, especially under heavy logging conditions, an asynchronous appender wraps around the file appender. 
It buffers log messages in a queue (up to 500 messages) before writing them out, reducing the impact of logging on application performance.

* Root Logger: Set at the INFO level, ensuring that information, warnings, and errors are logged by default. 
Both the console and asynchronous file appenders are attached to the root logger.
