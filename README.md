Console-Based Travelling Application

* Overview

This is a console-based travel application developed using Java and JDBC. The application allows users to search for travel destinations, book tickets, view bookings, and manage travel-related information efficiently.

* Features

1.User Registration and Login

2.Search for Destinations

3.Book Tickets

4.View Booking History

5.Cancel Bookings

Admin Panel for Managing Destinations and Bookings

Database Connectivity using JDBC

* Technologies Used

Java (JDK 8 or above)

JDBC (Java Database Connectivity)

MySQL (or any relational database)

Maven (for dependency management)

Prerequisites

Install Java Development Kit (JDK)

Install MySQL and create a database

Configure JDBC driver

Installation and Setup

Clone the repository:

git clone https://github.com/your-username/travel-app.git

Navigate to the project directory:

cd travel-app

Configure the database:

Create a MySQL database.

Import the provided travel_app.sql file to set up tables.

Update db.properties with your database credentials.

Compile and run the application:

javac -cp .:mysql-connector-java-8.0.26.jar Main.java
java -cp .:mysql-connector-java-8.0.26.jar Main

Database Schema

The database consists of the following tables:

users (id, name, email, password, role)

destinations (id, name, location, description, price)

bookings (id, user_id, destination_id, booking_date, status)

Usage

Run the application and follow the on-screen prompts.

Register or log in as an existing user.

Search and book travel destinations.

Admin users can manage destinations and bookings.

Future Enhancements

Implement GUI for better user experience.

Add payment gateway integration.

Improve security with password hashing and authentication tokens.

License

This project is licensed under the MIT License.

Author

[Kamutam Bhavani]

