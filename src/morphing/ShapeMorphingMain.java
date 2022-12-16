package morphing;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import polygonapprox.ImagePanel;
import polygonapprox.ImageUtility;
import polygonapprox.Point2D;
import polygonapprox.ShapePolygonalApproximationMain;

public class ShapeMorphingMain {

	static int startVertex = 3;
	static boolean animationFlag = true;
	static Image imageSrc = null;
	static Image imageTarget = null;
	static Point2D[] srcPolygon = null;
	static Point2D[] targetPolygon = null;
	static int[] inputSrcImage = null;
	static int srcImageWidth, srcImageHeight;
	static double lenRatio;
	static double[] lenMorph = null;
	static int[] morphMap = null;
	static Point2D[] polygon = null;
	static Point2D[] finalPolygon = null;
	static ArrayList<Point2D[]> listLineSeg = null;

	public static void doAnimation(){
		float alpha = (float) 0.66;
		Color color = new Color((float)0.5, (float) 0.5, (float)0.5, alpha); //Red 
		ImagePanel imgPanel1 = ImageUtility.displayBorderImageFrame(inputSrcImage, srcImageWidth, srcImageHeight, "MorphFinal", true, srcImageWidth + 20, 0);
		Point2D[] polygonAnimation = finalPolygon;
		int step = 1;
		boolean flag = true;
		ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(inputSrcImage, srcImageWidth, srcImageHeight, "MorphInitial", true, 0, srcImageHeight + 32);
		imgPanel2.drawPolygon(finalPolygon, color);
		imgPanel2.setShowImage(true);
	

		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imgPanel1.reset();
			imgPanel1.refreshView(imgPanel1.getImage());
			imgPanel1.drawPolygon(polygonAnimation, color);
			imgPanel1.setShowImage(true);
			flag = false;
			for(int i = 1; i < polygon.length; i++){
				Point2D[] lineSeg = listLineSeg.get(i - 1);
				if(step < lineSeg.length){
					polygonAnimation[i] = lineSeg[step];
					flag = true;
				}
			}
			if(!flag){
				imgPanel1.refreshView(imgPanel1.getImage());
				imgPanel1.drawPolygon(polygonAnimation, color);
				imgPanel1.setShowImage(true);

				break;
			}
			step++;
		}

	}
	public static double doMorphing(int t, int s){
		startVertex = t;
		Point2D startPt = srcPolygon[s];
		int curSrcNextVertex = (startVertex + 1)  % srcPolygon.length;;
		ArrayList<Point2D> polygonVertexList = new ArrayList<Point2D>();
		int k = 0;
		for(int i = startVertex, j = 0; j < targetPolygon.length; j++){
			//System.out.println("Target["+i+"] --->  "+"Morph["+k +"]");
			double lenSrc = 0;
			Point2D p0 = targetPolygon[i];
			Point2D p1 = targetPolygon[(i + 1) % targetPolygon.length];
			double lenTarget = lenRatio * ImageUtility.getPointDistance(p0, p1);
			lenMorph[i] = lenTarget;
			morphMap[i] = k;
			//System.out.println(i+"]lenTarget = "+lenTarget);
			while(true){
				//if(!polygonVertexList.contains(startPt)){
				polygonVertexList.add(startPt); k++;
				//}
				double len = ImageUtility.getPointDistance(startPt, srcPolygon[curSrcNextVertex]);
				//System.out.println(" lenSrc = "+(lenSrc+len));
				if(lenSrc + len > lenTarget){
					// cannot consider next point
					//System.out.println(" cannot consider next vertex = "+curSrcNextVertex);
					double d = Math.abs(lenTarget - lenSrc);
					if(d > 0){
						Point2D newPoint = ImageUtility.getIntermediatePoint(startPt, 
								srcPolygon[curSrcNextVertex],  d);
						//System.out.println("Intermediate Point = "+newPoint.toString()+" between "+startPt.toString()+" and "+srcPolygon[curSrcNextVertex].toString());
						startPt = new Point2D(Math.round(newPoint.x()), Math.round(newPoint.y()));
					}
					break;
				} else {
					startPt = srcPolygon[curSrcNextVertex];
					curSrcNextVertex = (curSrcNextVertex + 1) % srcPolygon.length;
					lenSrc = lenSrc + len;
				}
			}
			i = (i + 1) % targetPolygon.length;
		}
		polygon = new Point2D[polygonVertexList.size()];
		polygonVertexList.toArray(polygon);
		//		float alpha = (float) 0.66;
		//		Color color = new Color((float)0.5, (float) 0.5, (float)0.5, alpha); //Red 
		//		ImagePanel imgPanel1 = ImageUtility.displayBorderImageFrame(inputSrcImage, srcImageWidth, srcImageHeight, "MorphInitial", true);
		//		imgPanel1.setLineStrokeWidth(8);
		//		imgPanel1.drawPolygon(polygon, color);
		//		imgPanel1.setShowImage(true);

		finalPolygon = new Point2D[polygon.length];
		polygonVertexList.toArray(finalPolygon);
		finalPolygon[0] = polygon[0]; k++;
		int index = 0;
		for(int i = startVertex, j = 0; j < targetPolygon.length; j++, i = (i + 1) % targetPolygon.length){
			Point2D p0 = finalPolygon[index];
			double dy = targetPolygon[(i + 1) % targetPolygon.length].y() - targetPolygon[i].y();
			double dx = targetPolygon[(i + 1) % targetPolygon.length].x() - targetPolygon[i].x();
			//System.out.println("vertex = "+p0.toString());
			//System.out.println("SideLen = "+lenMorph[i]);
			//System.out.println("dy = "+dy+" dx = "+dx);
			//System.out.println("Slope = "+Math.abs(dy/dx));
			Point2D[] lineSeg = ImageUtility.getDDALine(p0, dy, dx, lenMorph[i]);
			//if(morphMap[(i + 1) % targetPolygon.length] < finalPolygon.length){
			//System.out.println((morphMap[(i + 1) % targetPolygon.length]));
			finalPolygon[morphMap[(i + 1) % targetPolygon.length]] = 
					new Point2D(Math.round(lineSeg[lineSeg.length - 1].x()), 
							Math.round(lineSeg[lineSeg.length - 1].y())); 
			//k = (k + 1) % finalPolygon.length;
			//}
			index = morphMap[(i + 1) % targetPolygon.length];
		}
		for(int i = startVertex, j = 0; j < morphMap.length; j++, i = (i + 1) % morphMap.length){
			int v = (morphMap[i] + 1) % finalPolygon.length;
			int end = morphMap[(i + 1) % morphMap.length];
			//System.out.println("start = "+ morphMap[i]+"("+finalPolygon[morphMap[i]].toString()+")"+
			//		" v = "+v+" end = "+end+"("+finalPolygon[end].toString()+")");
			while(v != end){
				double d = ImageUtility.getPointDistance(polygon[morphMap[i]], finalPolygon[v]);
				//double dy = finalPolygon[end].y() - finalPolygon[morphMap[i]].y();
				//double dx = finalPolygon[end].x() - finalPolygon[morphMap[i]].x();

				Point2D p = ImageUtility.getIntermediatePoint(finalPolygon[morphMap[i]], 
						finalPolygon[end], d);
				//System.out.println(p1.toString());
				//System.out.println("vertex = "+finalPolygon[morphMap[i]].toString());
				//System.out.println("SideLen = "+d);
				//System.out.println("dy = "+dy+" dx = "+dx);
				//System.out.println("Slope = "+Math.abs(dy/dx));

				//Point2D[] lineSeg = ImageUtility.getDDALine(finalPolygon[morphMap[i]], dy, dx, d);
				//Point2D p = new Point2D(Math.round(lineSeg[lineSeg.length - 1].x()), 
				//		Math.round(lineSeg[lineSeg.length - 1].y())); 
				p = new Point2D(Math.round(p.x()), Math.round(p.y()));
				//System.out.println(v+"]"+finalPolygon[v].toString()+" ---> "+p.toString());
				finalPolygon[v] = p;
				v = (v + 1) % polygon.length;
			}
		}
		//		if(!animationFlag){
		//			ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(inputSrcImage, srcImageWidth, srcImageHeight, "MorphFinal", true);
		//			imgPanel2.setLineStrokeWidth(8);
		//			imgPanel2.drawPolygon(finalPolygon, color);
		//			imgPanel2.setShowImage(true);
		//		}
		listLineSeg = new ArrayList<Point2D[]>();
		double deviation = 0;
		for(int i = 1; i < polygon.length; i++){
			Point2D[] lineSeg = ImageUtility.getBresenhamLine(polygon[i], finalPolygon[i]);
			deviation += ImageUtility.getPointDistance(polygon[i], finalPolygon[i]);
			listLineSeg.add(lineSeg);
		}
		return deviation;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imgFileNameSrc = "./data/elephant-16.gif"; //Source Image
		String imgFileNameTarget = "./data/elephant-17.gif"; //Destination Similar Image

		try {
			imageSrc = ImageIO.read(new File(imgFileNameSrc));
			imageTarget = ImageIO.read(new File(imgFileNameTarget));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		srcPolygon = ShapePolygonalApproximationMain.getFeaturePoint(imageSrc, 100, 5, "Src", 0, srcImageHeight + 32);
		System.out.println("Number of vertices in Source Polygon =  "+srcPolygon.length);
		inputSrcImage = new int[ShapePolygonalApproximationMain.inputImage.length];
		for(int i = 0; i < inputSrcImage.length; i++) {
			inputSrcImage[i] = ShapePolygonalApproximationMain.inputImage[i];
		}
		srcImageWidth = ShapePolygonalApproximationMain.imageWidth;
		srcImageHeight = ShapePolygonalApproximationMain.imageHeight;
		ImagePanel imgPanel2 = ImageUtility.displayBorderImageFrame(inputSrcImage, srcImageWidth, srcImageHeight, "MorphInitial", true, 0, srcImageHeight + 32);
		imgPanel2.setShowImage(true);
		targetPolygon = ShapePolygonalApproximationMain.getFeaturePoint(imageTarget, 100, 5, "Target", srcImageWidth + 20, srcImageHeight + 32);
		System.out.println("Number of vertices in Target Polygon =  "+targetPolygon.length);
		for(int i = 0; i < srcPolygon.length; i++){
			srcPolygon[i] = new Point2D(Math.round(srcPolygon[i].x()), Math.round(srcPolygon[i].y()));
		}
		for(int i = 0; i < targetPolygon.length; i++){
			targetPolygon[i] = new Point2D(Math.round(targetPolygon[i].x()), Math.round(targetPolygon[i].y()));
		}


		double Ls = 0.0, Lt = 0.0;
		for(int i = 0; i < srcPolygon.length; i++){
			Point2D p0 = srcPolygon[i];
			Point2D p1 = srcPolygon[(i + 1) % srcPolygon.length];
			Ls += ImageUtility.getPointDistance(p0, p1);
		}

		for(int i = 0; i < targetPolygon.length; i++){
			Point2D p0 = targetPolygon[i];
			Point2D p1 = targetPolygon[(i + 1) % targetPolygon.length];
			Lt += ImageUtility.getPointDistance(p0, p1);
		}
		lenMorph = new double[targetPolygon.length];
		morphMap = new int[targetPolygon.length];
		lenRatio = Ls/Lt;
		double minDeviation = Double.MAX_VALUE;
		int startVertexTargetWithMinDeviation = 0;
		int startVertexSourceWithMinDeviation = 0;
		for(int i = 0; i < targetPolygon.length; i++){
			for(int j = 0; j < srcPolygon.length; j++) {
				double dev = doMorphing(i, j);
				System.out.println("Start Target Vertex = "+i+" Start Source Vertex = "+j+" Total Deviation = "+dev);
				if(dev < minDeviation){
					minDeviation = dev;
					startVertexTargetWithMinDeviation = i;
					startVertexSourceWithMinDeviation = j;
				}
			}
		}
		System.out.println("Solution:: Start Target Vertex = "+startVertexTargetWithMinDeviation+" Start Source Vertex = "+startVertexSourceWithMinDeviation+" Total Deviation = "+minDeviation);
		doMorphing(startVertexTargetWithMinDeviation, startVertexSourceWithMinDeviation);
		if(animationFlag){
			doAnimation();
		}
		System.out.println("Morphing Over...");

	}

}
