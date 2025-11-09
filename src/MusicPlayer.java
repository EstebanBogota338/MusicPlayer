import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    //Atributos

    private  Clip clip;
    //Indica si la reproducción está en pausa
    private boolean paused = false;
    //Microsegundo donde se pausó
    private long pausePosition = 0;
    //Número de canciones en la cola de reproducción
    private int queuedCount = 0;
    //Evita eventos duplicados al detener
    private boolean ignoreStopEvent = false;
    private final UIInformation info;
    // Archivo actual de audio cargado
    private File currentFile;
    //Formato en segundos para ver donde se pausó
    private String pausedTimeText = "";
    private boolean repeatMode = false;
    private boolean shuffleMode = false;

    //Playlist principal - Lista de archivos de audio disponibles
    private List <File> playlist;
    // Busca el índice de la canción actual en nuestro ArrayList playlist
    private  int currentIndex = 0;

    //Copia de seguridad de la playlist, usada para restaurar al desactivar modo aleatorio
    private List<File> originalPlaylistOrder;
    private int originalCurrentIndex;

    //Constructor
    public MusicPlayer(UIInformation info){
        this.info = info;
        this.playlist = new ArrayList<>();
    }

    // Método para asignar la posición de la canción según la barra de progreso
    public void setPosition(long microseconds) {
        if (clip != null && clip.isOpen()) {
            try {
                //Obtenemos la posición actual de reproducción en microsegundos
                long currentPos = clip.getMicrosecondPosition();
                // Cambiamos si hay diferencia mayor a 0.1 segundos
                if (Math.abs(currentPos - microseconds) > 100000) {

                    boolean wasPlaying = clip.isRunning();

                    //Si se estaba reproduciendo lo detenemos temporalmente
                    if (wasPlaying) {
                        clip.stop();
                    }

                    //Aquí ocurre la magia, cambiamos la posición del clip
                    clip.setMicrosecondPosition(microseconds);

                    //Si estaba pausada, actualizamos la posición de pausa
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

    // Obtener los segundos actuales, usado para actualizar la interfaz, actualizar la barra de progreso e.t.c
    public long getCurrentTime(){
        if (clip != null && clip.isOpen()){
            return clip.getMicrosecondPosition();
        }
        return 0;
    }

    //Similar a la anterior, pero con el tiempo total en vez del tiempo actual
    public long getTotalTime(){
        if (clip != null && clip.isOpen()){
            return clip.getMicrosecondLength();
        }
        return 0;
    }

    //Ponemos el formato de segundos a minutos y segundos
    public String formatTime(long microseconds){
        long totalSeconds = microseconds / 1_000_000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    //Activar o desactivar modo aleatorio
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

    //Carga de archivos
    public  void load(File audioFile){
        if (audioFile == null) {
            info.showException("Archivo no válido",
                    "No se proporcionó ningún archivo de audio");
            return;
        }

        // Si la playlist está vacía inicializamos una nueva
        if (playlist == null){
            playlist = new ArrayList<>();
        }

        // ============ GESTIÓN DE PLAYLIST INTELIGENTE ============

        //Buscamos si el archivo ya existe en la lista
        int idx = playlist.indexOf(audioFile);

        //Si no fue encontrado el archivo, haremos lo siguiente
        if (idx == - 1){
            //Lo agregamos
            playlist.add(audioFile);
            //Lo asignamos a la última posición
            currentIndex = playlist.size() - 1;
        }else{
            currentIndex = idx;
        }

        // ============ CARGA DEL AUDIO ============

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {

            //Libera los recursos anteriores
            if (clip != null && clip.isOpen()){
                clip.close();
                clip = null;
            }

            //Crea un nuevo clip y carga el audio
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            //Actualiza el estado
            currentFile = audioFile;
            paused = false;
            pausePosition = 0;

            //Detector automático de fin de canción
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (ignoreStopEvent) {
                        ignoreStopEvent = false; // Limpia la bandera para evitar stops duplicados
                        return;
                    }

                    //Verifica si llegó al final (ultimo segundo en este caso)
                    long clipLength = clip.getMicrosecondLength();
                    long currentPos = clip.getMicrosecondPosition();
                    boolean finished = Math.abs(clipLength - currentPos) < 1_000_000; // 1 segundo

                    //Si terminó naturalmente pasa a la siguiente
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
                    "Ocurrió un problema al leer el archivo",
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

    //Reproducir o reanudar una canción
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
            //Bandera de seguridad, indica que el próximo stop debe ser ignorado, así las pausas no se confunden con fin de canción
            ignoreStopEvent = true;

            //Si estaba pausado se reanuda desde donde iba la canción
            if (paused){
                clip.setMicrosecondPosition(pausePosition);
            }

            //Inicia la canción
            clip.start();
            //Ya no está en pausa
            paused = false;

        }catch (Exception e){
            info.showException(
                    "Error al reproducir",
                    "Ocurrió un problema al iniciar la reproducción",
                    "Error de audio"
            );
        }
    }

    //Pausar canciones
    public void pause(){
        if (clip == null){
            info.showException(
                    "No se puede pausar",
                    "No hay ninguna canción cargada"
            );
            return;
        }

        //Solo pausamos si se está reproduciendo algo
        if (clip.isRunning()){
            //La bandera pasa a true porque el próximo stop es intencional (debido a que pausamos)
            ignoreStopEvent = true;

            //Guardamos la posición donde quedó la pausa
            pausePosition = clip.getMicrosecondPosition();
            //Detenemos el clip
            clip.stop();
            // Cambiamos a modo pausado
            paused = true;

            //Formato MM:SS
            long totalSeconds = pausePosition / 1_000_000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;

            pausedTimeText = String.format("%02d:%02d", minutes, seconds);
        }
    }

    //Detener canciones
    public void stop (){
        if (clip == null || !clip.isOpen()){
            return;
        }

        if (clip !=null){
            //Aquí es, false debido a que stop si dispara el evento final de la canción
            ignoreStopEvent = false;
            //Se detiene la canción
            clip.stop();
            //La posición de reproducción es 0, o sea el inicio
            clip.setMicrosecondPosition(0);
            //No pausamos, está detenido realmente
            paused = false;
            //La posición de pausa es 0 por el mismo motivo
            pausePosition = 0;
        }
    }

    //Reiniciar canción
    public void restart(){
        if (clip == null || !clip.isOpen()){
            return;
        }

        if (clip != null){
            //Aquí es, false debido a que stop si dispara el evento final de la canción
            ignoreStopEvent = false;
            //Se detiene la canción
            clip.stop();
            //La posición de reproducción es 0, o sea el inicio
            clip.setMicrosecondPosition(0);
            //La posición de pausa es 0 por el mismo motivo
            pausePosition = 0;
            //La canción no se pausa (se reproduce automáticamente de hecho)
            paused = false;
            //Aquí se reproduce automáticamente
            clip.start();
        }
    }

    //Agregar a la cola (al inicio)
    public void addToQueue(File file){
        if (file == null){
            return;
        }

        //Creamos una nueva playlist si no existe ninguna
        if (playlist == null){
            playlist = new ArrayList<>();
        }

        //Guardamos la posición de la próxima canción
        int insertIndex = currentIndex + 1;

        //Validamos los límites
        if (insertIndex > playlist.size()){
            //Si excede ponemos al final
            insertIndex = playlist.size();
        }

        //Método add para agregar el archivo a la queue en la posición calculada
        playlist.add(insertIndex, file);
        //Llevamos la cuenta de cuantas canciones hay en la cola
        queuedCount++;

        info.showSuccess(
                "Agregada a la cola",
                file.getName() + " se reproducirá después de la actual",
                "Cola de reproducción"
        );
    }

    //Agregar a la pila (al final)
    public  void addToPlaylist(File file){
        if (file == null){
            return;
        }

        //Creamos una playlist si no hay ninguna
        if (playlist == null){
            playlist = new ArrayList<>();
        }

        //Agregamos al final
        playlist.add(file);

        info.showSuccess(
                "Agregada a la playlist",
                file.getName() + " se añadió al final de la fila",
                "Fila de reproducción"
        );
    }

    //Eliminar siguiente canción
    public void clearQueue() {
        if (queuedCount <= 0 || playlist == null) {
            info.showException(
                    "Cola vacía",
                    "No hay canciones en la cola para eliminar",
                    "Cola de reproducción"
            );
            return;
        }

        //Calculamos la posición de la siguiente canción a eliminar
        int removeIndex = currentIndex + 1;
        //verificamos que el índice esté dentro de los límites
        if (removeIndex < playlist.size()){
            //Removemos el archivo que se encuentre en el índice calculado y decrementamos la cantidad de canciones en la cola
            File removed = playlist.remove(removeIndex);
            queuedCount--;

            info.showSuccess(
                    "Canción removida de la cola",
                    removed.getName() + " fue eliminada de la parte superior de la cola",
                    "Cola de reproducción"
            );
        }
    }

    //Eliminar toda la playlist
    public void clearPlaylist(){
        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Playlist vacía",
                    "No hay canciones en la lista para eliminar"
            );
            return;
        }

        //Primero que todo se detiene el clip
        stop();
        //Cerramos el clip
        close();
        //Quitamos todos los elementos de la playlist
        playlist.clear();
        //Asignamos current index a 0 porque no hay elementos
        currentIndex = 0;
        //El archivo actual es nulo por la misma razón
        currentFile = null;

        //Si la copia de seguridad no está vacía la limpiamos también
        if (originalPlaylistOrder != null) {
            originalPlaylistOrder.clear();
        }

        info.showSuccess(
                "Playlist eliminada",
                "Se eliminaron todas las canciones de la lista base",
                "Fila de reproducción"
        );
    }

    //Pasar a la siguiente canción
    public void next(){

        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Sin canciones",
                    "No hay una lista cargada"
            );
            return;
        }

        //Primer caso, modo aleatorio activado
        if (shuffleMode) {
            // Modo aleatorio: seleccionar canción random
            if (playlist.size() > 1) {
                //Variable para calcular un índice aleatorio
                int randomIndex;
                do {
                    //Calculamos un índice aleatorio
                    randomIndex = (int) (Math.random() * playlist.size());
                    //Verificamos que la variable randomIndex no sea mayor a currentIndex ni al tamaño de la playlist
                } while (randomIndex == currentIndex && playlist.size() > 1);
                //El índice actual se cambia por el índice aleatorio previamente calculado
                currentIndex = randomIndex;
            } else {
                currentIndex = 0;
            }
        }
        //Segundo caso si el modo bucle está activado
        else if (repeatMode) {
            //Al llegar al final volvemos al inicio, ejemplo si size = 3 y current = 2 (2 + 1) % 3 = 0
            currentIndex = (currentIndex + 1) % playlist.size();

            //Tercer caso flujo normal
        } else {
            //Avanzamos a la canción con el siguiente índice
            currentIndex++;

            //Sí se llega al final de la lista detener la canción
            if (currentIndex >= playlist.size()){
                stop();
                //Mantenemos el último índice válido
                currentIndex = Math.max(0, playlist.size() - 1);
                return;
            }
        }

        //Si hay canciones en la cola decrementamos el contador de esta
        if (queuedCount > 0) {
            queuedCount--;
        }

        //Salimos del modo de pausa
        paused = false;
        //Posición de pausa al inicio
        pausePosition = 0;
        //Cargamos el archivo de audio del current index
        load(playlist.get(currentIndex));
        //Reproducimos esa canción cargada previamente
        play();
    }

    //Ir a canción anterior
    public void previous(){
        if (playlist == null || playlist.isEmpty()){
            info.showException(
                    "Sin canciones",
                    "No hay una lista cargada"
            );
            return;
        }

        //Caso primera canción, se reproduce nuevamente, pues no hay a donde retroceder
        if (currentIndex <= 0){
            //Pausado igual a falso
            paused = false;
            //Posición pausa desde el inicio
            pausePosition = 0;
            //Cargamos la primera canción
            load(playlist.get(currentIndex));
            //La reproducimos
            play();
            return;
        }

        //Flujo normal

        //El index actual se decrementa debido a que estamos una canción atrás
        currentIndex--;

        //Pausado igual a falso
        paused = false;
        //Posición pausa desde el inicio
        pausePosition = 0;
        //Cargamos la canción con el índice calculado
        load(playlist.get(currentIndex));
        //Se reproduce la misma
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