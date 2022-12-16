package polygonapprox;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;



public class ImageUtility {
	static int windowHeight = 3;
	static int windowWidth = 3;
	static enum Turn { CLOCKWISE, COUNTER_CLOCKWISE, COLLINEAR }
	public static void doPadding(int[] roi, int width, int height, int dx){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(i < dx || i > height - dx || j < dx || j > width - dx){
					roi[i * width + j] = 0;
				}
			}
		}


	}
	public static Point2D getIntermediatePoint(Point2D p1, Point2D p2, double d){
		Point2D p = null;
		double x = 0, y = 0;
		
		if(p1.x() == p2.x()){
			x = p1.x();
			if(p2.y() > p1.y()){
				y = p1.y() + d;
			} else {
				y = p1.y() - d;
			}
		} else if(p1.y() == p2.y()){
			y = p1.y();
			if(p2.x() > p1.x()){
				x = p1.x() + d;
			} else {
				x = p1.x() - d;
			}
		} else {
			double m = Math.abs((p1.y() - p2.y())/(p1.x() - p2.x()));
			double deltaX = d/Math.sqrt(1 + m * m);
			double deltaY = (m * d)/Math.sqrt(1 + m * m);
			if(p2.x() < p1.x() && deltaX > 0){
				deltaX = -deltaX;
			}
			if(p2.y() < p1.y() && deltaY > 0){
				deltaY = -deltaY;
			} 
			x = p1.x() + deltaX;
			y = p1.y() + deltaY;
			//System.out.println("deltaX = "+deltaX+" deltaY = "+deltaY);
		}
		p = new Point2D(x, y);
		return p; 
	}
	/* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */
	static void combinationUtil(int arr[], int data[], int start,
			int end, int index, int r)
	{
		// Current combination is ready to be printed, print it
		if (index == r)
		{
			for (int j=0; j<r; j++)
				System.out.print(data[j]+" ");
			System.out.println("");
			return;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i=start; i<=end && end-i+1 >= r-index; i++)
		{
			data[index] = arr[i];
			combinationUtil(arr, data, i+1, end, index+1, r);
		}
	}

	// The main function that prints all combinations of size r
	// in arr[] of size n. This function mainly uses combinationUtil()
	static void printCombination(int arr[], int n, int r)
	{
		// A temporary array to store all combination one by one
		int data[]=new int[r];

		// Print all combination using temprary array 'data[]'
		combinationUtil(arr, data, 0, n-1, 0, r);
	}
	public static void combine(String instr, StringBuffer outstr, int index)
	{
		for (int i = index; i < instr.length(); i++)
		{
			outstr.append(instr.charAt(i));
			System.out.println(outstr);
			combine(instr, outstr, i + 1);
			outstr.deleteCharAt(outstr.length() - 1);
		}
	} 


	public static int[] getInvertedImage(int[] image){
		for(int i = 0; i < image.length; i++){
			image[i] = 255 - image[i];
		}
		return image;
	}
	public static Point2D getNextCurvePt(Point2D[] curve, Point2D pt){
		Point2D nextPt = null;
		for(int i = 0; i < curve.length - 1; i++){
			if(pt.x() == curve[i].x() && pt.y() == curve[i].y()){
				nextPt = curve[i+1];
				break;
			}
		}

		//System.out.println();
		return nextPt;
	}

	public static Point2D[] getCurve(Point2D[] curve, Point2D p1, Point2D p2){
		//System.out.println("getCurve::"+p1+"->"+p2);
		ArrayList<Point2D> curvePtList = new ArrayList<Point2D>();
		boolean start = false;
		boolean end = false;
		int i = 0;
		while(!end){
			Point2D p = curve[i];
			if(p.x() == p1.x() && p.y() == p1.y())
				start = true;
			if(start && p.x() == p2.x() && p.y() == p2.y()){
				curvePtList.add(p);
				end = true;
			}
			if(start && !end){
				curvePtList.add(p);
			}

			i = (i + 1) % curve.length;
		}


		Point2D[] curvePts = new Point2D[curvePtList.size()];
		for(int j = 0; j < curvePtList.size(); j++){
			curvePts[j] = curvePtList.get(j);
			//System.out.print(curvePts[j]);
		}
		//System.out.println();
		return curvePts;
	}
	//	public static Point2D[] getMinimumBoundingRectangle(Point2D[] boundary) {
	//		List<Point> points = new ArrayList<Point>();
	//
	//		for(int i = 0; i < boundary.length; i++) {
	//			points.add(new Point((int)boundary[i].x(), (int)boundary[i].y()));
	//		}
	//		List<Point> rectanglePointList = RotatingCalipers.getMinimumBoundingRectangle(points);
	//		Point2D[] rectanglePoints = new  Point2D[rectanglePointList.size()];
	//		int k = 0;
	//		for(Point pt : rectanglePointList){
	//			rectanglePoints[k] = new Point2D(pt.getX(), pt.getY());
	//			//System.out.println(k+"]"+rectanglePoints[k]);
	//			k++;
	//		}
	//		return rectanglePoints;
	//	}

	public static double getThickLine(Point2D[] convexHullPoints, int[] thickLineInfo){
		//Point2D[] pointlist = new Point2D[3];
		int n = convexHullPoints.length;
		int k = 0;

		int i;  //Looping

		double del1, del2, delmin = 0;  //del1 and del2 are shortest distance.
		//Finding largest distance from QN,Q1
		thickLineInfo[0] = n - 1;

		for(i = 1; i < n - 1; i++){
			if(delmin < getLineDistance(convexHullPoints[n-1], convexHullPoints[0], convexHullPoints[i])){
				delmin = getLineDistance(convexHullPoints[n-1], convexHullPoints[0], convexHullPoints[i]);
				k = i;
			}
		}
		thickLineInfo[1] = k;
		i = 1;
		while (i <= n - 1) {

			del1 = getLineDistance(convexHullPoints[i-1], convexHullPoints[i], convexHullPoints[k]);
			del2 = getLineDistance(convexHullPoints[i-1], convexHullPoints[i], convexHullPoints[(k+1)%n]);
			if(del1 < del2)
				k = (k+1) % n;
			else{
				if(del1 < delmin){
					delmin = del1;
					thickLineInfo[0] = i - 1;
					thickLineInfo[1] = k;
				}
				i = i + 1;
			}
		}
		//System.out.println("ConvexHull Size = "+n+" Side = "+thickLineInfo[0]+" dist = "+delmin);
		return delmin;
	}


	public static Point2D[] getPolygonMBR(Point2D[] curve, int lineThickness, int numKeyPts){
		ArrayList<MBRNode> mbrNodeList = new ArrayList<MBRNode>();

		MBRNode root = new MBRNode(curve, lineThickness);
		// mbrPoints[0] corresponds to start
		// mbrPoints[1] corresponds to end
		// create root node with start, end, cost
		mbrNodeList.add(root);
		long startTime = System.currentTimeMillis();
		// while nodeList size < numKeyPts
		while(true){
			// get worst cost node
			double maxCost = -1;
			MBRNode currentNode = null;
			//System.out.println("Start Scanning List ");
			for(MBRNode node : mbrNodeList){
				//System.out.println("cost="+node.getCostVal());
				if(maxCost < node.getCostVal()){
					maxCost = node.getCostVal();
					currentNode = node;
				}
			}
			if(currentNode == null)
				break;
			//System.out.println("End Scanning List ");
			if(mbrNodeList.size() >= numKeyPts){
				break;
			}
			if(currentNode.getThickness() < lineThickness){
				break;
			}
			//System.out.println("currentCurve::"+currentNode.getCurveStartPt()+"->"+currentNode.getCurveEndPt());
			//System.out.println("Cost::"+currentNode.getCostVal());
			double minCost = Double.MAX_VALUE;
			MBRNode child1 = null, child2 = null;
			Point2D[] convexPolygon = currentNode.getConvexHull();
			// for each pt ( = p) other than start and end pts

			for(int p = 0; p < convexPolygon.length; p++){
				if(convexPolygon[p].x() == currentNode.getCurveStartPt().x() &&
						convexPolygon[p].y() == currentNode.getCurveStartPt().y())
					continue;
				if(convexPolygon[p].x() == currentNode.getCurveEndPt().x() &&
						convexPolygon[p].y() == currentNode.getCurveEndPt().y())
					continue;
				//System.out.println("pivot::"+convexPolygon[p]);

				Point2D[] curve1 = getCurve(curve, currentNode.getCurveStartPt(), 
						convexPolygon[p]);
				//System.out.println("curve1:: "+curve1[0]+"->"+curve1[curve1.length -1]+" length::"+curve1.length);
				MBRNode mbrNode1 = new MBRNode(curve1, lineThickness);
				//System.out.println("curve1 cost = "+mbrNode1.cost);
				//System.out.println("curve2::");
				Point2D[] curve2 = getCurve(curve, convexPolygon[p], 
						currentNode.getCurveEndPt());
				//System.out.println("curve2:: "+curve2[0]+"->"+curve2[curve2.length -1]+" length::"+curve2.length);
				MBRNode mbrNode2 = new MBRNode(curve2, lineThickness);
				//System.out.println("curve2 cost = "+mbrNode2.cost);
				double meanCost = (mbrNode1.getCostVal() + mbrNode2.getCostVal())/2;
				double costVariance = ((mbrNode1.getCostVal() - meanCost) * (mbrNode1.getCostVal() - meanCost)
						+ (mbrNode2.getCostVal() - meanCost) * (mbrNode2.getCostVal() - meanCost))/2;
				//System.out.println("cost = "+cost+"::"+mbrNode1.getCostVal()+"+"+mbrNode2.getCostVal());
				//				double costVariation = (Math.abs(mbrNode1.getCostVal() - meanCost) 
				//						 + Math.abs(mbrNode2.getCostVal() - meanCost))/2;

				double cost = meanCost + costVariance;
				if(cost < minCost){
					minCost = cost;
					child1 = mbrNode1;
					child2 = mbrNode2;
				}

			}
			if(child1 != null && child2 != null){
				mbrNodeList.remove(currentNode);
				mbrNodeList.add(child1);
				mbrNodeList.add(child2);
			}
			//System.out.println("1]"+child1.curveStartPt+"->"+child1.curveEndPt);
			//System.out.println("1]"+"Cost = "+child1.cost);
			//System.out.println("2]"+child2.curveStartPt+"->"+child2.curveEndPt);
			//System.out.println("2]"+"Cost = "+child2.cost);
		}
		//		for(int j = 0; j < mbrNodeList.size(); j++){
		//			double sideLen = getPointDistance(mbrNodeList.get(j).getCurveStartPt(), 
		//					mbrNodeList.get(j).getCurveEndPt()); 
		//			if(sideLen < lineThickness)
		//				mbrNodeList.remove(j);
		//		}
		long estimatedTime = System.currentTimeMillis() - startTime;

		int[] curveKeyPtsIndex = new int[curve.length];
		for(int m = 0; m < curve.length; m++){
			curveKeyPtsIndex[m] = 0;
		}
		int numKeyPtObtained = 0;
		int i = 0;
		double totalMaxError = 0.0;
		double totalISError = 0.0;
		double totalThickness = 0.0;
		double maxError = 0.0;
		System.out.println();
		for(int j = 0; j < mbrNodeList.size(); j++){
			double error = mbrNodeList.get(j).getMaxErrorOriginal();
			if(maxError < error){
				maxError = error;
			}
			totalMaxError += error;///mbrNodeList.get(j).getCurve().length;
			totalISError += mbrNodeList.get(j).getISError();///mbrNodeList.get(j).getCurve().length;
			totalThickness += mbrNodeList.get(j).getThickness();///mbrNodeList.get(j).getCurve().length;
			//totalMaxErrorSelective += mbrNodeList.get(j).getMaxErrorSelective();///mbrNodeList.get(j).getCurve().length;
			//totalISErrorSelective += mbrNodeList.get(j).getISErrorSelective();///mbrNodeList.get(j).getCurve().length;
			Point2D p0 = mbrNodeList.get(j).getCurveStartPt();
			Point2D p1 = mbrNodeList.get(j).getCurveEndPt();
			for(int m = 0; m < curve.length; m++){
				if(curveKeyPtsIndex[m] != 1){
					if(curve[m].x() == p0.x() & curve[m].y() == p0.y()){
						curveKeyPtsIndex[m] = 1;
						numKeyPtObtained++;
					} else if(curve[m].x() == p1.x() & curve[m].y() == p1.y()){
						curveKeyPtsIndex[m] = 1;
						numKeyPtObtained++;
					} 
				}
			}
			System.out.print(j+"]"+p0+"->"+p1);
			System.out.println("::ThicknessCost = "+mbrNodeList.get(j).getThickness()+
					"::MaxError = "+mbrNodeList.get(j).getMaxError()+
					"::ISError = "+mbrNodeList.get(j).getISError());
			//System.out.println("    MaxErrorSelective = "+ mbrNodeList.get(j).getMaxErrorSelective());
			//System.out.println("    ISErrorSelective = "+ mbrNodeList.get(j).getISErrorSelective());

			i+=2;
		}
		numKeyPtObtained--;
		Point2D[] curveKeyPts = new Point2D[numKeyPtObtained];
		int k = 0;
		//System.out.println();
		for(int m = 0; m < curve.length; m++){
			if(curveKeyPtsIndex[m] == 1){
				//System.out.print(m+", ");
				if(m != curve.length - 1)
					curveKeyPts[k] = curve[m];
				k++;
			}
		}
		double maxErr = 0.0;
		double ISErr = 0.0;
		double sumErr = 0.0;
		for(int j1 = 0; j1 < (curveKeyPts.length - 1); j1++){
			Point2D p1 = curveKeyPts[j1];
			Point2D p2 = curveKeyPts[(j1+1)%curveKeyPts.length];

			Point2D curveSegment[] = getCurve(curve, p1, p2);
			for(int  j2 = 0;  j2 < curveSegment.length; j2++){
				Point2D p = curveSegment[j2];
				double error = getLineDistance(p1, p2, p);
				ISErr += error * error;
				sumErr += error;
				if(maxErr < error){
					maxErr = error;
				}
			}

		}
		//System.out.println();
		//Point2D[] curvePtsFinetune = getThickEdgePolygonCoverFinetune(curveKeyPts, 1);
		System.out.println("==========Performance=======================");
		System.out.println("Boundary Length = "+curve.length);
		System.out.println("Number of Key Points = "+numKeyPtObtained);
		System.out.println("Polygon Side Thickness = "+lineThickness);
		//System.out.println("Max Error per unit boundary point = "+maxError/curve.length);
		System.out.println("Max Error per unit boundary point = "+(maxErr)/curve.length);
		System.out.println("ISE Error  = "+ISErr);
		System.out.println("Weighted Error(ISE/CR^2) = "+((ISErr*numKeyPtObtained*numKeyPtObtained))/(curve.length*curve.length));
		System.out.println("Sum Error per unit boundary point = "+((sumErr)/ (curve.length)));
		System.out.println("Total Thickness Error per unit boundary point = "+totalThickness/curve.length);
		System.out.println("Estimated Time (millisecond) = "+estimatedTime);
		//System.out.println("Performance(FOM) = ");
		System.out.println("======================================");

		return curveKeyPts;
	}
	public static Point2D[] getThickEdgePolygonCoverFinetune(Point2D[] borderPt, int lineThickness){
		//System.out.println("getThickEdgePolygonCoverFinetune::");
		int start = 0;
		int end = borderPt.length - 1;
		int k = 0;
		int[] points = new int[end - start + 1];
		int len = 1;


		for (int i = start; i <= end; i+= len) {
			int x1 = (int) borderPt[i].x();
			int y1 = (int) borderPt[i].y();
			int x2 = (int) borderPt[(i + 1) % borderPt.length].x();
			int y2 = (int) borderPt[(i + 1) % borderPt.length].y();
			int x3 = (int) borderPt[(i + 2) % borderPt.length].x();
			int y3 = (int) borderPt[(i + 2) % borderPt.length].y();

			double d = ImageUtility.getLineDistance(x1, y1, x3, y3, x2, y2);
			//System.out.print(i+"]"+d+", ");

			if(d <= lineThickness){
				len = 2;
			} else {
				len = 1;
			}
			points[k++] = i;
		}
		Point2D[] pointsNew = new Point2D[k];
		for(int i = 0; i < k; i++){
			int x = (int) borderPt[points[i]].x();
			int y = (int) borderPt[points[i]].y();

			pointsNew[i] = new Point2D(x, y);
		}

		return pointsNew;
	}


	public static Point2D[] getConvexPolygon(Point2D[] boundary){
		//System.out.println("getConvexPolygon::");
		List<Point> points = new ArrayList<Point>();

		for(int i = 0; i < boundary.length; i++) {
			points.add(new Point((int)boundary[i].x(), (int)boundary[i].y()));
		}

		List<Point> convexHull = GrahamScan.getConvexHull(points);
		Point2D[] convexPolygon = new Point2D[convexHull.size()];
		if(convexHull.size() < 3){
			convexPolygon[0] = boundary[0];
			convexPolygon[1] = boundary[1];
		}
		int k = 0;
		for (Point pt : convexHull){
			convexPolygon[k] = new Point2D(pt.getX(), pt.getY());
			//System.out.println(k+"]"+convexPolygon[k]);
			k++;
		}
		return convexPolygon;
	}
	public static Point2D[] getConvexPolygon(Point2D[] boundary, int lineThickness){
		//System.out.println("getConvexPolygon::");
		List<Point> points = new ArrayList<Point>();

		for(int i = 0; i < boundary.length; i++) {
			points.add(new Point((int)boundary[i].x(), (int)boundary[i].y()));
		}

		List<Point> convexHull = GrahamScan.getConvexHull(points);
		Point2D[] convexPolygon = new Point2D[convexHull.size()];
		int k = 0;
		for (Point pt : convexHull){
			convexPolygon[k] = new Point2D(pt.getX(), pt.getY());
			//System.out.println(k+"]"+convexPolygon[k]);
			k++;
		}
		Point2D[] keyPt = convexPolygon;
		int prevNumPoints = convexPolygon.length;	
		int trial = 0;
		do {
			prevNumPoints = keyPt.length;
			keyPt = ImageUtility.getThickEdgePolygonCoverFinetune(keyPt, lineThickness);
			//System.out.println(trial+"]Number of Points:: "+keyPt.length);
			//for(int i = 0; i < keyPt.length; i++){
			//System.out.print(i+"["+borderPt[i].x()+" "+keyPt[i].y()+"]; ");
			//}
			//System.out.println();
			trial++;
		} while (prevNumPoints > keyPt.length);
		if(keyPt.length < 3){
			keyPt = new Point2D[2];
			keyPt[0] = boundary[0];
			keyPt[1] = boundary[boundary.length - 1];
		}
		return keyPt;
	}
	public static Point2D[] getConvexPolygon(Point2D[] boundary, int start, int end, int lineThickness){
		System.out.println("getConvexPolygon::");
		List<Point> points = new ArrayList<Point>();
		for(int i = start; i != (end + 1) % boundary.length ; i = (i + 1) % boundary.length) {
			points.add(new Point((int)boundary[i].x(), (int)boundary[i].y()));
		}

		List<Point> convexHull = GrahamScan.getConvexHull(points);
		Point2D[] convexPolygon = new Point2D[convexHull.size()];
		int k = 0;
		for (Point pt : convexHull){
			convexPolygon[k] = new Point2D(pt.getX(), pt.getY());
			//System.out.println(k+"]"+convexPolygon[k]);
			k++;
		}
		Point2D[] keyPt = convexPolygon;
		int prevNumPoints = convexPolygon.length;	
		int trial = 0;
		do {
			prevNumPoints = keyPt.length;
			keyPt = ImageUtility.getThickEdgePolygonCoverFinetune(keyPt, lineThickness);
			//System.out.println(trial+"]Number of Points:: "+keyPt.length);
			//for(int i = 0; i < keyPt.length; i++){
			//System.out.print(i+"["+borderPt[i].x()+" "+keyPt[i].y()+"]; ");
			//}
			//System.out.println();
			trial++;
		} while (prevNumPoints > keyPt.length);

		return keyPt;
	}

	public static ImagePanel displayImageFrame(Image img, String title){
		JFrame imgFrame = new JFrame();
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		imgFrame.setSize(width + 32 + 8, height + 64);
		imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setTitle("Image Frame");
		Container contentPane = imgFrame.getContentPane();
		ImagePanel imgPanel = new ImagePanel(title, 1, width, height);
		JScrollPane jScrollPane = new JScrollPane(imgPanel);
		// only a configuration to the jScrollPane...
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Then, add the jScrollPane to your frame
		imgFrame.getContentPane().add(jScrollPane);
		contentPane.add(imgPanel);
		imgPanel.refreshView(img);
		imgFrame.setVisible(true);
		return imgPanel;

	}
	public static int[] scaleImageFrame(int[] src,int width,int height,int sf)
	{
		int[] target= new int[sf*height*width*sf];int k=0;
		int tx = 0, ty = 0;
		//System.out.println("width = "+width+" height = "+height);
		for(int i=0;i<height;i++){
			tx = 0;
			for(int j=0;j<width;j++)
			{
				for(int p=0;p<sf;p++){
					for(int q=0;q<sf;q++){
						target[(ty+p)*width*sf +tx+q] = src[i*width +j];
						//System.out.println((ty + p) +" "+(tx+q) + " "+i+" "+j);
					}
				}
				tx += sf;
			}
			ty += sf;
		}
		//System.out.println("End ScaleImage...");
		return target;
	}
	public static Image getImage(int[][] src,int width,int height, boolean rgbFlag){
		int[] srcUpdated = new int[width * height];
		int k = 0;
		for(int i = 0; i <  height; i++){
			for(int j = 0; j < width; j++){
				srcUpdated[k++] = src[i][j];
			}
		}
		if(rgbFlag){
			k = 0;
			for(int i = 0; i <  height; i++){
				for(int j = 0; j < width; j++){
					srcUpdated[k++] = (255 << 24) |  (src[i][j] << 16)
							|  (src[i][j] << 8 ) | src[i][j];
				}

			}
		}
		Container n = new Container();

		Image outImage =  n.createImage (new MemoryImageSource (width, height, srcUpdated, 0, width));
		return outImage;

	}

	public static ImagePanel displayImageFrame(int[][] src,int width,int height, String title, boolean rgbFlag){
		int[] srcUpdated = new int[width * height];
		int k = 0;
		for(int i = 0; i <  height; i++){
			for(int j = 0; j < width; j++){
				srcUpdated[k++] = src[i][j];
			}
		}
		if(rgbFlag){
			k = 0;
			for(int i = 0; i <  height; i++){
				for(int j = 0; j < width; j++){
					srcUpdated[k++] = (255 << 24) |  (src[i][j] << 16)
							|  (src[i][j] << 8 ) | src[i][j];
				}

			}
		}
		JFrame imgFrame = new JFrame();
		imgFrame.setSize(width + 32 + 8 + 10, height + 64 + 10);
		imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setTitle(title);
		Container contentPane = imgFrame.getContentPane();
		ImagePanel imgPanel = new ImagePanel(title, 1, width, height);
		contentPane.add(imgPanel);

		Container n = new Container();

		Image outImage =  n.createImage (new MemoryImageSource (width, height, srcUpdated, 0, width));
		imgPanel.refreshView(outImage);
		imgFrame.setVisible(true);
		return imgPanel;

	}

	static ImagePanel displayImageFrame(int[] src,int width,int height, String title, boolean rgbFlag){
		int[] srcUpdated = new int[width * height];
		System.arraycopy(src, 0, srcUpdated, 0, width * height);
		if(rgbFlag){
			for(int k = 0; k < width * height; k++){			
				srcUpdated[k] = (255 << 24) |  (src[k] << 16)
						|  (src[k] << 8 ) | src[k];

			}
		}



		JFrame imgFrame = new JFrame();

		//imgFrame.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setTitle(title);

		//Container contentPane = imgFrame.getContentPane();
		ImagePanel imgPanel = new ImagePanel(title, 1, width, height);
		//contentPane.add(imgPanel);

		Container n = new Container();

		Image outImage =  n.createImage (new MemoryImageSource (width, height, srcUpdated, 0, width));
		//		BufferedImage  bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//		Graphics  big = bi.getGraphics();
		//		big.drawImage(outImage, 0, 0, null);
		//		try {
		//			ImageIO.write(bi, "png", new File(title));
		//		} catch (IOException e2) {
		//			// TODO Auto-generated catch block
		//			e2.printStackTrace();
		//		}
		imgPanel.refreshView(outImage);
		imgPanel.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgPanel.setSize(width + 32 + 8, height + 64);
		JScrollPane scroller = new JScrollPane(imgPanel);
		scroller.setAutoscrolls(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(scroller, BorderLayout.CENTER);

		imgFrame.setContentPane(panel);

		imgFrame.setSize(width + 32 + 8, height + 64);
		imgFrame.setContentPane(panel);
		imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setVisible(true);

		return imgPanel;

	}
	public static ImagePanel displayBorderImageFrame(int[] src,int width,int height, String title, boolean rFlag, int locX, int locY){
		int[] srcUpdated = new int[width * height];
		System.arraycopy(src, 0, srcUpdated, 0, width * height);
		if(rFlag){
			for(int k = 0; k < width * height; k++){
				if(srcUpdated[k] == 255){
					srcUpdated[k] = 0xFFFFFFFF;
				}else {
					srcUpdated[k] = (255 << 24) |  (255 << 16)
							|  (255 << 8 ) | 255;
				}

			}
		}



		

		//imgFrame.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		//Container contentPane = imgFrame.getContentPane();
		ImagePanel imgPanel = new ImagePanel(title, 1, width, height);
		//contentPane.add(imgPanel);

		Container n = new Container();

		Image outImage =  n.createImage (new MemoryImageSource (width, height, srcUpdated, 0, width));
		//		BufferedImage  bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//		Graphics  big = bi.getGraphics();
		//		big.drawImage(outImage, 0, 0, null);
		//		try {
		//			ImageIO.write(bi, "png", new File(title));
		//		} catch (IOException e2) {
		//			// TODO Auto-generated catch block
		//			e2.printStackTrace();
		//		}
		imgPanel.refreshView(outImage);
		imgPanel.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgPanel.setSize(width + 32 + 8, height + 64);
		JScrollPane scroller = new JScrollPane(imgPanel);
		scroller.setAutoscrolls(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(scroller, BorderLayout.CENTER);
		JFrame imgFrame = new JFrame();
		imgFrame.setTitle(title);
		imgFrame.setContentPane(panel);

		imgFrame.setSize(width + 32 + 8, height + 64);
		imgFrame.setContentPane(panel);
		imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setLocation(locX, locY);
		imgFrame.setVisible(true);

		return imgPanel;

	}
	public static ImagePanel displayBorderImageFrame(int[] src,int width,int height, String title, boolean rFlag){
		int[] srcUpdated = new int[width * height];
		System.arraycopy(src, 0, srcUpdated, 0, width * height);
		if(rFlag){
			for(int k = 0; k < width * height; k++){
				if(srcUpdated[k] == 255){
					srcUpdated[k] = 0xFFFFFFFF;
				}else {
					srcUpdated[k] = (255 << 24) |  (255 << 16)
							|  (255 << 8 ) | 255;
				}

			}
		}



		

		//imgFrame.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		//Container contentPane = imgFrame.getContentPane();
		ImagePanel imgPanel = new ImagePanel(title, 1, width, height);
		//contentPane.add(imgPanel);

		Container n = new Container();

		Image outImage =  n.createImage (new MemoryImageSource (width, height, srcUpdated, 0, width));
		//		BufferedImage  bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//		Graphics  big = bi.getGraphics();
		//		big.drawImage(outImage, 0, 0, null);
		//		try {
		//			ImageIO.write(bi, "png", new File(title));
		//		} catch (IOException e2) {
		//			// TODO Auto-generated catch block
		//			e2.printStackTrace();
		//		}
		imgPanel.refreshView(outImage);
		imgPanel.setPreferredSize(new Dimension(width + 32 + 8, height + 64));
		//imgPanel.setSize(width + 32 + 8, height + 64);
		JScrollPane scroller = new JScrollPane(imgPanel);
		scroller.setAutoscrolls(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(scroller, BorderLayout.CENTER);
		JFrame imgFrame = new JFrame();
		imgFrame.setTitle(title);
		imgFrame.setContentPane(panel);

		imgFrame.setSize(width + 32 + 8, height + 64);
		imgFrame.setContentPane(panel);
		imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imgFrame.setVisible(true);

		return imgPanel;

	}

	public static Point2D getLineIntersectionPoint(Point2D origin1, double slopeAngle1, Point2D origin2, double slopeAngle2){
		Point2D pt = null;
		double m1, m2, c1, c2;

		// y = m1*x + c1 and y = m2 * x + c2
		if(slopeAngle1 != 90 && slopeAngle2 != 90){
			m1 = Math.tan(Math.toRadians(slopeAngle1));
			m2 = Math.tan(Math.toRadians(slopeAngle2));
			c1 = origin1.y() - m1 * origin1.x();
			c2 = origin2.y() - m2 * origin2.x();

			double x = (c2 - c1) / (m1 - m2);
			double y = m1 * x + c1;
			//System.out.println("x = "+x+" y = "+y);
			pt = new Point2D(Math.round(x), Math.round(y));
		} else if(slopeAngle1 == 90){
			m2 = Math.tan(Math.toRadians(slopeAngle2));
			c2 = origin2.y() - m2 * origin2.x();
			double x = origin1.x();
			double y = m2 * x + c2;
			//System.out.println("x = "+x+" y = "+y);
			pt = new Point2D(Math.round(x), Math.round(y));

		} else if(slopeAngle2 == 90){
			m1 = Math.tan(Math.toRadians(slopeAngle1));
			c1 = origin1.y() - m1 * origin1.x();
			double x = origin2.x();
			double y = m1 * x + c1;
			//System.out.println("x = "+x+" y = "+y);
			pt = new Point2D(Math.round(x), Math.round(y));

		}

		return pt;
	}
	public static Point2D[] getExteriorPolygon(Point2D[] polygon, int thickness) {
		double x1 = 0, y1 = 0;
		double x2 = 0, y2 = 0;
		double x3 = 0, y3 = 0;
		Point2D[] exteriorPolygon = new Point2D[polygon.length];
		int index = 0;
		double xp, yp;

		for(int i = 0; i < polygon.length; i++){
			x1 = polygon[i].x();
			y1 = polygon[i].y();

			x2 = polygon[(i + 1) % polygon.length].x();
			y2 = polygon[(i + 1) % polygon.length].y();

			x3 = polygon[(i + 2) % polygon.length].x();
			y3 = polygon[(i + 2) % polygon.length].y();

			double m1 = getSlope(x1, y1, x2, y2);
			double m2 = getSlope(x2, y2, x3, y3);

			if(m1 == Double.NEGATIVE_INFINITY){
				double theta2 = Math.atan(m2);
				double c2;
				if(x2 < x3)
					c2 = getIntercept(x2, y2, x3, y3) - thickness / Math.cos(theta2);
				else
					c2 = getIntercept(x2, y2, x3, y3) + thickness / Math.cos(theta2);
				if(y1 > y2){
					xp = x1 - thickness;
				} else {
					xp = x1 + thickness;
				}

				yp = m2 * xp + c2;

			} else if (m2 == Double.NEGATIVE_INFINITY){
				double theta1 = Math.atan(m1);
				double c1;
				if(x1 < x2)
					c1 = getIntercept(x1, y1, x2, y2) - thickness / Math.cos(theta1);
				else
					c1 = getIntercept(x1, y1, x2, y2) + thickness / Math.cos(theta1);

				if(y2 > y3){
					xp = x2 - thickness;
				} else {
					xp = x2 + thickness;
				}

				yp = m1 * xp + c1;

			} else {
				double theta1 = Math.atan(m1);
				double theta2 = Math.atan(m2);
				double c1, c2;
				if(x1 < x2)
					c1 = getIntercept(x1, y1, x2, y2) - thickness / Math.cos(theta1);
				else
					c1 = getIntercept(x1, y1, x2, y2) + thickness / Math.cos(theta1);
				if(x2 < x3)
					c2 = getIntercept(x2, y2, x3, y3) - thickness / Math.cos(theta2);
				else
					c2 = getIntercept(x2, y2, x3, y3) + thickness / Math.cos(theta2);

				xp = (c1 - c2)/(m2 - m1);
				yp = (m2 * c1 - m1 * c2)/(m2 - m1);

			}
			exteriorPolygon[index] = new Point2D(xp, yp);
			index++;

		}
		//area = getPolygonArea(exteriorPolygon);
		return exteriorPolygon;
	}
	public static int[] getKeyPtIndexList(Point2D[] borderPt, Point2D[] keyPtList){
		int[] keyPtIndexList = new int[keyPtList.length];
		int k = 0;
		//System.out.println("getKeyPtIndexList");
		for(int i = 0; i < keyPtList.length; i++){
			for(int j = 0; j < borderPt.length; j++){
				if(keyPtList[i].x() == borderPt[j].x() && 
						keyPtList[i].y() == borderPt[j].y()){
					keyPtIndexList[k] = j;
					//System.out.println(k+"]keyPtIndex = "+j);
					k++;
					break;
				}
			}
		}
		return keyPtIndexList;
	}
	public static Point2D[] getRegressionPolygon(Point2D[] borderPt, int[] keyPtIndexList, int xub, int yub){
		Point2D[] regressionPolygon = new Point2D[keyPtIndexList.length];
		double xp = 0, yp = 0;
		int index = 0;
		//System.out.println("getRegressionPolygon");
		for(int i = 0; i < keyPtIndexList.length; i++){
			int p1 = keyPtIndexList[i];
			int p2 = keyPtIndexList[(i + 1) % keyPtIndexList.length];
			int p3 = keyPtIndexList[(i + 2) % keyPtIndexList.length];
			//System.out.println("KeyPoints::"+i+" "+(i + 1) % keyPtIndexList.length+" "+(i + 2) % keyPtIndexList.length);
			double[] line1 = LinearRegression.getRegressionLine(borderPt, p1, p2);
			double[] line2 = LinearRegression.getRegressionLine(borderPt, p2, p3);

			double m1 = line1[0], c1 = line1[1];
			double m2 = line2[0], c2 = line2[1];
			if(m1 == Double.NEGATIVE_INFINITY){
				xp = c1;
				yp = m2 * xp + c2;

			} else if (m2 == Double.NEGATIVE_INFINITY){
				xp = c2;
				yp = m1 * xp + c1;

			} else if(m2 != m1){
				xp = (c1 - c2)/(m2 - m1);
				yp = (m2 * c1 - m1 * c2)/(m2 - m1);

			}
			double thetaKeyPt = Math.toDegrees(getSlopeAngleRadian((int)borderPt[p1].x(), (int)borderPt[p1].y(),
					(int)borderPt[p3].x(), (int)borderPt[p3].y())) ;
			double cKeyPt = getIntercept(borderPt[p1].x(), borderPt[p1].y(),
					borderPt[p3].x(), borderPt[p3].y()) ;
			boolean flag1 = isAboveLine(thetaKeyPt, cKeyPt, borderPt[p2].x(), borderPt[p2].y());
			boolean flag2 = isAboveLine(thetaKeyPt, cKeyPt, xp, yp);
			double m3 = getSlopeAngleRadian((int)borderPt[p1].x(), (int)borderPt[p1].y(),
					(int)borderPt[p2].x(), (int)borderPt[p2].y());
			double m4 = getSlopeAngleRadian((int)borderPt[p2].x(), (int)borderPt[p2].y(),
					(int)borderPt[p3].x(), (int)borderPt[p3].y());

			if((!flag1 && flag2) || (flag1 && !flag2) 
					||Math.abs((m1-m3)/m3) > 4 || Math.abs((m2-m4)/m4) > 4){
				xp = borderPt[p2].x();
				yp = borderPt[p2].y();
			}
			//System.out.println(i+"]"+p1+" "+p2+" "+p3+" xp = "+xp+" yp = "+yp);
			//System.out.println(i+"]"+m1+" "+m2+" "+c1+" "+c2);
			//System.out.println(i+"]"+m3+" "+m4+" e1 = "+Math.abs((m1-m3)/m3)
			//		+" e2 = "+Math.abs((m2-m4)/m4));

			if(xp < 0 || yp < 0 || xp > xub || yp > yub){
				xp = borderPt[p2].x();
				yp = borderPt[p2].y();
			}

			regressionPolygon[index] = new Point2D(xp, yp);
			index++;


		}

		return regressionPolygon;

	}
	public static Point2D[] getInteriorPolygon(Point2D[] polygon, int thickness) {
		double x1 = 0, y1 = 0;
		double x2 = 0, y2 = 0;
		double x3 = 0, y3 = 0;
		Point2D[] interiorPolygon = new Point2D[polygon.length];
		int index = 0;
		double xp, yp;

		for(int i = 0; i < polygon.length; i++){
			x1 = polygon[i].x();
			y1 = polygon[i].y();

			x2 = polygon[(i + 1) % polygon.length].x();
			y2 = polygon[(i + 1) % polygon.length].y();

			x3 = polygon[(i + 2) % polygon.length].x();
			y3 = polygon[(i + 2) % polygon.length].y();

			double m1 = getSlope(x1, y1, x2, y2);
			double m2 = getSlope(x2, y2, x3, y3);

			if(m1 == Double.NEGATIVE_INFINITY){
				double theta2 = Math.atan(m2);
				double c2;
				if(x2 < x3)
					c2 = getIntercept(x2, y2, x3, y3) + thickness / Math.cos(theta2);
				else
					c2 = getIntercept(x2, y2, x3, y3) - thickness / Math.cos(theta2);
				if(y1 > y2){
					xp = x1 + thickness;
				} else {
					xp = x1 - thickness;
				}

				yp = m2 * xp + c2;

			} else if (m2 == Double.NEGATIVE_INFINITY){
				double theta1 = Math.atan(m1);
				double c1;
				if(x1 < x2)
					c1 = getIntercept(x1, y1, x2, y2) + thickness / Math.cos(theta1);
				else
					c1 = getIntercept(x1, y1, x2, y2) - thickness / Math.cos(theta1);

				if(y2 > y3){
					xp = x2 + thickness;
				} else {
					xp = x2 - thickness;
				}

				yp = m1 * xp + c1;

			} else {
				double theta1 = Math.atan(m1);
				double theta2 = Math.atan(m2);
				double c1, c2;
				if(x1 < x2)
					c1 = getIntercept(x1, y1, x2, y2) + thickness / Math.cos(theta1);
				else
					c1 = getIntercept(x1, y1, x2, y2) - thickness / Math.cos(theta1);
				if(x2 < x3)
					c2 = getIntercept(x2, y2, x3, y3) + thickness / Math.cos(theta2);
				else
					c2 = getIntercept(x2, y2, x3, y3) - thickness / Math.cos(theta2);

				xp = (c1 - c2)/(m2 - m1);
				yp = (m2 * c1 - m1 * c2)/(m2 - m1);

			}
			interiorPolygon[index] = new Point2D(xp, yp);
			index++;

		}
		//area = getPolygonArea(exteriorPolygon);
		return interiorPolygon;
	}

	public static double getThickSidedPolygonArea(Point2D[] polygon, int thickness, int width, int height) {
		//area = getPolygonArea(exteriorPolygon);
		Point2D[] exteriorPolygon = getExteriorPolygon(polygon, thickness);
		double area = getPolygonResolutionArea(exteriorPolygon, width, height);
		return area;
	}
	public static double getPolygonResolutionArea(Point2D[] polygon, int width, int height) {
		int x = 0;
		int y =0;
		double area = 0;
		for(y = 0; y < height; y++){
			for(x = 0; x < width; x++){
				if(insidePolygon(polygon, polygon.length, x, y)){
					area++;
				}

			}
		}
		return area;
	}

	public static double getPolygonArea(Point2D[] polygon) {
		double x1 = 0, y1 = 0;
		double x2 = 0, y2 = 0;
		double sum = 0;
		double area = 0;
		for(int i = 0; i < polygon.length; i++){
			x1 = polygon[i].x();
			y1 = polygon[i].y();

			x2 = polygon[(i + 1) % polygon.length].x();
			y2 = polygon[(i + 1) % polygon.length].y();

			sum += x1 * y2 - x2 * y1; 

		}
		area = Math.abs(sum) / 2;
		return area;
	}
	public static Point2D getPolygonCenter(Point2D[] polygon) {
		double x1 = 0, y1 = 0;
		double x2 = 0, y2 = 0;
		double x, y;
		double sumX = 0, sumY = 0;
		for(int i = 0; i < polygon.length; i++){
			x1 = polygon[i].x();
			y1 = polygon[i].y();

			x2 = polygon[(i + 1) % polygon.length].x();
			y2 = polygon[(i + 1) % polygon.length].y();

			sumX += (x1 + x2) * (x1 * y2 - x2 * y1);
			sumY += (y1 + y2) * (x1 * y2 - x2 * y1); 

		}
		double a = getPolygonArea(polygon);
		x = Math.abs(sumX / (6 * a));
		y = Math.abs(sumY / (6 * a));
		//System.out.println("Cx = "+x+" Cy = "+y);
		Point2D center = new Point2D(x, y);
		return center;

	}

	public static Point2D getCenter(Point2D[] points, int nvert){
		Point2D c;
		double x = 0.0;
		double y = 0.0;
		for(int i =0; i < nvert; i++){
			x += points[i].x();
			y += points[i].y();
		}
		c = new Point2D(x/nvert, y/nvert);

		return c;
	}
	public static double findTriangleArea(Point2D p1, Point2D p2, Point2D p3)
	{
		double area = 0.0;
		area = (p2.x() - p1.x()) * (p3.y() - p1.y()) - (p3.x() - p1.x()) * (p2.y() - p1.y());

		return area;

	}
	public static boolean insidePolygon(Point2D[] points, int nvert, double testx, double testy)
	{
		int i, j;
		boolean c = false;
		for (i = 0, j = nvert-1; i < nvert; j = i++) {
			if ( ((points[i].y()>testy) != (points[j].y()>testy)) &&
					(testx < (points[j].x()-points[i].x()) * (testy-points[i].y()) / (points[j].y()-points[i].y()) + points[i].x()) )
				c = !c;
		}
		return c;
	}
	public static Point2D[] getPerpendicularPoints(Point2D p1, Point2D p2, int d) {
		Point2D[] pointList = new Point2D[2];
		Point2D pmid = new Point2D((p1.x()+p2.x())/2, (p1.y()+p2.y())/2);

		double dx = pmid.x() - p2.x();
		double dy = pmid.y() - p2.y();

		double len = Math.sqrt(dx * dx + dy * dy);
		double ux = dx / len;
		double uy = dy / len;
		double x3 = pmid.x() + (d/2) * uy;
		double y3 = pmid.y() - (d/2) * ux;
		double x4 = pmid.x() - (d/2) * uy;
		double y4 = pmid.y() + (d/2) * ux;

		Point2D p = new Point2D(x3, y3); pointList[0] = p;
		p = new Point2D(x4, y4); pointList[1] = p;

		return pointList;
	}
	public static Turn getTurn(Point2D a, Point2D b, Point2D c) {

		double crossProduct = (((long)b.x() - a.x()) * ((long)c.y() - a.y())) -
				(((long)b.y() - a.y()) * ((long)c.x() - a.x()));

		if(crossProduct > 0) {
			return Turn.CLOCKWISE;
		}
		else if(crossProduct < 0) {
			return Turn.COUNTER_CLOCKWISE;
		}
		else {
			return Turn.COLLINEAR;
		}
	}
	// Given three colinear points p, q, r, the function checks if
	// point q lies on line segment 'pr'
	public static boolean  onSegment(Point2D p, Point2D q, Point2D r)
	{
		if (q.x() <= Math.max(p.x(), r.x()) && q.x() >= Math.min(p.x(), r.x()) &&
				q.y() <= Math.max(p.y(), r.y()) && q.y() >= Math.min(p.y(), r.y()))
			return true;

		return false;
	}
	public static boolean checkIntersect(Point2D p1, Point2D q1, Point2D p2, Point2D q2)
	{
		// Find whether Line:p1q1 intersects Line:p2q2
		// Find the four orientations needed for general and
		// special cases
		Turn turn1 = getTurn(p1, q1, p2);
		Turn turn2 = getTurn(p1, q1, q2);
		Turn turn3 = getTurn(p2, q2, p1);
		Turn turn4 = getTurn(p2, q2, q1);

		// General case
		if (turn1 != turn2 && turn3 != turn4)
			return true;

		// Special Cases
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1
		if (turn1 == Turn.COLLINEAR && onSegment(p1, p2, q1)) return true;

		// p1, q1 and p2 are colinear and q2 lies on segment p1q1
		if (turn2 == Turn.COLLINEAR && onSegment(p1, q2, q1)) return true;

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2
		if (turn3 == Turn.COLLINEAR && onSegment(p2, p1, q2)) return true;

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2
		if (turn4 == Turn.COLLINEAR && onSegment(p2, q1, q2)) return true;

		return false; // Doesn't fall in any of the above cases
	}
	public static boolean isQuasiVisiblePair(Point2D[] polygon, int vi, int vj, int threshold) {
		boolean visible = false;
		int numIntersection1 = 0;
		int numIntersection2 = 0;
		Point2D p1 = polygon[vi];
		Point2D q1 = polygon[vj];
		Point2D[] pmVerticalShift = getPerpendicularPoints(p1, q1, threshold);


		int n = polygon.length;
		for(int i = 0; i < n; i++){
			Point2D p2 = polygon[i];
			Point2D q2 = polygon[(i+1)%n];
			if(i == vi || (i+1) % n == vi || i == vj || (i+1) % n == vj){
				continue;
			}
			boolean intersectFlag = checkIntersect(p1, pmVerticalShift[0], p2, q2);
			if(intersectFlag){
				numIntersection1++;
			}
		}
		for(int i = 0; i < n; i++){
			Point2D p2 = polygon[i];
			Point2D q2 = polygon[(i+1)%n];
			if(i == vi || (i+1) % n == vi || i == vj || (i+1) % n == vj){
				continue;
			}
			boolean intersectFlag = checkIntersect(pmVerticalShift[0], q1, p2, q2);
			if(intersectFlag){
				numIntersection1++;
			}
		}
		if(numIntersection1 == 0){
			double pmLeftX = (p1.x() +  pmVerticalShift[0].x() )/ 2;
			double pmLeftY = (p1.y() + pmVerticalShift[0].y()) / 2;

			double pmRightX = (q1.x() +  pmVerticalShift[0].x() )/ 2;
			double pmRightY = (q1.y() + pmVerticalShift[0].y()) / 2;

			if(insidePolygon(polygon, polygon.length, pmVerticalShift[0].x(), pmVerticalShift[0].y()) &&
			   insidePolygon(polygon, polygon.length, pmLeftX, pmLeftY) &&
			   insidePolygon(polygon, polygon.length, pmRightX, pmRightY)){
				visible = true;
			} else {
				visible = false;
			}
		}
		if(!visible) {
			for(int i = 0; i < n; i++){
				Point2D p2 = polygon[i];
				Point2D q2 = polygon[(i+1)%n];
				if(i == vi || (i+1) % n == vi || i == vj || (i+1) % n == vj){
					continue;
				}
				boolean intersectFlag = checkIntersect(p1, pmVerticalShift[1], p2, q2);
				if(intersectFlag){
					numIntersection2++;
				}
			}
			for(int i = 0; i < n; i++){
				Point2D p2 = polygon[i];
				Point2D q2 = polygon[(i+1)%n];
				if(i == vi || (i+1) % n == vi || i == vj || (i+1) % n == vj){
					continue;
				}
				boolean intersectFlag = checkIntersect(pmVerticalShift[1], q1, p2, q2);
				if(intersectFlag){
					numIntersection2++;
				}
			}

			if(numIntersection2 == 0){
				double pmLeftX = (p1.x() +  pmVerticalShift[1].x() )/ 2;
				double pmLeftY = (p1.y() + pmVerticalShift[1].y()) / 2;

				double pmRightX = (q1.x() +  pmVerticalShift[1].x() )/ 2;
				double pmRightY = (q1.y() + pmVerticalShift[1].y()) / 2;

				if(insidePolygon(polygon, polygon.length, pmVerticalShift[1].x(), pmVerticalShift[1].y()) &&
						   insidePolygon(polygon, polygon.length, pmLeftX, pmLeftY) &&
						   insidePolygon(polygon, polygon.length, pmRightX, pmRightY)){
					visible = true;
				} else {
					visible = false;
				}
			}
		}
		System.out.println("QuasiVisible = "+visible+ " NumIntersection1 = " + numIntersection1+" NumIntersection2 = " + numIntersection2);
		System.out.println("===================================");
		return visible;
	}
	public static boolean isVisiblePair(Point2D[] polygon, Point2D p1, Point2D q1){
		boolean visible = false;
		int numIntersection = 0;
		int n = polygon.length;
		System.out.println("===================================");
		System.out.println("Visibility Test: "+p1.toString()+", "+q1.toString());
		if(p1.x() == q1.x() && p1.y() == q1.y()){
			visible = true;
		} else {
			for(int i = 0; i < n; i++){
				Point2D p2 = polygon[i];
				Point2D q2 = polygon[(i+1)%n];
				if((p2.x() == p1.x() && p2.y() == p1.y()) || (q2.x() == p1.x() && q2.y() == p1.y()) 
						|| (p2.x() == q1.x() && p2.y() == q1.y()) || (q2.x() == q1.x() && q2.y() == q1.y())){
					continue;
				}
				boolean intersectFlag = checkIntersect(p1, q1, p2, q2);
				if(intersectFlag){
					if(!liesOnSegment(p2, q2, q1)) {
						System.out.println("Intersects with side "+i);
						numIntersection++;
					}
				}
			}
			if(numIntersection == 0){
				double xMid = (p1.x() + q1.x()) / 2;
				double yMid = (p1.y() + q1.y()) / 2;

				if(insidePolygon(polygon, polygon.length, xMid, yMid)){
					visible = true;
				} else {
					visible = false;
				}
			}
		}
		System.out.println("Visible = "+visible+ " NumIntersection = " + numIntersection);
		System.out.println("===================================");
		return visible;
	}

	public static boolean isVisiblePair(Point2D[] polygon, int vi, int vj){
		boolean visible = false;
		int numIntersection = 0;
		int n = polygon.length;
		Point2D p1 = polygon[vi];
		Point2D q1 = polygon[vj];
		System.out.println("===================================");
		System.out.println("Visibility Test: "+vi+", "+vj);
		if(vi == vj){
			visible = true;
		} else if(vj == (vi + 1) % n || vi == (vj + 1) % n){
			visible = true;
		} else {
			for(int i = 0; i < n; i++){
				Point2D p2 = polygon[i];
				Point2D q2 = polygon[(i+1)%n];
				if(i == vi || (i+1) % n == vi || i == vj || (i+1) % n == vj){
					continue;
				}
				boolean intersectFlag = checkIntersect(p1, q1, p2, q2);
				if(intersectFlag){
					numIntersection++;
				}
			}
			if(numIntersection == 0){
				double xMid = (polygon[vi].x() + polygon[vj].x()) / 2;
				double yMid = (polygon[vi].y() + polygon[vj].y()) / 2;

				if(insidePolygon(polygon, polygon.length, xMid, yMid)){
					visible = true;
				} else {
					visible = false;
				}
			}
		}
		System.out.println("Visible = "+visible+ " NumIntersection = " + numIntersection);
		System.out.println("===================================");
		return visible;
	}
	static Point2D getLineIntersectionPoint(Point2D A, Point2D B, Point2D C, Point2D D)
	{
		// Line AB represented as a1x + b1y = c1
		double a1 = B.y() - A.y();
		double b1 = A.x() - B.x();
		double c1 = a1*(A.x()) + b1*(A.y());

		// Line CD represented as a2x + b2y = c2
		double a2 = D.y() - C.y();
		double b2 = C.x() - D.x();
		double c2 = a2*(C.x())+ b2*(C.y());

		double determinant = a1*b2 - a2*b1;

		if (determinant == 0)
		{
			// The lines are parallel. This is simplified
			// by returning a pair of FLT_MAX
			return null;
		}
		else
		{
			double x = (b2*c1 - b1*c2)/determinant;
			double y = (a1*c2 - a2*c1)/determinant;
			return new Point2D(x, y);
		}
	}
	public static Point2D getFootPerpendicular(Point2D p1, Point2D p2, Point2D p) {
		double x1 = p1.x(), y1 = p1.y(), x2 = p2.x(), y2 = p2.y(), x3 = p.x(), y3 = p.y();
		double px = x2 - x1, py = y2 - y1, dAB = px * px + py * py;
		double u = ((x3 - x1) * px + (y3 - y1) * py) / dAB;
		double x = x1 + u * px, y = y1 + u * py;

		Point2D foot = new Point2D(x, y);
		return foot;
	}
	public static boolean liesOnSegment(Point2D a, Point2D b, Point2D  c) {

	    double dotProduct = (c.x() - a.x()) * (c.x() - b.x()) + (c.y() - a.y()) * (c.y() - b.y());
	    if (dotProduct < 0) return true;
	    return false;
	}
	public static byte[][] getVisibilityPolygon(Point2D[] polygon, int threshold){
		int n = polygon.length;
		byte[][] adjMatrix = new byte[n][n];

		for(int vi = 0; vi < n; vi++){
			for(int vj = 0; vj < n; vj++){
				adjMatrix[vi][vj] = -1;
			}
		}
		for(int vi = 0; vi < n; vi++){
			for(int vj = 0; vj < n; vj++){
				if(adjMatrix[vi][vj] == -1){
					if(isVisiblePair(polygon, vi, vj)){
						adjMatrix[vi][vj] = 1;
						adjMatrix[vj][vi] = 1;
					} else if (isQuasiVisiblePair(polygon, vi, vj, threshold)) {
						adjMatrix[vi][vj] = 1;
						adjMatrix[vj][vi] = 1;
					}
					else {
						adjMatrix[vi][vj] = 0;
						adjMatrix[vj][vi] = 0;
					}
				}
			}
		}

		return adjMatrix;
	}
	public static int removeClique(byte[][] adjPolygonMat, byte[][] adjMat, int[] clique, Point2D[] mbrPts){
		int numSide = 0;
		for(int i = 0; i < clique.length; i++){
			boolean deleteFlag = true;
			for(int j = 0; j < adjPolygonMat[clique[i]].length; j++){
				if(adjPolygonMat[clique[i]][j] == 1){
					boolean found = false;
					for(int k = 0; k < clique.length; k++){
						if(clique[k] == j){
							found = true;
						} 
					}
					if(!found){
						deleteFlag = false;
					}
				}
			}
			if(deleteFlag){
				for(int j = 0; j < adjMat[clique[i]].length; j++){
					adjMat[clique[i]][j] = 0;
					adjMat[j][clique[i]] = 0;
					adjPolygonMat[clique[i]][j] = 0;
					adjPolygonMat[j][clique[i]] = 0;
				}
			}
		}
		for(int i = 0; i < clique.length - 1; i++){
			for(int j = i + 1; j < clique.length; j++){
				if(adjPolygonMat[clique[i]][clique[j]] == 0 || 
						adjPolygonMat[clique[j]][clique[i]] == 0){
					//System.out.println("Delete Pair: "+clique[i]+", "+clique[j]);
					adjMat[clique[i]][clique[j]] = 0;
					adjMat[clique[j]][clique[i]] = 0;
				}
			}
		}
		for(int i = 0; i < adjMat.length; i++){
			for(int j = 0; j < adjMat.length; j++){
				if(adjMat[i][j] == 1){
					Point2D p1 = mbrPts[i];
					Point2D q1 = mbrPts[j];
					for(int k = 0; k < clique.length; k++){
						Point2D p2 = mbrPts[clique[k]];
						Point2D q2 = mbrPts[clique[(k+1) % clique.length]];
						if(i == clique[k] || i == clique[(k+1) % clique.length]
								|| j == clique[k] || j == clique[(k+1) % clique.length])
							continue;
						if(adjPolygonMat[i][j] == 0 && checkIntersect(p1, q1, p2, q2)){
							//System.out.println("Intersecting Side "+clique[k]+", "+clique[(k+1) % clique.length]+" Delete pair "+i+", "+j);
							adjMat[i][j] = 0;
							break;
						}
					}
				}
			}
		}
		for(int  j = 0; j < clique.length; j++){
			int curNode = clique[j];
			int nextNode = clique[(j + 1) % clique.length];
			if((curNode+1) % mbrPts.length == nextNode){
				adjMat[curNode][nextNode] = 0;
				adjMat[nextNode][curNode] = 0;
			}
		}
		for(int i = 0; i < adjMat.length; i++){
			for(int j = 0; j < adjMat.length; j++){
				if(i != j && adjMat[i][j] == 1){
					numSide++;
				} 
			}
		}
		return numSide/2;
	}
	public static double getIntercept(double x1, double y1, double x2, double y2){
		double c;

		if(x1 != x2){
			double m = (y2 - y1)/(double)(x2 - x1);
			c = y1 - m * x1;
		} else {
			c = 0;
		}

		return c;
	}
	public static double getSlope(double x1, double y1, double x2, double y2){
		double m =  Double.NEGATIVE_INFINITY;;

		if(x1 != x2){
			m = (y2 - y1)/(double)(x2 - x1);
		}

		return m;
	}

	public static double getSlopeAngleRadian(double x1, double y1, double x2, double y2){
		double radTheta = 0.0;

		if(x1 == x2){
			radTheta = Math.toRadians(90);
		} else {
			radTheta = Math.atan((y2 - y1)/(double)(x2 - x1));
		}

		return radTheta;
	}
	public static boolean isAboveLine(double theta, double c, double x, double y){
		boolean res = false;
		if(theta == 90){
			if(x >= c)
				res = true;
		} else {
			double m = Math.tan(Math.toRadians(theta));

			if(y >= (m * x + c))
				res = true;
		}

		return res;
	}

	public static double getLineDistance(double theta, double c, double x, double y){
		double d = 0;
		if(theta == 0){ // horizontal
			d = Math.abs(c-y);
		}  else if (theta == 90){
			d = Math.abs(c-x);
		} else {
			double m = Math.tan(Math.toRadians(theta));
			double a = (x + m * y - m * c)/(m * m + 1);
			double dx = a - x;
			double dy = a * m + c - y;

			d = Math.sqrt(dx * dx + dy * dy);

		}

		return d;
	}
	public static double getNonAbsoluteLineDistance(double x0, double y0, double x1, double y1, double x, double y){
		double d = 0;
		if(x0 == x1){ // vertical
			d = x - x0;
		}  else if (y0 == y1){
			d = y - y0;
		} else {
			d = ((y0 - y1)*x+(x1 - x0)*y+(x0*y1-x1*y0))/Math.sqrt(Math.abs((x1 - x0)*(x1-x0) + (y1 - y0)* (y1 - y0)));
		}
		return d;
	}
	public static double getLineDistance(Point2D p0, Point2D p1, Point2D p){
		double d = 0;
		double x0 = p0.x();
		double y0 = p0.y();
		double x1 = p1.x();
		double y1 = p1.y();
		double x = p.x();
		double y = p.y();
		if(x0 == x1){ // vertical
			d = Math.abs(x - x0);
		}  else if (y0 == y1){
			d = Math.abs(y - y0);
		} else {
			d = Math.abs(((y0 - y1)*x+(x1 - x0)*y+(x0*y1-x1*y0))/Math.sqrt((x1 - x0)*(x1-x0) + (y1 - y0)* (y1 - y0)));
		}
		return d;
	}

	public static double getLineDistance(double x0, double y0, double x1, double y1, double x, double y){
		double d = 0;
		if(x0 == x1){ // vertical
			d = Math.abs(x - x0);
		}  else if (y0 == y1){
			d = Math.abs(y - y0);
		} else {
			d = Math.abs(((y0 - y1)*x+(x1 - x0)*y+(x0*y1-x1*y0))/Math.sqrt((x1 - x0)*(x1-x0) + (y1 - y0)* (y1 - y0)));
		}
		return d;
	}
	public static double getArea(int x1, int y1, int x2, int y2, int x3, int y3){
		double area = Math.abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));

		return area;
	}
	public static double getAngle(double x0, double y0, double x1, double y1, double x2, double y2){
		double angle = 0.0;
		double b = getPointDistance(x0, y0, x1, y1);
		double a = getPointDistance(x1, y1, x2, y2);
		double c = getPointDistance(x2, y2, x0, y0);
		angle = Math.toDegrees(Math.acos(((a * a + b * b - c * c)/(2*a*b))));
		return angle;
	}
	public static double CalculateAngleFromHorizontal(double startX, double startY, double endX, double endY)
	{
		double atan = Math.atan2(endY - startY, endX - startX); // Angle in radians
		double angleDegrees = atan * (180 / Math.PI);  // Angle in degrees (can be +/-)
		if (angleDegrees < 0.0)
		{
			angleDegrees = 360.0 + angleDegrees;
		}
		return angleDegrees;
	}

	// Angle from point2 to point 3 counter clockwise
	public static double CalculateAngle0To360(double centerX, double centerY, double x2, double y2, double x3, double y3)
	{
		double angle2 = CalculateAngleFromHorizontal(centerX, centerY, x2, y2);
		double angle3 = CalculateAngleFromHorizontal(centerX, centerY, x3, y3);
		return (360.0 + angle3 - angle2)%360;
		//return (360.0 + angle3 - angle2);
	}

	// Smaller angle from point2 to point 3
	public static double CalculateAngle0To180(double centerX, double centerY, double x2, double y2, double x3, double y3)
	{
		double angle = CalculateAngle0To360(centerX, centerY, x2, y2, x3, y3);
		if (angle > 180.0)
		{
			angle = 360 - angle;
		}
		return angle;
	}

	public static double getClockwiseAngle(double x0, double y0, double x1, double y1, double x2, double y2){
		// clockwise angle of vector (p0-p1) to align with (p1-p2) with respect to center at p1 
		double angle = 0.0;
		double dx1 = x0 - x1;
		double dx2 = x2 - x1;
		double dy1 = y0 - y1;
		double dy2 = y2 - y1;


		angle = Math.toDegrees(Math.atan2(dx1 * dy2 - dy1 * dx2, dx1 * dx2 + dy1 * dy2));
		if(angle < 0){
			angle += 360;
		}
		return angle;
	}

	public static double getPointDistance(int x0, int y0, int x1, int y1){
		double d = Math.sqrt((x1 - x0)*(x1 - x0) + (y1 - y0)* (y1 - y0));

		return d;
	}
	public static double getPointDistance(double x0, double y0, double x1, double y1){
		double d = Math.sqrt((x1 - x0)*(x1 - x0) + (y1 - y0)* (y1 - y0));

		return d;
	}
	public static double getPointDistance(Point2D p0, Point2D p1){
		double x0 = p0.x();
		double y0 = p0.y();
		double x1 = p1.x(); 
		double y1 = p1.y();;
		double d = Math.sqrt((x1 - x0)*(x1 - x0) + (y1 - y0)* (y1 - y0));

		return d;
	}

	public static void writeImageFile(int[] orig, int width, int height, String out){
		int maxPos = 0;
		for(int i = 0; i < orig.length; i++){
			if(orig[i] > orig[maxPos]){
				maxPos = i;
			}
		}

		int[] aux = new int[orig.length];
		for(int i = 0; i < orig.length; i++){
			int val =  0;
			if(orig[maxPos] != 0)
				val = (int)((orig[i] * 255) / orig[maxPos]); 
			aux[i] =  0xff000000 | ((int)(val) << 16 | (int)(val) << 8 | (int)(val));
		}
		Container n = new Container();
		Image output = n.createImage(new MemoryImageSource(width, height, aux, 0, width));
		try {
			BufferedImage  bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics  big1 = bi1.getGraphics();
			big1.drawImage(output, 0, 0, null);
			//ImageIO.write(bi1, "png", new File(imgPrefix[imgPrefix.length - 2]+"_retAna.png"));
			ImageIO.write(bi1, "png", new File(out));
			//displayImageFrame(bi1, "Output");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getRed(int pixel, ColorModel m_colorModel)
	{
		if ((m_colorModel instanceof IndexColorModel))	  
			return ((IndexColorModel)m_colorModel).getRed(pixel);
		else
			return ((DirectColorModel)m_colorModel).getRed(pixel);
	}

	// returns the green component value of a pixel
	public static int getGreen(int pixel, ColorModel m_colorModel)
	{
		if ((m_colorModel instanceof IndexColorModel))	  
			return ((IndexColorModel)m_colorModel).getGreen(pixel);
		else
			return ((DirectColorModel)m_colorModel).getGreen(pixel);
	}

	// returns the blue component value of a pixel
	public static int getBlue(int pixel, ColorModel m_colorModel)
	{
		if ((m_colorModel instanceof IndexColorModel))	  
			return ((IndexColorModel)m_colorModel).getBlue(pixel);
		else
			return ((DirectColorModel)m_colorModel).getBlue(pixel);
	}

	public static double[] normalize(double[] list, int length, int max){
		double[] norm = new double[length];
		int maxPos = 0;
		for(int i = 0; i < length; i++){
			if(list[i] > list[maxPos]){
				maxPos = i;
			}
		}
		for(int i = 0; i < length; i++){
			if(list[maxPos] != 0)
				norm[i] = ((list[i] * max) / list[maxPos]); 
		}

		return norm;
	}
	public static double findNormalizeFactor(double[] list, int length, double max){
		int maxPos = 0;
		for(int i = 0; i < length; i++){
			if(list[i] > list[maxPos]){
				maxPos = i;
			}
		}

		return (max/list[maxPos]);
	}
	public int[] equalizeHistogram(int[] src, int minTh){
		int[] cmf = new int[256];
		int[] hist = new int[256];
		int[] target = new int[src.length];
		for(int i = 0; i < 256; i++){
			cmf[i] = 0;
			hist[i] = 0;
		}
		for(int i = 0; i < src.length; i++){
			if(src[i] < minTh)
				src[i] = 0;
		}

		for(int i = 0; i < src.length; i++){
			int v = src[i] & 0xFF;
			try{
				hist[v]++;
			}catch(Exception e){
				System.out.println(v);
			}
		}
		cmf[0] = hist[0];
		int min = cmf[0];
		for(int i = 1; i < 255; i++){
			cmf[i] = cmf[i-1] + hist[i];
			if(min == 0){
				min = cmf[i];
			}
		}
		for(int i = 0; i < src.length; i++){
			target[i] = Math.round(((cmf[src[i]] - min)* 255)/(float)(src.length - min));
		}
		return target;
	}
	public static void erosion(int[] binImage , int[] afterErosion,int height , int width)
	{
		int k ;
		int[] window = null;
		window = new int[10];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				afterErosion[i*width+j]=0;
			}
		}		

		k = width + 1;
		for(int i = 1; i < height - 1; i++){
			for(int j = 1; j < width - 1; j++){

				window[0] = binImage[k - width - 1];
				window[1] = binImage[k - width];
				window[2] = binImage[k - width + 1];
				window[3] = binImage[k - 1];
				window[4] = binImage[k];
				window[5] = binImage[k + 1];
				window[6] = binImage[k + width - 1];
				window[7] = binImage[k + width];
				window[8] = binImage[k + width + 1];

				if((window[0]==0) || (window[1]==0) || (window[2]==0) || (window[3]==0)|| (window[4]==0) || 
						(window[5]==0) || (window[6]==0) || (window[7]==0) || (window[8]== 0))
				{
					afterErosion[k - width - 1] = 0;
					afterErosion[k - width] = 0;
					afterErosion[k - width + 1] = 0;
					afterErosion[k - 1] = 0;
					afterErosion[k] = 0;
					afterErosion[k + 1] = 0;
					afterErosion[k + width - 1] = 0;
					afterErosion[k + width] = 0;
					afterErosion[k + width + 1] = 0;
				}
				else
				{
					afterErosion[k - width - 1] = binImage[k - width - 1];
					afterErosion[k - width] = binImage[k - width];
					afterErosion[k - width + 1] = binImage[k - width +1];
					afterErosion[k - 1] = binImage[k - 1];
					afterErosion[k] = binImage[k];
					afterErosion[k + 1] = binImage[k + 1];
					afterErosion[k + width - 1] = binImage[k + width - 1];
					afterErosion[k + width] = binImage[k + width];
					afterErosion[k + width + 1] = binImage[k + width + 1];

				}		
				k++;
				if(j==width-2){
					k+=2;
				}
			}
		}	
	}




	public static void dialateGreyImg(int[] afterErosion , int[] afterDialate, int height , int width){
		//int k ;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				afterDialate[i*width+j] = 0;
			}
		}
		for(int i = ((windowHeight - 1) / 2); i < height - ((windowHeight - 1) / 2); i++){
			for(int j = ((windowWidth - 1) / 2); j < width - ((windowWidth - 1) / 2); j++){
				int k = 0;
				int max = 0;
				int startRow =  i - ((windowHeight - 1) / 2);
				int endRow = startRow + windowHeight;
				int startCol =  j - ((windowWidth - 1) / 2);
				int endCol = startCol + windowWidth;
				for(int r = startRow; r < endRow; r++){
					for(int c = startCol; c < endCol; c++){
						if(max < afterErosion[r * width + c]){
							max = afterErosion[r * width + c];
						}
					}
				}

				{
					for(int r = startRow; r < endRow; r++){
						for(int c = startCol; c < endCol; c++){
							afterDialate[r * width + c] = max;
						}
					}
				}
			}       
		}
	}
	public static void ErodeGreyImg(int[] afterErosion , int[] afterDialate, int height , int width){
		//int k ;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				afterDialate[i*width+j] = 0;
			}
		}
		for(int i = ((windowHeight - 1) / 2); i < height - ((windowHeight - 1) / 2); i++){
			for(int j = ((windowWidth - 1) / 2); j < width - ((windowWidth - 1) / 2); j++){
				int k = 0;
				int min = 255;
				int startRow =  i - ((windowHeight - 1) / 2);
				int endRow = startRow + windowHeight;
				int startCol =  j - ((windowWidth - 1) / 2);
				int endCol = startCol + windowWidth;
				for(int r = startRow; r < endRow; r++){
					for(int c = startCol; c < endCol; c++){
						if(min > afterErosion[r * width + c]){
							min = afterErosion[r * width + c];
						}
					}
				}

				{
					for(int r = startRow; r < endRow; r++){
						for(int c = startCol; c < endCol; c++){
							afterDialate[r * width + c] = min;
						}
					}
				}
			}       
		}
	}

	public static void dialate(int[] afterErosion , int[] afterDialate, int windowHeight, int windowWidth, int height , int width){
		//int k ;
		int[] window = null;
		window = new int[windowHeight * windowWidth];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				afterDialate[i*width+j] = 0;
			}
		}
		for(int i = ((windowHeight - 1) / 2); i < height - ((windowHeight - 1) / 2); i++){
			for(int j = ((windowWidth - 1) / 2); j < width - ((windowWidth - 1) / 2); j++){
				int k = 0;
				int sum = 0;
				int startRow =  i - ((windowHeight - 1) / 2);
				int endRow = startRow + windowHeight;
				int startCol =  j - ((windowWidth - 1) / 2);
				int endCol = startCol + windowWidth;
				for(int r = startRow; r < endRow; r++){
					for(int c = startCol; c < endCol; c++){
						window[k] = afterErosion[r * width + c];
						sum += window[k];
						k++;
					}
				}

				if(sum > 0)
				{
					for(int r = startRow; r < endRow; r++){
						for(int c = startCol; c < endCol; c++){
							afterDialate[r * width + c] = 255;
						}
					}
				}
			}       
		}
	}
	public static Point2D[] getBresenhamLine(Point2D p0, Point2D p1){
		ArrayList<Point2D> linePtList = new ArrayList<Point2D>();
		int x0 = (int)p0.x();
		int y0 = (int)p0.y();
		int x1 = (int)p1.x();
		int y1 = (int)p1.y();
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1; 
		int sy = y0 < y1 ? 1 : -1; 

		int err = dx-dy;
		int e2;

		while (true) 
		{
			linePtList.add(new Point2D(x0, y0));

			if (x0 == x1 && y0 == y1) 
				break;

			e2 = 2 * err;
			if (e2 > -dy) 
			{
				err = err - dy;
				x0 = x0 + sx;
			}

			if (e2 < dx) 
			{
				err = err + dx;
				y0 = y0 + sy;
			}
		}                     


		Point2D[] line = new Point2D[linePtList.size()];
		linePtList.toArray(line);
		return line;

	}
	public static Point2D[] getDDALine(Point2D p0, double dy, double dx, double lenLimit){
		ArrayList<Point2D> linePtList = new ArrayList<Point2D>();
		double x = p0.x();
		double y = p0.y();
		int deltaX = 0, deltaY = 0;

		if(Math.abs(dx) >= Math.abs(dy)){
			if(dx >= 0)
				deltaX = 1;
			else
				deltaX = -1;
		} else {
			if(dy >= 0)
				deltaY = 1;
			else
				deltaY = -1;
		}


		while (true) 
		{
			double len = getPointDistance(x, y, p0.x(), p0.y());
			if(len >= lenLimit)
				break;
			linePtList.add(new Point2D(x, y));
			if(Math.abs(dx) >= Math.abs(dy)){
				x = x + deltaX;
				if(dy >= 0)
					y = y + Math.abs(dy/dx);
				else
					y = y - Math.abs(dy/dx);
			} else {
				y = y + deltaY;
				if(dx >= 0)
					x = x + Math.abs(dx/dy);
				else
					x = x -  Math.abs(dx/dy);
			}

		}                     


		Point2D[] line = new Point2D[linePtList.size()];
		linePtList.toArray(line);
		return line;

	}

	public static void plotCenterLine(int[][] centerLineImage, int width, int height, Point2D[] line){
		for(int i = 0; i < line.length; i++){
			centerLineImage[(int)line[i].y()][(int)line[i].x()] = 255;
		}
	}
	public static Point2D[] getCenterLine(int[] inputImage, int width, int height, Point2D[] mbrPts, int[] selectClique) {
		// TODO Auto-generated method stub
		ArrayList<Point2D> centerLineList = new ArrayList<Point2D>();
		Point2D[] cliquePolygon = new Point2D[selectClique.length];
		for(int i = 0; i < selectClique.length; i++){
			cliquePolygon[i] = mbrPts[selectClique[i]];
		}
		// find center point
		Point2D center = getPolygonCenter(cliquePolygon);
		int intensity = 0;//inputImage[(int)center.y() * width + (int)center.x()];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				double dist = getPointDistance(center.x(), center.y(), x, y);
				if(dist < 4){
					intensity += inputImage[y * width + x];
				}
			}
		}
		System.out.println("Center Pt Intensity = "+intensity);
		if(intensity < 4){
			for(int i = 0; i < selectClique.length; i++){
				centerLineList.add(mbrPts[selectClique[i]]);
			}
			centerLineList.add(mbrPts[selectClique[0]]);
			Point2D[] centerLine = new Point2D[centerLineList.size()]; 
			centerLineList.toArray(centerLine);
			return centerLine;
		}
		//centerLineList.add(center);

		// find cutMidPtlist
		System.out.println();
		ArrayList<Point2D> cutMidPtlist = new ArrayList<Point2D>();
		int[] cutList = new int[cliquePolygon.length];
		int topMostPt = 0, bottomMostPt = 0, rightMostPt = 0, leftMostPt = 0;
		for(int  j = 0; j < cutList.length; j++){
			cutList[j] = 0;
		}
		for(int  j = 0; j < selectClique.length; j++){
			int curNode = selectClique[j];
			int nextNode = selectClique[(j + 1) % selectClique.length];
			System.out.print(" [curNode = "+curNode+" nextNode = "+nextNode);
			if(mbrPts[curNode].x() > center.x() && mbrPts[curNode].x() > mbrPts[rightMostPt].x()){
				rightMostPt = curNode;
			}
			if(mbrPts[curNode].x() < center.x() && mbrPts[curNode].x() < mbrPts[leftMostPt].x()){
				leftMostPt = curNode;
			}
			if(mbrPts[curNode].y() > center.y() && mbrPts[curNode].y() > mbrPts[bottomMostPt].y()){
				bottomMostPt = curNode;
			}
			if(mbrPts[curNode].y() < center.y() && mbrPts[curNode].y() < mbrPts[topMostPt].y()){
				topMostPt = curNode;
			}

			if((curNode+1) % mbrPts.length != nextNode){
				Point2D p0 = mbrPts[curNode];
				Point2D p1 = mbrPts[nextNode];
				System.out.print("::CUT");
				Point2D midPt = new Point2D((p0.x()+p1.x())/2, (p0.y()+p1.y())/2);
				cutMidPtlist.add(midPt);
				cutList[j] = 1;
				cutList[(j + 1) % cutList.length] = 1;
			}
			System.out.print("]");
		}
		System.out.println();
		System.out.println("["+topMostPt+","+bottomMostPt+","+rightMostPt+","+leftMostPt+"]");
		double maxDist = 0.0;
		int[] pair = new int[2];
		double dist = 0.0;
		for(int i = 0; i < cutMidPtlist.size(); i++){
			for(int j = i + 1; j < cutMidPtlist.size(); j++){
				dist = getPointDistance(cutMidPtlist.get(i), cutMidPtlist.get(j));
				if(maxDist < dist){
					maxDist = dist;
					pair[0] = i;
					pair[1] = j;
				}
			}
		}
		if(cutMidPtlist.size() == 0){
			maxDist = 0.0;
			dist = 0.0;

			for(int i = 0; i < cliquePolygon.length; i++){
				for(int j = i + 1; j < cliquePolygon.length; j++){
					dist = getPointDistance(cliquePolygon[i], cliquePolygon[j]);
					if(dist > maxDist){
						maxDist = dist;
						pair[0] = i;
						pair[1] = j;
					}

				}
			}
			centerLineList.add(cliquePolygon[pair[0]]);
			centerLineList.add(cliquePolygon[pair[1]]);

		}
		else if(cutMidPtlist.size() == 1){
			// terminal end
			centerLineList.add(cutMidPtlist.get(0));
			//centerLineList.add(center);
			Point2D p = null;
			maxDist = 0.0;
			dist = 0.0;
			for(int i = 0; i < cliquePolygon.length; i++){
				if(cutList[i] == 0){
					dist = getPointDistance(cutMidPtlist.get(0), cliquePolygon[i]);
					if(dist > maxDist){
						maxDist = dist;
						p = cliquePolygon[i];
					}
				}
			}
			double leftMostPtDist = getPointDistance(mbrPts[leftMostPt], p);
			double rightMostPtDist = getPointDistance(mbrPts[rightMostPt], p);
			System.out.println("l, r Sep = "+leftMostPtDist +","+rightMostPtDist);
			double topMostPtDist = getPointDistance(mbrPts[topMostPt], p);
			double bottomMostPtDist = getPointDistance(mbrPts[bottomMostPt], p);
			System.out.println("t, b Sep = "+topMostPtDist +","+bottomMostPtDist);

			//if(leftMostPtDist + rightMostPtDist > 14){

			//}
			centerLineList.add(p);

		} else if(cutMidPtlist.size() == 3){
			centerLineList.add(cutMidPtlist.get(0));
			centerLineList.add(center);
			centerLineList.add(cutMidPtlist.get(1));
			centerLineList.add(center);
			centerLineList.add(cutMidPtlist.get(2));

		} else {
			centerLineList.add(cutMidPtlist.get(pair[0]));
			centerLineList.add(center);
			centerLineList.add(cutMidPtlist.get(pair[1]));

		}
		Point2D[] centerLine = new Point2D[centerLineList.size()]; 
		centerLineList.toArray(centerLine);
		return centerLine;
	}

}
