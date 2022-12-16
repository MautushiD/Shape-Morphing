package polygonapprox;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Random;

class RamerDouglasPeuckerFilter
{

	private double epsilon;
	Point2D[] points;
	int[] polygonPointIndex;
	int polygonPointCount = 0;
	int maxPolygonPointCount = 0;

	public RamerDouglasPeuckerFilter(Point2D[] points, double epsilon)
	{
		if (epsilon <= 0)
		{
			throw new IllegalArgumentException("Epsilon nust be > 0");
		}
		this.epsilon = epsilon;
		this.points = points;
		polygonPointIndex = new int[points.length];
		for(int i = 0; i < points.length; i++){
			polygonPointIndex[i] = 0;
		}
		maxPolygonPointCount = points.length;

	}

	public int getMaxPolygonPointCount() {
		return maxPolygonPointCount;
	}

	public void setMaxPolygonPointCount(int maxPolygonPointCount) {
		this.maxPolygonPointCount = maxPolygonPointCount;
	}

	public double getEpsilon()
	{
		return epsilon;
	}
	public int doPiecewiseLinearApproximation(int startIndex, int endIndex, int depth)
	{
		//if(endIndex < startIndex)
			//return -1;
		if(polygonPointCount > maxPolygonPointCount){
			return -1;
		}
		System.out.println("startIndex = "+ startIndex+" endIndex = "+endIndex+" polygonPointCount = "+polygonPointCount);
		if(polygonPointIndex[startIndex] == 0){
			polygonPointCount++;
			polygonPointIndex[startIndex] = depth;
		}
		if(polygonPointIndex[endIndex] == 0){
			polygonPointCount++;
			polygonPointIndex[endIndex] = depth;
		}

		double dmax = epsilon;
		int idx = -1;
		double yspan = points[endIndex].y() - points[startIndex].y();
		double xspan = points[endIndex].x() - points[startIndex].x();
		double c = points[endIndex].x() * points[startIndex].y()
		- points[startIndex].x() * points[endIndex].y();
		double norm = sqrt(pow(xspan, 2) + pow(yspan, 2));
		for (int i = startIndex + 1; i != endIndex; i = (i + 1) % points.length)
		{
			double x0 = points[i].x();
			double y0 = points[i].y();
			double distance = abs(yspan * x0 -  xspan * y0 + c) / norm;
			System.out.println("distance = "+ distance);
			if (distance >= dmax)
			{
				idx = i;
				dmax = distance;
			}
		}
		System.out.println(" dmax = "+dmax+" idx = " +idx);
		if (dmax >= epsilon && idx > 0)
		{
			if(polygonPointIndex[idx] == 0){
				polygonPointCount++;
				polygonPointIndex[idx] = depth;
			}
			//if(polygonPointCount < maxPolygonPointCount){
				doPiecewiseLinearApproximation(startIndex, idx, depth + 1);
				doPiecewiseLinearApproximation(idx, endIndex, depth + 1);
			//}
		} 
		System.out.println("========================================");
		return idx;
	}
	public int getPolygonPointCount() {
		return polygonPointCount;
	}

	public int[] getpolygonPointIndex(){
		return polygonPointIndex;
	}


	public void setEpsilon(double epsilon)
	{
		if (epsilon <= 0)
		{
			throw new IllegalArgumentException("Epsilon nust be > 0");
		}
		this.epsilon = epsilon;
	}

	public static void main(String args[])
	{
	}
}