package polygonapprox;
import java.awt.Color;


public class MBRNode {
	private int start; // side
	private int end;// side
	private int pivot;
	private double thickness = 0;
	private double maxError = 0.0;
	private double maxErrorOriginal = 0.0;
	private double ISError = 0.0;
	private double costVal = 0;
	//private double maxErrorSelective = 0.0;
	//private double ISErrorSelective = 0.0;
	private Point2D[] mbrPts = new Point2D[3];
	private Point2D[] convexHull;
	private Point2D curveStartPt, curveEndPt;
	private double area = 0.0;
	private Point2D[] curve;

	public MBRNode(Point2D[] curve, int lineThickness){
		Point2D[] mbrPoints = new Point2D[3];
		//System.out.println("getMBR::"+curve[0]+"->"+curve[curve.length - 1]);
		// get convex hull
		Point2D[] convexPolygon = ImageUtility.getConvexPolygon(curve, lineThickness);

//		float alpha = (float) 0.66;
//		int[] borderImage = ShapePolygonalApproximationMain.traceContour.getBorderImage();
//		Color color = new Color((float)0.5, (float) 0.5, (float)0.5, alpha); //Red 
//		ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(borderImage, 
//				ShapePolygonalApproximationMain.imageWidth, 
//				ShapePolygonalApproximationMain.imageHeight, "MyMBR", true);
//		imgPanel2.setLineStrokeWidth(lineThickness);
//		imgPanel2.drawPolygon(convexPolygon, color);
//		imgPanel2.setShowImage(true);

		double minDist = Double.MAX_VALUE;
		int side = 0;
		int vert = 0;

		int[] thickLineInfo = new int[2];
		minDist = ImageUtility.getThickLine(convexPolygon, thickLineInfo);
		side = thickLineInfo[0];
		vert = thickLineInfo[1];
//		for(int v = 0; v < convexPolygon.length - 1; v++){
//			int v1 = v;
//			int v2 = (v + 1) % convexPolygon.length;
//			double maxDist = 0;
//			int vertex = 0;
//			for(int v3 = 0; v3 < convexPolygon.length; v3++){
//				if(v3 != v1 && v3 != v2){
//					Point2D p0 = convexPolygon[v1];
//					Point2D p1 = convexPolygon[v2];
//					Point2D p = convexPolygon[v3];
//					double dist = ImageUtility.getLineDistance(p0, p1, p);
//					if(dist > maxDist){
//						maxDist = dist;
//						vertex = v3;
//					}
//				}
//			}
//			if(maxDist < minDist){
//				minDist = maxDist;
//				side = v1;
//				vert = vertex;
//			}
//		}

		mbrPoints[0] = new Point2D(convexPolygon[side].x(), convexPolygon[side].y());
		mbrPoints[1] = new Point2D(convexPolygon[(side + 1) % convexPolygon.length].x(), convexPolygon[(side + 1) % convexPolygon.length].y());
		mbrPoints[2] = new Point2D(convexPolygon[vert].x(), convexPolygon[vert].y());

		this.start = side;
		this.end = (side + 1) % convexPolygon.length;
		this.pivot = vert;
		this.thickness = minDist;
		this.mbrPts = mbrPoints;
		this.convexHull = convexPolygon;
		curveStartPt = curve[0];
		curveEndPt = curve[curve.length - 1];
		this.curve = curve;
		area = ImageUtility.getPointDistance(mbrPts[0], mbrPts[1]) * thickness;

		double ISErrorCost = 0.0;
		double maxErrorCost = 0.0;
		double sumErrorCost = 0.0;
		double errorOriginal = 0.0;
		for(int i = 0; i < curve.length; i++){
			errorOriginal = ImageUtility.getLineDistance(this.curveStartPt,this.curveEndPt, curve[i]);
			//double cost = ImageUtility.getLineDistance(this.curveStartPt,this.curveEndPt, curve[i]);
			double error = Math.abs((thickness/2.0) - ImageUtility.getLineDistance(this.mbrPts[0],this.mbrPts[1], curve[i]));
			//double error = ImageUtility.getLineDistance(this.mbrPts[0],this.mbrPts[1], curve[i]);
			//double error = thickness;
			ISErrorCost += error * error;
			sumErrorCost += error;
			if(error > maxErrorCost)
				maxErrorCost = error;
			if(errorOriginal > maxErrorOriginal){
				maxErrorOriginal = errorOriginal;
			}
			//			if(DetectKeyPoints.isBenchmarkKeyPt(curve[i])){
			//				if(error > maxErrorSelective)
			//					maxErrorSelective = error;
			//				ISErrorSelective += error * error;
			//			}
		}
		this.ISError = ISErrorCost;
		this.maxError = maxErrorCost;

		//costVal = area;
		//costVal += maxError;
		//costVal = sumErrorCost * thickness;
		costVal = thickness;
		//costVal += thickness;
		//costVal = area;
		//costVal /= curve.length;//ImageUtility.getPointDistance(this.curveStartPt,this.curveEndPt);
	}
	public double getMaxErrorOriginal() {
		return maxErrorOriginal;
	}
	public Point2D[] getCurve() {
		return curve;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	public int getPivot() {
		return pivot;
	}
	public double getThickness() {
		return thickness;
	}
	public double getMaxError() {
		return maxError;
	}
	public double getISError() {
		return ISError;
	}
	public double getCostVal() {
		return costVal;
	}
	public Point2D[] getMbrPts() {
		return mbrPts;
	}
	public Point2D[] getConvexHull() {
		return convexHull;
	}
	public Point2D getCurveStartPt() {
		return curveStartPt;
	}
	public Point2D getCurveEndPt() {
		return curveEndPt;
	}

}
