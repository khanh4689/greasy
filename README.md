# 🛒 GearsY-Shop

**GearsY-Shop** is a modern e-commerce web application designed for selling **computer components**. Built with **Spring Boot** and **Thymeleaf**, it offers a seamless shopping experience for customers and robust management tools for administrators and staff.

---

## 🎨 UI/UX Design
👉 [View Figma Prototype](https://www.figma.com/design/tAi4PPnQkLg2SFXIZkEw3X/Untitled?node-id=0-1&p=f&t=lkRJec9Nn1gqULsX-0)

---

## 🚀 Features

### 👥 For Customers
- **Account Management**: Register, login, forgot password, and Google OAuth2 login.
- **Product Browsing**: Filter products by **category**, **promotion**, **featured**, or **newest**.
- **Shopping Cart**: Add, update, or remove items.
- **Checkout**: Pay via **COD** or **VNPay**.
- **Order Management**: Track and cancel orders (if not processed).
- **Profile Management**: Update profile, manage addresses, and view order history.
- **Product Feedback**: Leave reviews and ratings for products.

### 🛠️ For Admin/Staff
- **Product Management**: Create, update, delete products, manage stock, images, and descriptions.
- **Category Management**: Add, edit, or remove categories.
- **Promotion Management**: Set up discounts with start and end dates.
- **Supplier Management**: Manage supplier information.
- **Order Management**: Update order statuses (Pending, Shipping, Completed, Cancelled).
- **User Management**: Assign roles (USER, STAFF, ADMIN, BLOCK) and lock/unlock accounts.
- **Review Management**: Approve, hide, or delete customer reviews.
- **Statistics**: Generate revenue reports by month, quarter, or staff.

---

## 🔐 Security
- **Spring Security**: Role-based access control.
- **Roles**:
  - **ROLE_USER**: Customers
  - **ROLE_STAFF**: Staff
  - **ROLE_ADMIN**: Administrators
- **Password Hashing**: BCrypt for secure password storage.
- **JWT Authentication**: Secure API calls.
- **OAuth2**: Google login integration.

---

## 🛠️ Tech Stack
| Layer          | Technology                              |
|----------------|-----------------------------------------|
| **Back-end**   | Java 17, Spring Boot (MVC, JPA, Security) |
| **Database**   | PostgreSQL                             |
| **Front-end**  | Thymeleaf, Bootstrap 5, JavaScript     |
| **Payment**    | VNPay, COD                             |
| **Auth**       | JWT, OAuth2 (Google)                   |
| **Tools**      | Maven, IntelliJ IDEA, GitHub           |
| **Design**     | Figma                                  |

---

## ⚙️ Getting Started

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/khanh4689/greasy.git
cd greasy
---
### 2️⃣ Set Up the Database

Install PostgreSQL.
Create the database:
sqlCREATE DATABASE gearsy_db;

Import the schema:
bashpsql -U your_user -d gearsy_db -f database/gearsy_db.sql


### 3️⃣ Configure the Application
Edit src/main/resources/application.properties:
propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/gearsy_db
spring.datasource.username=your_user
spring.datasource.password=your_password
### 4️⃣ Run the Application
bash./mvnw spring-boot:run
Open in your browser: http://localhost:8080

# 👤 Demo Account

Email: khanhtrankevin123@gmail.com
Password: Admin1234@


# ✅ Development Process

Methodology: Agile with Scrum
Team Roles: Database, UI/UX, Backend, Testing
Version Control: GitHub
Testing: Unit tests + manual testing


# 📊 Future Improvements

Advanced sales reporting and analytics.
Enhanced UI/UX with migration to Vue.js or React.
AI-based product recommendation system.
Email notification system for orders and promotions.


# 📌 Git Workflow Guide
This guide ensures a clean and conflict-free collaboration for team members.
### 🔄 Step 1: Update the Project
### bashgit checkout main
git pull origin main
### 🌱 Step 2: Create a Feature Branch
Use the naming convention: feature/<feature-name>
bashgit checkout -b feature/login-page
### 💻 Step 3: Code & Commit
bashgit add .
git commit -m "Add User and Product entity"
### ⬆️ Step 4: Push Branch to GitHub
bashgit push origin feature/login-page
### 🔀 Step 5: Create a Pull Request (PR)

Go to the GitHub repository.
Click Compare & pull request.
Add a description and click Create pull request.

# 👀 Step 6: Review & Merge

Team lead reviews the code.
If approved, merge the PR into main.

### 🔄 Step 7: Sync Latest Changes
bashgit checkout main
git pull origin main
### ♻️ Step 8: Start a New Feature
Repeat from Step 2.

# 📝 Working Rules

### ❌ Never code directly on the main branch.
### ✅ Always create a PR to merge changes.
### 📝 Write clear and concise commit messages.
### 🔄 Sync with main regularly to avoid conflicts.
