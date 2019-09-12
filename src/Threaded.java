import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
public class Threaded extends RecursiveTask<Vector>
{
	static int SEQUENTIAL_CUTOFF = 1000;
	int lo, hi;
	CloudData cd;
	int[] pos;
	
	public Threaded(CloudData c, int l, int h)
	{
		cd = c;
		lo = l;
		hi = h;
	}
	
	protected Vector compute()
	{
		if(hi-lo < SEQUENTIAL_CUTOFF) 
		{
			pos = cd.locate(lo);
			int counter =0;
			int stop = hi-lo;
			Vector ans = new Vector(0,0);
			float wm = 0;
			float xm = 0;
			float ym = 0;
			float lm = 0;
			int div = 9;
			for (int t=pos[0];t<cd.dimt;t++)
			{
				for (int x=pos[1]; x<cd.dimx; x++)
				{
					for (int y=pos[2];y<cd.dimy;y++)
					{
						//ans.x += cd.advection[t][x][y].x;
						//ans.y += cd.advection[t][x][y].y;
						if (x==0||x==cd.dimx-1){div=2*div/3;}
						if (y==0||y==cd.dimx-1){div=2*div/3;}
						for (int i=-1; i<2;i++)
						{
							if (x==0&&(i==-1)){i=0;}
							for (int j=-1; j<2;j++)
							{
								if (y==0&&(j==-1)){j=0;}
								xm += cd.advection[t][x+i][y+j].x;
								ym += cd.advection[t][x+i][y+j].y;
								if (y==cd.dimy-1&&j==0){j++;}
							}
							if (x==cd.dimx-1&&i==0){i++;}
						}
						lm = Math.abs(cd.convection[t][x][y]);
						xm= xm/div;
						ym = ym/div;
						wm = (float)(Math.sqrt(xm*xm+ym*ym));
						if (lm>wm){cd.classification[t][x][y]=0;}
						else if(wm>0.2){cd.classification[t][x][y]=1;}
						else{cd.classification[t][x][y]=2;}
						xm = 0;
						ym = 0;
						div = 9;
						counter++;
						ans.x+=cd.advection[t][x][y].x;
						ans.y+=cd.advection[t][x][y].y;
						if (stop<=counter){return ans;}
					}
					pos[2]=0;
				}
				pos[1]=0;
			}
			System.out.println("Error: Overflow");
			return ans;
		} 
		else
		{
			Threaded left = new Threaded(cd,lo,(hi+lo)/2);
			Threaded right = new Threaded(cd,(hi+lo)/2,hi);
			left.fork();
			Vector rightAns= right.compute();
			Vector leftAns= left.join();
			rightAns.x+=leftAns.x;
			rightAns.y+=leftAns.y;
			return rightAns;
		} 
	}
	
	static final ForkJoinPool fjPool = new ForkJoinPool();
}
