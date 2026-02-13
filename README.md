# Banking Management System

## Overview

This is a native Android application built using Kotlin for the Final Exam of Software Engineering Year 3. It simulates a banking system where users can view their balance, transfer money, withdraw funds, and analyze their financial activities.

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern with **Repository Pattern** to separate data logic from UI.

- **Data Layer**:
  - **Room Database**: For local data persistence (`BankingDatabase`, `BankingDao`).
  - **Repository**: `BankingRepository` handles data operations for the ViewModel.
  - **Entities**: `User` and `Transaction` classes represents the database tables.

- **UI Layer**:
  - **Single Activity**: `MainActivity` hosts the `NavHostFragment`.
  - **Fragments**: `HomeFragment`, `TransferFragment`, `WithdrawFragment`, `AnalyticsFragment`, `ProfileFragment`.
  - **ViewBinding**: Replaces `findViewById` for type-safe view interaction.
  - **Navigation Component**: Manages app navigation and graph (`nav_graph.xml`).

- **ViewModel**: `BankingViewModel` survives configuration changes and provides data to the UI using `LiveData`.

## Features

1.  **Dashboard (Home)**:
    - Displays Total Current Balance and Account Number.
    - Quick actions for Transfer, Withdraw, Analytics, and Profile.
    - **RecyclerView** displaying recent transactions with custom adapters.

2.  **Money Transfer**:
    - Input recipient name and amount.
    - Validates sufficient funds before processing.
    - Updates User balance and records the transaction.

3.  **Withdrawal**:
    - Custom Keypad UI for inputting amount.
    - Real-time balance validation.

4.  **Analytics**:
    - **MPAndroidChart** (PieChart) visualizing Income vs. Expenses.
    - Dynamic calculation based on transaction history.

5.  **Profile Management**:
    - View user details.
    - Edit User Name.
    - **Dark Mode** toggle support.

## Tech Stack

- **Language**: Kotlin
- **Minimum SDK**: 21
- **Target SDK**: 34
- **Persistence**: Room Database
- **UI**: XML Layouts (Material Design 3)
- **Navigation**: Jetpack Navigation Component
- **Charts**: MPAndroidChart

## Setup & Requirements

1.  Open the project in Android Studio.
2.  Sync Gradle files.
3.  Run on an emulator or device (API 21+).
