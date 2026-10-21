# Syllabus Topic Mapping & Course Evidence
**Course**: Object Oriented Programming (`24SJPBCST304`)  
**Institution**: St. Joseph's College of Engineering and Technology, Palai (Autonomous)  
**Curriculum**: 2024 — Semester 3 (Common to CS/CA)  
**Project**: Employee Management System (EMS)

---

## 📌 Complete Syllabus Breakdown & Implementation Index

This document maps every concept from the official course syllabus to the exact Java files and components implemented in this project.

---

### Module 1: Java Fundamentals & OOP Basics

| Syllabus Topic | Concepts Covered | Implementation File / Location |
| :--- | :--- | :--- |
| **Simple Java Structure** | Class declaration, `main` method, JVM execution | [Main.java](file:///d:/stash/src/com/ems/main/Main.java) |
| **Primitive Data Types** | `int`, `double`, `boolean` for IDs, salary, flags | [User.java](file:///d:/stash/src/com/ems/model/User.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java), [Salary.java](file:///d:/stash/src/com/ems/model/Salary.java) |
| **Strings & Text** | String operations (`trim()`, `equals()`, concatenation) | [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java) |
| **Arrays** | 1D arrays for dropdown choices and table headers | [AttendanceMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/AttendanceMgmtPanel.java), [SalaryMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/SalaryMgmtPanel.java) |
| **Vector Class (Basics)** | `Vector<String>` and `Vector<Vector<Object>>` for `DefaultTableModel` | [EmployeeMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/EmployeeMgmtPanel.java), [DepartmentMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/DepartmentMgmtPanel.java), [LeaveMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/LeaveMgmtPanel.java) |
| **Operators** | Arithmetic (`+`, `-`, `*`), Relational, Logical (`&&`, `\|\|`), Conditional / Ternary (`? :`) | [SalaryCalculator.java](file:///d:/stash/src/com/ems/util/SalaryCalculator.java), [EmployeeHomePanel.java](file:///d:/stash/src/com/ems/ui/employee/EmployeeHomePanel.java) |
| **Control Statements** | Selection (`if-else`, `switch`), Iteration (`for`, `while`), Jump (`return`, `break`) | [UserDAO.java](file:///d:/stash/src/com/ems/dao/UserDAO.java), [EmployeeDAO.java](file:///d:/stash/src/com/ems/dao/EmployeeDAO.java), [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java) |
| **OOP Concepts** | Classes, Objects, Abstraction, Encapsulation | [User.java](file:///d:/stash/src/com/ems/model/User.java), [Department.java](file:///d:/stash/src/com/ems/model/Department.java), [Attendance.java](file:///d:/stash/src/com/ems/model/Attendance.java) |
| **Methods & Constructors** | Default & parameterized constructors, `this` keyword | [User.java](file:///d:/stash/src/com/ems/model/User.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java) |
| **Access Modifiers** | `private` variables with `public` getters/setters, `protected` members | All model classes in `com.ems.model` |

---

### Module 2: Polymorphism & Inheritance

| Syllabus Topic | Concepts Covered | Implementation File / Location |
| :--- | :--- | :--- |
| **Method Overloading** | Multiple methods with identical names but differing parameter signatures | [SalaryCalculator.java](file:///d:/stash/src/com/ems/util/SalaryCalculator.java) (`calculateNetSalary`), [EmployeeDAO.java](file:///d:/stash/src/com/ems/dao/EmployeeDAO.java) (`search`) |
| **Static Members** | Static constants and methods (`static getConnection()`, `static calculateNetSalary()`) | [DBConnection.java](file:///d:/stash/src/com/ems/dao/DBConnection.java), [SalaryCalculator.java](file:///d:/stash/src/com/ems/util/SalaryCalculator.java) |
| **Final Variables** | Compile-time constants (`final String ROLE_ADMIN`) | [User.java](file:///d:/stash/src/com/ems/model/User.java), [LeaveRequest.java](file:///d:/stash/src/com/ems/model/LeaveRequest.java) |
| **Inner Classes** | Event handler inner class (`LoginActionListener`) | [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java) |
| **Inheritance Hierarchy** | Super Class (`User`) $\rightarrow$ Sub Classes (`Admin`, `Employee`) | [User.java](file:///d:/stash/src/com/ems/model/User.java), [Admin.java](file:///d:/stash/src/com/ems/model/Admin.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java) |
| **The `super` Keyword** | Invoking superclass constructor and methods via `super(...)` | [Admin.java](file:///d:/stash/src/com/ems/model/Admin.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java) |
| **Protected Members** | Fields defined as `protected` in superclass accessible by subclasses | [User.java](file:///d:/stash/src/com/ems/model/User.java) |
| **Method Overriding** | Subclasses overriding abstract superclass method `getDashboardWelcome()` | [Admin.java](file:///d:/stash/src/com/ems/model/Admin.java), [Employee.java](file:///d:/stash/src/com/ems/model/Employee.java) |
| **Dynamic Method Dispatch**| Base class reference `User` resolved at runtime to `Admin` or `Employee` | [UserDAO.java](file:///d:/stash/src/com/ems/dao/UserDAO.java), [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java) |
| **Using `final` with Inheritance** | Base method `public final boolean isAdmin()` preventing override | [User.java](file:///d:/stash/src/com/ems/model/User.java) |

---

### Module 3: Packages, Interfaces & Exception Handling

| Syllabus Topic | Concepts Covered | Implementation File / Location |
| :--- | :--- | :--- |
| **Packages & CLASSPATH** | Clean package organization: `com.ems.model`, `dao`, `ui`, `exception`, `util` | Entire `src` folder structure |
| **Interfaces** | Interface definition: `GenericDAO<T>` with CRUD signatures | [GenericDAO.java](file:///d:/stash/src/com/ems/dao/GenericDAO.java) |
| **Implementing Interfaces** | Implementing `GenericDAO<T>` with concrete JDBC operations | [EmployeeDAO.java](file:///d:/stash/src/com/ems/dao/EmployeeDAO.java), [DepartmentDAO.java](file:///d:/stash/src/com/ems/dao/DepartmentDAO.java), [AttendanceDAO.java](file:///d:/stash/src/com/ems/dao/AttendanceDAO.java) |
| **Interface References** | Accessing concrete DAOs via interface reference type | [GenericDAO.java](file:///d:/stash/src/com/ems/dao/GenericDAO.java) |
| **Checked vs Unchecked Exceptions** | `SQLException` (checked), `NumberFormatException` (unchecked) | [EmployeeMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/EmployeeMgmtPanel.java), [SalaryCalculator.java](file:///d:/stash/src/com/ems/util/SalaryCalculator.java) |
| **try, catch, multiple catch, finally** | Safe JDBC resource management in `finally` and multi-tier exception traps | [DBConnection.java](file:///d:/stash/src/com/ems/dao/DBConnection.java), [UserDAO.java](file:///d:/stash/src/com/ems/dao/UserDAO.java) |
| **throw, throws** | Propagating exceptions through method signatures | [GenericDAO.java](file:///d:/stash/src/com/ems/dao/GenericDAO.java), [DBConnection.java](file:///d:/stash/src/com/ems/dao/DBConnection.java) |
| **Custom Exceptions** | Custom application exception hierarchy (`AppException`, `AuthenticationException`) | [AppException.java](file:///d:/stash/src/com/ems/exception/AppException.java), [AuthenticationException.java](file:///d:/stash/src/com/ems/exception/AuthenticationException.java) |

---

### Module 4: SOLID Principles, Swing GUI & JDBC Database

| Syllabus Topic | Concepts Covered | Implementation File / Location |
| :--- | :--- | :--- |
| **SOLID Principles** | Single Responsibility (Model/DAO/UI separation), Open-Closed (`GenericDAO`) | Architectural design across all packages |
| **MVC Architecture** | Model (Entities), View (Swing Panels), Controller (DAOs & Event Listeners) | `com.ems.model`, `com.ems.ui`, `com.ems.dao` |
| **Swing Containers** | Top-level containers (`JFrame`, `JDialog`), intermediate containers (`JPanel`, `JScrollPane`, `JSplitPane`) | [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java), [AdminDashboardFrame.java](file:///d:/stash/src/com/ems/ui/AdminDashboardFrame.java), [EmployeeDashboardFrame.java](file:///d:/stash/src/com/ems/ui/EmployeeDashboardFrame.java) |
| **Swing Controls** | `JLabel`, `JButton`, `JTextField`, `JPasswordField`, `JTable`, `JComboBox`, `JTextArea` | All UI classes |
| **Swing Layout Managers** | `BorderLayout`, `CardLayout` (sidebar panel switching), `GridLayout`, `FlowLayout` | [AdminDashboardFrame.java](file:///d:/stash/src/com/ems/ui/AdminDashboardFrame.java), [EmployeeDashboardFrame.java](file:///d:/stash/src/com/ems/ui/EmployeeDashboardFrame.java), [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java) |
| **Delegation Event Model** | Event Source (`JButton`), Event Object (`ActionEvent`), Listener Interface (`ActionListener`) | [LoginFrame.java](file:///d:/stash/src/com/ems/ui/LoginFrame.java), [EmployeeMgmtPanel.java](file:///d:/stash/src/com/ems/ui/admin/EmployeeMgmtPanel.java) |
| **JDBC Components** | `DriverManager`, `Connection`, `PreparedStatement`, `Statement`, `ResultSet` | [DBConnection.java](file:///d:/stash/src/com/ems/dao/DBConnection.java), [UserDAO.java](file:///d:/stash/src/com/ems/dao/UserDAO.java) |
| **CRUD Operations with JDBC** | `INSERT`, `SELECT`, `UPDATE`, `DELETE` operations using parameterized SQL queries | All DAOs in `com.ems.dao` |
