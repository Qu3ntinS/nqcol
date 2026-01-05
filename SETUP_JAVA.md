# Java Setup Instructions

## Issue
You have Java 25 installed, but Gradle 8.10.2 only supports up to Java 23. Additionally, Minecraft 1.21.4 requires Java 21.

## Solution

You need to install **Java 21** (LTS version) and configure your system to use it for this project.

### Option 1: Install Java 21 (Recommended)

1. Download Java 21 from one of these sources:
   - **Eclipse Adoptium (Temurin)**: https://adoptium.net/temurin/releases/?version=21
   - **Oracle JDK**: https://www.oracle.com/java/technologies/downloads/#java21

2. Install Java 21

3. Set JAVA_HOME environment variable to point to Java 21:
   ```powershell
   # Find where Java 21 is installed (usually C:\Program Files\Java\jdk-21)
   # Then set JAVA_HOME:
   [System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-21', 'User')
   ```

4. Restart your terminal/PowerShell

5. Verify:
   ```powershell
   java -version
   # Should show version 21
   ```

### Option 2: Use Java 21 Just for This Project

If you want to keep Java 25 as default but use Java 21 for this project:

1. Install Java 21 (see Option 1, step 1-2)

2. Create a `gradle.properties` file in the project root (already created) and set:
   ```properties
   org.gradle.java.home=C:\\Program Files\\Java\\jdk-21
   ```
   (Update the path to match your Java 21 installation)

### Option 3: Use SDKMAN (if on WSL/Linux/Mac)

If you're using WSL or have SDKMAN:
```bash
sdk install java 21.0.1-tem
sdk use java 21.0.1-tem
```

## After Installing Java 21

Once Java 21 is installed and configured, run:
```powershell
.\gradlew.bat build
```

The build should now work!

