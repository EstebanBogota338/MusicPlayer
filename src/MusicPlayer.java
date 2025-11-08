import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    private  Clip clip;
    private boolean paused = false;
    private long pausePosition = 0;
    private int queuedCount = 0;
    private boolean ignoreStopEvent = false;
    private final UIInformation info;
    private File currentFile;
    private String pausedTimeText = "";
    private boolean repeatMode = false;
    private boolean shuffleMode = false;

    //Fila, playlist base
    private List <File> playlist;
    private  int currentIndex = 0;

    private List<File> originalPlaylistOrder;
    private int originalCurrentIndex;

    //Constructor
    public MusicPlayer(UIInformation info){
        this.info = info;
        this.playlist = new ArrayList<>();
    }

    public void setPosition(long microseconds) {
        if (clip != null && clip.isOpen()) {
            try {
                // Solo cambiar posición si es significativamente diferente
                long currentPos = clip.getMicrosecondPosition();
                if (Math.abs(currentPos - microseconds) > 100000) { // 0.1 segundos de diferencia

                    boolean wasPlaying = clip.isRunning();

                    if (wasPlaying) {
                        clip.stop();
                    }

                    clip.setMicrosecondPosition(microseconds);

                    if (paused) {
                        pausePosition = microseconds;
                    }

                    // Reanudar solo si estaba reproduciendo y no está en pausa
                    if (wasPlaying && !paused) {
                        clip.start();
                    }
                }

            } catch (Exception e) {
                info.showException("Error al cambiar posición",
                        "No se pudo cambiar la posición de reproducción");
            }
        }
    }

    public long getCurrentTime(){
        if (clip != null && clip.isOpen()){
            return clip.getMicrosecondPosition();
        }
        return 0;
    }

    public long getTotalTime(){
        if (clip != null && clip.isOpen()){
            return clip.getMicrosecondLength();
        }
        return 0;
    }

    public String formatTime(long microseconds){
        long totalSeconds = microseconds / 1_000_000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void toggleShuffleMode() {
        this.shuffleMode = !this.shuffleMode;

        if (shuffleMode) {
            // Al activar shuffle: guardar orden actual
            originalPlaylistOrder = new ArrayList<>(playlist);
            originalCurrentIndex = currentIndex;

            // Mezclar la playlist (excepto la canción actual)
            if (playlist.size() > 1) {
                List<File> remainingSongs = new ArrayList<>();
                File currentSong = playlist.get(currentIndex);

                // Separar la canción actual del resto
                for (int i = 0; i < playlist.size(); i++) {
                    if (i != currentIndex) {
                        remainingSongs.add(playlist.get(i));
                    }
                }

                // Mezclar las demás canciones
                Collections.shuffle(remainingSongs);

                // Reconstruir playlist: canción actual + resto mezclado
                playlist.clear();
                playlist.add(currentSong);
                playlist.addAll(remainingSongs);
                currentIndex = 0; // La canción actual queda en posición 0
            }
        } else {
            // Al desactivar shuffle: restaurar orden original
            if (originalPlaylistOrder != null && !originalPlaylistOrder.isEmpty()) {
                playlist.clear();
                playlist.addAll(originalPlaylistOrder);

                // Encontrar la posición de la canción actual en el orden original
                File currentSong = getCurrentFile();
                if (currentSong != null) {
                    currentIndex = playlist.indexOf(currentSong);
                    if (currentIndex == -1) currentIndex = originalCurrentIndex;
                }
            }
        }
    }

    //Cargar cancion especifica
    public  void load(File audioFile){
        if (audioFile == null) {
            info.showException("Archivo no válido",
                    "No se proporcionó ningún archivo de audio");
            return;
        }

        if (playlist == null){
            playlist = new ArrayList<>();
        }

        int idx = playlist.indexOf(audioFile);
        if (idx == - 1){
            playlist.add(audioFile);
            currentIndex = playlist.size() - 1;
        }else{
            currentIndex = idx;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
            if (clip != null && clip.isOpen()){
                clip.close();
                clip = null;
            }

            clip = AudioSystem.getClip();
            clip.open(audioStream);

            currentFile = audioFile;
            paused = false;
            pausePosition = 0;

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (ignoreStopEvent) {
                        ignoreStopEvent = false; // limpiar la bandera
                        return;
                    }

                    long clipLength = clip.getMicrosecondLength();
                    long currentPos = clip.getMicrosecondPosition();
                    boolean finished = Math.abs(clipLength - currentPos) < 1_000_000; // 1 segundo

                    if (!paused && finished) {
                        SwingUtilities.invokeLater(() -> next());
                    }
                }
            });

        } catch (UnsupportedAudioFileException e) {
            info.showException(
                    "Archivo no soportado",
                    "El formato del archivo " + audioFile.getName() + " no es compatible",
                    "Error de audio"
            );
        } catch (IOException e) {
            info.showException(
                    "Error de lectura",
                    "Ocurrio un problema al leer el archivo",
                    "Error del sistema"
            );
        } catch (LineUnavailableException e) {
            info.showException(
                    "Error de audio",
                    "No se pudo acceder al dispositivo de sonido",
                    "Error del sistema"
            );
        }
    }

    public void play(){
        if (clip == null){
            info.showException(
                    "No hay ninguna canción cargada",
                    "Cargue una canción antes de reproducir"
            );
            return;
        }

        if (clip.isRunning()){
            return;
        }

        try {
            ignoreStopEvent = true;

            if (paused){
                clip.setMicrosecondPosition(pausePosition);
            }

            clip.start();
            paused = false;

        }catch (Exception e){
            info.showException(
                    "Error al reproducir",
                    "Ocurrió un problema al iniciar la reproducción",
                    "Error de audio"
            );
        }
    }

    public void pause(){
        if (clip == null){
            info.showException(
                    "No se puede pausar",
                    "No hay ninguna canción cargada"
            );
            return;
        }

        if (clip.isRunning()){
            ignoreStopEvent = true;

            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            paused = true;

            long totalSeconds = pausePosition / 1_000_000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;

            pausedTimeText = String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void stop (){
        if (clip == null || !clip.isOpen()){
            return;
        }

        if (clip !=null){
            ignoreStopEvent = false;
            clip.stop();
            clip.setMicrosecondPosition(0);
            paused = false;
            pausePosition = 0;
        }
    }

    public void restart(){
        if (clip == null || !clip.isOpen()){
            return;
        }

        if (clip != null){
            ignoreStopEvent = false;
            clip.stop();
            clip.setMicrosecondPosition(0);
            pausePosition = 0;
            paused = false;
            clip.start();
        }
    }

    public void addToQueue(File file){
        if (file == null){
            return;
        }

        if (playlist == null){
            playlist = new ArrayList<>();
        }

        int insertIndex = currentIndex + 1;
        if (insertIndex > playlist.size()){
            insertIndex = playlist.size();
        }

        playlist.add(insertIndex, file);
        queuedCount++;

        info.showSuccess(
                "Agregada a la cola",
                file.getName() + " se reproducirá después de la actual",
                "Cola de reproducción"
        );
    }

    public  void addToPlaylist(File file){
        if (file == null){
            return;
        }

        if (playlist == null){
            playlist = new ArrayList<>();
        }
        playlist.add(file);

        info.showSuccess(
                "Agregada a la playlist",
                file.getName() + " se añadió al final de la fila",
                "Fila de reproducción"
        );
    }

    public void clearQueue() {
        if (queuedCount <= 0 || playlist == null) {
            info.showException(
                    "Cola vacía",
                    "No hay canciones en la cola para eliminar",
                    "Cola de reproducción"
            );
            return;
        }

        int removeIndex = currentIndex + 1;
        if (removeIndex < playlist.size()){
            File removed = playlist.remove(removeIndex);
            queuedCount--;

            info.showSuccess(
                    "Canción removida de la cola",
                    removed.getName() + " fue eliminada de la parte superior de la cola",
                    "Cola de reproducción"
            );
        }
    }

    public void clearPlaylist(){
        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Playlist vacía",
                    "No hay canciones en la lista para eliminar"
            );
            return;
        }

        stop();
        close();
        playlist.clear();
        currentIndex = 0;
        currentFile = null;
        if (originalPlaylistOrder != null) {
            originalPlaylistOrder.clear();
        }

        info.showSuccess(
                "Playlist eliminada",
                "Se eliminaron todas las canciones de la lista base",
                "Fila de reproducción"
        );
    }

    public void next(){

        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Sin canciones",
                    "No hay una lista cargada"
            );
            return;
        }

        if (shuffleMode) {
            // Modo aleatorio: seleccionar canción random
            if (playlist.size() > 1) {
                int randomIndex;
                do {
                    randomIndex = (int) (Math.random() * playlist.size());
                } while (randomIndex == currentIndex && playlist.size() > 1);

                currentIndex = randomIndex;
            } else {
                currentIndex = 0;
            }
        }
        else if (repeatMode) {
            currentIndex = (currentIndex + 1) % playlist.size();
        } else {
            currentIndex++;

            if (currentIndex >= playlist.size()){
                stop();
                currentIndex = Math.max(0, playlist.size() - 1);
                return;
            }
        }

        if (queuedCount > 0) {
            queuedCount--;
        }

        paused = false;
        pausePosition = 0;
        load(playlist.get(currentIndex));
        play();
    }

    public void previous(){
        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Sin canciones",
                    "No hay una lista cargada"
            );
            return;
        }

        if (currentIndex <= 0){
            paused = false;
            pausePosition = 0;
            load(playlist.get(currentIndex));
            play();
            return;
        }

        currentIndex--;

        paused = false;
        pausePosition = 0;
        load(playlist.get(currentIndex));
        play();
    }

    //Verificar que se esté reproduciendo alguna canción
    public boolean isPlaying(){
        return clip != null && clip.isRunning();
    }

    //Verificar si está pausada
    public  boolean isPaused(){
        return paused;
    }

    //Cerrar recursos
    public void close(){
        if (clip != null){
            clip.close();
            clip = null;
            currentFile = null;
        }
    }
    //Getters
    public File getCurrentFile() {
        return currentFile;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getQueuedCount() {
        return queuedCount;
    }

    public List<File> getPlaylist() {
        return playlist;
    }

    public String getPausedTimeText() {
        return pausedTimeText;
    }

    public boolean isRepeatMode(){
        return repeatMode;
    }

    public String getCurrentTimeFormatted(){
        return formatTime(getCurrentTime());
    }

    public String getTotalTimeFormatted(){
        return formatTime(getTotalTime());
    }

    public boolean isShuffleMode() {
        return shuffleMode;
    }

    // Setters
    public void toggleRepeatMode(){
        this.repeatMode = !this.repeatMode;
    }
}