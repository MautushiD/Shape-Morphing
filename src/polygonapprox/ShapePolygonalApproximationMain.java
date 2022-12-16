package polygonapprox;
import java.awt.*;
import java.awt.image.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.lang.Math;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import javax.swing.event.*;


public class ShapePolygonalApproximationMain  {
	Image edgeImage, accImage, outputImage;
	static int TH_MIN = 0;
	static int TH_MAX = 200;
	static int TH_INIT = 40;
	static double threshold = (double)TH_INIT;
	static int lineThickness = 3;
	static double widthPolygon = 0;
	//static int orig[] = null;
	static Image edges;
	static int leftMostX;
	static int rightMostX;
	static int topMostY;
	static int bottomMostY;
	static int[] benchmarkKeyPt = null;
	public static int imageWidth, imageHeight;
	public static int[] inputImage = null;
	
	

	public static boolean isBenchmarkKeyPt(Point2D p){
		boolean ret = false;
		if(benchmarkKeyPt[(int)p.y() * imageWidth + (int)p.x()] == 255)
			ret = true;
		return ret;
	}
	public static int[] processImage(Image image){
		int width = image.getWidth(null); 
		int height = image.getHeight(null);
		int[] auxPixel = null;
		int alpha = 255;
		int[] roi = new int[height*width];
		benchmarkKeyPt = new int[height * width];
		leftMostX = width; rightMostX = 0; topMostY = height; bottomMostY = 0;
		//orig = new int[width * height];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, roi, 0, width);
		try {
			pg.grabPixels();
		}
		catch(InterruptedException e2) {
			System.out.println("error: " + e2);
		}
		//System.arraycopy(ciffPixel, 0, auxPixel, 0, width * height);
		int k = 0;
		ColorModel m_colorModel = pg.getColorModel();
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int pixVal = roi[k]; 
				int R = ImageUtility.getRed(pixVal, m_colorModel);
				int G = ImageUtility.getGreen(pixVal, m_colorModel);
				int B = ImageUtility.getBlue(pixVal, m_colorModel);
				int greyVal = (R + G + B) / 3;
				if(greyVal != 0 && greyVal != 255){
					//System.out.println("R="+R+" G="+G+" B="+B);
						benchmarkKeyPt[k] = 255;
				} else {
					benchmarkKeyPt[k] = 0;
				}

				if(greyVal > 128){
					if(i < topMostY){
						topMostY = i;
					}
					if(i > bottomMostY){
						bottomMostY = i;
					}
					if(j > rightMostX){
						rightMostX = j;
					}
					if(j < leftMostX){
						leftMostX = j;
					}

					roi[k] = 255;
				}
				else
					roi[k] = 0;
				k++;

				//				orig[k++] = (alpha << 24) |  (greyVal << 16)
				//				|  (greyVal << 8 ) | greyVal;
				//					roi[roiIndex++]=greyVal;

			}
		}
		return roi;
	}
	
	static int[] addElement(int[] a, int e,int n) {
		int prs = n;
		if(a[a.length-1]== n){
			a[a.length-1] = e;
		}else{
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
		}
	    return a;
	}
	
	 public static boolean contains(Point2D[] mbrPts,int length,Point test) {
         int i;
         int j;
         boolean result = false;
         for (i = 0, j = length - 1; i <length; j = i++) {
           if ((mbrPts[i].y() > test.y) != (mbrPts[j].y() > test.y) &&
               (test.x < (mbrPts[j].x() - mbrPts[i].x()) * (test.y - mbrPts[i].y()) / (mbrPts[j].y()-mbrPts[i].y()) + mbrPts[i].x())) {
             result = !result;
            }
         }
         return result;
       }
	public static TraceContour traceContour = null;
	public static Point2D[] getFeaturePoint(Image image, int numKeyPts, int lineThickness, String title, int locX, int locY){
		Point2D[] featurePt = null;
		int width, height;
		

		//ImageUtility.displayImageFrame(image, "OriginalInput");
		width = image.getWidth(null); height = image.getHeight(null);
		int[] roi = processImage(image);


		// padding
		int dx = lineThickness + 50;
		int ln = roi.length + 2 * (width + 2 * dx) * dx + 2 * height * dx;
		inputImage = new int[ln];
		
		int[] benchmarkKeyPtPadded = new int[ln];
		int k = (width + 2 * dx) * dx + dx;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				inputImage[k] = roi[i * width + j];
				benchmarkKeyPtPadded[k] = benchmarkKeyPt[i * width + j];
				k++;
			}
			k += 2 * dx;
		}
		width = width + 2 * dx;
		height = height + 2 * dx;
		imageWidth = width;
		imageHeight = height;
		benchmarkKeyPt = benchmarkKeyPtPadded;

		traceContour = new TraceContour(inputImage, width, height);
		Point2D[] borderPt = traceContour.mooreNeighborTracing();
		//Point2D[] mbrPts = borderPt;
		Point2D[] mbrPts = ImageUtility.getPolygonMBR(borderPt, lineThickness, numKeyPts);
		int prevNumPoints = mbrPts.length;	
		int trial = 0;
		do {
			prevNumPoints = mbrPts.length;
			mbrPts = ImageUtility.getThickEdgePolygonCoverFinetune(mbrPts, lineThickness);
			//System.out.println(trial+"]Number of Points:: "+keyPt.length);
			//for(int i = 0; i < keyPt.length; i++){
			//System.out.print(i+"["+borderPt[i].x()+" "+keyPt[i].y()+"]; ");
			//}
			//System.out.println();
			trial++;
		} while (prevNumPoints > mbrPts.length);
		if(mbrPts.length < 3){
			mbrPts = new Point2D[2];
			mbrPts[0] = borderPt[0];
			mbrPts[1] = borderPt[borderPt.length - 1];
		}
		featurePt = mbrPts;
		float alpha = (float) 0.66;
		Color color = new Color((float)0.5, (float) 0.5, (float)0.5, alpha); //Red 
		ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(inputImage, width, height, title, true, locX, locY);
		imgPanel2.setLineStrokeWidth(lineThickness);
		imgPanel2.drawPolygon(featurePt, color);
		imgPanel2.setShowImage(true);

		return featurePt;
	}
	public static void main(String[] args) {
		Image image = null;
		int height = 0;
		int width = 0;
		lineThickness = 1;
		int numKeyPts = 19;
		TraceContour.dropEveryBorderPtIndex = 0; // set to zero for normal execution; do not set to one
		String resultFileName = "result_"+"numKeyPts_"+numKeyPts+"_lineThickness_"+lineThickness;
		//String imgFileName = "./data/testSimplePolygon1.bmp";
		//String imgFileName = "./data/testRDP1.bmp";
		String imgFileName = "./data/chromosome60.bmp";
		//String imgFileName = "./data/semicircle.bmp"; // 52, WE2 = 0.22
		//String imgFileName = "./data/leaf.bmp";
		//String imgFileName = "test1.bmp";
		// Expt. data MPEG-7 
		//String imgFileName = "./data/bell-10.gif"; // 36, WE2 = 0.78
		//String imgFileName = "./data/butterfly-13.gif"; // 62, WE2 = 3.72
		//String imgFileName = "./data/chicken-5.gif"; // 66, WE2 = 3.68
		//String imgFileName = "./data/device6-9.gif"; // 39, WE2 = 0.20
		//String imgFileName = "./data/truck-07.gif";		
		// Expt. data MPEG-7 ICIC2016-KU
		//String imgFileName = "./data/vase.gif";
		//String imgFileName = "./data/bird-16.gif";
		//String imgFileName = "./data/butterfly-15.gif";
		//String imgFileName = "./data/frog-3.gif";
		//String imgFileName = "./data/personal_car-10.gif";	
		//String imgFileName = "./data/leaf2.bmp";
		//String imgFileName = "./data/france.bmp";
		//String imgFileName = "./data/netherland.bmp";
		//String imgFileName = "./data/testChord2.bmp";
		//String imgFileName = "./ShowResultImage2D.png";
		//String imgFileName = "./data/testFig_2.bmp";
		//String imgFileName = "./data/pamiTestData.bmp";
		//String imgFileName = "./data/elephant-2.gif";
		//String imgFileName = "./data/bat-15.gif";
		//String imgFileName = "./data/beetle-1.gif";
		//String imgFileName = "./data/test1.gif";
		//String imgFileName = "./data/bell-10.gif";
		//String imgFileName = "./data/testFig_2.bmp";
		//String imgFileName = "./data/chicken-15.gif";
		//String imgFileName = "./data/chicken-20.gif";
		//String imgFileName = "./data/vase.png";
		
		//String imgFileName = "./data/hexa2.png";
		//String imgFileName = "./polygons/poly8.bmp";
		
//		String imgFileName = "./data/tree-8.gif";
//		String imgFileName = "./data/tree-9.gif";
//		String imgFileName = "./data/tree-11.gif";
//		String imgFileName = "./data/tree-16.gif";
//		
//		String imgFileName = "./data/rat-10.gif";
//		String imgFileName = "./data/rat-11.gif";
//		String imgFileName = "./data/rat-12.gif";
//		String imgFileName = "./data/rat-13.gif";
//		
//		String imgFileName = "./data/frog-2.gif";
//		String imgFileName = "./data/frog-3.gif";
//		String imgFileName = "./data/frog-10.gif";
//		String imgFileName = "./data/frog-12.gif";
//		
//		String imgFileName = "./data/elephant-2.gif";
//		String imgFileName = "./data/elephant-5.gif";
//		String imgFileName = "./data/elephant-6.gif";
//		String imgFileName = "./data/elephant-7.gif";
//		
//		String imgFileName = "./data/chicken-1.gif";
//		String imgFileName = "./data/chicken-12.gif";
//		String imgFileName = "./data/chicken-15.gif";
//		String imgFileName = "./data/chicken-20.gif";
//		
//		String imgFileName = "./data/camel-5.gif";
//		String imgFileName = "./data/camel-10.gif";
//		String imgFileName = "./data/camel-11.gif";
//		String imgFileName = "./data/camel-13.gif";
//		
//		String imgFileName = "./data/butterfly-6.gif";
//		String imgFileName = "./data/butterfly-13.gif";
//		String imgFileName = "./data/butterfly-14.gif";
//		String imgFileName = "./data/butterfly-15.gif";
		
//		String imgFileName = "./data/bird-2.gif";
//		String imgFileName = "./data/bird-3.gif";
//		String imgFileName = "./data/bird-4.gif";
//		String imgFileName = "./data/bird-7.gif";
//		
//		String imgFileName = "./data/beetle-11.gif";
//		String imgFileName = "./data/beetle-12.gif";
//		String imgFileName = "./data/beetle-14.gif";
//		String imgFileName = "./data/beetle-15.gif";
		
		if(numKeyPts < 3){
			System.out.println("Number of Key Points must be greater than equal to 3...");
			return;
		}
		try {
			image = ImageIO.read(new File(imgFileName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//ImageUtility.displayImageFrame(image, "OriginalInput");
		width = image.getWidth(null); height = image.getHeight(null);
		int[] roi = processImage(image);
		int rectWidth = rightMostX - leftMostX;
		int rectHeight = bottomMostY - topMostY;

		//System.out.println("rectWidth = "+rectWidth+" rectHeight = "+rectHeight);

		// padding
		int dx = 0;//lineThickness + 20;
		int ln = roi.length + 2 * (width + 2 * dx) * dx + 2 * height * dx;
		int[] inputImage = new int[ln];
		/*for(int i=0;i<ln;i++){
			System.out.println(i+" "+inputImage[i]);
		}*/
		
		int[] benchmarkKeyPtPadded = new int[ln];
		int k = (width + 2 * dx) * dx + dx;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				inputImage[k] = roi[i * width + j];
				benchmarkKeyPtPadded[k] = benchmarkKeyPt[i * width + j];
				k++;
			}
			k += 2 * dx;
		}
		width = width + 2 * dx;
		height = height + 2 * dx;
		imageWidth = width;
		imageHeight = height;
		benchmarkKeyPt = benchmarkKeyPtPadded;

		traceContour = new TraceContour(inputImage, width, height);
		Point2D[] borderPt = traceContour.mooreNeighborTracing();
//		Point2D[] borderPt = new Point2D[BenchmarkData.chromosomeSize];
//		for(int i = 0; i < BenchmarkData.chromosomeSize; i++){
//			borderPt[i] = new Point2D(BenchmarkData.chromosomeX[i], 
//					BenchmarkData.chromosomeY[i]);
//		}
		//traceContour.lineWidth = lineThickness;

		//ImagePanel imgPanel1 = ImageUtility.displayImageFrame(traceContour.getContourImage(), width, height, "contour.png", true);
		//String str = "RotatingCaliper";

		//Point2D[] mbrPts = ImageUtility.getMBR(borderPt, lineWidth).mbrPts;
		Point2D[] mbrPts = ImageUtility.getPolygonMBR(borderPt, lineThickness, numKeyPts);
		//mbrPts = ImageUtility.getThickEdgePolygonCoverFinetune(mbrPts, lineThickness);
		//mbrPts = ImageUtility.getReverse(mbrPts);
//		Point2D[] mbrPts = BenchmarkData.getBenchmarkPt(BenchmarkData.chromosomeBenchmark,
//				BenchmarkData.chromosomeX, BenchmarkData.chromosomeY);
//		Error err = ImageUtility.getError(mbrPts, borderPt);
//		err.printVal();
		float alpha = (float) 0.66;
		Color color = new Color((float)0.5, (float) 0.5, (float)0.5, alpha); //Red 
		//int[] borderImage = traceContour.getBorderImage();
		
		//System.out.println("Polygon Code :: "+ ImageUtility.getPolygonShapeCode(mbrPts));
		ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(inputImage, width, height, "MyMBR", true);
		imgPanel2.setLineStrokeWidth(lineThickness);
		imgPanel2.drawPolygon(mbrPts, color);
		imgPanel2.setShowImage(true);
		imgPanel2.save("result/"+resultFileName+".png");
		
	}
	
}