
# Wakey App

Wakey is a Flutter-based mobile application designed to prevent devices from sleeping or locking, ensuring continuous usage for various scenarios like presentations, media playback, and more. The app leverages Android's foreground services and wake locks to keep the screen on and prevent the device from entering sleep mode.

## Features

- **Wake Lock Management**: Prevents the device from sleeping by acquiring a wake lock.
- **Foreground Service**: Runs a foreground service with a persistent notification to keep the app active.
- **Notification Handling**: Custom notifications to inform users when the service is running.
- **Screen Timeout Modification**: Optionally modifies system settings to prevent screen timeout.

## Getting Started

### Prerequisites

To run this project, you need to have the following installed:

- [Flutter SDK](https://flutter.dev/docs/get-started/install)
- [Android Studio](https://developer.android.com/studio) or another IDE with Flutter support
- Android device or emulator

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/wakey.git
   cd wakey
   ```

2. **Install dependencies**:

   Run the following command to get all the dependencies:

   ```bash
   flutter pub get
   ```

3. **Configure Android Settings**:

    - Open `android/app/src/main/AndroidManifest.xml` and ensure you have the necessary permissions:

   ```xml
   <uses-permission android:name="android.permission.WAKE_LOCK"/>
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
   <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
   <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
   ```

    - Set up the notification channel and foreground service settings in your `MainActivity.kt` and `WakeService.kt` files.

4. **Build the project**:

   Run the app on an emulator or a connected device:

   ```bash
   flutter run
   ```

## Usage

- **Start the Wake Service**: Open the app and click on the "Start Wake Service" button. This will start the foreground service and acquire a wake lock to prevent the device from sleeping.
- **Stop the Wake Service**: Click on the "Stop Wake Service" button to release the wake lock and stop the foreground service.
- **Notification Interaction**: Tapping on the persistent notification will open the app.

## Customization

### Notification Icon

To customize the notification icon:

1. Add your custom icon to the `android/app/src/main/res/drawable` directory.
2. Reference your icon in the `WakeService.kt` notification setup:

```kotlin
.setSmallIcon(R.drawable.your_custom_icon)
```

### Modify Screen Timeout

The app includes an optional feature to modify the system screen timeout settings to prevent the screen from turning off:

- Ensure that the necessary permission is granted to write system settings. The app will prompt the user to grant this permission.

## Contributing

Contributions are welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/my-new-feature`.
3. Commit your changes: `git commit -am 'Add some feature'`.
4. Push to the branch: `git push origin feature/my-new-feature`.
5. Submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or issues, please contact [your.email@example.com](mailto:your.email@example.com).

---

Thank you for using Wakey! We hope it serves your needs for keeping your device awake and running.
