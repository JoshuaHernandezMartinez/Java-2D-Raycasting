import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseManager extends MouseAdapter{
	
	public static int mouse_x, mouse_y;
	public static boolean drawReady = false;
	
	public MouseManager(){
		mouse_x = 0;
		mouse_y = 0;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
	}

}
