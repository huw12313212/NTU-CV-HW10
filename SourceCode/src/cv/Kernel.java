package cv;

public class Kernel {
	
	public final float Data[][];
	public final int OriginX;
	public final int OriginY;
	
	public Kernel(float[][] data,int originX,int originY)
	{
		Data = data;
		OriginX = originX;
		OriginY = originY;
	}
	
	public int GetHeight()
	{
		return Data.length;
	}
	
	public int GetWidth()
	{
		return Data[0].length;
	}
	
	

}
