package view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;

public class Giaodienchonthe extends JFrame {

    private JPanel contentPane;
    private boolean isOnePlayer;
    private int xOffset = 0;
    private int yOffset = 0;
    private boolean playerGoesFirst = false;  // true = người chơi đi trước
    private static Giaodienchinh mainFrame;
    public Giaodienchonthe(boolean isOnePlayer, Giaodienchinh mainFrame) {
        setUndecorated(true);
        this.isOnePlayer = isOnePlayer;
        // Chuẩn bị giá trị X và O ngẫu nhiên cho 2 thẻ
        List<String> cards = Arrays.asList("X", "O");
        Collections.shuffle(cards); // Xáo trộn ngẫu nhiên
        String card1Value = cards.get(0);  // Gán cho thẻ 1
        String card2Value = cards.get(1);  // Gán cho thẻ 2
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800); // Tăng kích thước cửa sổ
        setLocation(mainFrame.getLocation());
        ImageIcon background = new ImageIcon(getClass().getResource("/picture/nen.jpg"));
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                Color semiTransparent = new Color(0, 0, 0, 120); // Đen nhẹ
//                g2.setColor(semiTransparent);
//                g2.fillRect(0, 0, getWidth(), getHeight());
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setBorder(new EmptyBorder(7, 7, 7, 7)); // Tăng border (5*1.3333)
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

        // Dòng hướng dẫn
        JLabel lblTitle = new JLabel("Vui lòng chọn 1 tấm thẻ!");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 37)); // Tăng font (28*1.3333)
        lblTitle.setBounds(400, 82, 533, 66); // Căn giữa: (1200-533)/2 ≈ 400, 50*1.6495 ≈ 82, 400*1.3333 ≈ 533, 40*1.6495 ≈ 66
        contentPane.add(lblTitle);

        // Thẻ bài úp 1
        JButton card1 = new JButton(new ImageIcon(getClass().getResource("/picture/theup.jpg")));
        card1.setBounds(350, 247, 200, 282); // Căn giữa: (1200-133*2-100)/2 ≈ 450, 150*1.6495 ≈ 247, 100*1.3333 ≈ 133, 141*1.6495 ≈ 232
        contentPane.add(card1);

        // Thẻ bài úp 2
        JButton card2 = new JButton(new ImageIcon(getClass().getResource("/picture/theup.jpg")));
        card2.setBounds(700, 247, 200, 282); // Vị trí: 450+133+100 ≈ 583, 247, 133, 232
        contentPane.add(card2);

        // Dòng thông báo người chơi
        String playerText = isOnePlayer ? "Bạn là player! " : "Bạn là player1!";
        JLabel lblNote = new JLabel(playerText + " Nếu chọn \"O\" bạn được đi trước.");
        lblNote.setForeground(Color.WHITE);
        lblNote.setFont(new Font("SansSerif", Font.PLAIN, 27)); // Tăng font (20*1.3333)
        lblNote.setBounds(340, 600, 667, 49); // Căn giữa: (1200-667)/2 ≈ 350, 320*1.6495 ≈ 528, 500*1.3333 ≈ 667, 30*1.6495 ≈ 49
        contentPane.add(lblNote);

        // Tạo Button btnTiepTuc
        JButton btnTiepTuc = new JButton("Vào chơi!");
        btnTiepTuc.setForeground(new Color(255, 255, 255));
        btnTiepTuc.setFont(new Font("Sans Serif Collection", Font.PLAIN, 27)); // Tăng font (20*1.3333)
        btnTiepTuc.setFocusPainted(false);
        btnTiepTuc.setContentAreaFilled(false);
        btnTiepTuc.setBorder(BorderFactory.createEmptyBorder());
        btnTiepTuc.setBackground(new Color(0, 0, 0));
        btnTiepTuc.setBounds(525, 620, 200, 82); // Căn giữa: (1200-200)/2 ≈ 525, 360*1.6495 ≈ 594, 150*1.3333 ≈ 200, 50*1.6495 ≈ 82
        // Thiết lập viền bo tròn cho nút
        btnTiepTuc.setBorder(BorderFactory.createEmptyBorder());
        btnTiepTuc.setFocusPainted(false);  // Loại bỏ viền khi nút được nhấn
        btnTiepTuc.setContentAreaFilled(false);  // Tắt vùng màu của nút

        // Sử dụng RoundRectangle để bo tròn góc
        btnTiepTuc.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c.getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 40, 40));  // Tăng bo tròn (30*1.3333)
                super.paint(g, c);
                g2d.dispose();
            }
        });
        btnTiepTuc.setVisible(false);

        card1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Hiển thị thẻ 1 với X hoặc O ngẫu nhiên
                if (card1Value.equals("X")) {
                    card1.setIcon(new ImageIcon(getClass().getResource("/picture/theX.jpg")));
                    lblNote.setText(isOnePlayer ? "Bot đi trước!" : "Player2 đi trước!");
                    playerGoesFirst = false;
                } else {
                    card1.setIcon(new ImageIcon(getClass().getResource("/picture/theO.jpg")));
                    lblNote.setText(isOnePlayer ? "Player đi trước!" : "Player1 đi trước!");
                    playerGoesFirst = true;
                }
                lblNote.setBounds(545, 550, 667, 49); // Căn giữa: (1200-667)/2 ≈ 400
                card2.setEnabled(false);
                lblTitle.setText("Hoàn tất");
                lblTitle.setBounds(550, 82, 533, 66); // Căn giữa: (1200-533)/2 ≈ 500
                btnTiepTuc.setVisible(true);
            }
        });

        card2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (card2Value.equals("X")) {
                    card2.setIcon(new ImageIcon(getClass().getResource("/picture/theX.jpg")));
                    lblNote.setText(isOnePlayer ? "Bot đi trước!" : "Player2 đi trước!");
                    playerGoesFirst = false;
                } else {
                    card2.setIcon(new ImageIcon(getClass().getResource("/picture/theO.jpg")));
                    lblNote.setText(isOnePlayer ? "Player đi trước!" : "Player1 đi trước!");
                    playerGoesFirst = true;
                }
                lblNote.setBounds(545, 550, 667, 49); // Căn giữa: (1200-667)/2 ≈ 400
                card1.setEnabled(false);
                lblTitle.setText("Hoàn tất");
                lblTitle.setBounds(550, 82, 533, 66); // Căn giữa: (1200-533)/2 ≈ 500
                btnTiepTuc.setVisible(true);
            }
        });

        btnTiepTuc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Giaodienmanchoi manChoi = new Giaodienmanchoi(isOnePlayer, playerGoesFirst, Giaodienchonthe.this);
                manChoi.setVisible(true);
                dispose();
            }
        });

        contentPane.add(btnTiepTuc);
        JButton btnQuayLai = new JButton();
		btnQuayLai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
				  mainFrame.setVisible(true);
				  dispose();

			}
			
		});
		btnQuayLai.setIcon(new ImageIcon(getClass().getResource("/picture/back.png")));
		//btnQuayLai.setIcon(null);
		btnQuayLai.setBounds(10, 10, 100, 100);
		// Làm cho nút trong suốt và không viền
		btnQuayLai.setContentAreaFilled(false);
		btnQuayLai.setBorderPainted(false);
		btnQuayLai.setFocusPainted(false);
		contentPane.add(btnQuayLai);
    }
}