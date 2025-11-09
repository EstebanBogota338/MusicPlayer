import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        UIInformation info = new UIInformation();
        MusicPlayer player = new MusicPlayer(info);

    // Abrir selector de carpeta
        String carpetaSeleccionada = info.selectMusicFolder(null);

        if (carpetaSeleccionada == null) {
            info.showException(
                    "Sin Selección",
                    "No se seleccionó ninguna carpeta de música",
                    "Operación cancelada"
            );
            return;
        }

        File tracksFolder = new File(carpetaSeleccionada);

        if (!tracksFolder.exists() || !tracksFolder.isDirectory()){
            info.showException(
                    "Carpeta no válida",
                    "La carpeta seleccionada no existe o no es válida",
                    "Error, sistema de archivos"
            );
            return;
        }

        //Almacenar las rutas de las canciones
        List<File> songFiles = new ArrayList<>();

        //Filtramos solo los archivos .wav
        File [] files = tracksFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (files == null || files.length == 0){
            info.showException(
                    "Carpeta vacia",
                    "No se encontro ningun elemento dento de la carpeta 'Tracks' ",
                    "Error, sistema de archivos"
            );
            return;
        }

        // ========== AQUÍ EMPIEZA LA ANIMACIÓN ==========

        // Crear la ventana de carga
        UIInformation.LoadingDialog loadingWindow = info.createLoadingWindow(null);

        // Mostrar la ventana en un hilo separado
        new Thread(() -> {
            loadingWindow.setVisible(true);
        }).start();

        // Cargar archivos con animación
        new Thread(() -> {
            int total = files.length;

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                songFiles.add(f);

                // Actualizar progreso
                loadingWindow.updateProgress(i + 1, total, f.getName());

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Pausa antes de cerrar
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Cerrar ventana
            loadingWindow.close();
            info.showSuccess(
                    "Carga completada!",
                    "Se cargaron " + songFiles.size() + " canciones",
                    "Musica lista"
            );

            //Mostrar el menú
            SwingUtilities.invokeLater(() -> {
                new UIMusicPlayer(songFiles, info).setVisible(true);
            });
        }).start();
    }
}