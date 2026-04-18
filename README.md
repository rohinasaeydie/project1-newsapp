# 📰 Android News App

Android News App is a mobile application that allows users to explore news from around the world and from their local area in an interactive and user-friendly way. After logging in, users can search for articles by keyword, browse different news sources, and discover location-based news by long-pressing on the map.

The app uses a news API for article data, Google Maps for location-based exploration, Jetpack Compose for the user interface, and Kotlin coroutines for smooth network requests.

## ✨ Features

- 🔐 User login
- 🔎 Search articles by keyword
- 🗂️ Browse articles from different news sources
- 🌍 Explore global and local news
- 🗺️ View location-based news by long-pressing on the map
- 📰 Category filters for news browsing
- ⏭️ Pagination for top headlines
- 🖼️ Interactive article cards with images
- ⚡ Smooth network handling with Kotlin coroutines

## 📱 Application Screens

### Login
- User login screen

### MainSearch
- Search for news articles by keyword
- Browse different news categories

### SourcesScreen
- View and select available news sources

### ResultsScreen
- Display article results with images and details

### MapScreen
- Long-press on the map to explore location-based news

### TopHeadlines
- Browse paginated top headlines

## ⚙️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Maps:** Google Maps
- **Networking:** Kotlin coroutines
- **API:** News API

## 🔌 API Usage

This app uses external APIs to:
- retrieve news articles
- search articles by keyword
- filter by source
- support local news exploration through map-based interaction

## 🧪 Example Test Data

To test the app, these examples may help:

- **Search term without selecting a source:** `Covid`
- **Search term with a source:** `Dubai Chocolate` + `ABC News`
- **Map location for local news:** long-press near `Turkiye`

## 🚧 Limitations

- During testing, scrolling could sometimes be difficult on the screen, but using a keyboard to scroll worked.

## 🔒 Security Note

API keys and local credentials have been removed from this repository for security purposes, and the original API key used in development may no longer be active.

If you would like to run this project locally, you may need to provide your own API credentials and local configuration.

## ▶️ Running the Project

To run the app locally:

1. Clone the repository
2. Open the project in Android Studio
3. Add your own API keys and local configuration files
4. Sync Gradle dependencies
5. Run the project on an emulator or Android device

## 🎥 Demo Video

[Watch the demo video here](https://drive.google.com/file/d/1kct92V31o1Ou8rcEwq0pETaRqz2ShmTv/view?usp=sharing)

## 👩‍💻 Author

**Rohina Saeydie**  
Computer Science Student at The George Washington University

## 📌 Repository Note

This project was originally created for **CSCI 4237** as Project 1 and focuses on building an interactive Android news application using live article data, map-based exploration, and Jetpack Compose.
