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
		 
		 
		/* Kernel RobertsKernelR1 = new Kernel(new float[][]{
				  { -1,0},
				  {0,1},
				},0,0); */
	
		 
		 ArrayList<Integer> originImg = GetByteData(fileName);

		 /*WriteOut(Roberts,"./assets/Roberts.im");*/

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
				results.add(0);
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
