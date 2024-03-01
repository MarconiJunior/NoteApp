# NoteApp (Jetpack Compose) 
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-%230075FF.svg)](https://developer.android.com/jetpack/compose)
![badge-Android](https://img.shields.io/badge/Platform-Android-brightgreen)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

Note application built with jetpack Compose and MVVM architecture.<br>

# Main Features

- Create, Read, Update and Delete note
- Set/Edit Note color
- Order notes list by date, color and name in order descending or ascending
- Undo delete action

 <p align="center" width="100%">
   <img width="30%" height="50%" src="https://github.com/MarconiJunior/NoteApp/assets/102702355/57883e4c-dfc3-4388-a151-3e2300a0217d" />
 </p>

## Architecture 🏗️
  - MVVM Architecture (Model - ComposableView - ViewModel)
  - Repository pattern
  - Hilt - dependency injection

<p align="center">
  <img width="40%" height="25%" src="https://github.com/piashcse/Hilt-MVVM-Compose-Movie/blob/master/screenshots/mvvm.png" />
</p>
<p align="center">
<b>Fig.  MVVM (Model - ComposableView - ViewModel) design pattern.</b>
</p>

## Built With 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Jetpack Compose is Android’s modern toolkit for building native UI.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
  - [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - The Paging library helps you load and display pages of data from a larger dataset from local storage or over network
- [Dependency Injection](https://developer.android.com/training/dependency-injection)
  - [Hilt](https://dagger.dev/hilt) - Easier way to incorporate Dagger DI into Android apps.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
