# LRU Client

Минимальное современное Java desktop-приложение под Windows
Gradle + JavaFX + (JAR) + jpackage (EXE)

## Быстрый запуск

### Рекомендуемый способ (первый запуск):
```powershell
.\setup.bat
```
Или просто дважды кликните на файл `setup.bat`

Скрипт автоматически:
- Проверит наличие Gradle wrapper
- Скачает его если нужно
- Запустит приложение

### Альтернативный способ:
```powershell
.\gradlew.bat run
```

**Инструкция:**
1. Откройте папку проекта в проводнике
2. Дважды кликните на `setup.bat` **ИЛИ**
3. Нажмите правой кнопкой мыши на пустом месте в папке
4. Выберите "Открыть PowerShell здесь" или "Открыть терминал здесь"
5. Вставьте команду: `.\gradlew.bat run`
6. Нажмите Enter

Приложение запустится автоматически.

---

## Требования
- JDK 17+ должен быть установлен

**Проверка версии Java:**
```powershell
java -version
```

---

## Структура проекта
- build.gradle / settings.gradle — сборка Gradle
- src/main/java/app/MainApp.java — точка входа JavaFX
- src/main/java/app/MainController.java — контроллер для FXML
- src/main/resources/ui.fxml — разметка UI

---

## Для разработчиков

### Запуск в режиме разработки
```powershell
.\setup.bat
```
Или классически:
```powershell
.\gradlew.bat run
```

### Сборка JAR
```powershell
.\gradlew.bat clean jar
```
Результат: build/libs/myapp-1.0.0.jar

### Упаковка в EXE/MSI
```powershell
.\gradlew.bat clean jpackage
```

### Два режима сборки:

**1) DEBUG (портативная папка для тестов; перезаписывается)**
```powershell
.\gradlew.bat packageDebug
```
Результат: dist/debug/MyApp/
Запуск: dist/debug/MyApp/MyApp.exe

**2) RELEASE (каждая сборка сохраняется отдельным файлом)**
```powershell
.\gradlew.bat -PappVersion="1.0.0" packageRelease
```
Результат: dist/releases/MyApp-1.0.0-YYYYMMDD-HHMMSS.msi

---

## Где искать результаты
- build/jpackage/ — результаты jpackage
- dist/debug/ — debug app-image (папка)
- dist/releases/ — история релизных MSI

---

## Устранение проблем

**Ошибка: "Unable to access jarfile gradle-wrapper.jar"**
Решение: Файл gradle-wrapper.jar уже должен быть в папке. Если отсутствует, скачайте его вручную.

**Приложение не запускается**
1. Проверьте установку JDK 17+
2. Убедитесь, что вы в правильной папке проекта
3. Попробуйте перезапустить PowerShell

---

## Подсказка для пользователей
Для демонстрации приложения достаточно дважды кликнуть на `setup.bat` или выполнить:
```powershell
.\setup.bat
```
Всё остальное настроено автоматически. Скрипт сам скачает нужные файлы.
