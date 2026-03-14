# Resume Analyzer Full-Stack System

## Description
A comprehensive full-stack web application that allows users to upload, analyze, and manage their resumes. The platform provides a secure and intuitive interface for AI-powered resume parsing, text extraction, skill gap detection, and resume matching using Apache Tika.

## Live Demo
- **Frontend App:** https://resu-match-ai-ft.vercel.app
- **Backend API Server:** https://resumatch-ai-bk.onrender.com

## Features
- Secure User Authentication and Authorization (JWT)
- Robust Resume Parsing and Text Extraction (PDF, DOCX)
- Skill Gap Detection and Match Analysis
- Intuitive Dashboard to Upload and Manage Resumes
- Responsive Modern UI Design for Web and Mobile Devices

## Tech Stack

### Frontend
- **React:** UI library
- **Vite:** Next Generation Frontend Build Tooling
- **Tailwind CSS:** Utility-first CSS framework for styling
- **Lucide React:** Icon library

### Backend
- **Java:** Version 17
- **Spring Boot:** Framework for building the RESTful API (Version 3.5.1)
  - **Spring Data JPA:** For database mapping and interactions
  - **Spring Security:** For securing API endpoints and managing JWT authentication
  - **Spring Web:** For building web applications and RESTful APIs
- **Apache Tika:** For document content and text extraction
- **PostgreSQL:** Primary relational database
- **Maven:** Build automation and dependency management

## Installation

### Prerequisites
- Node.js and npm
- Java SDK (JDK 17 or higher recommended)
- Maven
- PostgreSQL

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd resume-analyzer/resume-analyzer
Build and run the Spring Boot application:
```bash
mvn clean install
mvn spring-boot:run
