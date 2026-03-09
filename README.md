# SCORM File Uploading System – Full Stack Application

The SCORM File Uploading System is a full stack application designed to upload, process, and display SCORM learning content through a web interface.

This repository contains both the frontend and backend components of the system.

## Technology Stack

Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate

Frontend

* Next.js / React
* JavaScript
* HTML
* CSS

## System Overview

The application allows users to upload SCORM packages which are processed and stored by the backend. The frontend provides an interface for uploading and interacting with SCORM content.

## Project Structure

scorm-file-uploading-system-fullstack

backend
Handles SCORM file processing, storage, and API development.

frontend
Provides the user interface for uploading and accessing SCORM content.

## Features

* Upload SCORM packages
* Store and manage SCORM content
* Display SCORM learning modules
* REST API integration between frontend and backend

## How the System Works

1. User uploads a SCORM package through the frontend interface.
2. The frontend sends the file to the backend using REST APIs.
3. The backend processes and stores the SCORM package.
4. The frontend retrieves and displays the SCORM content.

## Running the Application

Backend

Run the Spring Boot application on

http://localhost:8080

Frontend

Start the frontend development server

http://localhost:3000

Ensure the backend server is running before starting the frontend application.
