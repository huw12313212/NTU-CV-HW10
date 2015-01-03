package cv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class cv {

    static Random random = new Random();
	public static void main(String[] args) throws IOException {

		 String fileName = "./assets/lena.im";
		 int headerLength = 172;
		 int imageWidth = 512;
		 int imageHeight = 512;
		 
		 
		 Kernel Laplacian1 = new Kernel(new float[][]{
				  { 0, 1, 0},
				  { 1,-4, 1},
				  { 0, 1, 0}
		          },1,1); 
		 
		 Kernel Laplacian2 = new Kernel(new float[][]{
				  { 1.0f/3, 1.0f/3, 1.0f/3},
				  { 1.0f/3,-8.0f/3, 1.0f/3},
				  { 1.0f/3, 1.0f/3, 1.0f/3}
		          },1,1); 
	
		 Kernel MinimumVarianceLaplacian = new Kernel(new float[][]{
				  { 2.0f/3, -1.0f/3, 2.0f/3},
				  { -1.0f/3,-4.0f/3, -1.0f/3},
				  { 2.0f/3, -1.0f/3, 2.0f/3}
		          },1,1); 
		 
		 Kernel LoGKernel = new Kernel(new float[][]{
				 {   0,   0,   0,  -1,  -1,  -2,  -1,  -1,   0,   0,   0},
				 {   0,   0,  -2,  -4,  -8,  -9,  -8,  -4,  -2,   0,   0},
				 {   0,  -2,  -7, -15, -22, -23, -22, -15,  -7,  -2,   0},
				 {  -1,  -4, -15, -24, -14,  -1, -14, -24, -15,  -4,  -1},
				 {  -1,  -8, -22, -14,  52, 103,  52, -14, -22,  -8,  -1},
				 {  -2,  -9, -23,  -1, 103, 178, 103,  -1, -23,  -9,  -2},
				 {  -1,  -8, -22, -14,  52, 103,  52, -14, -22,  -8,  -1},
				 {  -1,  -4, -15, -24, -14,  -1, -14, -24, -15,  -4,  -1},
				 {   0,  -2,  -7, -15, -22, -23, -22, -15,  -7,  -2,   0},
				 {   0,   0,  -2,  -4,  -8,  -9,  -8,  -4,  -2,   0,   0},
				 {   0,   0,   0,  -1,  -1,  -2,  -1,  -1,   0,   0,   0},
		 },5,5);
		 
		 Kernel DoGKernel = new Kernel(new float[][]{
				 {  -1,  -3,  -4,  -6,  -7,  -8,  -7,  -6,  -4,  -3,  -1},
				 {  -3,  -5,  -8, -11, -13, -13, -13, -11,  -8,  -5,  -3},
				 {  -4,  -8, -12, -16, -17, -17, -17, -16, -12,  -8,  -4},
				 {  -6, -11, -16, -16,   0,  15,   0, -16, -16, -11,  -6},
				 {  -7, -13, -17,   0,  85, 160,  85,   0, -17, -13,  -7},
				 {  -8, -13, -17,  15, 160, 283, 160,  15, -17, -13,  -8},
				 {  -7, -13, -17,   0,  85, 160,  85,   0, -17, -13,  -7},
				 {  -6, -11, -16, -16,   0,  15,   0, -16, -16, -11,  -6},
				 {  -4,  -8, -12, -16, -17, -17, -17, -16, -12,  -8,  -4},
				 {  -3,  -5,  -8, -11, -13, -13, -13, -11,  -8,  -5,  -3},
				 {  -1,  -3,  -4,  -6,  -7,  -8,  -7,  -6,  -4,  -3,  -1},
		 },5,5);
		 
		 ArrayList<Integer> originImg = GetByteData(fileName);
		 ArrayList<Integer> LaplacianImg1 = CrossingEdgeDetector(originImg,headerLength,imageWidth,imageHeight,Laplacian1,15);
		 ArrayList<Integer> LaplacianImg2 = CrossingEdgeDetector(originImg,headerLength,imageWidth,imageHeight,Laplacian2,15);
		 ArrayList<Integer> MinimumVarianceLaplacianImg = CrossingEdgeDetector(originImg,headerLength,imageWidth,imageHeight,MinimumVarianceLaplacian,20);
		 ArrayList<Integer> LoGImg = CrossingEdgeDetector(originImg,headerLength,imageWidth,imageHeight,LoGKernel,3000);
		 ArrayList<Integer> DoGKernelImg = CrossingEdgeDetector(originImg,headerLength,imageWidth,imageHeight,DoGKernel,1);
		 
		 WriteOut(LaplacianImg1,"./assets/LaplacianImg1_15.im");
		 WriteOut(LaplacianImg2,"./assets/LaplacianImg2_15.im");
		 WriteOut(MinimumVarianceLaplacianImg,"./assets/MinimumVarianceLaplacianImg_20.im");
		 WriteOut(LoGImg,"./assets/LoGImg_3000.im");
		 WriteOut(DoGKernelImg,"./assets/DoGKernelImg_1.im");

	}
	
	public static ArrayList<Integer> CrossingEdgeDetector(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel kernel,int threshold)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		ArrayList<Integer> temp = InitWhite(origin,headerLength,width,height);
		
		for(int y = 0 ; y < height; y++)
		{
			for(int x = 0 ; x < width ; x++)
			{
				float tempValue = CalculateKernel(origin,headerLength,width,height,kernel,x,y);
				temp.set(headerLength+y*width+x,(int)tempValue);
			}
		}
		
		for(int y = 0 ; y < height; y++)
		{
			for(int x = 0 ; x < width ; x++)
			{	
				for(int y2 = -1; y2 < 2 ; y2++)
				{
					//System.out.println(x+":"+y);
					
					for(int x2 = -1; x2 < 2 ; x2++)
					{
						if(isDifferenceGreaterThan(temp,headerLength,width,height,x,y,x2,y2,threshold))
						{
							results.set(headerLength+y*width+x,0);
						}
					}
				}
			}
		}
	
		return results;
	}
	
	public static boolean isDifferenceGreaterThan(ArrayList<Integer> origin,int headerLength, int width, int height,int x,int y,int x2,int y2,int threshold)
	{
		int newIndexX = x + x2;
		int newIndexY = y + y2;
		
		if(newIndexX < 0) return false;
		if(newIndexY < 0) return false;
		if(newIndexX >= width) return false;
		if(newIndexY >= height) return false;
		
		int originValue = origin.get(headerLength+width*y+x);
		int nearValue = origin.get(headerLength+width*newIndexY+newIndexX);
		
		if(originValue > threshold && nearValue < -threshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static ArrayList<Integer> DetectGradientEdgeWithMax(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel[] kernels,int threshold)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		
			for(int y = 0 ; y < height; y++)
			{
				for(int x = 0 ; x < width ; x++)
				{
					float resultValue = CalculateKernel(origin,headerLength,width,height , kernels[0],x,y);
					for(int kernelCounter = 1 ; kernelCounter<kernels.length;kernelCounter++)
					{
						float newKernelResult = CalculateKernel(origin,headerLength,width,height , kernels[kernelCounter],x,y);
						if(newKernelResult>resultValue)resultValue = newKernelResult;
					}
					
					if(resultValue > threshold)
					{
						results.set(headerLength+y*width+x,0);
					}
					else
					{
						results.set(headerLength+y*width+x,255);
					}
				}
			}
		
		return results;
	}
	
	public static ArrayList<Integer> DetectGradientEdge(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel r1,Kernel r2,int threshold)
	{
		ArrayList<Integer> results = InitWhite(origin,headerLength,width,height);
		
		for(int y = 0 ; y < height; y++)
		{
			for(int x = 0 ; x < width ; x++)
			{
				float R1Result = CalculateKernel(origin,headerLength,width,height , r1,x,y);
				float R2Result = CalculateKernel(origin,headerLength,width,height , r2,x,y);
				
				if(Math.sqrt(R1Result*R1Result + R2Result*R2Result) > threshold)
				{
					results.set(headerLength+y*width+x,0);
				}
				else
				{
					results.set(headerLength+y*width+x,255);
				}
			}
		}
		
		return results;
	}
	
	public static float CalculateKernel(ArrayList<Integer> origin,int headerLength, int width, int height,Kernel kernel,int x,int y)
	{
		float sum = 0;
		
		for(int kernelY = 0 ; kernelY< kernel.GetHeight() ; kernelY++ )
		{
			for(int kernelX = 0 ; kernelX < kernel.GetWidth() ; kernelX++)
			{
				int globalX = x + kernelX - kernel.OriginX;
				int globalY = y + kernelY - kernel.OriginY;
				
				if(globalX<0)continue;
				if(globalY<0)continue;
				if(globalX>=width)continue;
				if(globalY>=height)continue;
				
				float result = kernel.Data[kernelY][kernelX] * origin.get(headerLength+globalY*width + globalX);
				
				sum += result;
			}
		}
		
		return sum;
	}

	public static ArrayList<Integer> InitWhite(ArrayList<Integer> origin,int headerLength, int width, int height)
	{
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int i = 0 ; i < headerLength ; i++)
		{
			results.add(origin.get(i));
		}
		
		for(int i = 0 ; i < width ; i ++)
		{
			for(int j = 0 ; j<height ; j++)
			{
				results.add(255);
			}
		}
		
		return results;
	}

	
	public static ArrayList<Integer> GetByteData(String fileName) throws IOException
	{
		 File f = new File(fileName);
		 ArrayList<Integer> bytes = new ArrayList<Integer>();
		
		 //System.out.println("file exist:"+f.exists());
		
		 FileInputStream in = null;	
		 in = new FileInputStream(fileName);
		 
		 int c;
		 while ((c = in.read()) != -1) {
			 bytes.add(c);
        }
		 
		 return bytes;
	}
	
	public static void WriteOut(ArrayList<Integer> data,String name) throws IOException
	{
		File f = new File(name);
		if(f.exists())f.delete();
		FileOutputStream out = null;
		out = new FileOutputStream(name);
		
		for(int i : data)
		{
			out.write((byte)i);
		}
		
		out.flush();
		out.close();
		
	}
	

}
