# Parchate

Parchate es una aplicación Android diseñada para descubrir y gestionar eventos en Colombia. La app permite a los usuarios explorar eventos por categoría, ubicación y fecha; crear y administrar sus propios eventos; interactuar con un chatbot impulsado por IA para recomendaciones personalizadas; guardar eventos favoritos; y gestionar sus perfiles de usuario.

## Funcionalidades

- **Descubrimiento de Eventos**: Explora eventos con filtros por categoría, ubicación, fecha, precio (gratis/de pago) y modalidad (online/presencial)
- **Creación de Eventos**: Crea y gestiona tus propios eventos con detalles como título, descripción, ubicación, fecha/hora, precios e imágenes
- **Chatbot con IA**: Obtén recomendaciones personalizadas de eventos a través de un chatbot impulsado por IA (Parche IA) usando Groq
- **Integración con Mapas**: Visualiza eventos en un mapa interactivo y obtén direcciones
- **Sistema de Favoritos**: Guarda y gestiona tus eventos favoritos para acceder rápidamente
- **Perfiles de Usuario**: Gestiona tu información personal y cambia contraseñas de forma segura
- **Vista de Calendario**: Visualiza eventos en formato de calendario
- **Autenticación**: Autenticación segura de usuarios con verificación de correo electrónico mediante Firebase

## Stack Tecnológico

### Lenguajes y Frameworks
- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - Kit de herramientas moderno de UI para Android para construir interfaces nativas
- **Material Design 3** - Sistema de diseño de Google para interfaces funcionales y atractivas

### Servicios y APIs
- **Firebase Authentication** - Autenticación segura con correo/contraseña
- **Firebase Firestore** - Base de datos NoSQL en la nube para almacenar eventos y datos de usuarios
- **Firebase Storage** - Almacenamiento en la nube para imágenes de eventos
- **Google Maps Platform** - Servicios de Mapas, Places y Geolocalización
- **Groq API** - Inferencia de IA para el asistente chatbot (usando el modelo gemma2-9b-it)

### Librerías
- **Coil** - Carga rápida de imágenes para Android
- **OkHttp** - Cliente HTTP eficiente para llamadas a APIs
- **Kotlin Serialization** - Análisis y generación de JSON
- **Kotlin Coroutines** - Programación asíncrona
- **AndroidX Lifecycle** - Gestión del ciclo de vida de componentes UI
- **AndroidX Navigation** - Manejo de navegación dentro de la app

## Arquitectura

Parchate sigue el patrón arquitectónico **MVVM (Model-View-ViewModel)**:
- **Model**: Clases de datos que representan eventos, usuarios y respuestas de API
- **View**: Componentes UI en Jetpack Compose que definen la interfaz de usuario
- **ViewModel**: Maneja datos relacionados con la UI y la lógica de negocio, sobrevive a cambios de configuración

La app utiliza:
- **StateFlow** para la gestión reactiva del estado en los ViewModels
- **Navigation Component** para manejar las transiciones entre pantallas
- **Patrón Repository** para abstraer las fuentes de datos (implementaciones con Firebase)

## Pantallas y Flujos Principales

1. **Pantalla de Inicio** - Lanzador de la app con navegación al home o al login
2. **Flujo de Autenticación** - Login, registro, verificación de correo, restablecimiento de contraseña
3. **Pantalla Principal (Home)** - Interfaz principal de exploración de eventos con:
    - Funcionalidad de búsqueda
    - Chips de filtro (categoría, modalidad, ubicación, precio)
    - Visualización de tarjetas de eventos
    - Estados vacío/error/cargando
4. **Pantalla de Creación de Eventos** - Formulario para crear nuevos eventos con:
    - Título, descripción, categoría, fecha/hora
    - Ubicación (con selector de mapa)
    - Precio (gratis/de pago), aforo
    - Detalles del organizador
    - Carga de imágenes
5. **Pantalla de Detalles del Evento** - Vista detallada de eventos individuales
6. **Pantalla del Mapa** - Mapa interactivo con la ubicación de los eventos
7. **Pantalla del Chatbot** - Asistente de IA para recomendaciones de eventos:
    - Chips de sugerencias rápidas
    - Interfaz de conversación
    - Indicadores de escritura
    - Respuestas basadas en ubicación
8. **Pantalla de Perfil** - Gestión del perfil de usuario:
    - Información personal
    - Historial de eventos
    - Funcionalidad de cambio de contraseña
    - Cerrar sesión
9. **Pantalla de Favoritos** - Colección de eventos favoritos del usuario
10. **Pantalla de Calendario** - Vista de calendario de eventos

## Requisitos

### Software
- Android Studio Flamingo o más reciente
- JDK 11
- Android SDK 36 (objetivo), SDK mínimo 24

### Hardware
- Dispositivo Android o emulador con:
    - Cámara (para imágenes de eventos)
    - Servicios de ubicación (para funciones del mapa)
    - Conexión a Internet

### Archivos de Configuración
El proyecto requiere los siguientes archivos de configuración:

1. **google-services.json** - Configuración de Firebase (colocar en el directorio `app/`)
2. **local.properties** - Claves de API para Google Maps y Places:
   ```
   MAPS_API_KEY=tu_clave_de_api_de_google_maps_aqui
   PLACES_API_KEY=tu_clave_de_api_de_google_places_aqui
   ```

### Configuración de Firebase
Para usar los servicios de Firebase:
1. Crea un proyecto en Firebase en https://console.firebase.google.com/
2. Habilita Authentication (Correo/Contraseña)
3. Habilita Firestore Database
4. Habilita Firebase Storage
5. Agrega tu app Android al proyecto de Firebase
6. Descarga el archivo `google-services.json` y colócalo en el directorio `app/`

### Configuración de Google Cloud
Para la funcionalidad del mapa:
1. Crea un proyecto en Google Cloud Console
2. Habilita Maps SDK para Android, Places API y Geocoding API
3. Genera las claves de API y agrégalas a `local.properties`

### Configuración de Groq API
Para el chatbot con IA:
1. Regístrate en https://console.groq.com/
2. Crea una clave de API
3. Reemplaza el valor de marcador en `ChatbotViewModel.kt`:
   ```kotlin
   private val groqApiKey = "tu_clave_de_api_de_groq_aqui"
   ```

## Instalación y Configuración

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/parchate.git
   cd parchate
   ```

2. **Configura Firebase**
    - Sigue las instrucciones de configuración de Firebase indicadas arriba
    - Coloca `google-services.json` en `app/`

3. **Configura las claves de API**
    - Crea `local.properties` en la raíz del proyecto con:
      ```
      MAPS_API_KEY=tu_clave_de_api_de_google_maps_aqui
      PLACES_API_KEY=tu_clave_de_api_de_google_places_aqui
      ```

4. **Configura la API de Groq**
    - Abre `app/src/main/java/com/universidad/parchate/ui/viewmodel/ChatbotViewModel.kt`
    - Reemplaza `"Poner API"` con tu clave de API de Groq:
      ```kotlin
      private val groqApiKey = "tu_clave_de_api_de_groq_aqui"
      ```

5. **Compila y ejecuta**
    - Abre el proyecto en Android Studio
    - Sincroniza las dependencias de Gradle
    - Ejecuta en un emulador o dispositivo físico (API 24+)

## Estructura del Proyecto

```
parchate/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/universidad/parchate/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/          # Clases de datos (Evento, User)
│   │   │   │   │   ├── repository/     # Abstracciones de acceso a datos
│   │   │   │   │   └── service/        # Implementaciones con Firebase
│   │   │   │   ├── Navigation/         # Grafo de navegación y rutas
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/        # Pantallas UI (Home, Login, Profile, etc.)
│   │   │   │   │   │   ├── components/ # Componentes UI reutilizables
│   │   │   │   │   │   ├── theme/      # Archivos de tema de Material Design
│   │   │   │   │   │   └── viewmodel/  # ViewModels para la gestión del estado
│   │   │   │   └── MainActivity.kt     # Punto de entrada
│   │   │   └── res/                    # Recursos (strings, layouts, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── local.properties
```

## Capturas de Pantalla y Demo

Dado que este repositorio es solo de código, no se incluyen capturas de pantalla. Sin embargo, la aplicación cuenta con:

- **Pantalla Principal**: Lista desplazable verticalmente de tarjetas de eventos con opciones de filtro en la parte superior
- **Tarjeta de Evento**: Muestra imagen del evento, título, categoría, fecha/hora, ubicación, precio y botón de favorito
- **Formulario de Creación de Evento**: Formulario con múltiples secciones e campos de entrada para todos los detalles del evento
- **Vista del Mapa**: Google Maps interactivo con marcadores para las ubicaciones de los eventos
- **Interfaz del Chat**: UI conversacional con burbujas de mensajes, indicadores de escritura y chips de sugerencias rápidas
- **Pantalla de Perfil**: Visualización de información del usuario con opciones para editar y cambiar contraseña

## Autores y Colaboradores

- **Esteban Rueda** 
- **Camilo Maldonado**
- **Mateo Cubillos**
- **Albert Maecha**

## Licencia

Este proyecto no especifica explícitamente una licencia. Por favor, consulta con el propietario del repositorio para obtener información sobre la licencia.

## Contexto del Proyecto

Parchate es un proyecto universitario, como lo indica el nombre del paquete `com.universidad.parchate`. La aplicación se enfoca en el descubrimiento de eventos y la planificación social, con especial énfasis en eventos y ubicaciones de Colombia.

## Agradecimientos

- Google por las herramientas de desarrollo Android, Firebase y Maps Platform
- JetBrains por el lenguaje de programación Kotlin
- Groq por proporcionar capacidades de inferencia de IA
- La comunidad de código abierto por las diversas librerías utilizadas en este proyecto



# Parchate

Parchate is an Android application designed for discovering and managing events in Colombia. The app allows users to browse events by category, location, and date; create and manage their own events; interact with an AI-powered chatbot for personalized recommendations; save favorite events; and manage their user profiles.

## Features

- **Event Discovery**: Browse events with filters by category, location, date, price (free/paid), and modality (online/in-person)
- **Event Creation**: Create and manage your own events with details like title, description, location, date/time, pricing, and images
- **AI Chatbot**: Get personalized event recommendations through an AI-powered chatbot (Parche IA) powered by Groq
- **Maps Integration**: View events on an interactive map and get directions
- **Favorites System**: Save and manage your favorite events for quick access
- **User Profiles**: Manage your personal information and change passwords securely
- **Calendar View**: View events in a calendar format
- **Authentication**: Secure user authentication with email verification via Firebase

## Tech Stack

### Languages & Frameworks
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern Android UI toolkit for building native interfaces
- **Material Design 3** - Google's design system for beautiful, functional UIs

### Services & APIs
- **Firebase Authentication** - Secure user authentication with email/password
- **Firebase Firestore** - NoSQL cloud database for storing events and user data
- **Firebase Storage** - Cloud storage for event images
- **Google Maps Platform** - Maps, Places, and Geolocation services
- **Groq API** - AI inference for the chatbot assistant (using gemma2-9b-it model)

### Libraries
- **Coil** - Fast image loading for Android
- **OkHttp** - Efficient HTTP client for API calls
- **Kotlin Serialization** - JSON parsing and generation
- **Kotlin Coroutines** - Asynchronous programming
- **AndroidX Lifecycle** - UI component lifecycle management
- **AndroidX Navigation** - In-app navigation handling

## Architecture

Parchate follows the **MVVM (Model-View-ViewModel)** architectural pattern:
- **Model**: Data classes representing events, users, and API responses
- **View**: Jetpack Compose UI components defining the user interface
- **ViewModel**: Handles UI-related data and business logic, survives configuration changes

The app uses:
- **StateFlow** for reactive state management in ViewModels
- **Navigation Component** for handling screen transitions
- **Repository Pattern** for abstracting data sources (Firebase implementations)

## Main Screens & Flows

1. **Start Screen** - App launcher with navigation to home or login
2. **Authentication Flow** - Login, registration, email verification, password reset
3. **Home Screen** - Main event browsing interface with:
   - Search functionality
   - Filter chips (category, modality, location, price)
   - Event cards display
   - Empty/error/loading states
4. **Create Event Screen** - Form for creating new events with:
   - Title, description, category, date/time
   - Location (with map picker)
   - Pricing (free/paid), capacity
   - Organizer details
   - Image upload
5. **Event Details Screen** - Detailed view of individual events
6. **Map Screen** - Interactive map showing event locations
7. **Chatbot Screen** - AI assistant for event recommendations:
   - Quick suggestion chips
   - Conversation interface
   - Typing indicators
   - Location-based responses
8. **Profile Screen** - User profile management:
   - Personal information
   - Event history
   - Password change functionality
   - Sign out
9. **Favorites Screen** - Collection of user's favorite events
10. **Calendar Screen** - Calendar view of events

## Requirements

### Software
- Android Studio Flamingo or newer
- JDK 11
- Android SDK 36 (target), minimum SDK 24

### Hardware
- Android device or emulator with:
  - Camera (for event images)
  - Location services (for map features)
  - Internet connectivity

### Configuration Files
The project requires the following configuration files:

1. **google-services.json** - Firebase configuration (place in `app/` directory)
2. **local.properties** - API keys for Google Maps and Places:
   ```
   MAPS_API_KEY=your_google_maps_api_key_here
   PLACES_API_KEY=your_google_places_api_key_here
   ```

### Firebase Setup
To use Firebase services:
1. Create a Firebase project at https://console.firebase.google.com/
2. Enable Authentication (Email/Password)
3. Enable Firestore Database
4. Enable Firebase Storage
5. Add your Android app to the Firebase project
6. Download the `google-services.json` file and place it in the `app/` directory

### Google Cloud Setup
For map functionality:
1. Create a project in Google Cloud Console
2. Enable Maps SDK for Android, Places API, and Geocoding API
3. Generate API keys and add them to `local.properties`

### Groq API Setup
For the chatbot AI:
1. Sign up at https://console.groq.com/
2. Create an API key
3. Replace the placeholder value in `ChatbotViewModel.kt`:
   ```kotlin
   private val groqApiKey = "your_groq_api_key_here"
   ```

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/parchate.git
   cd parchate
   ```

2. **Configure Firebase**
   - Follow the Firebase setup instructions above
   - Place `google-services.json` in `app/`

3. **Set up API keys**
   - Create `local.properties` in the project root with:
     ```
     MAPS_API_KEY=your_google_maps_api_key_here
     PLACES_API_KEY=your_google_places_api_key_here
     ```

4. **Configure Groq API**
   - Open `app/src/main/java/com/universidad/parchate/ui/viewmodel/ChatbotViewModel.kt`
   - Replace `"Poner API"` with your actual Groq API key:
     ```kotlin
     private val groqApiKey = "your_groq_api_key_here"
     ```

5. **Build and run**
   - Open the project in Android Studio
   - Sync Gradle dependencies
   - Run on an emulator or physical device (API 24+)

## Project Structure

```
parchate/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/universidad/parchate/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/          # Data classes (Evento, User)
│   │   │   │   │   ├── repository/     # Data access abstractions
│   │   │   │   │   └── service/        # Firebase implementations
│   │   │   │   ├── Navigation/         # Navigation graph and routes
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/        # UI screens (Home, Login, Profile, etc.)
│   │   │   │   │   │   ├── components/ # Reusable UI components
│   │   │   │   │   │   ├── theme/      # Material Design theme files
│   │   │   │   │   │   └── viewmodel/  # ViewModels for state management
│   │   │   │   └── MainActivity.kt     # Entry point
│   │   │   └── res/                    # Resources (strings, layouts, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── local.properties
```

## Screenshots & Demo

Due to the nature of this repository being code-only, screenshots are not included. However, the application features:

- **Home Screen**: A vertically scrollable list of event cards with filter options at the top
- **Event Card**: Displays event image, title, category, date/time, location, price, and favorite button
- **Create Event Form**: Multi-section form with input fields for all event details
- **Map View**: Interactive Google Maps with markers for event locations
- **Chat Interface**: Conversational UI with message bubbles, typing indicators, and quick suggestion chips
- **Profile Screen**: User information display with edit and password change options

## Authors & Contributors

- **Esteban Rueda** - Primary developer (based on Git configuration)

## License

This project does not explicitly state a license in the reviewed files. Please check with the repository owner for licensing information.

## Project Background

Parchate appears to be a university project, as indicated by the package name `com.universidad.parchate`. The application focuses on event discovery and social planning, with particular emphasis on Colombian events and locations.

## Acknowledgments

- Google for Android development tools, Firebase, and Maps Platform
- JetBrains for Kotlin programming language
- Groq for providing AI inference capabilities
- The open-source community for various libraries used in this project