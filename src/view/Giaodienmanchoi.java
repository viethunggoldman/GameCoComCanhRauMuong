package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import controller.BoardPanel;
import controller.GameManager;

public class Giaodienmanchoi extends JFrame {

    private GameManager gameManager;
    private JLabel lblAIStatus;
    private JLabel lblPlayerStatus;
    private RoundedPanel aiStatusBox;
    private RoundedPanel playerStatusBox;
    private JLabel soldiersLabelAI;
    private JLabel soldiersLabelPlayer;
    private BoardPanel boardPanel;
    private JPanel aiInfoPanel;
    private JPanel playerInfoPanel;
    private JPanel overlayPanel;
    private JPanel settingsPanel;
    private boolean isOnePlayer;
    private final int[] mouseX = {0};
    private final int[] mouseY = {0};

    public Giaodienmanchoi(boolean isOnePlayer, boolean playerGoesFirst, Giaodienchonthe mainFrame) {
        setUndecorated(true);
        setLocation(mainFrame.getLocation());
        this.isOnePlayer = isOnePlayer;
        gameManager = new GameManager(isOnePlayer, playerGoesFirst, null);
        gameManager.setOnGameEndCallback(() -> {
            SwingUtilities.invokeLater(() -> {
            	  // Cập nhật số quân trước khi hiển thị người thắng
            	//update
                updateSoldiersCount(gameManager.getRedCount(), gameManager.getYellowCount());
                //updtae
                int redScore = gameManager.getRedCount();
                int yellowScore = gameManager.getYellowCount();

                String scoreLine;
                String winnerName;

                if (isOnePlayer) {
                    scoreLine = "Player " + yellowScore + " - " + redScore + " AI";
                    winnerName = (yellowScore > redScore) ? "Player" : "AI";
                } else {
                    scoreLine = "Player1 " + yellowScore + " - " + redScore + " Player2";
                    winnerName = (yellowScore > redScore) ? "Player1" : "Player2";
                }

                String winnerMessage = "<html><div style='text-align: center;'>" +
                        scoreLine + "<br/>Xin chúc mừng " + winnerName + "!</div></html>";

                JPanel winnerPanel = createWinnerPanel(winnerMessage);

                overlayPanel.removeAll();
                overlayPanel.setLayout(new GridBagLayout());
                overlayPanel.add(winnerPanel);
                overlayPanel.setVisible(true);
                //update
                overlayPanel.revalidate();
                overlayPanel.repaint();
                //update
                gameManager.setGamePaused(true);
            });
        });

        gameManager.setupInitialPieces();
        
        try {
            boardPanel = new BoardPanel(gameManager, this::updateTurnHighlight, this);
            System.out.println("boardPanel initialized: " + (boardPanel != null));
        } catch (Exception e) {
            System.out.println("Error initializing boardPanel: " + e.getMessage());
            e.printStackTrace();
        }
        gameManager.setBoardPanel(boardPanel);

        if (gameManager.isOnePlayer() && !gameManager.isPlayerGoesFirst()) {
            EventQueue.invokeLater(() -> {
                System.out.println("Bot goes first. Triggering bot move...");
                gameManager.autoBotMoveIfNeeded();
            });
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(null);
        setBackground(Color.WHITE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);
     
        initGameUIComponents(layeredPane, isOnePlayer);
        initOverlayAndSettings(layeredPane);
        updateTurnHighlight();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX[0] = e.getX();
                mouseY[0] = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - mouseX[0];
                int y = e.getYOnScreen() - mouseY[0];
                setLocation(x, y);
            }
        });
    }

    public void restartGame() {
        getContentPane().removeAll();
        revalidate();
        repaint();

        this.gameManager = new GameManager(isOnePlayer, gameManager.isPlayerGoesFirst(), null);
        
        gameManager.setOnGameEndCallback(() -> {
            SwingUtilities.invokeLater(() -> {
            	// Cập nhật số quân trước khi hiển thị người thắng
            	//update
                updateSoldiersCount(gameManager.getRedCount(), gameManager.getYellowCount());
                //update
                int redScore = gameManager.getRedCount();
                int yellowScore = gameManager.getYellowCount();
                String scoreLine;
                String winnerName;

                if (gameManager.isOnePlayer()) {
                    scoreLine = "Player " + yellowScore + " - " + redScore + " AI";
                    winnerName = (yellowScore > redScore) ? "Player" : "AI";
                } else {
                    scoreLine = "Player1 " + yellowScore + " - " + redScore + " Player2";
                    winnerName = (yellowScore > redScore) ? "Player1" : "Player2";
                }

                String winnerMessage = "<html><div style='text-align: center;'>" +
                        scoreLine + "<br/>Xin chúc mừng " + winnerName + "!</div></html>";

                JPanel winnerPanel = createWinnerPanel(winnerMessage);
                overlayPanel.removeAll();
                overlayPanel.setLayout(new GridBagLayout());
                overlayPanel.add(winnerPanel);
                overlayPanel.setVisible(true);
                gameManager.setGamePaused(true);
            });
        });

        gameManager.setupInitialPieces();

        this.boardPanel = new BoardPanel(gameManager, this::updateTurnHighlight, this);
        gameManager.setBoardPanel(boardPanel);

        if (gameManager.isOnePlayer() && !gameManager.isPlayerGoesFirst()) {
            EventQueue.invokeLater(() -> gameManager.autoBotMoveIfNeeded());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(null);
        setBackground(Color.WHITE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);
        
        initGameUIComponents(layeredPane, isOnePlayer);
        initOverlayAndSettings(layeredPane);
        updateTurnHighlight();
        revalidate();
        repaint();
    }

    private void initGameUIComponents(JLayeredPane layeredPane, boolean isOnePlayer) {
        String topPlayerName = isOnePlayer ? "AI" : "Player 2";
        String topIConAvatar = isOnePlayer ? "/picture/com.png" : "/picture/person.png";
        String bottomPlayerName = isOnePlayer ? "Player" : "Player 1";

        aiInfoPanel = createInfoPanel(topPlayerName, topIConAvatar);
        aiInfoPanel.setBounds(27, 27, 373, 133);
        layeredPane.add(aiInfoPanel, JLayeredPane.PALETTE_LAYER);

        playerInfoPanel = createInfoPanel(bottomPlayerName, "/picture/person.png");
        playerInfoPanel.setBounds(800, 587, 373, 133);
        layeredPane.add(playerInfoPanel, JLayeredPane.PALETTE_LAYER);

        JButton settingIcon = new JButton(new ImageIcon(getClass().getResource("/picture/setting.png")));
        settingIcon.setBounds(1100, 13, 67, 67);
        settingIcon.setContentAreaFilled(false);
        settingIcon.setBorderPainted(false);
        settingIcon.setFocusPainted(false);
        layeredPane.add(settingIcon, JLayeredPane.DRAG_LAYER);

        boardPanel.setPreferredSize(new Dimension(600, 600)); // Tăng từ 533x533 lên 600x600
        boardPanel.setBounds(300, 100, 600, 600); // Căn giữa: (1200-600)/2=300, (800-600)/2=100
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        boardPanel.enableInteraction();
         
        // Thêm ảnh bên trái bàn cờ
        JLabel leftImageLabel = new JLabel();
        leftImageLabel.setIcon(new ImageIcon(getClass().getResource("/picture/raumuongluoc.png")));
        leftImageLabel.setPreferredSize(new Dimension(200, 200));
        leftImageLabel.setBounds(50, 300, 200, 200); // Căn giữa theo chiều dọc: (800-200)/2=300
        layeredPane.add(leftImageLabel, JLayeredPane.DEFAULT_LAYER);

        // Thêm ảnh bên phải bàn cờ
        JLabel rightImageLabel = new JLabel();
        rightImageLabel.setIcon(new ImageIcon(getClass().getResource("/picture/raumuongxao.png")));
        rightImageLabel.setPreferredSize(new Dimension(200, 203));
        rightImageLabel.setBounds(950, 300, 200, 203); // Căn giữa theo chiều dọc, đối xứng với ảnh trái
        layeredPane.add(rightImageLabel, JLayeredPane.DEFAULT_LAYER);
        
        settingIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameManager.setGamePaused(true);
                boardPanel.setGamePaused(true);
                overlayPanel.setVisible(true);
                settingsPanel.setVisible(true);
            }
        });
    }

    private void initOverlayAndSettings(JLayeredPane layeredPane) {
        overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setBounds(0, 0, 1200, 800);
        overlayPanel.setVisible(false);
        layeredPane.add(overlayPanel, JLayeredPane.MODAL_LAYER);

        settingsPanel = createSettingsPanel();
        settingsPanel.setBounds(450, 225, 300, 350);
        settingsPanel.setVisible(false);
        layeredPane.add(settingsPanel, JLayeredPane.POPUP_LAYER);
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(210, 180, 140));
        button.setForeground(Color.BLACK);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JButton b = (JButton) c;
                int width = b.getWidth();
                int height = b.getHeight();

                g2.setColor(b.getBackground());
                g2.fillRoundRect(0, 0, width - 1, height - 1, 20, 20);

                super.paint(g2, c);
                g2.dispose();
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
            }
        });
    }

    private JPanel createWinnerPanel(String winner) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(winner);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(300, 80));
        titleLabel.setMaximumSize(new Dimension(300, 80));
        titleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setPreferredSize(new Dimension(300, 10));
        separator.setMaximumSize(new Dimension(300, 10));
        separator.setForeground(Color.BLACK);

        JButton replayButton = new JButton("Chơi lại");
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayButton.setPreferredSize(new Dimension(150, 40));
        replayButton.setMaximumSize(new Dimension(150, 40));
        customizeButton(replayButton);
        replayButton.addActionListener(e -> {
        	//update
        	overlayPanel.setVisible(false);
        	//update
            restartGame();
        });

        JButton homeButton = new JButton("Về trang chủ");
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeButton.setPreferredSize(new Dimension(150, 40));
        homeButton.setMaximumSize(new Dimension(150, 40));
        customizeButton(homeButton);
        homeButton.addActionListener(e -> {
            java.awt.Point currentLocation = this.getLocation();
            Giaodienchinh mainFrame = new Giaodienchinh();
            mainFrame.setLocation(currentLocation);
            mainFrame.setVisible(true);
            this.dispose();
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(replayButton);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(homeButton);
        
        RoundedPanel roundedPanel = new RoundedPanel(new Color(176,224,230));
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setPreferredSize(new Dimension(300, 350));
        roundedPanel.add(contentPanel, BorderLayout.CENTER);

        return roundedPanel;
    }

    private JPanel createSettingsPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Tạm dừng");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setPreferredSize(new Dimension(300, 10));
        separator.setMaximumSize(new Dimension(300, 10));
        separator.setForeground(Color.BLACK);

        JButton continueButton = new JButton("Tiếp tục");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setPreferredSize(new Dimension(150, 40));
        continueButton.setMaximumSize(new Dimension(150, 40));
        customizeButton(continueButton);
        continueButton.addActionListener(e -> {
            gameManager.setGamePaused(false);
            if(gameManager.isOnePlayer()) {
                gameManager.getBotPlayer().resumeIfInterrupted();
            }
            boardPanel.setGamePaused(false);
            overlayPanel.setVisible(false);
            settingsPanel.setVisible(false);
        });

        JButton replayButton = new JButton("Chơi lại");
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayButton.setPreferredSize(new Dimension(150, 40));
        replayButton.setMaximumSize(new Dimension(150, 40));
        customizeButton(replayButton);
        replayButton.addActionListener(e -> {
            restartGame();
        });

        JButton homeButton = new JButton("Về trang chủ");
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeButton.setPreferredSize(new Dimension(150, 40));
        homeButton.setMaximumSize(new Dimension(150, 40));
        customizeButton(homeButton);
        homeButton.addActionListener(e -> {
            java.awt.Point currentLocation = this.getLocation();
            Giaodienchinh mainFrame = new Giaodienchinh();
            mainFrame.setLocation(currentLocation);
            mainFrame.setVisible(true);
            this.dispose();
        });

        contentPanel.add(titleLabel);
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(continueButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(replayButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(homeButton);

        RoundedPanel roundedPanel = new RoundedPanel(new Color(200, 255, 200));
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setPreferredSize(new Dimension(300, 350));
        roundedPanel.add(contentPanel, BorderLayout.CENTER);

        return roundedPanel;
    }

    private void updateTurnHighlight() {
        if (aiStatusBox == null || playerStatusBox == null) return;
        GameManager.Player current = gameManager.getCurrentPlayer();
        boolean isPlayer = gameManager.isPlayerGoesFirst();

        if (isPlayer) {
            if (current == GameManager.Player.YELLOW) {
                playerStatusBox.setBackgroundColor(new Color(220, 255, 220));
                aiStatusBox.setBackgroundColor(new Color(230, 230, 230));
                lblPlayerStatus.setText("Trạng thái: Đến lượt bạn");
                lblAIStatus.setText("Trạng thái: Đến lượt đối thủ");
            } else {
                aiStatusBox.setBackgroundColor(new Color(220, 255, 220));
                playerStatusBox.setBackgroundColor(new Color(230, 230, 230));
                lblPlayerStatus.setText("Trạng thái: Đến lượt đối thủ");
                lblAIStatus.setText("Trạng thái: Đến lượt bạn");
            }
        } else {
            if (current == GameManager.Player.YELLOW) {
                playerStatusBox.setBackgroundColor(new Color(220, 255, 220));
                aiStatusBox.setBackgroundColor(new Color(230, 230, 230));
                lblPlayerStatus.setText("Trạng thái: Đến lượt bạn");
                lblAIStatus.setText("Trạng thái: Đến lượt đối thủ");
            } else {
                aiStatusBox.setBackgroundColor(new Color(220, 255, 220));
                playerStatusBox.setBackgroundColor(new Color(230, 230, 230));
                lblPlayerStatus.setText("Trạng thái: Đến lượt đối thủ");
                lblAIStatus.setText("Trạng thái: Đến lượt bạn");
            }
        }
    }

    private JPanel createInfoPanel(String name, String iconPath) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(373, 133));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setPreferredSize(new Dimension(120, 120));
        iconPanel.setBackground(Color.WHITE);
        iconPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        if (icon.getIconWidth() == -1) {
            System.out.println("Error: Failed to load icon at path: " + iconPath);
        }
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        panel.add(iconPanel, gbc);

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(Color.WHITE);
        namePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        namePanel.setPreferredSize(new Dimension(200, 53));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 21));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        namePanel.add(nameLabel, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.7;
        panel.add(namePanel, gbc);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(7, 13, 7, 0));

        JLabel soldiersLabel = new JLabel("Số quân còn lại: 4");
        soldiersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        soldiersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("Trạng thái: Đến lượt bạn");
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        RoundedPanel statusBox = new RoundedPanel(Color.LIGHT_GRAY);
        statusBox.setLayout(new BorderLayout());
        statusBox.setPreferredSize(new Dimension(200, 33));
        statusBox.setBorder(new EmptyBorder(3, 7, 3, 7));
        statusBox.add(statusLabel, BorderLayout.CENTER);

        soldiersLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 17));

        statusPanel.add(soldiersLabel);
        statusPanel.add(Box.createVerticalStrut(7));
        statusPanel.add(statusBox);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(statusPanel, gbc);

        if (name.equals("AI") || name.equals("Player 2")) {
            lblAIStatus = statusLabel;
            aiStatusBox = statusBox;
            soldiersLabelAI = soldiersLabel;
        } else {
            lblPlayerStatus = statusLabel;
            playerStatusBox = statusBox;
            soldiersLabelPlayer = soldiersLabel;
        }
        return panel;
    }

    public void updateSoldiersCount(int redCount, int yellowCount) {
        if (soldiersLabelAI != null) {
            soldiersLabelAI.setText("Số quân còn lại: " + redCount);
        }
        if (soldiersLabelPlayer != null) {
            soldiersLabelPlayer.setText("Số quân còn lại: " + yellowCount);
        }
    }
}

class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 20;

    public RoundedPanel(Color bgColor) {
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcs.width, arcs.height);
    }
}