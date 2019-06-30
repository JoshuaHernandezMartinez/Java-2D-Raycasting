import java.awt.Dimension;

import javax.swing.JFrame;

public class View{
	
	public static final int WIDTH = 840;
	public static final int HEIGHT = 389;
	
	public View() {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(WIDTH, HEIGHT));
		frame.setTitle("Light&Sight");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		ViewPanel v = new ViewPanel();
		MouseManager m = new MouseManager();
		
		frame.add(v);
		frame.addMouseListener(m);
		frame.addMouseMotionListener(m);
		
		frame.setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		new View();
	}

}
