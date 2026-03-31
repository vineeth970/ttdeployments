$ErrorActionPreference = "Stop"

if (-Not (Test-Path "$env:TEMP\jdk17")) {
    Write-Host "Downloading OpenJDK 17..."
    Invoke-WebRequest -Uri "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip" -OutFile "$env:TEMP\jdk17.zip"
    Write-Host "Extracting OpenJDK 17..."
    Expand-Archive -Path "$env:TEMP\jdk17.zip" -DestinationPath "$env:TEMP\jdk17" -Force
}

if (-Not (Test-Path "$env:TEMP\maven")) {
    Write-Host "Downloading Maven..."
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile "$env:TEMP\maven.zip"
    Write-Host "Extracting Maven..."
    Expand-Archive -Path "$env:TEMP\maven.zip" -DestinationPath "$env:TEMP\maven" -Force
}

$env:JAVA_HOME = "$env:TEMP\jdk17\jdk-17.0.2"
$env:M2_HOME = "$env:TEMP\maven\apache-maven-3.9.6"
$env:PATH = "$env:JAVA_HOME\bin;$env:M2_HOME\bin;$env:PATH"

Write-Host "Java Version:"
java -version
Write-Host "Maven Version:"
mvn -version

cd "c:\Users\shivani reddy\Downloads\scrms-backend-fixed\scrms-backend-fixed"
mvn clean compile
