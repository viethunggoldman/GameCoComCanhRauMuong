package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionEvent;

public class Giaodienhuongdan extends JFrame {

	private JPanel contentPane;
	private static Giaodienchinh mainFrame;
	private int xOffset = 0;
	private int yOffset = 0;
	
	public Giaodienhuongdan(Giaodienchinh mainFrame) {
		setUndecorated(true); // Loại bỏ thanh tiêu đề (cùng các nút thu nhỏ/phóng to/thoát)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 800);
		 // LẤY TỌA ĐỘ TỪ MAIN FRAME
	    setLocation(mainFrame.getLocation());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Tạo một panel với ảnh nền
        contentPane = new BackgroundPanel("/picture/giaodienhuongdan.png");
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
	// Phương thức tĩnh để hiển thị cửa sổ
//		public static void showHuongDan() {
//			EventQueue.invokeLater(new Runnable() {
//				public void run() {
//					try {
//						Giaodienhuongdan frame = new Giaodienhuongdan(); // lỗi dòng này
//						frame.setVisible(true);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
}
