# 🌦️ Weather App ☀️🌧️

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![OpenWeatherMap](https://img.shields.io/badge/OpenWeatherMap-7FDBFF?logo=openweathermap&logoColor=white)](https://openweathermap.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

A beautiful and feature-rich Android weather application that provides real-time weather updates with a clean, modern UI. Built with ❤️ using Kotlin and OpenWeatherMap API.

## 🌟 Features

### ☁️ Current Weather
- Real-time temperature in Celsius/Fahrenheit
- Weather conditions (sunny, cloudy, rainy, etc.)
- Atmospheric pressure and humidity levels
- Wind speed and direction
- Sunrise and sunset times
- Feels-like temperature

### 📍 Location Services
- Automatic location detection using GPS
- Manual city search
- Save favorite locations
- Background location updates

### 🌈 UI/UX
- Beautiful Material Design 3 theming
- Dynamic color theming based on weather conditions
- Animated weather icons
- Dark/Light mode support
- Responsive layout for all screen sizes

### ⚡ Performance
- Fast and lightweight
- Offline support with cached data
- Optimized battery usage
- Minimal data consumption

## 🛠 Tech Stack

| Category | Technologies |
|----------|--------------|
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-7F52FF?logo=kotlin&logoColor=white) |
| **Minimum SDK** | ![API](https://img.shields.io/badge/API-24%2B-brightgreen) |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Dependency Injection** | Hilt |
| **Asynchronous** | Kotlin Coroutines, Flow |
| **Network** | Retrofit, OkHttp |
| **Image Loading** | Coil |
| **Local Database** | Room |
| **Location** | Google Play Services Location |
| **Permissions** | Dexter |
| **Build** | Gradle (Kotlin DSL) |

## 🚀 Getting Started

### Prerequisites
- Android Studio Flamingo (2022.2.1) or later
- Android SDK 34
- JDK 17
- OpenWeatherMap API key

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/weatherapp.git
   cd weatherapp
   ```

2. **Get an API key**
   - Sign up at [OpenWeatherMap](https://openweathermap.org/api)
   - Get your API key from the dashboard

3. **Configure API Key**
   Create or edit `local.properties` in the root directory:
   ```properties
   WEATHER_API_KEY=your_api_key_here
   ```

4. **Build and Run**
   - Open the project in Android Studio
   - Sync project with Gradle files
   - Run the app on an emulator or physical device

## 🛠 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/krishhh/weatherapp/
│   │   │   ├── activities/     # Activities and Fragments
│   │   │   ├── adapters/       # RecyclerView Adapters
│   │   │   ├── data/           # Data layer
│   │   │   │   ├── local/      # Local database (Room)
│   │   │   │   ├── remote/     # Remote data sources (API)
│   │   │   │   └── repository/ # Repository implementations
│   │   │   ├── di/            # Dependency Injection
│   │   │   ├── models/         # Data models
│   │   │   ├── network/        # API services
│   │   │   ├── utils/          # Utility classes
│   │   │   └── viewmodels/     # ViewModels
│   │   └── res/                # Resources
│   │       ├── drawable/       # Vector assets
│   │       ├── layout/         # XML layouts
│   │       ├── values/         # Colors, strings, styles
│   │       └── ...
│   └── test/                   # Unit tests
└── build.gradle.kts            # App level build config
```

## 📦 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| [AndroidX Core KTX](https://developer.android.com/kotlin/ktx) | 1.12.0 | Kotlin extensions for Android |
| [AndroidX AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat) | 1.7.1 | Backward compatibility |
| [Material Components](https://github.com/material-components/material-components-android) | 1.12.0 | Material Design components |
| [ConstraintLayout](https://developer.android.com/training/constraint-layout) | 2.2.1 | Advanced UI layouts |
| [Retrofit](https://square.github.io/retrofit/) | 2.9.0 | Type-safe HTTP client |
| [OkHttp](https://square.github.io/okhttp/) | 4.11.0 | HTTP client |
| [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) | 1.7.3 | Asynchronous programming |
| [Hilt](https://dagger.dev/hilt/) | 2.48 | Dependency injection |
| [Room](https://developer.android.com/topic/libraries/architecture/room) | 2.6.1 | Local database |
| [Coil](https://coil-kt.github.io/coil/) | 2.5.0 | Image loading |
| [Dexter](https://github.com/Karumi/Dexter) | 6.2.3 | Runtime permissions |
| [Google Play Services Location](https://developers.google.com/android/guides/setup) | 21.0.1 | Location services |

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for the weather data API
- [Material Design](https://material.io/design) for the design guidelines
- [Shields.io](https://shields.io/) for the badges

## 🌟 Show your support

Give a ⭐️ if this project helped you!

---

<div align="center">
  Made with ❤️ and ☕ by <b>Krish</b>
</div>
