
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class ViewPanel extends JPanel{
	
	final int radius = 5;
	
	Segment[] segments;
	Segment[] rays;
	
	BufferedImage background, foreground;
	
	Timer looper = new Timer(1000/60, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
		
	});
	
	public ViewPanel() {
		
		try {
			background= ImageIO.read(ViewPanel.class.getResourceAsStream("/background.png"));
			foreground= ImageIO.read(ViewPanel.class.getResourceAsStream("/foreground.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		segments = new Segment[] {
				// Border
				new Segment(new Vector(0,0), new Vector(View.WIDTH,0)),
				new Segment(new Vector(View.WIDTH,0), new Vector(View.WIDTH,View.HEIGHT)),
				new Segment(new Vector(View.WIDTH,View.HEIGHT), new Vector(0,View.HEIGHT)),
				new Segment(new Vector(0,View.HEIGHT), new Vector(0,0)),
				
				// polygon #1
				
				new Segment(new Vector(100, 150), new Vector(120, 50)),
				new Segment(new Vector(120, 50), new Vector(200, 80)),
				new Segment(new Vector(200, 80), new Vector(140, 210)),
				new Segment(new Vector(140, 210), new Vector(100, 150)),
				
				// polygon #2
				
				new Segment(new Vector(100, 200), new Vector(120, 250)),
				new Segment(new Vector(120, 250), new Vector(60, 300)),
				new Segment(new Vector(60, 300), new Vector(100, 200)),
				
				// polygon #3
				
				new Segment(new Vector(200, 260), new Vector(220, 150)),
				new Segment(new Vector(220, 150), new Vector(300, 200)),
				new Segment(new Vector(300, 200), new Vector(350, 320)),
				new Segment(new Vector(350, 320), new Vector(200, 260)),
				
				// polygon #4
				
				new Segment(new Vector(540, 60), new Vector(560, 40)),
				new Segment(new Vector(560, 40), new Vector(570, 70)),
				new Segment(new Vector(570, 70), new Vector(540, 60)),
				
				// polygon #5
				
				new Segment(new Vector(650, 190), new Vector(760, 170)),
				new Segment(new Vector(760, 170), new Vector(740, 270)),
				new Segment(new Vector(740, 270), new Vector(630, 290)),
				new Segment(new Vector(630, 290), new Vector(650, 190)),
				
				// polygon #6
				
				new Segment(new Vector(600, 95), new Vector(780, 50)),
				new Segment(new Vector(780, 50), new Vector(680, 150)),
				new Segment(new Vector(680, 150), new Vector(600, 95)),
		};
		
		looper.start();
		
	}
	
	void createRays() {
		
		rays = new Segment[segments.length * 3];
		
		int counter = 0;
		
		for(int s = 0; s < segments.length; s++) {
			
			Vector a = new Vector(MouseManager.mouse_x, MouseManager.mouse_y);
			Vector b = segments[s].b;
			Vector c = b.subtract(a).normalize();
			
			Segment s1 = new Segment(
					a,
					b
					);
			Segment s2 = new Segment(
					a,
					a.add(c.setDir(c.getAngle() + 0.00001f).normalize().scale(View.WIDTH))
					);
			Segment s3 = new Segment(
					a,
					a.add(c.setDir(c.getAngle() - 0.00001f).normalize().scale(View.WIDTH))
					);
			
			
			
			rays[counter] = s1;
			rays[counter + 1] = s2;
			rays[counter + 2] = s3;
			
			counter += 3;
			
		}
		
	}
	
	void sortRays() {
		
		ArrayList<Segment> list = new ArrayList<Segment>();
		
		for(int r = 0; r < rays.length; r++) {
			list.add(rays[r]);
		}
		
		list.sort(new Comparator<Segment>() {

			@Override
			public int compare(Segment e1, Segment e2) {
				
				Vector v1 = e1.b.subtract(e1.a);
				Vector v2 = e2.b.subtract(e2.a);
				
				
				
				if(v1.getAngle() < v2.getAngle())
					return -1;
				if(v1.getAngle() > v2.getAngle())
					return 1;
				
				return 0;
			}
			
		});
		
		for(int i = 0; i < list.size(); i++) {
			
			rays[i] = list.get(i);
			
		}
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, View.WIDTH, View.HEIGHT);
		
		// draw segments
		
		g2d.setStroke(new BasicStroke(0));
		
		g2d.setColor(Color.GRAY);
		
		for(int i = 0; i < segments.length; i++) {
			
			g2d.drawLine((int)segments[i].a.x, (int)segments[i].a.y,
					(int)segments[i].b.x, (int)segments[i].b.y);
			
		}
		
		createRays();
		sortRays();
		
		// cast rays
		
		for(int r = 0; r < rays.length; r++) {
			Segment ray = rays[r];
			
			Vector closestIntersect = ray.b;
			float distToIntersect = View.WIDTH;
			
			for(int i = 0; i < segments.length; i++) {
				
				Intersect inter = getIntersection(ray, segments[i]);
				
				if(inter != null) {
					
					float d =  inter.distance;
					
					if(distToIntersect > d) {
						distToIntersect = d;
						closestIntersect = inter.intersect;
					}
				}
				
			}
			
			ray.b = closestIntersect;
			
		}
		
		//drawRays(g2d);
		
		g2d.drawImage(background, 0, 0, null);
		
		drawPolygons(g2d);
		
	}
	
	void drawRays(Graphics2D g2d) {
		
		g2d.setColor(Color.GRAY);
		
		for(int r = 0; r < rays.length; r++) {
			
			Segment ray = rays[r];
			
			g2d.drawLine((int)ray.a.x, (int)ray.a.y, (int)ray.b.x, (int)ray.b.y);
			
			g2d.fillOval((int)ray.b.x - radius, (int)ray.b.y - radius,
					radius * 2, radius * 2);
		}
	}
	
	void drawPolygons(Graphics2D g2d) {
		
		for(int r = 0; r < rays.length; r++) {
			
			Segment ray1 = rays[r];
			Segment ray2;
			
			if(r == rays.length-1) {
				ray2 = rays[0];
			}else {
				ray2 = rays[r + 1];
			}
			
			
			int[] x_points = {(int) ray1.a.x, (int) ray1.b.x, (int) ray2.b.x};
			int[] y_points = {(int) ray1.a.y, (int) ray1.b.y, (int) ray2.b.y};
			
			Polygon p = new Polygon(x_points, y_points, 3);
			
			
			
			//g2d.fillPolygon(p);
			
			g2d.setClip(p);
			
			g2d.drawImage(foreground, 0, 0, null);
			
		}
	}
	
	Intersect getIntersection(Segment s1, Segment s2) {
		
		Vector p = s1.a;
		Vector q = s2.a;
		Vector r = s1.b.subtract(s1.a);
		Vector s = s2.b.subtract(s2.a);
		
		float t = (crossProduct(q, s) - crossProduct(p, s)) / crossProduct(r, s);
		float u = (crossProduct(p, r) - crossProduct(q, r)) / crossProduct(s, r);
		
		if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
			return new Intersect(new Vector(p.x + t * r.x, p.y + t * r.y), t);
		}
		return null;
	}
	
	float crossProduct(Vector a, Vector b) {
		return a.x * b.y - a.y * b.x;
	}
	
	class Intersect{
		Vector intersect;
		float distance;
		
		Intersect(Vector i, float d){
			this.intersect = i;
			this.distance = d;
		}
		
	}
	
	class Segment{
		
		Vector a;
		Vector b;
		
		Segment(Vector a, Vector b){
			this.a = a;
			this.b = b;
		}
		
	}
	
	class Vector{
		float x;
		float y;
		
		Vector(float x, float y){
			this.x = x;
			this.y = y;
		}
		
		Vector add(Vector v) {
			return new Vector(x + v.x, y + v.y);
		}
		
		Vector subtract(Vector v) {
			return new Vector(x - v.x, y - v.y);
		}
		
		float getM() {
			return (float)(Math.sqrt(x * x + y * y));
		}
		
		Vector normalize() {
			float m = getM();
			x = x / m;
			y = y / m;
			return this;
		}
		
		Vector scale(float t) {
			x *= t;
			y *= t;
			return this;
		}
		
		Vector setDir(float angle) { 
			
			float m = getM();
			return new Vector((float) (m * Math.cos(angle)), (float) (m * Math.sin(angle)));
			
		}
		
		float getAngle() {
			return (float) Math.atan2(y, x);
		}
		
	}

}
