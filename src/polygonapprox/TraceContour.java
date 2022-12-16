package polygonapprox;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;




public class TraceContour {
	int height;
	int width;
	int[] borderImage;
	int[] image;
	int[] borderX;
	int[] borderY;
	int[] keyPoint;
	int[] refinedKeyPtX;
	int[] refinedKeyPtY;
	int refinedKeyPtSize = 0;
	float[] smoothX;
	float[] smoothY;
	int smoothBorderLen=0;
	int keyPointLen = 0;
	int boundaryLen1 = 0;

	int boundaryLen = 0;
	int BLACK = 255;
	int WHITE = 0;
	int imageArea = 0;
	int lineWidth = 0;
	static int dropEveryBorderPtIndex = 0;

	public int getBoundaryLen() {
		return boundaryLen;
	}
	public void setBoundaryLen(int boundaryLen) {
		this.boundaryLen = boundaryLen;
	}
	public int getImageArea() {
		return imageArea;
	}
	public void setImageArea(int imageArea) {
		this.imageArea = imageArea;
	}
	TraceContour(int[] imgIn, int imgWidth, int imgHeight){
		width = imgWidth;
		height = imgHeight;
		imageArea = 0;
		image = new int[width*height];
		borderImage = new int[width * height];
		for(int i = 0; i < width*height; i++){
			image[i] = imgIn[i] & 0xFF;
			if(image[i] == 0xFF){
				imageArea++;
			}
			borderImage[i] = WHITE;
		}
		borderX = new int[width * height];
		borderY = new int[width * height];
		smoothX = new float[width * height];
		smoothY = new float[width * height];

	}
	public int[] getContourImage(){
		int[] contourImage = new int[width * height];
		for(int i = 0; i < width*height; i++){
			contourImage[i] = 255;
		}
		for (int i = 0; i < boundaryLen; i++) {
			contourImage[ borderY[i] * width + borderX[i]]= 0;
			//System.out.println("["+borderX[i]+","+ borderY[i]+"]->["+ borderX[(i + 1) % boundaryLen]+","+ borderY[(i + 1) % boundaryLen]+"]");
		}


		return contourImage;
	}

	public Point2D[] mooreNeighborTracing(){
		int pos = 0, flag = 0;
		boolean inside = false;
		boundaryLen = 0;

		for(int y = 1; y < height - 1; y ++)
		{
			for(int x = 1; x < width - 1; x ++)
			{
				pos = x + y * width;

				// Scan for BLACK pixel
				if(borderImage[pos] > 0 && !inside)    // Entering an already discovered border
				{
					inside = true;

				}
				else if(image[pos] == BLACK && inside)  // Already discovered border point
				{
					continue;
				}
				else if(image[pos] == WHITE && inside)  // Leaving a border
				{
					inside = false;
				}
				else if(image[pos] == BLACK && !inside)  // Undiscovered border point
				{
					if(pos != 0 && borderImage[pos] == 0){
						borderImage[pos] = 1;   // Mark the start pixel
						borderX[boundaryLen] = x;
						borderY[boundaryLen] = y;
						//System.out.println("X :"+borderX[boundaryLen]+" Y :"+borderY[boundaryLen]);
						boundaryLen++;
						flag=1;
					}
					int checkLocationNr = 1;  // The neighbor number of the location we want to check for a new border point
					int checkPosition;      // The corresponding absolute array address of checkLocationNr
					int newCheckLocationNr;   // Variable that holds the neighborhood position we want to check if we find a new border at checkLocationNr
					int startPos = pos;      // Set start position
					int counter = 0;       // Counter is used for the jacobi stop criterion
					int counter2 = 0;       // Counter2 is used to determine if the point we have discovered is one single point

					// Defines the neighborhood offset position from current position and the neighborhood
					// position we want to check next if we find a new border at checkLocationNr
					int[][] neighborhood = {
							{-1,7},
							{-3-width,7},
							{-width-2,1},
							{-1-width,1},
							{1,3},
							{3+width,3},
							{width+2,5},
							{1+width,5}
					};
					// Trace around the neighborhood
					while(true)
					{
						checkPosition = pos + neighborhood[checkLocationNr-1][0];
						newCheckLocationNr = neighborhood[checkLocationNr-1][1];

						if(image[checkPosition] == BLACK) // Next border point found
						{
							if(checkPosition == startPos)
							{
								counter ++;

								// Stopping criterion (jacob)
								if(newCheckLocationNr == 1 || counter >= 3)
								{
									// Close loop
									inside = true; // Since we are starting the search at were we first started we must set inside to true
									break;
								}
							}

							checkLocationNr = newCheckLocationNr; // Update which neighborhood position we should check next
							pos = checkPosition; 
							counter2 = 0;             // Reset the counter that keeps track of how many neighbors we have visited
							if(checkPosition != 0 && borderImage[checkPosition] != boundaryLen){
								borderImage[checkPosition] = boundaryLen; // Set the border pixel
								int y1=checkPosition/width;
								int x1=checkPosition-y1*width;
								borderX[boundaryLen] = x1;
								borderY[boundaryLen] = y1;
								//	System.out.println("X :"+borderX[boundaryLen]+" Y :"+borderY[boundaryLen]);

								boundaryLen++;
							}
						}
						else
						{
							// Rotate clockwise in the neighborhood
							checkLocationNr = 1 + (checkLocationNr % 8);
							if(counter2 > 8)
							{
								// If counter2 is above 8 we have traced around the neighborhood and
								// therefore the border is a single black pixel and we can exit
								counter2 = 0;
								break;
							}
							else
							{
								counter2 ++;
							}
						}
					}
				}

				if(flag==1)break;
			}
			if(flag==1)break;
		}



		for(int i = 0; i < boundaryLen; i ++){
			if(borderX[i] == 0 && borderY[i] == 0){
				borderX[i] = borderX[(i+1) % boundaryLen];
				borderY[i] =  borderY[(i+1) % boundaryLen];

			}
		}
		int[] borderXnew = new int[boundaryLen];
		int[] borderYnew = new int[boundaryLen];
		int  boundaryLenNew = 0;
		for (int i = 0; i < boundaryLen; i++) {
			if(!(image[borderY[i] * width + borderX[i] - 1] == 255 && 
					image[borderY[i] * width + borderX[i] + 1] == 255
					&& image[(borderY[i] - 1) * width + borderX[i] - 1] == 255 && 
					image[(borderY[i] + 1) * width + borderX[i]] == 255)){
				borderXnew[boundaryLenNew] = borderX[i];
				borderYnew[boundaryLenNew] = borderY[i];
				boundaryLenNew++;
			}
		}
		boundaryLen = boundaryLenNew;
		for (int i = 0; i < boundaryLen; i++) {
			borderX[i] = borderXnew[i];
			borderY[i] = borderYnew[i];
		}
		System.out.println("boundaryLen = "+boundaryLen);

		
		int cnt = 0;
		int drop = dropEveryBorderPtIndex;
		ArrayList<Point2D> borderPtList = new ArrayList<Point2D>();
		for (int i = 0; i < boundaryLen; i++) {
			int x = borderX[i];
			int y = borderY[i];
			if(drop != 0){
				if(cnt != drop - 1){
					borderPtList.add(new Point2D(x, y));
				} else {
					borderImage[y * width + x] = 0;
				}
				
				cnt = (cnt + 1) % drop;
			} else {
				borderPtList.add(new Point2D(x, y));
			}
		}
		Point2D[] borderPt = new Point2D[borderPtList.size()];
		for (int i = 0; i < borderPtList.size(); i++){
			borderPt[i] = borderPtList.get(i);
		}
		for(int i = 0; i < borderImage.length; i++){
			if(borderImage[i] != 0){
				borderImage[i] = 255;
			}
		}
		return borderPt;
	}
	public int[] getBorderImage() {
		return borderImage;
	}


}
