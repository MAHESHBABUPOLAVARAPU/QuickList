# 📋 QuickList – Android Task Management Application

QuickList is a modern Android task management application designed to help users create, manage, and track daily tasks through a clean, responsive, and reliable mobile interface. Built using **Kotlin** and **Jetpack Compose**, the app emphasises simplicity, offline reliability, and secure cloud synchronisation.

---

## 🚀 Project Overview

QuickList enables authenticated users to maintain a personalised task list that remains consistent across sessions and devices. The application adopts a focused feature set, prioritising correctness, responsiveness, and maintainable architecture over unnecessary complexity.

The project was developed incrementally using an **agile, sprint-based approach**, allowing architectural decisions to evolve in response to real implementation challenges and feedback.

---

## 🎯 Intended Users

QuickList is designed for:
- Students managing academic tasks  
- Early-career professionals organising daily responsibilities  
- Individuals seeking a lightweight, personal productivity tool  

The application is **single-user focused** and does not support collaborative or enterprise workflows.

---

## 🧩 Key Features

- 🔐 Secure user authentication  
- ✅ Create, edit, complete, and delete tasks  
- ☁️ Cloud-based task persistence  
- 📦 Offline access with local caching  
- 🖼️ Optional task image uploads  
- 🔄 Real-time UI updates  
- 📱 Clean navigation with bottom tabs  
- 📳 Haptic feedback for key interactions  

---

## 🏗️ Architecture & Design

QuickList follows a **layered Android architecture**, separating concerns across:

- **UI Layer** – Jetpack Compose screens  
- **ViewModel Layer** – State management and lifecycle awareness  
- **Repository Layer** – Data abstraction and synchronisation  
- **Data Layer** – Cloud and local persistence  

This structure ensures predictable state handling, reduced UI recomposition issues, and improved testability and maintainability.

---

## 🛠️ Technology Stack

| Category | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Authentication | Firebase Authentication |
| Cloud Database | Firebase Firestore |
| Local Database | Room DB |
| Networking | Retrofit |
| Media Storage | Cloudinary |
| Dependency Injection | Hilt |
| Development Tools | Android Studio, Git |

---

## 💾 Data Persistence Strategy

QuickList uses a **hybrid storage model**:

- **Firestore** acts as the authoritative cloud data source  
- **Room DB** provides offline access and faster UI responsiveness  

Offline changes are synchronised once connectivity is restored, ensuring data consistency while preserving usability during network interruptions.

---

## 🔐 Security & Privacy

- User authentication handled securely via Firebase  
- Firestore rules restrict access to user-owned data only  
- All network communication uses secure protocols  
- No unnecessary permissions requested  
- No analytics, background tracking, or intrusive data collection  

---

## 🔄 Agile Development Process

The project was delivered over **five sprints**, each focusing on a distinct functional layer:

1. Authentication & Navigation Setup  
2. Core Task Management  
3. Offline Caching & Bottom Navigation  
4. Image Upload Integration  
5. Activity, Profile & Final Refinement  

---

## 🌱 Future Enhancements

- Task categorisation and filtering  
- Improved offline conflict resolution  
- Optional reminders and notifications  
- Enhanced activity insights  

---

## 📎 Project Links

- **GitHub Repository**  
  https://github.com/MAHESHBABUPOLAVARAPU/QuickList  

- **Trello Board**  
  https://trello.com/b/7RAA4vvr/quicklist  

---

## 👤 Author

**Mahesh Polavarapu**  
Student ID: S3522812  
Module: CIS4034-N  
