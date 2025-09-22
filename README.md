## 📰 **News Headlines App - README.md**

```markdown
# News Headlines App

Aplikasi Android sederhana yang menampilkan daftar headline berita dari NewsAPI.org dengan fitur
navigasi ke detail artikel.

## 🚀 How to Run the App

### Prerequisites

- **Android Studio** Ladybug | 2024.2.1 atau yang lebih baru
- **Android SDK** dengan API level 24 (Android 7.0) atau lebih tinggi
- **Java 11** atau lebih baru
- **Internet connection** untuk mengakses NewsAPI.org

### Setup Steps

1. **Clone/Download** project ini ke local machine
2. **Open** project di Android Studio
3. **Wait** untuk Gradle sync selesai
4. **Connect** Android device atau setup emulator (API 24+)
5. **Configure Apikey** API Key NewsAPI dikonfigurasi di [
   `Constants.kt`](app/src/main/java/com/mangazep/newsheadlinesapp/util/Constants.kt)
5. **Run** aplikasi dengan menekan tombol Run (▶️) atau `Shift + F10`

### Configuration

- API Key NewsAPI sudah dikonfigurasi di [
  `Constants.kt`](app/src/main/java/com/mangazep/newsheadlinesapp/util/Constants.kt)
- Default country: Indonesia (`"id"`)
- Base URL: `https://newsapi.org/v2/`

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

## ⭐ What I'm Proud Of / Enjoyed Building

### 1. **Clean Architecture Implementation**

- **MVVM Pattern** dengan proper separation of concerns
- **Repository Pattern** untuk clean data layer abstraction
- **Dependency Injection** menggunakan Hilt untuk scalable codebase
- **Package structure** yang terorganisir dengan baik

### 2. **Modern Android Development Stack**

- **Jetpack Compose** untuk modern, declarative UI
- **Paging 3** untuk efficient data loading dengan infinite scroll
- **Navigation Compose** untuk type-safe navigation
- **Coroutines + Flow** untuk reactive programming

### 3. **Comprehensive Error Handling**

- **Network errors** dengan retry functionality
- **Empty state** dengan user-friendly messaging
- **Loading states** dengan proper indicators
- **Exception handling** di semua layer (UI, Repository, Network)

### 4. **Professional UI/UX**

- **Material Design 3** components
- **Responsive image loading** dengan Coil
- **Proper state management** dengan sealed classes
- **Snackbar notifications** untuk user feedback

### 5. **Robust Testing Strategy**

- **Unit tests** untuk ViewModel dan Repository layers
- **Mocking strategies** untuk network calls
- **Edge case testing** (empty data, network errors)

## 🔧 What I'd Improve or Add Next

### Features Improvements

- **Offline support** dengan Room database caching
- **Search functionality** untuk filtering articles
- **Category filtering** (sports, technology, etc.)
- **Share article** functionality
- **Bookmark/Favorites** feature
- **Dark theme** support

### Technical Improvements

- **Crashlytics** integration
- **Performance monitoring** dengan Firebase
- **Modularization** untuk multi-module architecture

## 🎯 Challenges Faced and Solutions

### 1. **Country Parameter Issue**

**Challenge**: Default country parameter menggunakan `"us"` yang tidak sesuai requirement Indonesia.

**Solution**:

- Update [
  `NewsApiService.kt`](app/src/main/java/com/mangazep/newsheadlinesapp/data/api/NewsApiService.kt)
  default country ke `"id"`
- Implementasi fallback mechanism jika data Indonesia kosong

### 2. **Empty State Handling**

**Challenge**: Ketika API mengembalikan data kosong, user tidak mendapat feedback yang jelas.

**Solution**:

- Tambah `HeadlinesUiState.Empty` state
- Implementasi snackbar message untuk empty state
- Buat dedicated empty state UI dengan retry button

### 3. **Paging Implementation**

**Challenge**: Mengimplementasikan pagination yang efficient dengan error handling.

**Solution**:

- Gunakan **Paging 3** library dengan `PagingSource`
- Implementasi proper error handling di `NewsPagingSource`
- Handle loading states untuk initial load dan pagination

### 4. **Navigation with Complex Data**

**Challenge**: Passing Article object antar screen dengan type safety.

**Solution**:

- Implementasi `@Parcelize` untuk Article data class
- Gunakan Navigation Compose dengan `savedStateHandle`
- Type-safe navigation dengan proper argument handling

### 5. **Testing Challenges**

**Challenge**: Testing suspend functions dan LiveData interactions.

**Solution**:

- Implementasi `InstantTaskExecutorRule` untuk LiveData testing
- Gunakan `TestDispatcher` untuk coroutines testing
- Mock network calls dengan `doAnswer` untuk suspend functions

### 6. **Image Loading Performance**

**Challenge**: Handling image loading dari URL dengan berbagai ukuran.

**Solution**:

- Implementasi **Coil** untuk efficient image loading
- Placeholder dan error handling untuk images
- Proper sizing dan content scaling

## 🏗️ Architecture Overview

```
📁 app/src/main/java/com/mangazep/newsheadlinesapp/
├── 📁 data/
│   ├── 📁 api/          # Network interfaces
│   ├── 📁 model/        # Data classes
│   ├── 📁 paging/       # Paging source
│   └── 📁 repository/   # Data repositories
├── 📁 di/               # Dependency injection
├── 📁 ui/
│   ├── 📁 components/   # Reusable UI components
│   ├── 📁 headlines/    # Headlines screen
│   ├── 📁 detail/       # Detail screen
│   ├── 📁 navigation/   # Navigation setup
│   └── 📁 theme/        # App theming
└── 📁 util/            # Utilities & constants
```

## 🛠️ Tech Stack

| Category          | Technology                |
|-------------------|---------------------------|
| **Language**      | Kotlin 100%               |
| **UI Framework**  | Jetpack Compose           |
| **Architecture**  | MVVM + Repository Pattern |
| **DI**            | Hilt (Dagger)             |
| **Networking**    | Retrofit + OkHttp         |
| **Image Loading** | Coil                      |
| **Pagination**    | Paging 3                  |
| **Navigation**    | Navigation Compose        |
| **Testing**       | JUnit + Mockito + Truth   |
| **Build Tool**    | Gradle (Kotlin DSL)       |

## 📊 Key Features

- ✅ **News Headlines List** dengan infinite scrolling
- ✅ **Article Detail View** dengan title, description, dan image
- ✅ **Pull-to-refresh** functionality
- ✅ **Error handling** dengan retry mechanism
- ✅ **Loading indicators** untuk better UX
- ✅ **Empty state handling** dengan snackbar notifications
- ✅ **Offline-first ready** architecture
- ✅ **Type-safe navigation**
- ✅ **Comprehensive unit testing**

## 🧪 Testing

Unit test coverage untuk:

- ✅ **HeadlinesViewModel** - All UI states and user interactions
- ✅ **DetailViewModel** - Article data management
- ✅ **NewsRepository** - Data layer abstraction
- ✅ **NewsPagingSource** - Network calls and error handling

Run tests:

```bash
./gradlew testDebugUnitTest
```

---

*Last updated: September 2025*

```
