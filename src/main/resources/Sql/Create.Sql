CREATE DATABASE librarymanagement;
USE librarymanagement;

CREATE TABLE Authors (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(100) NOT NULL,
                         email VARCHAR(100),
                         phone VARCHAR(20),
                         address VARCHAR(255),
                         description TEXT
);

-- Create the Publishers table
CREATE TABLE Publishers (
                            id INT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            email VARCHAR(255),
                            address VARCHAR(255),
                            contact VARCHAR(20)
);

-- Create the Books table
CREATE TABLE Books (
                       id INT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       authorId INT,
                       publisherId INT,
                       category VARCHAR(50),
                       isbn VARCHAR(20),
                       publishYear INT,
                       quantity INT,
                       quantityAvailable INT,
                       status VARCHAR(20),
                       FOREIGN KEY (authorId) REFERENCES Authors(id),
                       FOREIGN KEY (publisherId) REFERENCES Publishers(id)
);

-- Create the Members table
CREATE TABLE Members (
                         id INT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255),
                         phone VARCHAR(20),
                         address VARCHAR(255),
                         joinDate DATE,
                         status VARCHAR(20)
);

-- Create the Librarians table
CREATE TABLE Librarians (
                            id INT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            email VARCHAR(255),
                            phone VARCHAR(20),
                            username VARCHAR(50) NOT NULL,
                            password VARCHAR(255) NOT NULL
);

-- Create the Loans table
CREATE TABLE Loans (
                       id INT PRIMARY KEY,
                       bookId INT,
                       memberId INT,
                       borrowDate DATE,
                       dueDate DATE,
                       returnDate DATE,
                       status VARCHAR(20),
                       FOREIGN KEY (bookId) REFERENCES Books(id),
                       FOREIGN KEY (memberId) REFERENCES Members(id),
                       FOREIGN KEY (librarianId) REFERENCES Librarians(id)
);

-- Create the Fines table
CREATE TABLE Fines (
                       id INT PRIMARY KEY,
                       loanId INT,
                       memberId INT,
                       amount DOUBLE,
                       reason TEXT,
                       fineDate DATE,
                       status VARCHAR(20),
                       FOREIGN KEY (loanId) REFERENCES Loans(id),
                       FOREIGN KEY (memberId) REFERENCES Members(id)
);

-- Create indexes
CREATE INDEX idx_title ON Books(title);
CREATE INDEX idx_member_name ON Members(name);
CREATE INDEX idx_librarian_username ON Librarians(username);
