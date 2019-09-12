import java.lang.Math;
public class Main
{
	public static void main(String [] args)
	{
		long tick[] = new long[101];
		CloudData cd = new CloudData();
		int total=0;
		int average;
		long sd = 0;
		cd.readData("largesample_input.txt");
		System.gc();
		for (int j=0; j<5; j++)
		{
			Threaded t = new Threaded(cd, 0, cd.dim()-1);//check if -1 neccesary
			cd.setWind(t.compute());
			//cd.Classification();
		}
		tick[0] = System.currentTimeMillis();
		for (int i=1; i<101; i++)
		{
			Threaded t = new Threaded(cd, 0, cd.dim()-1);//check if -1 neccesary
			cd.setWind(t.compute());
			//cd.Classification();
			tick[i]=System.currentTimeMillis();
		}
		long tock[] = new long[100];
		for (int j=0;j<100;j++)
		{
			tock[j] = tick[j+1]-tick[j];
			total += tock[j];
		}
		average = total/100;
		for (int k=0; k<tock.length;k++)
		{
			sd+= Math.pow(tock[k]-average,2);
		}
		sd = sd/100;
		System.out.println("Time: "+average);
		System.out.println("Std Dev: "+sd);
		cd.writeData("output.txt");
	}
	/*public static void main(String [] args)
	{
		CloudData cd = new CloudData();
		cd.readData("largesample_input.txt");
		Threaded t = new Threaded(cd, 0, cd.dim()-1);//check if -1 neccesary
		cd.setWind(t.compute());
		
		cd.writeData("test.txt");
	}*/
}
