# Employee Management System (EMS)
**College Course**: Object Oriented Programming (`24SJPBCST304`) — Semester 3  
**Institution**: St. Joseph's College of Engineering and Technology, Palai  
**Technology Stack**: Java SE 17, Java Swing (GUI), JDBC, MySQL

---

## 📖 Project Overview

The **Employee Management System (EMS)** is an MVP desktop application built using pure **Java Swing** and **JDBC with MySQL**. It adheres strictly to the college course syllabus, avoiding extraneous third-party frameworks while implementing all required core Object-Oriented Programming (OOP) concepts, graphical user interfaces, and relational database persistence.

---

## 🚀 Key Features

### 1. Dual-Panel Login System
- **Admin Portal**: Authenticates administrators and routes directly to the Admin Dashboard.
- **Employee Portal**: Authenticates employees and opens their personalized, data-isolated portal.
- Default credentials:
  - **Admin**: `admin` / `admin123`
  - **Employee**: `john` / `john123` (or `jane` / `jane123`, `robert` / `robert123`)

### 2. Admin Dashboard
- **Dashboard Overview**: Summary stat cards for Total Employees, Total Departments, Today's Attendance, and Pending Leaves.
- **Employee Management**: Full CRUD operations (Add, View, Update, Delete) with live text search and table view.
- **Department Management**: Add, update, delete departments, and assign employees to departments.
- **Attendance Management**: Mark daily attendance, view date-filtered records, and update incorrect entries.
- **Leave Management**: Review leave applications with instant "Approve" or "Reject" status updates.
- **Salary Management**: Select employee, calculate net salary (`Basic + HRA + DA - Deductions`), and maintain monthly payroll records.
- **Settings**: Manage user login credentials and change passwords.
- **Logout**: Safe logout returning to login screen.

### 3. Employee Dashboard
- **Strict Data Isolation**: Employees can only view and manage their own records.
- **Dashboard Overview**: Personalized welcome banner, employee ID, and quick status summaries.
- **My Profile**: View personal information, contact details, assigned department, and designation.
- **My Attendance**: Daily and monthly attendance logs with attendance percentage metrics.
- **My Leave**: Submit leave requests (Casual, Sick, Annual) and check approval status in real time.
- **My Salary**: Detailed pay slip breakdown and complete historical payment logs.
- **Logout**: Safe session exit.

---

## 🛠️ Prerequisites & Setup

### 1. Requirements
- **Java JDK 17** (or JDK 8+) installed and configured on your `PATH`.
- **MySQL Server** (via MySQL Server, MySQL Workbench, XAMPP, or WAMP).

### 2. Database Setup
1. Open your MySQL client (MySQL Workbench, phpMyAdmin, or command-line `mysql`).
2. Run the provided script:
   ```sql
   source database.sql;
   ```
   *(Or copy-paste the contents of [database.sql](file:///d:/stash/database.sql) into your MySQL client and execute).*
3. Verify database settings in `src/com/ems/dao/DBConnection.java`:
   - URL: `jdbc:mysql://localhost:3306/ems_db`
   - User: `root`
   - Password: `root` *(Change to your local MySQL password, e.g., empty `""` or `"root"`)*.

---

## 💻 How to Run in Eclipse IDE

1. Open **Eclipse**.
2. Go to **File** $\rightarrow$ **Open Projects from File System...** (or **File** $\rightarrow$ **Import...** $\rightarrow$ **General** $\rightarrow$ **Existing Projects into Workspace**).
3. Set **Import source** (or root directory) to `d:\stash` and click **Finish**.
4. In Project Explorer, expand `src` $\rightarrow$ `com.ems.main`, right-click [`Main.java`](file:///d:/stash/src/com/ems/main/Main.java) $\rightarrow$ **Run As** $\rightarrow$ **Java Application**.

---

## 📂 Project Structure

```
d:\stash\
│
├── database.sql                  # MySQL schema and demo data
├── db.properties                 # Database connection settings
├── .project                      # Eclipse project configuration
├── .classpath                    # Eclipse classpath and build path
├── README.md                     # Project overview and instructions
├── SYLLABUS_MAPPING.md           # Syllabus topic mapping guide
│
├── lib\
│   └── mysql-connector-j-8.3.0.jar # Official MySQL JDBC Driver
│
├── bin\                          # Compiled .class bytecode
│
└── src\
    └── com\
        └── ems\
            ├── main\Main.java
            ├── model\            # User, Admin, Employee, Department, Attendance, LeaveRequest, Salary
            ├── dao\              # GenericDAO, DBConnection, UserDAO, EmployeeDAO, etc.
            ├── exception\        # AppException, AuthenticationException
            ├── util\             # SalaryCalculator
            └── ui\               # LoginFrame, AdminDashboardFrame, EmployeeDashboardFrame, Panels
```

---

## 📚 Syllabus Compliance

For an exhaustive, topic-by-topic mapping linking course modules directly to code line numbers, please inspect:
👉 [SYLLABUS_MAPPING.md](file:///d:/stash/SYLLABUS_MAPPING.md)
