package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Giaodienchinh extends JFrame {

    private static final long serialVersionUID = 1L;
    private BackgroundPanel contentPane;
    private int xOffset = 0;
    private int yOffset = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Giaodienchinh frame = new Giaodienchinh();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Giaodienchinh() {
        setUndecorated(true); // Loại bỏ thanh tiêu đề
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(150, 50, 1200, 800); // Tăng kích thước cửa sổ
        contentPane = new BackgroundPanel("/picture/background_main.png");
        contentPane.setForeground(new Color(0, 128, 0));
        contentPane.setBackground(Color.GRAY);
        contentPane.setBorder(new EmptyBorder(7, 7, 7, 7)); // Điều chỉnh border (5*1.3333)
        contentPane.setLayout(null);

        // Xử lý di chuyển cửa sổ khi nhấn và kéo chuột
        contentPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                xOffset = e.getX();
                yOffset = e.getY();
            }
        });

        contentPane.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - xOffset, y - yOffset);
            }
        });
        setContentPane(contentPane);

        JButton btnBatDau = new JButton("Bắt đầu");
        btnBatDau.setBounds(791, 318, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 193*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnBatDau);
        contentPane.add(btnBatDau);

        JButton btnHuongDan = new JButton("Hướng dẫn");
        btnHuongDan.setBounds(791, 465, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 282*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnHuongDan);
        btnHuongDan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Giaodienhuongdan huongDanFrame = new Giaodienhuongdan(Giaodienchinh.this);
                huongDanFrame.setVisible(true);
                setVisible(false);
            }
        });
        contentPane.add(btnHuongDan);

        JButton btnKetThuc = new JButton("Kết thúc");
        btnKetThuc.setBounds(791, 626, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 380*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnKetThuc);
        btnKetThuc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExitDialog();
            }
        });
        contentPane.add(btnKetThuc);

        JButton btnOnePlayer = new JButton("Một người chơi");
        btnOnePlayer.setBounds(791, 318, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 193*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnOnePlayer);
        btnOnePlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Giaodienchonthe chonThe = new Giaodienchonthe(true, Giaodienchinh.this);
                chonThe.setVisible(true);
                Giaodienchinh.this.setVisible(false);
            }
        });
        contentPane.add(btnOnePlayer);

        JButton btnTwoPlayer = new JButton("Hai người chơi");
        btnTwoPlayer.setBounds(791, 465, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 282*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnTwoPlayer);
        btnTwoPlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Giaodienchonthe chonThe = new Giaodienchonthe(false, Giaodienchinh.this);
                chonThe.setVisible(true);
                Giaodienchinh.this.setVisible(false);
            }
        });
        contentPane.add(btnTwoPlayer);

        JButton btnQuaylai = new JButton("Quay lại");
        btnQuaylai.setBounds(791, 626, 373, 104); // Điều chỉnh vị trí và kích thước (593*1.3333, 380*1.6495, 280*1.3333, 63*1.6495)
        customizeButton(btnQuaylai);
        contentPane.add(btnQuaylai);

        btnBatDau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnBatDau.setVisible(false);
                btnHuongDan.setVisible(false);
                btnKetThuc.setVisible(false);
                btnOnePlayer.setVisible(true);
                btnTwoPlayer.setVisible(true);
                btnQuaylai.setVisible(true);
            }
        });

        btnQuaylai.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnOnePlayer.setVisible(false);
                btnTwoPlayer.setVisible(false);
                btnQuaylai.setVisible(false);
                btnBatDau.setVisible(true);
                btnHuongDan.setVisible(true);
                btnKetThuc.setVisible(true);
            }
        });
    }

    // Phương thức tùy chỉnh nút
    private void customizeButton(JButton button) {
        button.setForeground(new Color(0, 128, 0));
        button.setBackground(new Color(221, 160, 221));
        button.setFont(new Font("Sans Serif Collection", Font.PLAIN, 40)); // Tăng kích thước font (30*1.3333)
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c.getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 40, 40)); // Tăng bo tròn (30*1.3333)
                super.paint(g, c);
                g2d.dispose();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(0, 128, 0));
            }
        });
    }

    // Phương thức hiển thị hộp thoại xác nhận thoát game
    private void showExitDialog() {
        JDialog exitDialog = new JDialog(this, "Xác nhận", true);
        exitDialog.setUndecorated(true);
        exitDialog.setSize(400, 247); // Tăng kích thước hộp thoại (300*1.3333, 150*1.6495)
        exitDialog.setLocationRelativeTo(this); // Căn giữa màn hình

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3)); // Tăng độ dày viền (2*1.3333)
        dialogPanel.setBackground(new Color(176, 226, 255));

        JLabel messageLabel = new JLabel("Bạn có muốn thoát game?");
        messageLabel.setFont(new Font("Sans Serif Collection", Font.BOLD, 21)); // Tăng kích thước font (16*1.3333)
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(new EmptyBorder(33, 0, 33, 0)); // Tăng padding (20*1.6495)

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());

        JButton yesButton = new JButton("Có");
        yesButton.setPreferredSize(new Dimension(133, 66)); // Tăng kích thước nút (100*1.3333, 40*1.6495)
        yesButton.setMaximumSize(new Dimension(133, 66));
        yesButton.setForeground(Color.BLACK);
        yesButton.setBackground(Color.WHITE);
        yesButton.setFont(new Font("Sans Serif Collection", Font.PLAIN, 19)); // Tăng kích thước font (14*1.3333)
        yesButton.setBorder(BorderFactory.createEmptyBorder());
        yesButton.setFocusPainted(false);
        yesButton.setContentAreaFilled(false);
        yesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                yesButton.setForeground(Color.CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                yesButton.setForeground(new Color(0, 128, 0));
            }
        });
        yesButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c.getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 27, 27)); // Tăng bo tròn (20*1.3333)
                g2d.setColor(Color.BLACK);
                g2d.draw(new RoundRectangle2D.Float(0, 0, c.getWidth() - 1, c.getHeight() - 1, 27, 27));
                super.paint(g, c);
                g2d.dispose();
            }
        });

        yesButton.addActionListener(e -> System.exit(0));

        JButton noButton = new JButton("Không");
        noButton.setPreferredSize(new Dimension(133, 66)); // Tăng kích thước nút (100*1.3333, 40*1.6495)
        noButton.setMaximumSize(new Dimension(133, 66));
        noButton.setForeground(Color.BLACK);
        noButton.setBackground(Color.WHITE);
        noButton.setFont(new Font("Sans Serif Collection", Font.PLAIN, 19)); // Tăng kích thước font (14*1.3333)
        noButton.setBorder(BorderFactory.createEmptyBorder());
        noButton.setFocusPainted(false);
        noButton.setContentAreaFilled(false);
        noButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                noButton.setForeground(Color.CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                noButton.setForeground(new Color(0, 128, 0));
            }
        });
        noButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c.getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 27, 27)); // Tăng bo tròn (20*1.3333)
                g2d.setColor(Color.BLACK);
                g2d.draw(new RoundRectangle2D.Float(0, 0, c.getWidth() - 1, c.getHeight() - 1, 27, 27));
                super.paint(g, c);
                g2d.dispose();
            }
        });

        noButton.addActionListener(e -> exitDialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(Box.createHorizontalStrut(33)); // Tăng khoảng cách (20*1.6495)
        buttonPanel.add(noButton);
        buttonPanel.add(Box.createHorizontalGlue());

        dialogPanel.add(messageLabel);
        dialogPanel.add(buttonPanel);

        exitDialog.add(dialogPanel);
        exitDialog.setVisible(true);
    }
}