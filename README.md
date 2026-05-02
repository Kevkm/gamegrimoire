Game Grimoire 📖
A cross-platform gaming statistics tracker that unifies your Steam and Xbox game libraries into one dashboard. Built as a senior project for CSCI4840, Spring 2026.
Overview
Game Grimoire allows users to create a personal account and connect their gaming platforms to view their entire library, track playtime, and see statistics all in one place.
Tech Stack

Frontend: React, Vite, React Router, Axios, React Icons
Backend: Spring Boot, Spring Security, JWT, JPA/Hibernate
Database: PostgreSQL
APIs: Steam Web API, Microsoft Xbox Live OAuth 2.0

Setup

Create a PostgreSQL database named gamegrimoiredb
Add your credentials to application.properties
Run the backend: ./mvnw spring-boot:run
Run the frontend: cd frontend && npm install && npm run dev
Open http://localhost:5173

How to Use

Register a Grimoire account at /register
Link Steam by clicking the Steam icon on the left sidebar and entering your Steam ID
Link Xbox by clicking the Xbox icon and signing in through Microsoft
View your library in the center of the dashboard with cover images and playtime
View your stats on the right sidebar showing total games, hours, and most played game
Logout using the button in the navbar — your connections are saved for next login

Known Limitations

Epic Games has no public API available
Steam Family Sharing games are not included
ML recommendation engine was not completed due to time constraints

Author
Kevin Mejia — CSCI4840 — Spring 2026

