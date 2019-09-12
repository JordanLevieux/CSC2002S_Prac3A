//package cloudscapes;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Math;

public class CloudData{

	protected Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	protected float [][][] convection; // vertical air movement strength, that evolves over time
	protected int [][][] classification; // cloud type per grid point, evolving over time
	protected int dimx, dimy, dimt; // data dimensions
	Vector wind = new Vector(0,0);
	
	void setWind(Vector w)
	{
		wind = w;
	}
	// overall number of elements in the timeline grids
	int dim(){
		return dimt*dimx*dimy;
	}
	
	// convert linear position into 3D location in simulation grid
	int[] locate(int pos)
	{
		int [] ind = new int[3];
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
		return ind;
	}
	
	
	//Linear classification
	void Classification()
	{
		float wm = 0;
		float xm = 0;
		float ym = 0;
		float lm = 0;
		int div = 9;
		float totalx = 0;
		float totaly = 0;
		for (int t=0; t<dimt; t++)
		{
			for (int x=0; x<dimx; x++)
				for (int y=0; y<dimy; y++)
				{
					totalx += advection[t][x][y].x;
					totaly += advection[t][x][y].y;
					if (x==0||x==dimx-1){div=2*div/3;}
					if (y==0||y==dimx-1){div=2*div/3;}
					for (int i=-1; i<2;i++)
					{
						if (x==0&&(i==-1)){i=0;}
						for (int j=-1; j<2;j++)
						{
							if (y==0&&(j==-1)){j=0;}
							xm += advection[t][x+i][y+j].x;
							ym += advection[t][x+i][y+j].y;
							if (y==dimy-1&&j==0){j++;}
						}
						if (x==dimx-1&&i==0){i++;}
					}
					lm = Math.abs(convection[t][x][y]);
					xm= xm/div;
					ym = ym/div;
					wm = (float)(Math.sqrt(xm*xm+ym*ym));
					if (lm>wm){classification[t][x][y]=0;}
					else if(wm>0.2){classification[t][x][y]=1;}
					else{classification[t][x][y]=2;}
					xm = 0;
					ym = 0;
					div = 9;
				}
		}
		wind.x = totalx/dim();
		wind.y = totaly/dim();
	}
	
	// read cloud simulation data from file
	void readData(String fileName){ 
		try{ 
			Scanner sc = new Scanner(new File(fileName), "UTF-8");
			
			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt(); 
			dimy = sc.nextInt();
			//sc.nextLine();
			
			// initialize and load advection (wind direction and strength) and convection
			advection = new Vector[dimt][dimx][dimy];
			convection = new float[dimt][dimx][dimy];
			for(int t = 0; t < dimt; t++)
			{
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						advection[t][x][y] = new Vector();
						advection[t][x][y].x = Float.parseFloat(sc.next());
						advection[t][x][y].y = Float.parseFloat(sc.next());
						convection[t][x][y] = Float.parseFloat(sc.next());
					}
				//sc.nextLine();
			}
			classification = new int[dimt][dimx][dimy];
			sc.close();
		} 
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}

	
	// write classification output to file
	void writeData(String fileName){
		 try{ 
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", wind.x/dim(), wind.y/dim());
			 
			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						printWriter.printf("%d ", classification[t][x][y]);
					}
				 }
				 printWriter.printf("\n");
		     }
				 
			 printWriter.close();
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}
	
}
