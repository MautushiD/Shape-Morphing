package polygonapprox;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Image image = null;
	private int width;
	private int height;
	private int x0 = 16;
	private int y0 = 16;
	private int gap = 0;
	private int id = 0;
	int[][] mark = null;

	int keyPointsLen = 0;
	int[] keyPointX;
	int[] keyPointY;
	boolean showImage = false;
	String polygonCode = "";
	int lineStrokeWidth = 3;
	int polygonVertexNum = 0;
	public Image getImage(){
		return image;
	}
	public int getLineStrokeWidth() {
		return lineStrokeWidth;
	}

	public void setLineStrokeWidth(int lineStrokeWidth) {
		this.lineStrokeWidth = lineStrokeWidth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private Color polygonColor = Color.green;

	public synchronized String getPolygonCode() {
		return polygonCode;
	}

	public synchronized void setPolygonCode(String polygonCode) {
		this.polygonCode += polygonCode;
	}

	public synchronized boolean isShowImage() {
		return showImage;
	}
	public synchronized void reset(){
		int k = 0;
		for(int i = 0; i < height; i ++){
			for(int j = 0; j < width; j++){
				mark[i][j] = -1;
				k++;
			}
		}
		keyPointX = new int[height * width];
		keyPointY = new int[height * width];
		keyPointsLen = 0;
		showImage = false;
		polygonCode = "";
	}
	public synchronized void setShowImage(boolean showImage) {
		this.showImage = showImage;
		//repaint();
	}
	public synchronized void drawPolygon(Point2D[] polygon, Color polygonColor){
		if(polygon == null)
			return;
		for(int i = 0; i < polygon.length; i++){
			this.markImage((int)polygon[i].x(), (int)polygon[i].y(), i);
		}
		polygonVertexNum = polygon.length;
		this.polygonColor = polygonColor;
	}

	public ImagePanel(String title, int index, int width, int height){
		setSize(width + 2 * x0, height + 2 * y0);
		this.width = width;
		this.height = height;
		id = index;
		mark = new int[height][width];
		for(int i = 0; i < height; i ++){
			for(int j = 0; j < width; j++){
				mark[i][j] = -1;
			}
		}
		keyPointX = new int[height * width];
		keyPointY = new int[height * width];
		keyPointsLen = 0;
		setBackground(Color.black);
		//setBorder(new TitledBorder(title));
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	}

	public  synchronized void refreshView(Image img){
		image = img;
		if (image != null) {
			repaint();
		} 

	}
	public  synchronized void markImage(int x, int y, int v){
		if(y > height || y < 0 || x < 0 || x > width)
			return;
		mark[y][x] = v;
		if(v > -1){
			//System.out.println(x+", "+y);
			keyPointX[keyPointsLen] = x0 - 1 + x;
			keyPointY[keyPointsLen] = y0 - 1 + y;
			keyPointsLen++;
		}
		//repaint();
	}

	protected void paintComponent(Graphics g) {
		if (image != null && g != null) {
			if(image.getWidth(null) != width || image.getHeight(null) != height){
				g.drawImage(image.getScaledInstance(width, height, Image.SCALE_REPLICATE),x0, y0, null);
			}
			else {
				g.drawImage(image, x0, y0, null);
			}

		}
		//g.setColor(Color.CYAN);
		//g.drawPolygon(keyPointX, keyPointY, keyPointsLen);


		Graphics2D g2 = (Graphics2D) g;
		//float alpha = (float) 0.66;
		//Color color = new Color((float)0, (float) 1, (float)0, alpha); //Red 
		Color color = polygonColor;
		if(showImage){
			if(keyPointsLen > 2){
				g2.setColor(color);
				g2.setStroke(new BasicStroke(lineStrokeWidth * 2));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawPolygon(keyPointX, keyPointY, keyPointsLen);
				//System.out.println("draw polygon");
			}

			//			for(int i = 0; i < keyPointsLen - 1; i++){
			//				//g.setColor(Color.CYAN);
			//				//g.drawLine(keyPointX[i], keyPointY[i], keyPointX[(i+1)%keyPointsLen], keyPointY[(i+1)%keyPointsLen]);
			//				
			//				double x1 = keyPointX[i];
			//				double y1 = keyPointY[i];
			//				
			//				double x2 = keyPointX[(i+1)%keyPointsLen];
			//				double y2 = keyPointY[(i+1)%keyPointsLen];
			//				
			//				g2.setColor(color);
			//				g2.setStroke(new BasicStroke(lineStrokeWidth * 2));
			//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//				g2.draw(new Line2D.Float((int)x1, (int)y1, 
			//						(int)x2, (int)y2));
			//				g2.setColor(Color.BLUE);
			//				g2.setStroke(new BasicStroke(lineStrokeWidth>>1));
			//				g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
			//				//g.drawString("("+(int)(x1 - x0 + 1)+","+(int)(y1 - y0 + 1)+")", keyPointX[i]+ 10, keyPointY[i] + 10);
			//
			//
			////				g.drawLine(keyPointX[i], keyPointY[i], 
			////						keyPointX[(i+1)%keyPointsLen], keyPointY[(i+1)%keyPointsLen]);
			//
			//			}
			//			g2.setColor(color);
			//			g2.setStroke(new BasicStroke(lineStrokeWidth * 2));
			//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//			g2.draw(new Line2D.Float(keyPointX[keyPointsLen - 1], keyPointY[keyPointsLen - 1], 
			//					keyPointX[0], keyPointY[0]));
			//			g2.setColor(Color.BLUE);
			//			g2.setStroke(new BasicStroke(lineStrokeWidth>>1));
			//
			//			g2.drawLine(keyPointX[keyPointsLen - 1], keyPointY[keyPointsLen - 1], 
			//					keyPointX[0], keyPointY[0]);
			for(int i = 0; i < height; i ++){
				for(int j = 0; j < width; j++){
					if(mark[i][j] >= 0){
						g.setColor(Color.YELLOW);
						g.fill3DRect(x0 + j - 1, y0 + i - 1, 5, 5, true);
						//g.setColor(Color.GREEN);
						//if(mark[i][j] > 0){
						//g.drawString(""+mark[i][j], x0 + j + 10, y0 + i + 10);
						//}
					}else if(mark[i][j] == -2){
						g.setColor(Color.MAGENTA);
						g.fill3DRect(x0 + j - 1, y0 + i - 1, 5, 5, true);
					}
				}

			}

			for(int i = 0; i < polygonVertexNum; i++){
				//g.setColor(Color.CYAN);
				//g.drawLine(keyPointX[i], keyPointY[i], keyPointX[(i+1)%keyPointsLen], keyPointY[(i+1)%keyPointsLen]);

				//double x1 = keyPointX[i];
				//double y1 = keyPointY[i];
				//double x2 = keyPointX[(i+1)%keyPointsLen];
				//double y2 = keyPointY[(i+1)%keyPointsLen];

				//g2.setColor(Color.RED);
				//g2.setStroke(new BasicStroke(3));
				//g2.draw(new Line2D.Float((int)x1, (int)y1, 
				//		(int)x2, (int)y2));
				g.setColor(Color.BLUE);
				g.drawString(""+i, keyPointX[i]+ 10, keyPointY[i] + 10);


			}
			g.drawString(polygonCode, x0 + 10, y0 + 10);

		}
	}
	public void save(String fileName){
		BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D cg = bImg.createGraphics();
		this.paintAll(cg);
		try {
			ImageIO.write(bImg, "png", new File(fileName));
		} catch (IOException e){

		}
	}

}
