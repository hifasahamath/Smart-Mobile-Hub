# Smart Mobile Hub

## AI-Powered Mobile & Electronics E-Commerce Platform

## 1. Project Overview

Build a production-quality, full-stack e-commerce web application named **Smart Mobile Hub** for selling smartphones, tablets, laptops, smartwatches, earbuds, chargers, accessories, and other consumer electronics.

The application is designed for the Sri Lankan market and must support:

- LKR currency
- Sri Lankan delivery zones
- Cash on Delivery
- Bank Transfer
- Pay at Store
- Bank slip upload and verification
- Customer accounts
- Product variants and SKU-level inventory
- Wishlist
- Product comparison
- Order tracking
- Admin/owner management
- Sales analytics
- AI-powered shopping assistant
- AI-powered product comparison
- AI recommendations
- Search and filtering
- Responsive mobile/desktop UI

The project must be implemented as a clean, maintainable, modular system with a strong backend architecture.

Do not create a superficial demo. Build the system as a realistic commercial e-commerce application.

---

# 2. Primary Engineering Goal

The main goal is to demonstrate strong modern software engineering using:

- Next.js
- TypeScript
- Java 21
- Spring Boot
- Spring Cloud
- Spring Security
- Microservices
- REST APIs
- Apache Kafka
- Event-driven architecture
- PostgreSQL
- Redis
- AI integration
- Docker
- Automated testing
- CI/CD
- Monitoring
- Distributed tracing
- OpenAPI documentation

The system should be designed so that the architecture can be explained clearly in a technical interview.

Prioritize:

1. Correctness
2. Security
3. Maintainability
4. Separation of responsibilities
5. Scalability
6. Testability
7. Observability
8. Good user experience

Do not sacrifice architecture quality merely to make implementation faster.

---

# 3. Technology Stack

## Frontend

Use:

- Next.js
- TypeScript
- Tailwind CSS
- shadcn/ui
- TanStack Query
- Zod
- React Hook Form
- Lucide React

Frontend requirements:

- Responsive design
- Desktop and mobile support
- Server-side rendering where beneficial
- Accessible components
- Loading states
- Empty states
- Error states
- Form validation
- Optimistic UI where appropriate
- Proper API error handling
- Strong TypeScript typing

Do not use JavaScript in the frontend. Use TypeScript throughout.

---

# 4. Backend

Use:

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- Maven

Backend requirements:

- RESTful API design
- DTO-based API boundaries
- Service layer
- Repository layer
- Global exception handling
- Request validation
- Structured logging
- Secure authentication
- Role-based authorization
- Pagination
- Filtering
- Sorting
- Database transaction management
- Consistent API response/error format
- API versioning where appropriate

Do not expose JPA entities directly from API controllers.

Use DTOs.

---

# 5. Architecture

Implement the application using microservices.

Do not create excessive microservices merely for the sake of saying "microservices."

Use meaningful business boundaries.

Recommended services:

1. API Gateway
2. Auth Service
3. Catalog Service
4. Inventory Service
5. Order Service
6. Payment Service
7. Notification Service
8. AI Service
9. Analytics Service

Each service must have a clearly defined responsibility.

---

# 6. API Gateway

Use:

- Spring Cloud Gateway

Responsibilities:

- Route requests to backend services
- Validate authentication where appropriate
- Forward JWT information
- Centralize API access
- Apply rate limiting where appropriate
- Provide a single external backend URL
- Handle gateway-level concerns

Example:

```text
/api/auth/**        -> Auth Service
/api/products/**    -> Catalog Service
/api/inventory/**   -> Inventory Service
/api/orders/**      -> Order Service
/api/payments/**    -> Payment Service
/api/notifications/** -> Notification Service
/api/ai/**          -> AI Service
/api/analytics/**   -> Analytics Service
```

Do not put business logic inside the gateway.

---

# 7. Auth Service

Use:

- Spring Boot
- Spring Security
- JWT
- PostgreSQL

Responsibilities:

- Registration
- Login
- Logout
- Refresh token handling
- Password hashing
- Customer profile
- Role management
- Account status
- Authorization

Roles:

```text
CUSTOMER
ADMIN
```

Optionally support:

```text
SUPER_ADMIN
```

Use secure password hashing such as BCrypt.

JWT must contain only necessary claims.

Do not store passwords in plain text.

Do not expose sensitive authentication data to the frontend.

---

# 8. Catalog Service

Responsibilities:

- Products
- Categories
- Brands
- Product images
- Variant groups
- Variant options
- SKUs
- Product specifications
- Product search
- Product filtering
- Product sorting
- Featured products
- Trending products
- Deals
- Related products

Domain structure:

```text
Product
 ├── Brand
 ├── Category
 ├── Images
 ├── Variant Groups
 │     └── Variant Options
 └── SKUs
       └── SKU Option Values
```

Example variant groups:

```text
Storage
Color
RAM
```

Example:

```text
Storage:
128GB
256GB
512GB

Color:
Black
Silver
Blue
```

An SKU must represent an actual purchasable variant.

Example:

```text
IPH15P-256-BLACK
```

---

# 9. Inventory Service

Responsibilities:

- SKU stock
- Stock availability
- Stock reservation
- Stock release
- Stock adjustment
- Low-stock detection
- Inventory history
- Concurrency protection

This service owns inventory rules.

Do not let the frontend directly modify stock.

Important requirement:

Prevent overselling when multiple customers attempt to purchase the same SKU simultaneously.

Use appropriate transaction handling and row-level locking or another safe concurrency mechanism.

Example workflow:

```text
Customer
   |
   v
Order Service
   |
   v
Inventory Service
   |
   +--> Reserve Stock
```

Cancellation:

```text
Order Cancelled
      |
      v
Inventory Service
      |
      +--> Release Stock
```

Do not rely entirely on frontend validation for stock protection.

---

# 10. Order Service

Responsibilities:

- Cart checkout
- Order creation
- Order items
- Order totals
- Delivery information
- Fulfillment mode
- Order status
- Order cancellation
- Customer order history
- Order detail
- Order state transitions

Supported order statuses:

```text
PENDING
CONFIRMED
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

Implement valid state transitions.

Do not allow arbitrary status changes.

Example:

```text
PENDING
  |
  v
CONFIRMED
  |
  v
PROCESSING
  |
  v
SHIPPED
  |
  v
DELIVERED
```

Cancellation may be allowed according to business rules.

Store historical order snapshots.

Order items should preserve:

- Product name at purchase time
- SKU label at purchase time
- Unit price at purchase time
- Quantity
- Line total

The system must not depend on the current product record to reconstruct historical orders.

---

# 11. Payment Service

Supported payment methods:

```text
COD
BANK_TRANSFER
PAY_AT_STORE
```

Responsibilities:

- Payment records
- Payment status
- Bank transfer records
- Bank slip upload metadata
- Payment verification
- Payment rejection
- Payment audit trail

Payment status:

```text
PENDING
PAID
FAILED
REJECTED
```

For bank transfer:

```text
Customer
   |
   +--> Upload bank slip
            |
            v
       Payment Service
            |
            v
       ADMIN REVIEW
         /       \
        /         \
   VERIFIED      REJECTED
```

Never trust the client to mark a payment as verified.

Only authorized administrators may verify or reject payments.

---

# 12. Notification Service

Responsibilities:

- Order confirmation notifications
- Payment verification notifications
- Order shipped notifications
- Order delivered notifications
- Order cancellation notifications
- Low-stock alerts for administrators

Initial implementation can support email.

Design the service so additional notification providers can be added later.

Use Kafka events where appropriate.

Example:

```text
OrderCreated
      |
      v
Kafka
      |
      v
Notification Service
      |
      v
Send Email
```

---

# 13. AI Service

Use:

- Gemini API as the primary AI provider

Keep the service independent from the frontend.

The frontend must never directly expose the Gemini API key.

AI responsibilities:

- Shopping assistant
- Product search assistance
- Product recommendations
- Product comparison
- Buying advice
- Product explanation
- Store policy questions

## Critical AI rule

The AI must not invent:

- Product prices
- Stock quantities
- Product availability
- Delivery fees
- Store policies
- Product specifications

For factual store information, retrieve authoritative data from backend services/database before generating the response.

Example:

```text
Customer:
"Do you have iPhone 15 Pro 256GB?"

AI Service
   |
   v
Intent Detection
   |
   v
Catalog Service
   |
   v
Inventory Service
   |
   v
Actual product data
   |
   v
Gemini
   |
   v
Final response
```

The LLM is responsible for language generation, not authoritative inventory decisions.

---

# 14. AI Features

## AI Shopping Assistant

Support questions such as:

```text
Show me phones under LKR 150,000
Which phone is best for gaming?
Do you have iPhone 15 Pro 256GB?
Which phone has the best camera?
What phones are available in blue?
What is your delivery policy?
```

Use intent classification.

Example intents:

```text
PRODUCT_SEARCH
PRODUCT_AVAILABILITY
PRODUCT_PRICE
PRODUCT_COMPARISON
BUYING_ADVICE
STORE_POLICY
GENERAL_TECH
UNKNOWN
```

For deterministic intents, query backend data instead of relying only on the LLM.

---

# 15. AI Product Comparison

Allow customers to select multiple products.

Display structured comparison:

```text
Price
Display
Processor
RAM
Storage
Camera
Battery
Operating System
```

AI can summarize:

```text
Best for Gaming
Best Camera
Best Battery
Best Value
```

The AI summary should be generated only from the selected product data.

---

# 16. Analytics Service

Track business events such as:

```text
PRODUCT_VIEWED
PRODUCT_SEARCHED
ADD_TO_CART
CHECKOUT_STARTED
ORDER_CREATED
PAYMENT_VERIFIED
ORDER_CANCELLED
ORDER_SHIPPED
ORDER_DELIVERED
```

Analytics responsibilities:

- Revenue
- Orders
- Top-selling products
- Conversion funnel
- Product views
- Add-to-cart rate
- Sales trends
- Order status distribution
- Low-stock reporting

Do not make analytics queries unnecessarily expensive on transactional services.

Use event-driven collection where appropriate.

---

# 17. Apache Kafka

Use Apache Kafka for asynchronous communication.

Define domain events.

Examples:

```text
OrderCreated
OrderConfirmed
OrderCancelled
PaymentVerified
PaymentRejected
OrderShipped
OrderDelivered
StockReserved
StockReleased
StockLow
ProductCreated
ProductUpdated
```

Example:

```text
Order Service
     |
     | OrderCreated
     v
   Kafka
     |
     +--------> Inventory Service
     |
     +--------> Notification Service
     |
     +--------> Analytics Service
```

Do not use Kafka for every request.

Use REST for synchronous queries and Kafka for asynchronous events.

---

# 18. Redis

Use Redis for:

- Product caching
- Frequently accessed catalog data
- Rate limiting
- Temporary data
- Frequently used store configuration
- Other performance-sensitive data where appropriate

Do not cache data that must always be strongly consistent unless invalidation is handled correctly.

Inventory availability must remain authoritative in the inventory service.

---

# 19. Database

Use:

```text
Supabase PostgreSQL
```

Supabase is being used as a managed PostgreSQL platform.

Important:

The application backend must communicate with PostgreSQL through the backend services.

Do not allow the browser to directly perform privileged database operations.

Prefer service-owned schemas/databases logically.

Recommended ownership:

```text
Auth Service
   -> authentication/profile data

Catalog Service
   -> products/categories/brands/variants/SKUs

Inventory Service
   -> stock/inventory records

Order Service
   -> orders/order items

Payment Service
   -> payment records/slips

Analytics Service
   -> analytics/event data
```

Do not tightly couple services through direct cross-service database access.

A service should own its data.

---

# 20. Supabase Storage

Use Supabase Storage for:

- Product images
- Brand logos
- Hero banners
- Bank slip files

Sensitive files such as bank slips must not be publicly accessible.

Use signed URLs or another secure access mechanism for private files.

Do not expose storage credentials in the frontend.

---

# 21. Product Data Model

Create a robust product model.

Product should support:

```text
id
name
slug
brandId
categoryId
description
shortDescription
basePrice
compareAtPrice
specifications
featured
trending
active
createdAt
updatedAt
```

Product images:

```text
id
productId
url
altText
sortOrder
isPrimary
```

Variant groups:

```text
id
productId
name
sortOrder
```

Variant options:

```text
id
variantGroupId
value
sortOrder
```

SKU:

```text
id
productId
skuCode
price
compareAtPrice
stockQuantity
active
```

SKU-option relationship:

```text
skuId
optionId
```

---

# 22. Delivery

Support Sri Lankan delivery zones.

Delivery zone should include:

```text
id
name
deliveryFee
estimatedDelivery
active
sortOrder
```

Checkout should calculate:

```text
Subtotal
+
Delivery Fee
=
Total
```

Store pickup:

```text
Delivery Fee = 0
```

Do not let the frontend submit arbitrary delivery fees.

The backend must calculate the final amount.

---

# 23. Cart

Support:

- Add item
- Remove item
- Change quantity
- Variant selection
- Stock validation
- Price calculation
- Wishlist
- Recently viewed products

The backend must revalidate:

- Product status
- SKU status
- Price
- Stock
- Delivery fee

during checkout.

Never trust totals sent by the browser.

---

# 24. Checkout

Checkout flow:

```text
Cart
  |
  v
Delivery / Pickup
  |
  v
Address
  |
  v
Payment Method
  |
  v
Order Review
  |
  v
Order Creation
  |
  v
Payment Handling
  |
  v
Confirmation
```

The backend must perform final validation before creating the order.

Never rely solely on frontend validation.

---

# 25. Admin Dashboard

Create an `/admin` section.

Admin features:

## Dashboard

Display:

- Orders today
- Revenue today
- Pending payments
- Pending bank slips
- Low-stock products
- Total orders
- Total revenue
- Sales trend

## Product Management

Support:

- Create product
- Update product
- Delete/deactivate product
- Upload images
- Create variants
- Generate SKUs
- Update prices
- Update stock
- Featured toggle
- Trending toggle

## Categories

CRUD.

## Brands

CRUD.

## Orders

Support:

- Search
- Filter
- Status changes
- Order details
- Payment verification
- Customer information
- Delivery information

## Delivery Zones

CRUD.

## Analytics

Provide:

- Revenue trend
- Orders by status
- Top products
- Product performance
- Sales over selected date ranges

---

# 26. Admin Security

All admin APIs must require:

```text
Authenticated JWT
+
ADMIN role
```

Do not rely only on hidden frontend routes.

Authorization must be enforced server-side.

Frontend route protection is only a user-experience measure.

Backend authorization is the actual security boundary.

---

# 27. Frontend Pages

Customer pages should include:

```text
/
Shop
Product Details
Search Results
Compare
Cart
Checkout
Order Success
Orders
Order Details
Favorites
Profile
Login
Register
Forgot Password
About
```

Admin pages:

```text
/admin
/admin/products
/admin/products/new
/admin/products/:id
/admin/categories
/admin/brands
/admin/orders
/admin/orders/:id
/admin/delivery-zones
/admin/analytics
/admin/settings
```

---

# 28. Frontend UX

Build a polished modern commerce interface.

Requirements:

- Mobile-first responsive design
- Clear navigation
- Search
- Product filtering
- Product sorting
- Product cards
- Product image gallery
- Variant selector
- Stock status
- Cart drawer
- Checkout steps
- Toast notifications
- Skeleton loading
- Empty states
- Error handling
- Confirmation dialogs
- Accessible forms
- Keyboard accessibility

Use shadcn/ui for common UI primitives.

Use Lucide React icons.

Avoid unnecessary visual complexity.

---

# 29. Search

Implement:

- Product name search
- Brand search
- Category search
- SKU search where appropriate
- Price filtering
- Availability filtering
- Sorting

Support:

```text
Newest
Price Low -> High
Price High -> Low
Name A -> Z
```

Debounce search input where appropriate.

---

# 30. Product Recommendations

Support:

- Related products
- Frequently bought together
- Similar products
- Trending products
- Featured products

AI recommendations must be based on actual catalog data.

Do not fabricate products.

---

# 31. Security Requirements

Implement:

- JWT authentication
- Role-based access control
- BCrypt password hashing
- Input validation
- SQL injection protection through parameterized ORM queries
- CORS configuration
- API rate limiting where appropriate
- Secure secrets management
- No API secrets in frontend code
- No sensitive data in logs
- Secure file access
- Authorization checks on every protected backend operation

Use environment variables for:

```text
Database credentials
JWT secrets
Gemini API key
Kafka credentials
Redis credentials
Storage credentials
```

Never commit secrets to Git.

Provide `.env.example` files.

---

# 32. API Design

Use RESTful conventions.

Example:

```text
GET    /api/v1/products
GET    /api/v1/products/{id}
POST   /api/v1/products
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

Orders:

```text
GET    /api/v1/orders
GET    /api/v1/orders/{id}
POST   /api/v1/orders
POST   /api/v1/orders/{id}/cancel
```

Payments:

```text
GET    /api/v1/payments/{id}
POST   /api/v1/payments/{id}/slip
POST   /api/v1/payments/{id}/verify
POST   /api/v1/payments/{id}/reject
```

AI:

```text
POST /api/v1/ai/chat
POST /api/v1/ai/compare
POST /api/v1/ai/recommend
```

Use API versioning:

```text
/api/v1/...
```

---

# 33. API Response Format

Use a consistent response structure.

Success example:

```json
{
  "success": true,
  "data": {},
  "message": "Request successful"
}
```

Error example:

```json
{
  "success": false,
  "message": "Product not found",
  "code": "PRODUCT_NOT_FOUND",
  "timestamp": "..."
}
```

Validation errors should identify the invalid fields.

---

# 34. Pagination

Use pagination for:

- Products
- Orders
- Customers
- Analytics listings
- Admin tables

Example:

```text
?page=0&size=20&sort=name,asc
```

Do not return thousands of records by default.

---

# 35. Testing

Use:

- JUnit 5
- Mockito
- Spring Boot Test
- Testcontainers

Testing layers:

## Unit tests

Test:

- Business rules
- Services
- Validators
- Mappers

## Integration tests

Test:

- REST APIs
- PostgreSQL
- Redis
- Kafka
- Security
- Transactions

Use Testcontainers where appropriate.

Important scenarios to test:

- Concurrent stock purchase
- Insufficient stock
- Order cancellation
- Payment verification
- Unauthorized admin access
- Invalid JWT
- Invalid product ID
- Invalid checkout data

---

# 36. Docker

All backend services must be containerizable.

Create Dockerfiles for:

```text
api-gateway
auth-service
catalog-service
inventory-service
order-service
payment-service
notification-service
ai-service
analytics-service
```

Provide a `docker-compose.yml` for local development.

Local infrastructure should include:

```text
PostgreSQL
Redis
Kafka
Kafka UI if useful
Jaeger
Prometheus
Grafana
```

The frontend should also be containerizable.

---

# 37. GitHub Actions

Create CI workflows.

At minimum:

```text
Pull Request
    |
    +--> Build frontend
    +--> Build backend
    +--> Run tests
    +--> Check formatting
```

On main branch:

```text
Build
Test
Package
Docker build
```

Do not automatically deploy until the deployment configuration is intentionally defined.

---

# 38. Monitoring

Use:

- Spring Boot Actuator
- Micrometer
- Prometheus
- Grafana

Expose useful metrics:

- Request count
- Request latency
- Error count
- JVM metrics
- Database metrics
- Kafka metrics
- Service health

Use health endpoints for container orchestration and deployment.

---

# 39. Distributed Tracing

Use:

- OpenTelemetry
- Jaeger

Every request should have trace/correlation information where practical.

Example:

```text
Frontend
   |
   v
Gateway
   |
   v
Order Service
   |
   v
Inventory Service
   |
   v
Kafka
   |
   +--> Notification Service
   |
   +--> Analytics Service
```

The tracing system should make it possible to follow the lifecycle of a request/event across services.

---

# 40. Documentation

Use:

- OpenAPI
- Swagger UI
- springdoc-openapi

Every service should document its API.

Documentation should include:

- Endpoints
- Request models
- Response models
- Authentication requirements
- Error responses
- Example requests
- Example responses

---

# 41. Project Structure

Use a repository structure similar to:

```text
smart-mobile-hub/

├── frontend/
│
├── backend/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── catalog-service/
│   ├── inventory-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── notification-service/
│   ├── ai-service/
│   └── analytics-service/
│
├── infrastructure/
│   ├── docker/
│   ├── kafka/
│   ├── postgres/
│   ├── redis/
│   ├── prometheus/
│   ├── grafana/
│   └── jaeger/
│
├── docs/
│
├── .github/
│   └── workflows/
│
├── docker-compose.yml
├── README.md
└── AGENT_INSTRUCTIONS.md
```

---

# 42. Backend Service Internal Structure

Each Spring Boot service should follow a clean structure.

Example:

```text
src/main/java/com/smartmobilehub/catalog/

├── controller/
├── service/
├── repository/
├── entity/
├── dto/
│   ├── request/
│   └── response/
├── mapper/
├── exception/
├── config/
├── security/
├── event/
└── CatalogApplication.java
```

Do not place everything in a single package.

Keep responsibilities separated.

---

# 43. Code Quality

Follow these rules:

- Use meaningful names
- Keep methods focused
- Avoid giant classes
- Avoid duplicated business logic
- Prefer composition
- Use interfaces where they improve testability and architecture
- Keep controllers thin
- Put business rules in services/domain logic
- Keep repository responsibilities focused
- Add useful comments only where the logic is non-obvious
- Do not generate unnecessary abstractions

Do not create code merely to satisfy an architecture pattern.

Every abstraction must have a reason.

---

# 44. Configuration

Use environment variables.

Examples:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

JWT_SECRET

REDIS_HOST
REDIS_PORT

KAFKA_BOOTSTRAP_SERVERS

GEMINI_API_KEY

SUPABASE_URL
SUPABASE_SERVICE_ROLE_KEY
```

Never commit real values.

Create example files:

```text
.env.example
```

---

# 45. Deployment Design

The application must be designed for cloud deployment.

Frontend:

```text
Next.js
```

Backend:

```text
Spring Boot services
```

Database:

```text
Supabase PostgreSQL
```

Storage:

```text
Supabase Storage
```

Redis:

```text
Managed Redis
```

Kafka:

```text
Managed Kafka or hosted Kafka
```

Do not assume every infrastructure component will run permanently on a free tier.

Prioritize a deployment design that can scale to a real cloud environment.

---

# 46. Environment Separation

Support:

```text
development
test
production
```

Do not hardcode environment-specific URLs.

Frontend must obtain the API base URL from environment configuration.

---

# 47. Error Handling

Implement centralized exception handling using:

```text
@RestControllerAdvice
```

Handle:

- Validation errors
- Authentication errors
- Authorization errors
- Resource not found
- Business rule violations
- Database failures
- External API failures
- Kafka failures

Return safe errors to clients.

Do not expose stack traces in production responses.

---

# 48. Business Rules

Important business rules include:

## Stock

A customer cannot purchase more units than available.

## Price

The backend calculates the authoritative order total.

## Delivery

Delivery fees are determined by backend-configured delivery zones.

## Payment

Only authorized administrators can verify bank slips.

## Orders

Only valid order-state transitions are allowed.

## Permissions

Customers may access only their own orders/profile.

Admins may access administrative functions according to role.

Customers can visit, browse, search, filter, sort, users can see their total price of the products added to cart, view products and categories, compare products without login/register. only login/register need when a user places an order. users can see their total price of the products added to cart, view products and categories, compare products without login/register. only login/register need when a user places an order.  

---

# 49. Event-Driven Workflows

Implement meaningful Kafka workflows.

Example 1:

```text
Order Created
      |
      v
Kafka
 ├── Inventory Service
 ├── Notification Service
 └── Analytics Service
```

Example 2:

```text
Payment Verified
      |
      v
Kafka
 ├── Order Service
 ├── Notification Service
 └── Analytics Service
```

Example 3:

```text
Order Cancelled
      |
      v
Kafka
 └── Inventory Service
         |
         v
    Release Stock
```

Events should contain enough information for consumers to process them independently.

Use event versioning where appropriate.

---

# 50. Idempotency

Design asynchronous consumers to be idempotent.

If Kafka delivers the same message more than once, processing it repeatedly must not corrupt data.

Examples:

- Do not release stock twice
- Do not send duplicate payment confirmations unnecessarily
- Do not create duplicate analytics records unintentionally

---

# 51. Frontend API Layer

Do not scatter raw fetch calls throughout UI components.

Create a frontend API/data layer.

Example:

```text
frontend/
└── src/
    ├── api/
    ├── components/
    ├── features/
    ├── hooks/
    ├── lib/
    ├── schemas/
    ├── types/
    └── app/
```

Use TanStack Query for server state.

Use Zod for validation.

Use React Hook Form for forms.

---

# 52. State Management

Do not introduce Redux unless it becomes genuinely necessary.

Use:

- TanStack Query for server state
- React state/context for small client-side state
- URL query parameters for search/filter state when appropriate

Keep state ownership clear.

---

# 53. Authentication Flow

Recommended flow:

```text
User
 |
 | login
 v
Auth Service
 |
 | JWT
 v
Frontend
 |
 | Bearer token
 v
API Gateway
 |
 v
Backend Service
```

Protected routes must validate authentication.

Admin routes additionally validate:

```text
role = ADMIN
```

---

# 54. Admin UI Security

Do not rely on:

```text
if (user.role === "ADMIN")
```

alone in the frontend.

The backend must independently verify authorization.

---

# 55. AI Security

Never expose:

```text
GEMINI_API_KEY
```

to the browser.

Requests must go through:

```text
Frontend
   |
   v
AI Service
   |
   v
Gemini API
```

Add reasonable validation and rate limiting to AI endpoints.

---

# 56. Performance

Optimize:

- Product images
- API payloads
- Database queries
- Pagination
- Caching
- Frontend rendering
- Search queries

Do not optimize prematurely.

Measure before making architectural changes.

---

# 57. Accessibility

The frontend must:

- Have semantic HTML
- Support keyboard navigation
- Use accessible labels
- Use sufficient contrast
- Provide useful focus states
- Provide alt text for product images
- Support screen readers where practical

Do not rely exclusively on color to communicate state.

---

# 58. SEO

Use Next.js metadata support.

Product pages should have:

- SEO title
- Description
- Open Graph metadata
- Canonical URL
- Product-specific metadata where appropriate

Generate sensible metadata based on actual product information.

---

# 59. Seed Data

Create realistic development seed data.

Include:

- Several categories
- Several brands
- Smartphones
- Tablets
- Accessories
- Multiple product variants
- Multiple SKUs
- Different price ranges
- Different stock levels
- Featured products
- Trending products

Do not use fake lorem ipsum product descriptions.

Use realistic sample product information while clearly treating it as development/demo data.

---

# 60. Initial Development Order

Implement in this order.

## Phase 1

Repository setup.

Create:

```text
frontend
backend services
docker infrastructure
GitHub Actions
documentation
```

## Phase 2

Auth Service.

Implement:

- Registration
- Login
- JWT
- Roles
- Profiles

## Phase 3

Catalog Service.

Implement:

- Products
- Categories
- Brands
- Variants
- SKUs
- Search/filtering

## Phase 4

Inventory Service.

Implement:

- Stock
- Reservation
- Release
- Concurrency protection

## Phase 5

Order Service.

Implement:

- Checkout
- Orders
- Order status
- Cancellation

## Phase 6

Payment Service.

Implement:

- Payment methods
- Bank slips
- Verification

## Phase 7

Kafka integration.

Implement meaningful domain events.

## Phase 8

Notification Service.

## Phase 9

AI Service.

## Phase 10

Analytics.

## Phase 11

Admin dashboard.

## Phase 12

Monitoring and tracing.

## Phase 13

Full integration testing.

## Phase 14

Production deployment preparation.

Do not attempt to build all services completely at once.

---

# 61. Development Strategy

Build incrementally.

At the end of every meaningful phase:

1. Compile
2. Run tests
3. Start services
4. Test APIs
5. Verify database behavior
6. Verify frontend integration
7. Fix errors before moving on

Do not accumulate large numbers of untested files.

---

# 62. Agent Behavior Rules

The coding agent must:

- Read this file before making architecture decisions
- Follow the specified technology stack
- Preserve business consistency across services
- Prefer simple maintainable solutions
- Avoid unnecessary dependencies
- Avoid unnecessary abstraction
- Never expose secrets
- Never bypass backend authorization
- Never trust frontend totals or permissions
- Write tests for meaningful business logic
- Update documentation when architecture changes
- Keep the application runnable throughout development
- Verify generated code by compiling/testing it

Before introducing a new technology, first determine whether an existing technology in this specification already solves the problem.

Do not replace technologies without a clear technical reason.

---

# 63. Agent Must Not

Do not:

- Replace Spring Boot with Node.js
- Replace PostgreSQL with another database
- Replace Kafka with random messaging libraries
- Remove JWT authentication
- Move privileged database operations into the browser
- Put API keys in frontend environment variables intended for browser exposure
- Hardcode production credentials
- Create fake APIs that are never implemented
- Create placeholder implementations for core business logic
- Create microservices with no meaningful responsibility
- Put all backend code into one service
- Put all business logic into controllers
- Return entities directly from controllers
- Skip testing important business logic
- Skip validation
- Bypass service boundaries unnecessarily

---

# 64. Definition of Done

A feature is not considered complete until:

- Backend implementation exists
- Frontend integration exists where required
- Validation exists
- Authentication/authorization is correct
- Error handling exists
- Tests exist for important logic
- Database changes are complete
- API documentation is updated
- Docker setup still works
- Existing functionality remains working

---

# 65. Final Product Expectations

The final application should feel like a real e-commerce platform, not a university CRUD demo.

The customer should be able to:

```text
Browse
Search
Filter
View products
Choose variants
Compare devices
Add to cart
Checkout
Choose delivery/pickup
Choose payment method
Upload bank slip
Track orders
Manage profile
Use AI assistant
```

The administrator should be able to:

```text
Manage products
Manage variants
Manage SKUs
Manage stock
Manage orders
Verify payments
Manage categories
Manage brands
Manage delivery zones
View analytics
Manage AI configuration
Manage users and roles
```

The engineering system should demonstrate:

```text
Microservices
REST
Kafka
Event-driven architecture
JWT security
PostgreSQL
Redis
AI integration
Automated testing
Docker
CI/CD
Monitoring
Distributed tracing
API documentation
```

---

# 66. Architecture Principle

The most important architectural rule is:

```text
Frontend
   |
   v
API Gateway
   |
   +-----------------------------+
   |             |               |
   v             v               v
Auth         Catalog          Order
Service      Service          Service
                             |
                             v
                        Inventory
                          Service

             +-----------------------+
             |
             v
            Kafka
             |
       +-----+------+----------------+
       |            |                |
       v            v                v
 Notification    Analytics        Inventory

             AI Service
                 |
                 v
             Gemini API

Services
   |
   v
PostgreSQL / Redis
```

Business ownership must remain clear.

Use synchronous REST when an immediate response is required.

Use Kafka when work can be asynchronous and event-driven.

Use Redis for performance-sensitive temporary/cached data.

Use PostgreSQL for authoritative business data.

Use Gemini only through the AI Service.

---

# 67. Non-Functional Requirements

The system should aim for:

- Secure by default
- Observable
- Testable
- Maintainable
- Cloud-ready
- Containerized
- Horizontally scalable where appropriate
- Resilient to external service failures
- Clear service boundaries
- Clear API contracts

Do not claim "production-ready" unless the relevant functionality has actually been implemented and tested.

---

# 68. Final Technology List

## Frontend

```text
Next.js
TypeScript
Tailwind CSS
shadcn/ui
TanStack Query
Zod
React Hook Form
Lucide React
```

## Backend

```text
Java 21
Spring Boot
Spring Cloud Gateway
Spring Security
Spring Data JPA
Hibernate
JWT
Maven
```

## Architecture

```text
Microservices
REST APIs
Apache Kafka
Event-Driven Architecture
```

## Data

```text
Supabase PostgreSQL
Supabase Storage
Redis
```

## AI

```text
Gemini API
```

## Testing

```text
JUnit 5
Mockito
Spring Boot Test
Testcontainers
```

## DevOps

```text
Git
GitHub
Docker
Docker Compose
GitHub Actions
```

## Monitoring

```text
Spring Boot Actuator
Micrometer
Prometheus
Grafana
OpenTelemetry
Jaeger
```

## Documentation

```text
OpenAPI
Swagger UI
springdoc-openapi
```

Build the project according to this specification.\
Do not simplify core architecture unless there is a concrete technical reason.\
When making implementation decisions not explicitly covered here, choose the simplest solution that preserves the architecture and business requirements.
