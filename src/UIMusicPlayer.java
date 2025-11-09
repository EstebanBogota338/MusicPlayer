import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class UIMusicPlayer extends JFrame {

    private final MusicPlayer player;
    private final JComboBox<String> songSelector;
    private final JLabel statusLabel;
    private final JLabel queueLabel;
    private final JLabel timeLabel;
    private final JTextArea playlistArea;
    private final List<File> songs;
    private final UIInformation info;
    private final Timer progressTimer;
    private final JSlider progressSlider;
    private boolean isSliderChanging = false;

    //Panel de ondas de audio
    private final MusicWavePanel wavePanel;

    //Botones para aleatorio y bucle
    private JButton btnRepeat;
    private JButton btnShuffle;

    // Colores negro y dorado
    private final Color BLACK = new Color(20, 20, 20);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color GOLD = new Color(212, 175, 55);
    private final Color LIGHT_GOLD = new Color(230, 190, 80);
    private final Color DARK_GOLD = new Color(180, 150, 45);
    private final Color TEXT_WHITE = new Color(240, 240, 240);
    private final Color TEXT_GRAY = new Color(180, 180, 180);

    public UIMusicPlayer(List<File> songs, UIInformation info) {
        this.songs = songs;
        this.info = info;
        this.player = new MusicPlayer(info);

        setTitle("Reproductor de Música");
        setSize(700, 950);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BLACK);

        // ========== PANEL SUPERIOR (Header) ==========
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(DARK_GRAY);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Reproductor de Música");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        headerPanel.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Selecciona una canción para comenzar");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // ========== PANEL PRINCIPAL ==========
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BLACK);
        mainPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

        // ========== SELECTOR DE CANCIÓN ==========
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        selectorPanel.setBackground(DARK_GRAY);
        selectorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        selectorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectLabel = new JLabel("Biblioteca de Canciones");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectLabel.setForeground(GOLD);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectorPanel.add(selectLabel);

        selectorPanel.add(Box.createVerticalStrut(12));

        songSelector = new JComboBox<>();
        for (File song : songs) {
            songSelector.addItem(song.getName());
        }
        songSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        songSelector.setMaximumSize(new Dimension(500, 35));
        songSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        songSelector.setBackground(DARK_GRAY);
        songSelector.setForeground(TEXT_WHITE);
        songSelector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        selectorPanel.add(songSelector);

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== PANEL DE ESTADO ==========
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(DARK_GRAY);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(12, 20, 12, 20)
        ));
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("[DETENIDO] Estado: Selecciona una canción para comenzar");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(statusLabel);

        statusPanel.add(Box.createVerticalStrut(8));

        timeLabel = new JLabel(" --:-- / --:--");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TEXT_GRAY);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(timeLabel);

        mainPanel.add(statusPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== PANEL DE ONDAS DE AUDIO ==========
        JPanel waveContainer = new JPanel();
        waveContainer.setLayout(new BoxLayout(waveContainer, BoxLayout.Y_AXIS));
        waveContainer.setBackground(BLACK);
        waveContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        waveContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        waveContainer.setMaximumSize(new Dimension(600, 140));

        JLabel waveTitle = new JLabel("Visualizador de Audio");
        waveTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        waveTitle.setForeground(GOLD);
        waveTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        waveContainer.add(waveTitle);

        waveContainer.add(Box.createVerticalStrut(8));

        wavePanel = new MusicWavePanel();
        waveContainer.add(wavePanel);

        mainPanel.add(waveContainer);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== BARRA DE PROGRESO ==========
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(BLACK);
        progressPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressPanel.setMaximumSize(new Dimension(550, 60));

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(BLACK);
        progressSlider.setForeground(GOLD);

        // Personalizar la apariencia del slider
        progressSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(progressSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GOLD);
                g2d.fillOval(thumbRect.x, thumbRect.y, 12, 12);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Track de fondo
                g2d.setColor(DARK_GRAY);
                g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 2,
                        trackRect.width, 4, 4, 4);

                // Track de progreso
                if (progressSlider.getValue() > 0) {
                    g2d.setColor(GOLD);
                    int progressWidth = (int) (trackRect.width * (progressSlider.getValue() / 100.0));
                    g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 2,
                            progressWidth, 4, 4, 4);
                }
            }
        });

        progressSlider.setPreferredSize(new Dimension(550, 25));
        progressSlider.setMaximumSize(new Dimension(550, 25));
        progressSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressSlider.setOpaque(true);

        // Eventos del slider
        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (player.getCurrentFile() != null && player.getTotalTime() > 0) {
                    isSliderChanging = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isSliderChanging && player.getCurrentFile() != null && player.getTotalTime() > 0) {
                    double percent = (double) e.getX() / progressSlider.getWidth();
                    int newValue = (int) (percent * 100);
                    progressSlider.setValue(Math.max(0, Math.min(100, newValue)));

                    long newPosition = (long) (percent * player.getTotalTime());
                    player.setPosition(newPosition);
                }
                isSliderChanging = false;
            }
        });

        progressPanel.add(progressSlider);
        mainPanel.add(progressPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ========== CONTROLES PRINCIPALES DE REPRODUCCIÓN ==========
        JPanel mainControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        mainControlsPanel.setBackground(BLACK);
        mainControlsPanel.setMaximumSize(new Dimension(550, 70));
        mainControlsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPrevious = createControlButton("ANT", "Canción anterior", GOLD, 50);
        JButton btnPlay = createControlButton("PLAY", "Reproducir", GOLD, 55);
        JButton btnPause = createControlButton("PAUSE", "Pausar", GOLD, 55);
        JButton btnStop = createControlButton("STOP", "Detener", GOLD, 55);
        JButton btnNext = createControlButton("SIG", "Siguiente canción", GOLD, 50);

        mainControlsPanel.add(btnPrevious);
        mainControlsPanel.add(btnPlay);
        mainControlsPanel.add(btnPause);
        mainControlsPanel.add(btnStop);
        mainControlsPanel.add(btnNext);

        mainPanel.add(mainControlsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ========== CONTROLES SECUNDARIOS ==========
        JPanel secondaryControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        secondaryControlsPanel.setBackground(BLACK);
        secondaryControlsPanel.setMaximumSize(new Dimension(550, 50));
        secondaryControlsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnShuffle = createControlButton("RND", "Modo aleatorio: DESACTIVADO", TEXT_GRAY, 45);
        JButton btnRestart = createControlButton("RST", "Reiniciar canción", GOLD, 45);
        btnRepeat = createControlButton("RPT", "Repetir playlist: DESACTIVADO", TEXT_GRAY, 45);

        secondaryControlsPanel.add(btnShuffle);
        secondaryControlsPanel.add(btnRestart);
        secondaryControlsPanel.add(btnRepeat);

        mainPanel.add(secondaryControlsPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== GESTIÓN DE LISTAS ==========
        JPanel managementPanel = new JPanel();
        managementPanel.setLayout(new GridLayout(2, 2, 12, 12));
        managementPanel.setBackground(BLACK);
        managementPanel.setMaximumSize(new Dimension(550, 100));
        managementPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAddQueue = createButton("Agregar a Cola", GOLD);
        JButton btnAddPlaylist = createButton("Agregar al Final", GOLD);
        JButton btnClearQueue = createButton("Limpiar Cola", GOLD);
        JButton btnClearPlaylist = createButton("Limpiar Playlist", GOLD);

        managementPanel.add(btnAddQueue);
        managementPanel.add(btnAddPlaylist);
        managementPanel.add(btnClearQueue);
        managementPanel.add(btnClearPlaylist);

        mainPanel.add(managementPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== INFORMACIÓN DE COLA ==========
        JPanel queueInfoPanel = new JPanel();
        queueInfoPanel.setLayout(new BoxLayout(queueInfoPanel, BoxLayout.Y_AXIS));
        queueInfoPanel.setBackground(DARK_GRAY);
        queueInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(12, 20, 12, 20)
        ));
        queueInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        queueLabel = new JLabel("Próxima: Ninguna");
        queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        queueLabel.setForeground(GOLD);
        queueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        queueInfoPanel.add(queueLabel);

        mainPanel.add(queueInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ========== LISTA DE REPRODUCCIÓN ==========
        JPanel playlistPanel = new JPanel();
        playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
        playlistPanel.setBackground(BLACK);
        playlistPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel playlistTitle = new JLabel("Lista de Reproducción Actual");
        playlistTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playlistTitle.setForeground(GOLD);
        playlistTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        playlistPanel.add(playlistTitle);

        playlistPanel.add(Box.createVerticalStrut(10));

        playlistArea = new JTextArea(8, 45);
        playlistArea.setEditable(false);
        playlistArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        playlistArea.setBackground(DARK_GRAY);
        playlistArea.setForeground(TEXT_WHITE);
        playlistArea.setCaretColor(GOLD);
        playlistArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        playlistArea.setLineWrap(true);
        playlistArea.setWrapStyleWord(true);

        JScrollPane playlistScrollPane = new JScrollPane(playlistArea);
        playlistScrollPane.setPreferredSize(new Dimension(550, 180));
        playlistScrollPane.setMaximumSize(new Dimension(550, 180));
        playlistScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        playlistScrollPane.setBorder(BorderFactory.createLineBorder(GOLD, 1));
        playlistScrollPane.setBackground(BLACK);
        playlistScrollPane.getViewport().setBackground(DARK_GRAY);
        playlistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        playlistScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar verticalScrollBar = playlistScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));
        verticalScrollBar.setBackground(DARK_GRAY);
        verticalScrollBar.setForeground(GOLD);

        playlistPanel.add(playlistScrollPane);

        mainPanel.add(playlistPanel);

        add(mainPanel, BorderLayout.CENTER);

        // ========== TIMER PARA ACTUALIZAR TIEMPO ==========
        progressTimer = new Timer(500, e -> updateTimeDisplay());
        progressTimer.start();

        // ========== ACCIONES DE LOS BOTONES ==========

        btnPlay.addActionListener(e -> {
            File selected = songs.get(songSelector.getSelectedIndex());

            if (player.getCurrentFile() == null || !player.getCurrentFile().equals(selected)) {
                player.load(selected);
                updateWindowTitle(selected.getName());

                //SINCRONIZAR SOLO AL CAMBIAR DE CANCIÓN
                for (int i = 0; i < songSelector.getItemCount(); i++) {
                    if (songSelector.getItemAt(i).equals(selected.getName())) {
                        songSelector.setSelectedIndex(i);
                        break;
                    }
                }
            }

            player.play();
            statusLabel.setText("Reproduciendo: " + selected.getName());
            statusLabel.setForeground(GOLD);

            wavePanel.setPlaying(true);
            wavePanel.setVolume(0.8);

            updateQueueInfo();
            updatePlaylistDisplay();
        });

        btnPause.addActionListener(e -> {
            player.pause();
            String pausedAt = player.getPausedTimeText();
            if (!pausedAt.isEmpty()) {
                statusLabel.setText("[PAUSE] Pausado en " + pausedAt);
            } else {
                statusLabel.setText("[PAUSE] Pausado");
            }
            statusLabel.setForeground(LIGHT_GOLD);

            //SINCRONIZAR AL PAUSAR
            if (player.getCurrentFile() != null) {
                String currentSongName = player.getCurrentFile().getName();
                for (int i = 0; i < songSelector.getItemCount(); i++) {
                    if (songSelector.getItemAt(i).equals(currentSongName)) {
                        songSelector.setSelectedIndex(i);
                        break;
                    }
                }
            }

            wavePanel.setPlaying(false);
            wavePanel.setVolume(0.4);
        });

        btnStop.addActionListener(e -> {
            player.stop();
            statusLabel.setText("Detenido");
            statusLabel.setForeground(TEXT_GRAY);
            progressSlider.setValue(0);
            timeLabel.setText("00:00 / " + player.getTotalTimeFormatted());

            wavePanel.setPlaying(false);
            wavePanel.setVolume(0.2);
        });

        btnRestart.addActionListener(e -> {
            player.restart();
            statusLabel.setText("Reiniciando canción actual...");
            statusLabel.setForeground(GOLD);

            wavePanel.setPlaying(true);
            wavePanel.setVolume(0.8);
        });

        btnRepeat.addActionListener(e -> {
            player.toggleRepeatMode();
            boolean isRepeatOn = player.isRepeatMode();

            if (isRepeatOn) {
                btnRepeat.setBackground(GOLD);
                btnRepeat.setForeground(BLACK);
                btnRepeat.setToolTipText("Repetir playlist: ACTIVADO");
                info.showSuccess("Modo repetición ACTIVADO",
                        "La playlist se repetirá automáticamente", "Repetir Playlist");
            } else {
                btnRepeat.setBackground(TEXT_GRAY);
                btnRepeat.setForeground(BLACK);
                btnRepeat.setToolTipText("Repetir playlist: DESACTIVADO");
                info.showSuccess("Modo repetición DESACTIVADO",
                        "La playlist se detendrá al finalizar", "Repetir Playlist");
            }

            updateRepeatIndicator();
        });

        btnShuffle.addActionListener(e -> {
            player.toggleShuffleMode();
            boolean isShuffleOn = player.isShuffleMode();

            if (isShuffleOn) {
                btnShuffle.setBackground(GOLD);
                btnShuffle.setForeground(BLACK);
                btnShuffle.setToolTipText("Modo aleatorio: ACTIVADO");
                info.showSuccess("Modo aleatorio ACTIVADO",
                        "Las canciones se reproducirán en orden aleatorio", "Reproducción Aleatoria");
            } else {
                btnShuffle.setBackground(TEXT_GRAY);
                btnShuffle.setForeground(BLACK);
                btnShuffle.setToolTipText("Modo aleatorio: DESACTIVADO");
                info.showSuccess("Modo aleatorio DESACTIVADO",
                        "Las canciones se reproducirán en orden normal", "Reproducción Aleatoria");
            }

            updateShuffleIndicator();
            updatePlaylistDisplay();
        });

        btnNext.addActionListener(e -> {
            File previous = player.getCurrentFile();
            player.next();
            File current = player.getCurrentFile();

            if (current != null) {
                if (previous == null || !current.getName().equals(previous.getName())) {
                    statusLabel.setText("Reproduciendo: " + current.getName());
                    statusLabel.setForeground(GOLD);
                    updateWindowTitle(current.getName());

                    //SINCRONIZAR SOLO CUANDO CAMBIA LA CANCIÓN
                    for (int i = 0; i < songSelector.getItemCount(); i++) {
                        if (songSelector.getItemAt(i).equals(current.getName())) {
                            songSelector.setSelectedIndex(i);
                            break;
                        }
                    }

                    wavePanel.setPlaying(true);
                    wavePanel.setVolume(0.8);
                } else {
                    statusLabel.setText("No hay más canciones en la cola");
                    statusLabel.setForeground(TEXT_GRAY);
                    wavePanel.setPlaying(false);
                    wavePanel.setVolume(0.3);
                }
            } else {
                statusLabel.setText("No hay canción actual");
                statusLabel.setForeground(TEXT_GRAY);
                wavePanel.setPlaying(false);
                wavePanel.setVolume(0.2);
            }

            updateQueueInfo();
            updatePlaylistDisplay();
        });

        btnPrevious.addActionListener(e -> {
            File previous = player.getCurrentFile();
            player.previous();
            File current = player.getCurrentFile();

            if (current != null) {
                if (previous == null || !current.getName().equals(previous.getName())) {
                    statusLabel.setText("Reproduciendo: " + current.getName());
                    statusLabel.setForeground(GOLD);
                    updateWindowTitle(current.getName());

                    for (int i = 0; i < songSelector.getItemCount(); i++) {
                        if (songSelector.getItemAt(i).equals(current.getName())) {
                            songSelector.setSelectedIndex(i);
                            break;
                        }
                    }

                    wavePanel.setPlaying(true);
                    wavePanel.setVolume(0.8);
                } else {
                    statusLabel.setText("Ya estás en la primera canción");
                    statusLabel.setForeground(TEXT_GRAY);
                    wavePanel.setPlaying(false);
                    wavePanel.setVolume(0.3);
                }
            } else {
                statusLabel.setText("No hay canción actual");
                statusLabel.setForeground(TEXT_GRAY);
                wavePanel.setPlaying(false);
                wavePanel.setVolume(0.2);
            }

            updateQueueInfo();
            updatePlaylistDisplay();
        });

        btnAddQueue.addActionListener(e -> {
            File selected = songs.get(songSelector.getSelectedIndex());
            player.addToQueue(selected);
            updateQueueInfo();
        });

        btnAddPlaylist.addActionListener(e -> {
            File selected = songs.get(songSelector.getSelectedIndex());
            player.addToPlaylist(selected);
            updateQueueInfo();
        });

        btnClearQueue.addActionListener(e -> {
            player.clearQueue();
            updateQueueInfo();
        });

        btnClearPlaylist.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "<html><div style='text-align: center; color: white;'>¿Estás seguro de eliminar <b>todas</b> las canciones de la playlist?<br>Esta acción no se puede deshacer.</div></html>",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                player.clearPlaylist();
                statusLabel.setText("Playlist eliminada");
                statusLabel.setForeground(TEXT_GRAY);
                updateQueueInfo();
                updatePlaylistDisplay();
                updateWindowTitle("Sin canción");

                wavePanel.setPlaying(false);
                wavePanel.setVolume(0.1);
            }
        });

        updatePlaylistDisplay();
    }

    private JButton createControlButton(String icon, String tooltip, Color color, int size) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(BLACK);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(size, size));

        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(LIGHT_GOLD);
                btn.setBorder(BorderFactory.createLineBorder(GOLD, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
                btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            }
        });
        return btn;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(BLACK);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setHorizontalAlignment(SwingConstants.CENTER);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(LIGHT_GOLD);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void updateWindowTitle(String songName) {
        setTitle("Reproductor de Música - " + songName);
    }

    private void updateTimeDisplay() {
        SwingUtilities.invokeLater(() -> {
            if (player.getCurrentFile() != null && player.getTotalTime() > 0) {
                String currentTime = player.getCurrentTimeFormatted();
                String totalTime = player.getTotalTimeFormatted();

                String timePrefix = "";
                if (player.isRepeatMode()) timePrefix += "[R] ";
                if (player.isShuffleMode()) timePrefix += "[S] ";
                if (timePrefix.isEmpty()) timePrefix = "TIME: ";

                timeLabel.setText(timePrefix + currentTime + " / " + totalTime);

                if (!isSliderChanging) {
                    int progress = (int) ((double) player.getCurrentTime() / player.getTotalTime() * 100);
                    progressSlider.setValue(progress);
                }

                if (player.isPlaying()) {
                    timeLabel.setForeground(GOLD);
                } else if (player.isPaused()) {
                    timeLabel.setForeground(LIGHT_GOLD);
                } else {
                    timeLabel.setForeground(TEXT_GRAY);
                }

            } else {
                String timePrefix = "";
                if (player.isRepeatMode()) timePrefix += "[R] ";
                if (player.isShuffleMode()) timePrefix += "[S] ";
                if (timePrefix.isEmpty()) timePrefix = "Time: ";
                timeLabel.setText(timePrefix + "--:-- / --:--");
                timeLabel.setForeground(TEXT_GRAY);
                progressSlider.setValue(0);
            }
        });
    }

    private void updateRepeatIndicator() {
        boolean isRepeatOn = player.isRepeatMode();
        String currentText = statusLabel.getText();

        if (isRepeatOn && !currentText.contains("[R]")) {
            statusLabel.setText("[R] " + currentText);
        } else if (!isRepeatOn && currentText.contains("[R]")) {
            statusLabel.setText(currentText.replace("[R] ", ""));
        }
    }

    private void updateShuffleIndicator() {
        boolean isShuffleOn = player.isShuffleMode();
        String currentText = statusLabel.getText();

        if (isShuffleOn && !currentText.contains("[S]")) {
            statusLabel.setText("[S] " + currentText);
        } else if (!isShuffleOn && currentText.contains("[S]")) {
            statusLabel.setText(currentText.replace("[S] ", ""));
        }
    }

    private void updateQueueInfo() {
        List<File> currentPlaylist = player.getPlaylist();

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            int currentIndex = player.getCurrentIndex();

            if (currentIndex + 1 < currentPlaylist.size()) {
                File nextSong = currentPlaylist.get(currentIndex + 1);

                if (player.getQueuedCount() > 0 && currentIndex + 1 <= player.getCurrentIndex() + player.getQueuedCount()) {
                    queueLabel.setText("Próxima en cola: " + nextSong.getName());
                } else {
                    queueLabel.setText("Próxima: " + nextSong.getName());
                }
                queueLabel.setForeground(GOLD);
            } else {
                queueLabel.setText("Próxima: Ninguna");
                queueLabel.setForeground(TEXT_GRAY);
            }
        } else {
            queueLabel.setText("Próxima: Ninguna");
            queueLabel.setForeground(TEXT_GRAY);
        }
        updatePlaylistDisplay();
    }

    private void updatePlaylistDisplay() {
        List<File> currentPlaylist = player.getPlaylist();

        StringBuilder playlistText = new StringBuilder();

        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            playlistText.append("LISTA ACTUAL (").append(currentPlaylist.size()).append(" canciones)\n\n");

            for (int i = 0; i < currentPlaylist.size(); i++) {
                File song = currentPlaylist.get(i);

                if (i == player.getCurrentIndex()) {
                    playlistText.append(">> ");
                } else if (i > player.getCurrentIndex() && i <= player.getCurrentIndex() + player.getQueuedCount()) {
                    playlistText.append("-> ");
                } else {
                    playlistText.append("- ");
                }

                playlistText.append(String.format("%2d", i + 1))
                        .append(". ")
                        .append(song.getName())
                        .append("\n");
            }
        } else {
            playlistText.append("No hay canciones en la lista de reproducción.\n");
            playlistText.append("Selecciona una canción y haz clic en 'Agregar a Playlist'.");
        }

        playlistArea.setText(playlistText.toString());
        playlistArea.setCaretPosition(0);
    }

    @Override
    public void dispose() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
        if (wavePanel != null) {
            wavePanel.cleanup();
        }
        if (player != null) {
            player.close();
        }
        super.dispose();
    }
}