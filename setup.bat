@echo off
echo Проверка Gradle Wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper JAR не найден. Скачивание...
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.14.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    if %ERRORLEVEL% EQU 0 (
        echo Gradle wrapper успешно скачан!
    ) else (
        echo Ошибка при скачивании wrapper. Проверьте подключение к интернету.
        pause
        exit /b 1
    )
) else (
    echo Gradle wrapper уже найден.
)

echo.
echo Запуск приложения...
.\gradlew.bat run
