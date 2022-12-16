package polygonapprox;

public class LinearRegression { 
		public static double[] getRegressionLine(Point2D[] borderPt, int start, int end){
		double[] line = new double[2];
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		int n  = 0;
		double theta;
		int x1 = (int) borderPt[start].x();
		int y1 = (int) borderPt[start].y();
		int x2 = (int) borderPt[end].x();
		int y2 = (int) borderPt[end].y();

		//System.out.println("start = "+start+" end = "+end+" theta = "+Math.toDegrees(ImageUtility.getSlopeAngle(x1, y1, x2, y2)));
		for(int i = start; n < borderPt.length && i != (end + 1) % borderPt.length; i = (i + 1) % borderPt.length){
			int x = (int) borderPt[i].x();
			int y = (int) borderPt[i].y();
			
			sumx += x;
			sumy += y;
			sumx2 += x * x;
			n++;
			
		}
		double xbar = sumx / n;
		double ybar = sumy / n;
		
	       // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for(int i = start; n < borderPt.length && i != (end + 1) % borderPt.length; i = (i + 1) % borderPt.length){
			int x = (int) borderPt[i].x();
			int y = (int) borderPt[i].y();

            xxbar += (x - xbar) * (x - xbar);
            yybar += (y - ybar) * (y - ybar);
            xybar += (x - xbar) * (y - ybar);
        }
		double m, c;
		System.out.println("xxbar = "+xxbar);
		if(xxbar == 0){
			m = Double.NEGATIVE_INFINITY;
			c = sumx/n;
			theta = 90;
		}

		else {
			m = xybar / xxbar;
			c = ybar - m * xbar;
			theta = Math.toDegrees( Math.atan(m));
		}
         

        // print results
        System.out.println("y   = (" + m + ") * x + (" + c+")");
       
		line[0] = m;
		line[1] = c;
		System.out.println("getRegressionLine:: theta = "+theta+" c = "+c);
		
		return line;
	}
	public static double[] getRegressionLine2(Point2D[] borderPt, int start, int end){
		double[] line = new double[2];
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0, sumxy = 0.0;
		int n  = 0;
		double theta;
		int x1 = (int) borderPt[start].x();
		int y1 = (int) borderPt[start].y();
		int x2 = (int) borderPt[end].x();
		int y2 = (int) borderPt[end].y();

		//System.out.println("start = "+start+" end = "+end+" theta = "+Math.toDegrees(ImageUtility.getSlopeAngle(x1, y1, x2, y2)));
		for(int i = start; n < borderPt.length && i != (end + 1) % borderPt.length; i = (i + 1) % borderPt.length){
			int x = (int) borderPt[i].x();
			int y = (int) borderPt[i].y();
			
			sumx += x;
			sumy += y;
			sumxy += x * y;
			sumx2 += x * x;
			n++;
			
		}
		double m, c;
		double div = n*sumx2-sumx*sumx;
		if(div == 0){
			m = Double.NEGATIVE_INFINITY;
			c = sumx/n;
			theta = 90;
		}
		else {
			//c =((sumx2*sumy - sumx*sumxy)*1.0/div*1.0);
			m =((n*sumxy - sumx*sumy)/div);
			c = (sumy - m * sumx)/(double)n;
			theta = Math.toDegrees(Math.atan(m));
		}
			
		
		line[0] = m;
		line[1] = c;
		System.out.println("getRegressionLine:: theta = "+theta+" c = "+c);
		
		return line;
	}

    public static void main(String[] args) { 
    	
    }
 }
