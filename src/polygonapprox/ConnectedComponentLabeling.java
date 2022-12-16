package polygonapprox;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ConnectedComponentLabeling {

	private static final int FOREGROUND_START_LABEL = 16;
	private static final int FOREGROUND = 255;
	static int[] stackx_CCL = null;
	static int[] stacky_CCL = null;
	static int[] component_CCL;

	private static void ScanEightNeighbourhood(int[] image, int width, int height, int label) {
		// TODO Auto-generated method stub
		int pointer = 1;
		int stackxAddr = 0;
		int stackyAddr = 0;
		int matrixAddr = 0;
		int compMatrixAddr = 0;
		int stackxValue, stackyValue;
		int xValue, yValue;
		int endCol = width - 1;
		int endRow = height - 1;

		stackxAddr++;
		stackyAddr++;



		// Until the stack is empty, iteratively pop form the stack and check
		// for its eight neighbourhood pixels
		while(pointer > 0)
		{
			// Decrease the stack pointer for poping
			pointer--;
			stackxAddr--;
			stackyAddr--;
			// Poping values from column stack and row stack
			stackxValue = stackx_CCL[stackxAddr];
			stackyValue = stacky_CCL[stackyAddr];

			// the pixel is not in first column
			if(stackyValue > 0)
			{
				yValue = stackyValue - 1;

				// Checks whether left pixel is foreground
				compMatrixAddr = matrixAddr + (stackxValue * width) + yValue;
				if(image[compMatrixAddr] == FOREGROUND)
				{   
					// label is assigned to the component
					image[compMatrixAddr] = label;

					component_CCL[label - FOREGROUND_START_LABEL]++;

					// pushing stackxValue to stack address of row
					stackx_CCL[stackxAddr] = stackxValue;

					// pushing yValue to stack address of column
					stacky_CCL[stackyAddr] = yValue;

					// pointer is incremented
					pointer++;

					// stack address of row and column is incremented
					stackxAddr++;
					stackyAddr++;
				}

				// the pixel is not in first row
				if(stackxValue > 0)
				{
					xValue = stackxValue - 1;

					// Checks whether top-left corner pixel is foreground
					compMatrixAddr = matrixAddr + (xValue * width) + yValue;
					if(image[compMatrixAddr] == FOREGROUND)
					{
						// label is assigned to the component
						image[compMatrixAddr] = label;

						component_CCL[label - FOREGROUND_START_LABEL]++;
						// pushing stackxValue to stack address of row
						stackx_CCL[stackxAddr] = xValue;

						// pushing yValue to stack address of column
						stacky_CCL[stackyAddr] = yValue;

						// pointer is incremented
						pointer++;

						// stack address of row and column is incremented
						stackxAddr++;
						stackyAddr++;
					}
				}

				// the pixel is not in last row
				if(stackxValue < endRow)
				{
					xValue = stackxValue + 1;

					// Checks whether bottom-left corner pixel is foreground
					compMatrixAddr = matrixAddr + (xValue * width) + yValue;
					if(image[compMatrixAddr] == FOREGROUND)
					{
						// label is assigned to the component
						image[compMatrixAddr] = label;

						component_CCL[label - FOREGROUND_START_LABEL]++;

						// pushing stackxValue to stack address of row
						stackx_CCL[stackxAddr] = xValue;

						// pushing yValue to stack address of column
						stacky_CCL[stackyAddr] = yValue;

						// pointer is incremented
						pointer++;

						// stack address of row and column is incremented
						stackxAddr++;
						stackyAddr++;
					}
				}
			}

			// the pixel is not in last column
			if(stackyValue < endCol)
			{
				yValue = stackyValue + 1;

				// Checks whether right pixel is foreground
				compMatrixAddr = matrixAddr + (stackxValue * width) + yValue;
				if(image[compMatrixAddr] == FOREGROUND)
				{
					// label is assigned to the component
					image[compMatrixAddr] = label;

					component_CCL[label - FOREGROUND_START_LABEL]++;

					// pushing stackxValue to stack address of row
					stackx_CCL[stackxAddr] = stackxValue;

					// pushing yValue to stack address of column
					stacky_CCL[stackyAddr] = yValue;

					// pointer is incremented
					pointer++;

					// stack address of row and column is incremented
					stackxAddr++;
					stackyAddr++;
				}

				// the pixel is not in first row
				if(stackxValue > 0)
				{
					xValue = stackxValue - 1;

					// Checks whether top-right corner pixel is foreground
					compMatrixAddr = matrixAddr + (xValue * width) + yValue;
					if(image[compMatrixAddr] == FOREGROUND)
					{
						// label is assigned to the component
						image[compMatrixAddr] = label;

						component_CCL[label - FOREGROUND_START_LABEL]++;

						// pushing xValue to stack address of row
						stackx_CCL[stackxAddr] = xValue;

						// pushing yValue to stack address of column
						stacky_CCL[stackyAddr] = yValue;

						// pointer is incremented
						pointer++;

						// stack address of row and column is incremented
						stackxAddr++;
						stackyAddr++;
					}
				}

				// the pixel is not in last row
				if(stackxValue < endRow)
				{
					xValue = stackxValue + 1;
					// Checks whether bottom-right corner pixel is foreground
					compMatrixAddr = matrixAddr + (xValue * width) + yValue;
					if(image[compMatrixAddr] == FOREGROUND)
					{
						// label is assigned to the component
						image[compMatrixAddr] = label;

						component_CCL[label - FOREGROUND_START_LABEL]++;

						// pushing xValue to stack address of row
						stackx_CCL[stackxAddr] = xValue;

						// pushing yValue to stack address of column
						stacky_CCL[stackyAddr] = yValue;

						// pointer is incremented
						pointer++;

						// stack address of row and column is incremented
						stackxAddr++;
						stackyAddr++;
					}
				}
			}

			// the pixel is not in first row
			if(stackxValue > 0)
			{
				xValue = stackxValue - 1;

				// Checks whether top pixel is foreground
				compMatrixAddr = matrixAddr + (xValue * width) + stackyValue;
				if(image[compMatrixAddr] == FOREGROUND)
				{
					// label is assigned to the component
					image[compMatrixAddr] = label;

					component_CCL[label - FOREGROUND_START_LABEL]++;

					// pushing xValue to stack address of row
					stackx_CCL[stackxAddr] = xValue;

					// pushing stackyValue to stack address of column
					stacky_CCL[stackyAddr] = stackyValue;

					// pointer is incremented
					pointer++;

					// stack address of row and column is incremented
					stackxAddr++;
					stackyAddr++;
				}
			}

			// the pixel is not in last row
			if(stackxValue < endRow)
			{
				xValue = stackxValue + 1;

				// Checks whether outtom pixel is foreground
				compMatrixAddr = matrixAddr + (xValue * width) + stackyValue;
				if(image[compMatrixAddr] == FOREGROUND)
				{
					// label is assigned to the component
					image[compMatrixAddr] = label;


					component_CCL[label - FOREGROUND_START_LABEL]++;

					// pushing xValue to stack address of row
					stackx_CCL[stackxAddr] = xValue;

					// pushing stackyValue to stack address of column
					stacky_CCL[stackyAddr] = stackyValue;

					// pointer is incremented
					pointer++;

					// stack address of row and column is incremented
					stackxAddr++;
					stackyAddr++;
				}
			}

		}

	}


	public static void processEightNbhCCL(int[] image, int width, int height){
		int value = FOREGROUND_START_LABEL;

		stackx_CCL = new int[width * height];
		stacky_CCL = new int[width * height];
		component_CCL = new int[width * height];
		for(int k = 0; k < component_CCL.length; k++)
			component_CCL[k] = 0;

		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				if(image[col + row * width] == FOREGROUND){
					image[col + row * width] = value;

					// pushing row to stack address of row                
					stackx_CCL[0] = row;
					// pushing col to stack address of column                
					stacky_CCL[0] = col;

					// Scans eight neighbours of a pixel and mark them  with label if
					// they are foreground.
					ScanEightNeighbourhood(image, width, height, value);

					// value is incremented    
					//value++;
					value += FOREGROUND_START_LABEL;

				}
			}
		}
	}

	public static int[] getComponent(int[] src,int label){
		int[] target=new int[src.length];
		for(int i=0;i< src.length;i++)
		{
			if(src[i]==label+FOREGROUND_START_LABEL)
			{
				target[i]=255;
			}

		}
		return target;
	}
	public static int getComponentSize(int[] src,int label){
		int size = 0;
		for(int i=0;i< src.length;i++)
		{
			if(src[i]==label+FOREGROUND_START_LABEL)
			{
				size++;
			}

		}
		return size;
	}

	public static int getMaxComponentLabel(){
		int max = 0;
		for(int i=1; i < component_CCL.length;i++)
		{
			if(component_CCL[i]>component_CCL[max])
			{
				max = i;
			}
		}
		System.out.println("MAX COMPONENT = "+max);
		return max;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int height = 0;
		int width = 0;
		//int[][] imgx = null;
		int[] ciffPixel = null;
		BufferedImage image = null;
		int alpha = 255;

		try {
			image = ImageIO.read(new File("3.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		width = image.getWidth(null); height = image.getHeight(null);
		System.out.println("Width ="+width+" Height="+height);
		ciffPixel = new int[width * height];
		//imgx = new int[height][width];
		PixelGrabber pg = new PixelGrabber(
				image, 0, 0, width, height, ciffPixel, 0, width);
		int k = 0;
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ColorModel m_colorModel = pg.getColorModel();	
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int pixVal = ciffPixel[k]; 
				int R = ImageUtility.getRed(pixVal, m_colorModel);
				int G = ImageUtility.getGreen(pixVal, m_colorModel);
				int B = ImageUtility.getBlue(pixVal, m_colorModel);
				int greyVal = (R + G + B) / 3;
				if(greyVal > 128)
					ciffPixel[k] = 255;
				else
					ciffPixel[k] = 0;
				k++;

			}

		}
		// Boundary Padding
		for(int j = 0; j < width; j++){
			ciffPixel[j] = 0;
			ciffPixel[width + j] = 0;
			ciffPixel[2 * width + j] = 0;

			ciffPixel[(height - 3) * width + j] = 0;
			ciffPixel[(height - 2) * width + j] = 0;
			ciffPixel[(height - 1) * width + j] = 0;
		}
		for(int i = 0; i < height; i++){
			ciffPixel[i*width] = 0;
			ciffPixel[i*width+1] = 0;
			ciffPixel[i*width+2] = 0;

			ciffPixel[i*width + (width - 1)] = 0;
			ciffPixel[i*width + (width - 2)] = 0;
			ciffPixel[i*width + (width - 3)] = 0;
		}

		processEightNbhCCL(ciffPixel, width, height);
		//int[] componentImg = getComponent(ciffPixel, getMaxComponentLabel());
		//DetectCorners.displayImageFrame(componentImg, width, height, "15.png", true);


		for(k = 0; k < width * height; k++){
			ciffPixel[k] = (alpha << 24) |  (ciffPixel[k] << 16)
			|  (ciffPixel[k] << 8 ) | ciffPixel[k];
		}

		Container n = new Container();
		Image outImage =  n.createImage (new MemoryImageSource (width, height, ciffPixel, 0, width));
		try {
			BufferedImage  bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics  big = bi.getGraphics();
			big.drawImage(outImage, 0, 0, null);
			ImageIO.write(bi, "png", new File("ccl.png"));
			//DetectCorners.displayImageFrame(outImage, "ccl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

}

