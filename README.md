# OnlyBooks
# Project Description
The **OnlyBooks** is a web application developed using Spring Boot and Java 17, enabling users to efficiently search for books using multiple criteria. The application is powered by a MySQL database for storing and managing book, user, and order data.

To get started, users need to register, after which they can authenticate and begin browsing books. The app allows users to add books to their cart, place orders, view past orders, and manage cart items.

## Problem Description
The 'OnlyBooks' addresses the following challenges that users often face:
- **Limited Access to Books:** Traditional bookstores have a restricted selection, and may not offer rare or niche editions.
- **Difficulty Finding Books:** Searching through large catalogs without effective filtering options can be overwhelming and time-consuming.
- **Lack of Personalization:** Without tailored recommendations or advanced search filters, users might struggle to find books that meet their needs or interests.
## How 'OnlyBooks' Solves the Problem:
The 'OnlyBooks' provides a wide selection of books from various genres and categories, offering a smooth browsing experience.
### Key features include:
## Powerful Search & Filtering:

- **Price:** Find books within your desired budget.
- **Category:** Sort books by genres, types, etc.
- **Author:** Search by author name.
- **Description:** Filter books by keywords in their descriptions.
- **Title & ISBN:** Easily locate books by title or ISBN.
- **Shopping Cart & Orders:** Add books to your cart, modify quantities, and place orders. You can also view your past orders and manage cart items.

- **Administrator Privileges:** Admins can manage the system by modifying order statuses and editing book/category details.

## Technologies Used
- **Java 17**
- **Spring Boot:** Used for building the backend of the application.
- **Spring Security:** Implements authentication and authorization using JWT (JSON Web Tokens) for secure user login and session management.
- **Spring Data JPA:** Provides easy integration with MySQL through JPA, simplifying database operations and reducing boilerplate code.
- **MapStruct:** Used for object mapping, simplifying the transformation of data between entities and DTOs.
- **MySQL:** Database.
- **Liquibase:** Used for database version control.
- **Docker:** Containerization for application deployment.
- **Swagger:** Used for API documentation and testing, providing an interactive interface to explore and test the RESTful endpoints.
- **Junit**, **Mockito**, **Testcontainers:** Testing frameworks used for unit testing, mocking dependencies, and testing database interactions in a containerized environment.
## Design Patterns
- **Builder Pattern:** Used for creating complex objects with a flexible and readable syntax.
- **Strategy Pattern:** The Strategy pattern is used to make different algorithms interchangeable, allowing for greater scalability and flexibility in the application.
## Database structure
![image](https://github.com/user-attachments/assets/02ba9a17-62bd-4741-a404-6d932c53cb17)

# API Endpoints

## Authentication Management
### Endpoints available for unauthorized users

##### Authenticates a user using their credentials (username and password). If the credentials are valid, it generates and returns a JWT token, enabling secure access to protected endpoints.
- **POST** **/auth/login**

##### Registers a new user by saving their details in the database. This endpoint allows new users to create an account and gain access to the application.
- **POST** **/auth/register**

## Book Management
### Endpoints available for users with role user&admin

##### Adds a new book to the catalog. This endpoint ensures that all necessary details about the book are saved in the database, making it available for future searches and orders.
- **POST** **/books**

##### Updates the details of an existing book by its unique ID. Useful for modifying information like title, price, or category as needed.
- **PUT** **/books/{id}**

##### Performs a soft delete on a book by its unique ID, marking it as deleted while retaining it in the database for record-keeping or potential restoration.
- **DELETE** **/books/{id}**

### Endpoints available for users with role user&admin

##### Retrieves a list of all available books, offering users an overview of the catalog.
- **GET** **/books**

##### Fetches detailed information about a specific book by its unique ID, helping users make informed decisions before adding the book to their cart.
- **GET** **/books/{id}** get book by id.

##### Performs a search for books based on dynamic parameters. The search allows filtering by various criteria, such as title, author, category, price, and more, providing flexible and efficient results.
- **GET** **/books/search**

## Category Management
### Endpoints available for users with role admin
##### Creates a new category by adding its details to the database. This endpoint is used to expand the catalog of book categories.
- **POST** **/categories**

##### Updates the details of an existing category identified by its ID. Useful for modifying category names or descriptions.
- **PUT** **/categories/{id}**
##### Deletes a category by its ID. This operation uses soft delete, meaning the category is marked as inactive rather than being permanently removed.
- **DELETE** **/categories/{id}**

### Endpoints available for users with role user&admin
##### Retrieves a list of all available categories. Useful for displaying the full range of categories to users.
- **GET** **/categories**

##### Retrieves detailed information about a specific category by its ID. This can include the category's name, description, and any additional metadata.
- **GET** **/categories/{id}**

##### Retrieves a list of books associated with a specific category by its ID. This endpoint is useful for filtering books based on their category.
- **GET** **/categories/{id}/books**

## Shopping Cart Management
### Endpoints available for users with role user&admin
##### Retrieves the current user's shopping cart, including all items and their quantities. Useful for viewing the cart's content before placing an order.
- **GET** **/cart**

##### Adds a book to the user's shopping cart. The request should include the book ID and the desired quantity to be added.
- **POST** **/cart**

##### Updates the quantity of a specific item in the shopping cart by its cart item ID. This endpoint allows users to adjust the number of books they want to purchase.
- **PUT** **/cart/items/{cartItemId}**

##### Removes a specific item from the shopping cart by its cart item ID. Useful for removing unwanted items before finalizing an order.
- **DELETE** **/cart/items/{cartItemId}**

## Order Management
### Endpoints available for users with role user&admin
##### Creates a new order for the current user. The user provides a delivery address, and the order is finalized using the items in the shopping cart. The total cost of the order is calculated, and the order is saved with the specified address.
- **POST** **/orders**

##### Retrieves the order history for the current user, displaying all past orders and their details.
- **GET** **/orders***

##### Fetches all items in a specific order by the given order ID. Useful for viewing the contents of a particular order.
- **GET** **/orders/{orderId}/items**

##### Retrieves detailed information about a specific item in a particular order.
- **GET** **/orders/{orderId}/items/{itemId}**

### Endpoints available for users with role admin
##### Allows administrators to update the status of an order by its ID. This is useful for managing order workflows, such as marking orders as shipped or canceled.
- **PATCH**  **/orders/{id}**

# How to run the project
## Note: The project has not been deployed to AWS yet, but I will be deploying it there soon. In the meantime, please clone this repository to your local machine.
### Prerequisites:
- **Java 17 must be installed on your system.**
- **MySQL should be installed and running. Ensure you have the credentials (username and password) to access your MySQL database.**
### Clone the Repository:
git clone https://github.com/dlvsn/only-books 
cd <project_directory>
### Set Up the Database:
- **Create a MySQL database.**
- **Update the application.properties or application.yml file in the src/main/resources directory with your database credentials and other configurations. Example:**  
  spring.datasource.url=jdbc:mysql://localhost:3306/<your_database_name>  
  spring.datasource.username=<your_username>  
  spring.datasource.password=<your_password>
### Run the Application:
- **Open a terminal in the project directory and execute:**  
  ./mvnw spring-boot:run
- **Alternatively, if you are using an IDE like IntelliJ IDEA, open the project, locate the Application class (usually in the src/main/java directory), and run it.**

### Access the API Documentation:
- **The project includes Swagger for API documentation. Once the application is running, open your browser and navigate to:**
  http://localhost:8080/swagger-ui/index.html
# Now you can explore and test the available endpoints!
