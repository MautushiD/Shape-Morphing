package polygonapprox;
import java.awt.Point;

public class Lines {
  public double slope;
    public double yIntercept;
    public double pyIntercept1;
    public double pyIntercept2;
    public double pslope;
    public double errorThreshold = ShapePolygonalApproximationMain.lineThickness;
   
    public Lines(Point pt0, Point pt1){
    if(pt0.equals(pt1)) throw new IllegalArgumentException("Cannot create straight line from two identical points");
    int i,j;
//   i=(pt0.y - pt1.y);
//   j=(pt0.x - pt1.x);
//    slope=(double)i/j;
    slope = ImageUtility.getSlope(pt0.x, pt0.y, pt1.x, pt1.y);
    if(slope==Double.POSITIVE_INFINITY || slope==Double.NEGATIVE_INFINITY){
    	this.pslope=0;
    }
    if(slope==0){
    	pslope=Double.POSITIVE_INFINITY;
    }
    else{
    //this.yIntercept = pt0.y - this.slope * pt0.x;
    	this.yIntercept = ImageUtility.getIntercept(pt0.x, pt0.y, pt1.x, pt1.y);
    this.pslope=(-1/this.slope);
    }}
   void PLines1(Point pt0,double pslope){
        this.pyIntercept1=pt0.y-this.pslope*pt0.x;
    }
   void PLines2(Point pt0,double pslope){
        this.pyIntercept2=pt0.y-this.pslope*pt0.x;
    }
   
   int PointLocationP(Point pt0, Point p1,Point p2){
	   double k;
	   int p = 0;
	   k= Math.sqrt(((p1.y-p2.y)*(p1.y-p2.y))+((p1.x-p2.x)*(p1.x-p2.x)));
	   double k1,k2 = 0;
	   double d1;
	   d1=Math.sqrt(1+pslope*pslope);
	   k1=Math.abs(pt0.y-(pslope*pt0.x)-pyIntercept1);
	   k1=k1/d1;
	   k2=Math.abs(pt0.y-(pslope*pt0.x)-pyIntercept2);
	   k2=k2/d1;
	   
	   //double theta = Math.toDegrees(pslope);
	   //k1 = ImageUtility.getLineDistance(theta, pyIntercept1, pt0.x, pt0.y);
	  // k2 = ImageUtility.getLineDistance(theta, pyIntercept2, pt0.x, pt0.y);
	  // k = ImageUtility.getPointDistance(p1.x, p1.y, p2.x, p2.y);
	   
	   //System.out.println("d1= "+d1+" k= " +k+" k1= "+k1+" k2= "+k2);
	   double temp1=(k1+k2);
	   int temp2=(int)Math.abs(temp1-k);
	   if(k1>0 && k2>0){
	   if(temp2<=errorThreshold)
		   p=1;
	   else if(temp1>temp2)
		   p=-1;
	   }
	   else if(k1==0 || k2==0)
	    p=0;
   return p;    
   }
   
    int PointLocation(Point pt0,double slope,double yIntercept){
   
       if(pt0.y==(slope*pt0.x + yIntercept))
   return 0;
       else{
           if(pt0.y<(slope*pt0.x + yIntercept))
               return -1;
       else
               return 1;
       }
   }
}