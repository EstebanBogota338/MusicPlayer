#!/bin/bash
set -Eeuo pipefail

check_java() {
    if ! command -v java &> /dev/null; then
        echo "Java no está instalado"
        exit 1
    fi

    if ! command -v javac &> /dev/null; then
        echo "JDK no está instalado (solo JRE)"
        exit 1
    fi

    echo "JDK instalado correctamente"
    java -version
}

download_tracks(){
  # Verificar si ya existe la carpeta 
  if [ -d "src/tracks" ]; then
    echo "Carpeta src/tracks ya existe, omitiendo descarga"
    return 0
  fi

  # Descargar el archivo zip en src/
  curl -sL -o src/tracks.zip https://github.com/ZeroProtecPlus/MusicPlayer/releases/download/v1-tracks/tracks.zip
  
  # Descomprimir y limpiar
  unzip -q src/tracks.zip -d src/
  rm src/tracks.zip
  
  echo "Tracks descargados correctamente"
}

run(){
  echo "Ejecutando Programa..."
  javac -d out src/*.java
  java -cp out Main
}

check_java
download_tracks
run