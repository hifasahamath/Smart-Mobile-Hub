-- Smart Mobile Hub — PostgreSQL Init Script
-- Creates separate schemas for each microservice
-- All services share a single PostgreSQL instance but use schema separation

-- Note: Spring Boot auto-creates tables via Hibernate ddl-auto.
-- This script just ensures the database and any required extensions exist.

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create application user (if needed)
-- In production, each service would have its own database or at minimum its own schema.
-- For development, we share the default database and let each Spring Boot app
-- manage its own tables via JPA/Hibernate auto-DDL.
