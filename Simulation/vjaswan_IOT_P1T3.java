

/*
 * 
 * Vidhisha Jaswani (Unity ID:vjaswan)
 * Project 1
 * Simulation Task 
 * Part 3: Batch Means
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;


//Node class that has time and type of activity. This node is inserted in priority queue.
class Prio_Queue
{
    int id;
	String activity;
    double time;
    
    public Prio_Queue(String activity,double time)
    {
        this.activity=activity;
        this.time=time;
        
    }
    public String getActivity()
    {
        return activity;
    }
    public double getTime()
    {
        return time;
    }
    public void setID(int id)
    {
    	this.id=id;
    }
    public int getID()
    {
    	return id;
    }
}

/* 
 * Custom comparator to return the node with lower time. If both times are same, apply logic that 
 * always pick CLA first. Then out of CLS and CLR pick CLS.
 */
class ActivityComparator implements Comparator<Prio_Queue>
{
    public int compare(Prio_Queue p1, Prio_Queue p2)
    {
        if(p1.getTime()>p2.getTime())
            return 1;
        else if(p1.getTime()<p2.getTime())
            return -1;
        else if(p1.getTime()==p2.getTime())
        {
        	
        	if(p1.getActivity().equals("CLA") && p2.getActivity().equals("CLS") )
        	{	
        		return -1;
        	}
        	else if(p2.getActivity().equals("CLA") && p1.getActivity().equals("CLS"))
        	{	
        		return 1;
        	
        	}
        	else if(p1.getActivity().equals("CLA") && p2.getActivity().equals("CLR") )
        	{	
        		return -1;
        	}
        	else if(p2.getActivity().equals("CLA") && p1.getActivity().equals("CLR"))
        	{	
        		return 1;
        	
        	}
        	else if(p1.getActivity().equals("CLS") && p2.getActivity().equals("CLR"))
        		return -1;
        	else if(p2.getActivity().equals("CLS") && p1.getActivity().equals("CLR"))
        		return 1;
        	
        	
        	
        }
        return -1;
    }
}

/*
 * Main class, takes mean inter-arrival time, mean orbit time, service time, buffer size and when 
 * to stop simulation into consideration.
 */
public class vjaswan_IOT_P1T3 {
    
    
    static BufferedWriter bw = null;
    static FileWriter fw = null;
    static double masterClock=0,interArrival=0,orbitTime=0,serviceTime=0;
    static int buffer=0,num_req=0;
    static int stop=0;
    static PriorityQueue<Prio_Queue> pq = new
            PriorityQueue<Prio_Queue>(5, new ActivityComparator());
    //set seed to 1
    static int seed=1;
    static Random generator=new Random(seed);
    
    static Queue<Integer> queue=new LinkedList<Integer>();
    static int currID;
    static double[][] metrics;
    
    //function to generate and return random numbers between 0 and 1.
    public static double generateRandom()
    {
        
        double r=generator.nextDouble();
        return r;

    }
    
    //get exponential time using rate
    public static double getExpTime(double rate)
    {
    	double time=0;
    	time=-rate*Math.log(generateRandom());
    	return time;
    }
    
    public static void startSimulation()
    {
       try {
       double update=0;
       double cla=0,cls=0;
       ArrayList<Double> clr=new ArrayList<Double>();
       bw.newLine();

       Prio_Queue temp=pq.poll();
       cla=temp.getTime();
       
       currID=temp.getID();
       bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());
       
       masterClock=cla;

       String currActivity=temp.getActivity();
       cls=masterClock+serviceTime;

       Prio_Queue first_service=new Prio_Queue("CLS",cls);
       pq.add(first_service);
       
       metrics=new double[stop+1][4];
       while(currID<=stop)
       {
           
           if(currActivity.equals("CLA"))
           {
               
        	   cla=masterClock+getExpTime(interArrival);
               Prio_Queue next=new Prio_Queue("CLA",cla);
               next.setID(currID+1);
               pq.add(next); 
        	   // If type of activity is CLA and buffer is full then next arrival calculated and this requests orbits.
               if(num_req>=buffer)
               {
                   
            	   
            	   update=masterClock+getExpTime(orbitTime);

                   Prio_Queue orbit=new Prio_Queue("CLR",update);
                   orbit.setID(currID);

                   pq.add(orbit);  
                   clr.add(update);
                   metrics[currID][0]=masterClock;
                   metrics[currID][1]=masterClock;
                   
                   bw.newLine();
                   bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());
               }
               
        	   //If type of activity is CLA and buffer has space.
               else
               {
                   
            	   if(cls==Integer.MAX_VALUE)
                   {
                	   
                	  
                	   
                	   cls=masterClock+serviceTime;
                       Prio_Queue next2=new Prio_Queue("CLS",cls);
                       pq.add(next2); 
                       

                   }

                   
                   num_req++;
                   queue.add(currID);
                   metrics[currID][0]=masterClock;
                   
                   bw.newLine();
                   bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());
               }
               
               
           }
           
           //If type of activity CLS then next service time is calculated.
           else if(currActivity.equals("CLS"))
           {
               
        	   
        	   
               num_req--;
               int servicedID=queue.remove();
        	   metrics[servicedID][3]=masterClock;

        	   if(num_req==0)
        	   {
        		   cls=Integer.MAX_VALUE;
        	   }
        	   else if(num_req>0)
               {
            	   
            	   
            	   cls=masterClock+serviceTime;
                   Prio_Queue next=new Prio_Queue("CLS",cls);
                   pq.add(next); 
                   bw.newLine();
                   bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());
                   
            	    
               }
        	   
               
            	   
               
        
           }
           else if(currActivity.equals("CLR"))
           {
               //If type of activity CLR and buffer is full, then orbits again.
               if(num_req>=buffer)
               {
                   clr.remove(masterClock);
            	   update=masterClock+getExpTime(orbitTime);
                   Prio_Queue next=new Prio_Queue("CLR",update);
                   next.setID(currID);

                   pq.add(next);  
                   clr.add(update);
                   bw.newLine();
                   bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());
               }
               
        	   //If type of activity CLR and buffer has space then activity is put in buffer.
               else
               {
                   
            	   if(cls==Integer.MAX_VALUE)
                   {
                	   
                	   
                	   
                	   cls=masterClock+serviceTime;
                       Prio_Queue next2=new Prio_Queue("CLS",cls);
                       pq.add(next2); 

                   

                   }
            	   queue.add(currID);
            	   metrics[currID][2]=masterClock;
            	   num_req++;
                   clr.remove(masterClock);
                   bw.newLine();
                   bw.write(masterClock+"\t"+currID+"\t"+cla+"\t"+cls+"\t"+num_req+"\t"+queue.toString()+"\t"+clr.toString());

               }
               
               
               
               
           }
           //pop next node from priority queue.
           Prio_Queue temp2=pq.poll();
           masterClock=temp2.getTime();
           currActivity=temp2.getActivity();
           currID=temp2.getID();

           
       }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
    }
    //method to divide the data in batches and get means of those batches.
    public static double[] batchMeans(String type)
    {
    	double[] means=new double[51];
    	int s=stop+1;
    	
    	double[] vals=new double[s];
    	
    	if(type.equals("T"))
    	{
	    	for(int i=0;i<=stop;i++)
	    	{
	    		vals[i]=metrics[i][3]-metrics[i][0];

	    	}
    	}
    	else if(type.equals("D"))
    	{
	    	for(int i=1;i<=stop;i++)
	    	{
    			vals[i]=metrics[i][2]-metrics[i][1];

	    	}
    	}
    	double sum=0;
    	int index=0;
    	for(int k=1001;k<stop;k++)
    	{
    		if(k%1000==0 )
    		{
    			sum=sum+vals[k];
    			means[index]=sum/1000;
    			
    			
    			index++;
    			sum=0;
    			
    		}
    		else
    		{
    			sum=sum+vals[k];
    		}

    	}
    	
    	
        
    	return means;
    }
    //method to calculate super mean of the means of batches.
    public static double superMean(double[] vals)
    {
    	double superMean=0,sum=0;
    	for(int i=0;i<vals.length;i++)
    	{
    		sum=sum+vals[i];
    	}
    	superMean=sum/(vals.length-1);
    	return superMean;
    }
    //method to calculate standard deviation
    public static double sdOfBatchandSuper(double[] arr,double supermean)
    {
    	double result=0,sum=0;
    	int len=50;
    	for(int i=0;i<(len-1);i++)
    	{
    		double val=Math.abs(arr[i]-supermean);
    		sum=sum+Math.pow(val, 2);
    	}
    	result=sum/len;
    	result=Math.pow(result, 0.5);
    	
    	
    	return result;
    }
    //method to get the 95th percentils of each batch
    public static double[] percentiles(double[] vals)
    {
    	double[] output=new double[50];
    	double[] temp=new double[1000];
    	int index=0,temp_index=0;
    	for(int k=1001;k<stop;k++)
    	{
    		
    		if(k%1000==0 )
    		{
    			
    			Arrays.sort(temp);
    			output[index]=temp[949];
    			temp=new double[1000];
    			temp_index=0;
    			index++;
    		}
    		else
    		{
    			temp[temp_index]=vals[k];
    			temp_index++;
    		}

    	}
    	
    	
    	
    	
    	
    	
    	return output;
    }
    
    public static void main(String[] strs)
    {             
       Scanner sc=new Scanner(System.in); 
       
       
       System.out.println("Enter Mean Arrival Time");
       interArrival=sc.nextDouble();
       
       System.out.println("Enter Mean Orbitting Time");
       orbitTime=sc.nextDouble();
       
       System.out.println("Enter Service Time");
       serviceTime=sc.nextDouble();
       
       System.out.println("Enter Buffer Size");
       buffer=sc.nextInt(); 
       
       stop=51000;
      
       sc.close();
       
       Prio_Queue first=new Prio_Queue("CLA",2);
       first.setID(1);

       pq.add(first);
       

       
       
       
       
       try {
       File file = new File("output_1.xls");
       
       if (!file.exists()) {
           file.createNewFile();
       }
       
       fw = new FileWriter(file.getAbsoluteFile(), false);
       bw = new BufferedWriter(fw);
       bw.write("MC\tID\tCLA\tCLS\tNumber of Requests\tQueue\tCLR");
       
       startSimulation();
       System.out.println("Simulation Completed");


       
       
       
       }
       catch(IOException e)
       {
           e.printStackTrace();
       }
       finally
       {
           try {
               if(bw!=null)
                   bw.close();
               if(fw!=null)
                   fw.close();
           }
           catch(IOException e)
           {
               e.printStackTrace();
           }
       }
       
       try {
    	   fw = new FileWriter("output_2.xls");
           bw = new BufferedWriter(fw);
           
           bw.write("First Arrival\tFirst Retransmission\tEnters the queue after Retransmission\tService Time\tT\tD");
           
           
           
           for(int i=0;i<stop;i++)
           {
        	   bw.newLine();
        	   for(int j=0;j<4;j++)
        	   {
        		  bw.write(Double.toString(metrics[i][j])+"\t");
        	   }
        	   bw.write(Double.toString(metrics[i][3]-metrics[i][0])+"\t"+Double.toString(metrics[i][2]-metrics[i][1]));
           }
           
       }
       catch(Exception e)
       {
           e.printStackTrace();

       }
       finally {
    	   
    	   try {
               if(bw!=null)
                   bw.close();
               if(fw!=null)
                   fw.close();
           }
           catch(IOException e)
           {
               e.printStackTrace();
           }
    	   
       }
       
       double[] T=batchMeans("T");
       double[] D=batchMeans("D");
       
       double superMean_T=superMean(T);
       double superMean_D=superMean(D);
       
       
       
       double sd_T_1=sdOfBatchandSuper(T,superMean_T);
       double CI_upper_T=superMean_T+1.96*(sd_T_1)/(Math.pow(50, 0.5));
       double CI_lower_T=superMean_T-1.96*(sd_T_1)/(Math.pow(50, 0.5));
       
       System.out.println("Details of T");
       System.out.println("SuperMean:"+superMean_T+" SD:"+sd_T_1+" CI:["+CI_lower_T+","+CI_upper_T+"]");
       
       double sd_D_1=sdOfBatchandSuper(D,superMean_D);
       double CI_upper_D=superMean_D+1.96*(sd_D_1)/(Math.pow(50, 0.5));
       double CI_lower_D=superMean_D-1.96*(sd_D_1)/(Math.pow(50, 0.5));
       
       System.out.println("Details of D");
       System.out.println("SuperMean:"+superMean_D+" SD:"+sd_D_1+" CI:["+CI_lower_D+","+CI_upper_D+"]");
       
       
       double[] all_T=new double[stop+1];
       double[] all_D=new double[stop+1];
       for(int i=0;i<=stop;i++)
   	   {	
    	   all_T[i]=metrics[i][3]-metrics[i][0];
    	   all_D[i]=metrics[i][2]-metrics[i][1];


   	   }
       double[] T_95=percentiles(all_T);
       double superMean_T_95=superMean(T_95);
       double sd_T_95=sdOfBatchandSuper(T_95,superMean_T_95);
       double CI_upper_T_95=superMean_T_95+1.96*(sd_T_95)/(Math.pow(50, 0.5));
       double CI_lower_T_95=superMean_T_95-1.96*(sd_T_95)/(Math.pow(50, 0.5));
       
       System.out.println("Details of T_95");
       System.out.println("SuperMean:"+superMean_T_95+" SD:"+sd_T_95+" CI:["+CI_lower_T_95+","+CI_upper_T_95+"]");
       
       double[] D_95=percentiles(all_D);
       double superMean_D_95=superMean(D_95);
       double sd_D_95=sdOfBatchandSuper(D_95,superMean_D_95);
       double CI_upper_D_95=superMean_D_95+1.96*(sd_D_95)/(Math.pow(50, 0.5));
       double CI_lower_D_95=superMean_D_95-1.96*(sd_D_95)/(Math.pow(50, 0.5));
       
       System.out.println("Details of D_95");
       System.out.println("SuperMean:"+superMean_D_95+" SD:"+sd_D_95+" CI:["+CI_lower_D_95+","+CI_upper_D_95+"]");
       
       
       


       

       
       


       
       
       

       
        
    }

}
 

