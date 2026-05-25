# 🏠 Ethiopia Real Estate App

> A modern, Firebase-powered Android real estate platform built for Ethiopia's property market.

---

## 📋 Project Info

| | |
|---|---|
| **University** | Adigrat University — College of Engineering & Technology |
| **Department** | Software Engineering |
| **Course** | Mobile Application Development (Seng4061) |
| **Year / Semester** | Year 4 — Semester 1 |
| **Submission Date** | 5/7/2026 E.C |
| **Instructor** | Inst. Kibrom |

### 👥 Group Members

| Name | ID Number |
|---|---|
| Filmon Amare | 15469/12 |
| Ataklti Tesfay | 14965/12 |
| Samuel Tesgay | 24744/13 |
| Dawit Abadi | 19210/12 |
| Abraham Kidane | 19067/12 |
| Yonas Hailekiros | 19553/12 |

---

## 📱 App Overview

**Ethiopia Real Estate** is a full-featured Android real estate application built with **Jetpack Compose** and **Firebase**. It connects property seekers with listings across Ethiopia, offering a smooth browsing experience with powerful search, filtering, map integration, and direct agent contact — all backed by a real-time Firestore database.

---

## ✨ Features

### 👤 For Users
- 🔐 **Authentication** — Secure login, registration, and password reset via Firebase Auth
- 🏘 **Property Feed** — Browse all listings with pull-to-refresh
- 🔍 **Smart Search** — Search by city, title, or price
- 🎛 **Advanced Filters** — Filter by property type (Apartment, House, Villa, Commercial, Land) and budget range
- 🏠 / 🔑 **Buy & Rent Toggle** — Switch between properties for sale and for rent
- ❤️ **Favorites** — Save properties and sync across sessions via Firestore
- 🕐 **Recently Viewed** — Quick access to last 5 viewed properties
- 📋 **Property Detail** — Full image gallery, stats, description, and interactive map
- 🗺 **OSM Map Integration** — View exact property location on an interactive map
- 📤 **Share Property** — Share listing details via any app
- 📞 **Call Agent** — One-tap phone call to agent
- 💬 **Book via Telegram** — Direct booking through Telegram
- 🔔 **Notifications** — In-app notification screen with unread badge
- 👤 **Profile Screen** — Manage account and logout

### 🛠 For Admins
- ➕ **Add Listings** — Full form with image upload (gallery or URL), property details
- ✏️ **Edit Listings** — Update any property in real time
- 🗑 **Delete Listings** — Remove properties from Firestore
- 📍 **Map Location Picker** — Tap on OSM map to pin exact property coordinates
- 📊 **Dashboard Stats** — Total, For Sale, and For Rent counts at a glance
- 🔄 **Pull-to-Refresh** — Live sync with Firestore

---

## 🏗 Technical Architecture

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Authentication** | Firebase Authentication |
| **Database** | Cloud Firestore |
| **Storage** | Firebase Storage |
| **Maps** | OSMDroid (OpenStreetMap) |
| **Image Loading** | Coil (AsyncImage) |
| **Navigation** | State-based Compose navigation |
| **Min SDK** | Android 7.0 (API 24) |

---

## 🔐 Security

- Firebase Auth with email/password
- Role-based access control (admin vs. user) stored in Firestore
- Admin email hardcoded as fallback: `admin@realestate.com`
- Favorites and view counts persisted per user UID

---

## 📸 App Screens

| Screen | Description |
|---|---|
| Splash Screen | Animated launch screen |
| Login / Register / Forgot Password | Full auth flow |
| Home (User) | Hero card, featured listings, Buy/Rent toggle |
| Property Detail | Gallery, map, stats, share, call, book |
| Favorites | Saved properties synced to Firestore |
| Profile | User info and logout |
| Notifications | In-app notification feed |
| Admin Panel | Add / Edit / Delete listings with map picker |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- A Firebase project with Firestore, Auth, and Storage enabled

### Setup
1. Clone the repo:
   ```bash
   git clone https://github.com/fili112/Realestateapp.git
   ```
2. Open in Android Studio
3. Replace `app/google-services.json` with your own Firebase config
4. Run on emulator or physical device (API 24+)

---

## 🔮 Future Enhancements

- 💬 Real-time chat between buyers and agents
- 🔔 Push notifications for price drops and new listings
- 🌐 Amharic / English localization
- 🤖 AI-powered property recommendations
- 📊 Advanced analytics dashboard for admins
- 🗺 Google Maps integration

---

## 📄 License

This project was developed for academic purposes at Adigrat University.

---

<p align="center">Made with ❤️ in Tigray, Ethiopia 🇪🇹</p>
