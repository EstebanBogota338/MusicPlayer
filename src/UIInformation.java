import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIInformation {

    // Colores negro y dorado
    private final Color BLACK = new Color(20, 20, 20);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color GOLD = new Color(212, 175, 55);
    private final Color LIGHT_GOLD = new Color(230, 190, 80);
    private final Color TEXT_WHITE = new Color(240, 240, 240);
    private final Color TEXT_GRAY = new Color(180, 180, 180);

    // ========== MÉTODOS DE ERROR ==========

    public void showException(String titulo, String mensaje, String tituloVentana) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(20, 15));
        panel.setBackground(BLACK);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Panel de texto (derecha)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BLACK);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);

        textPanel.add(Box.createVerticalStrut(12));

        JLabel messageLabel = new JLabel("<html><div style='width: 280px; color: #F0F0F0;'>" + mensaje + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_WHITE);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(messageLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        UIManager.put("OptionPane.background", BLACK);
        UIManager.put("Panel.background", BLACK);
        UIManager.put("Button.background", GOLD);
        UIManager.put("Button.foreground", BLACK);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 11));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));

        JOptionPane.showMessageDialog(null, panel, tituloVentana, JOptionPane.ERROR_MESSAGE);
    }

    public void showException(String titulo, String mensaje) {
        showException(titulo, mensaje, "Error en la Aplicación");
    }

    public void showException(String mensaje) {
        showException("Error del Sistema", mensaje, "Error en la Aplicación");
    }

    // ========== MÉTODOS DE ÉXITO ==========

    public void showSuccess(String titulo, String mensaje, String tituloVentana) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(20, 15));
        panel.setBackground(BLACK);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Panel para el icono (izquierda)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setBackground(BLACK);

        // Crear un círculo dorado con check
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar círculo dorado
                g2d.setColor(GOLD);
                g2d.fillOval(5, 5, 40, 40);

                // Dibujar check negro
                g2d.setColor(BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(15, 25, 22, 32);
                g2d.drawLine(22, 32, 35, 15);
            }
        };
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconPanel.add(iconLabel);

        panel.add(iconPanel, BorderLayout.WEST);

        // Panel de texto (derecha)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BLACK);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);

        textPanel.add(Box.createVerticalStrut(12));

        JLabel messageLabel = new JLabel("<html><div style='width: 280px; color: #F0F0F0;'>" + mensaje + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_WHITE);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(messageLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        UIManager.put("OptionPane.background", BLACK);
        UIManager.put("Panel.background", BLACK);
        UIManager.put("Button.background", GOLD);
        UIManager.put("Button.foreground", BLACK);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 11));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));

        JOptionPane.showMessageDialog(null, panel, tituloVentana, JOptionPane.PLAIN_MESSAGE);
    }

    public void showSuccess(String titulo, String mensaje) {
        showSuccess(titulo, mensaje, "Operación Exitosa");
    }

    public void showSuccess(String mensaje) {
        showSuccess("Éxito", mensaje, "Operación Exitosa");
    }

    // ========== SELECTOR DE CARPETA ==========

    public String selectMusicFolder(JFrame parent) {
        
        // Crear el JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Seleccionar Carpeta de Música");
        fileChooser.setApproveButtonText("Seleccionar");
        
        // Obtener el directorio del proyecto
        String projectPath = System.getProperty("user.dir");
        fileChooser.setCurrentDirectory(new java.io.File(projectPath));

        // Personalizar colores del FileChooser
        customizeFileChooser(fileChooser);

        // Mostrar el diálogo
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    private void customizeFileChooser(JFileChooser fileChooser) {
        // Aplicar tema oscuro al FileChooser
        UIManager.put("FileChooser.background", BLACK);
        UIManager.put("Panel.background", BLACK);
        UIManager.put("Label.foreground", TEXT_WHITE);
        UIManager.put("Label.background", BLACK);
        UIManager.put("TextField.background", DARK_GRAY);
        UIManager.put("TextField.foreground", TEXT_WHITE);
        UIManager.put("TextField.caretForeground", GOLD);
        UIManager.put("TextField.selectionBackground", GOLD);
        UIManager.put("TextField.selectionForeground", BLACK);
        UIManager.put("ComboBox.background", DARK_GRAY);
        UIManager.put("ComboBox.foreground", TEXT_WHITE);
        UIManager.put("ComboBox.selectionBackground", GOLD);
        UIManager.put("ComboBox.selectionForeground", BLACK);
        UIManager.put("List.background", DARK_GRAY);
        UIManager.put("List.foreground", TEXT_WHITE);
        UIManager.put("List.selectionBackground", GOLD);
        UIManager.put("List.selectionForeground", BLACK);
        UIManager.put("Button.background", GOLD);
        UIManager.put("Button.foreground", BLACK);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 11));
        UIManager.put("Table.background", DARK_GRAY);
        UIManager.put("Table.foreground", TEXT_WHITE);
        UIManager.put("Table.selectionBackground", GOLD);
        UIManager.put("Table.selectionForeground", BLACK);
        UIManager.put("TableHeader.background", BLACK);
        UIManager.put("TableHeader.foreground", GOLD);
        UIManager.put("ScrollPane.background", BLACK);
        UIManager.put("Viewport.background", DARK_GRAY);

        // Aplicar cambios
        SwingUtilities.updateComponentTreeUI(fileChooser);
    }

    // ========== VENTANA DE CARGA ==========

    public LoadingDialog createLoadingWindow(JFrame parent) {
        return new LoadingDialog(parent);
    }

    // Clase interna para la ventana de carga
    public class LoadingDialog extends JDialog {
        private JLabel statusLabel;
        private JLabel dotsLabel;
        private JProgressBar progressBar;
        private JLabel fileLabel;
        private Timer dotsTimer;
        private int dotCount = 0;

        public LoadingDialog(JFrame parent) {
            super(parent, "Cargando Canciones", true);
            setSize(450, 250);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setUndecorated(true);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout(0, 20));
            mainPanel.setBackground(BLACK);
            mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBackground(BLACK);

            JLabel titleLabel = new JLabel("🎵 Cargando Música");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(GOLD);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(titleLabel);

            topPanel.add(Box.createVerticalStrut(10));

            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            statusPanel.setBackground(BLACK);

            statusLabel = new JLabel("Escaneando archivos");
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            statusLabel.setForeground(TEXT_WHITE);
            statusPanel.add(statusLabel);

            dotsLabel = new JLabel("...");
            dotsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dotsLabel.setForeground(GOLD);
            statusPanel.add(dotsLabel);

            topPanel.add(statusPanel);
            mainPanel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(BLACK);

            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
            progressBar.setForeground(GOLD);
            progressBar.setBackground(DARK_GRAY);
            progressBar.setBorder(BorderFactory.createLineBorder(GOLD, 1));
            progressBar.setMaximumSize(new Dimension(400, 25));
            progressBar.setPreferredSize(new Dimension(400, 25));
            progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(progressBar);

            centerPanel.add(Box.createVerticalStrut(15));

            fileLabel = new JLabel("Preparando...");
            fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            fileLabel.setForeground(TEXT_GRAY);
            fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(fileLabel);

            mainPanel.add(centerPanel, BorderLayout.CENTER);

            mainPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD, 2),
                    new EmptyBorder(30, 30, 30, 30)
            ));

            add(mainPanel);
            startDotsAnimation();
        }

        private void startDotsAnimation() {
            dotsTimer = new Timer(400, e -> {
                dotCount = (dotCount + 1) % 4;
                String dots = ".".repeat(dotCount);
                dotsLabel.setText(dots + " ".repeat(3 - dotCount));
            });
            dotsTimer.start();
        }

        public void updateProgress(int current, int total, String fileName) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setMaximum(total);
                progressBar.setValue(current);
                progressBar.setString(current + " / " + total);
                fileLabel.setText(fileName);
            });
        }

        public void close() {
            if (dotsTimer != null) {
                dotsTimer.stop();
            }
            dispose();
        }
    }
}