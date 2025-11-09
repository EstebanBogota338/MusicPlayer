# Music Player

## English

### Description

A fully functional music player developed in Java with a modern dark and gold themed interface. Features include playlist management, audio visualization, and an intuitive user interface.

### Requirements

- JDK (Java Development Kit) 11 or higher
- Bash shell (for running the automated script)

### Installation and Execution

#### On Linux

1. Open a terminal in the project directory
2. Make the script executable:

   ```bash
   chmod +x scripts/main.sh
   ```

3. Run the script:

   ```bash
   ./scripts/main.sh
   ```

#### On Windows

You have two options:

##### Option 1: Using Git Bash (Recommended)

1. Install [Git for Windows](https://git-scm.com/download/win) if you haven't already
2. Open Git Bash in the project directory
3. Run the script:

   ```bash
   ./scripts/main.sh
   ```

##### Option 2: Using WSL (Windows Subsystem for Linux)

1. Install WSL if you haven't already
2. Open WSL terminal in the project directory
3. Run the script:

   ```bash
   ./scripts/main.sh
   ```

### What does the script do?

The `main.sh` script automatically:

- Checks if JDK is properly installed
- Downloads the music tracks folder (if not already present)
- Compiles all Java source files
- Runs the application

### Manual Execution

If you prefer to run the application manually:

```bash
# Compile the source files
javac -d out src/*.java

# Run the application
java -cp out Main
```

---

## Español

### Descripción

Un reproductor de música completamente funcional desarrollado en Java con una interfaz moderna de tema oscuro y dorado. Incluye gestión de listas de reproducción, visualización de audio e interfaz de usuario intuitiva.

### Requisitos

- JDK (Java Development Kit) 11 o superior
- Shell Bash (para ejecutar el script automatizado)

### Instalación y Ejecución

#### En Linux

1. Abre una terminal en el directorio del proyecto
2. Dale permisos de ejecución al script:

   ```bash
   chmod +x scripts/main.sh
   ```

3. Ejecuta el script:

   ```bash
   ./scripts/main.sh
   ```

#### En Windows

Tienes dos opciones:

##### Opción 1: Usando Git Bash (Recomendado)

1. Instala [Git para Windows](https://git-scm.com/download/win) si aún no lo tienes
2. Abre Git Bash en el directorio del proyecto
3. Ejecuta el script:

   ```bash
   ./scripts/main.sh
   ```

##### Opción 2: Usando WSL (Windows Subsystem for Linux)

1. Instala WSL si aún no lo tienes
2. Abre la terminal WSL en el directorio del proyecto
3. Ejecuta el script:

   ```bash
   ./scripts/main.sh
   ```

### ¿Qué hace el script?

El script `main.sh` automáticamente:

- Verifica que JDK esté correctamente instalado
- Descarga la carpeta de pistas musicales (si no está presente)
- Compila todos los archivos fuente de Java
- Ejecuta la aplicación

### Ejecución Manual

Si prefieres ejecutar la aplicación manualmente:

```bash
# Compilar los archivos fuente
javac -d out src/*.java

# Ejecutar la aplicación
java -cp out Main
```
