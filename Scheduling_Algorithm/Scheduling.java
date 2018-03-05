package assignment02_scheduling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Assignment02_Scheduling {

	public static int PCB_proc_count;
	public static int PCB_proc_finished_count;
	public static boolean stop_flag = false;
	public static long total_cpu_time;
	public static long total_cpu_busy_time;
	public static long turnaround_time=0;
	public static long waiting_time=0;
	public static long response_time=0;
	
	
    public static void main(String[] args) throws IOException, InterruptedException {
    	int i = 0;
        String arg;
        //String algo = "RR";
        String algo = null;
        int quantum = 0;
        //int quantum = 8;
        //String inputfile = "input.txt";
        String inputfile = null;
        
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            if (arg.equals("-alg")) {
                if (i < args.length){
                	algo = args[i++];
                	//System.out.println("CHECK - CPU Scheduling Alg: " + algo);
                }	
                else
                    System.err.println("-alg requires a Algorithm Name. [FIFO|SJF|PR|RR]");
                    
            }
            else if(arg.equals("-quantum")){
            	if (i < args.length){
                	quantum = Integer.parseInt(args[i++]);
                	//System.out.println("CHECK CPU Scheduling Alg RR quantum: " + quantum);
                }	
                else
                    System.err.println("-quantum requires a quantum number. [integer(ms)]");
            }
            else if(arg.equals("-input")){
            	if (i < args.length){
                	inputfile = args[i++];
                	//System.out.println("CHECK Input File Name: " + inputfile);
                }	
                else
                    System.err.println("-input requires a File Name. [file name]");
            }
            else{
            	System.err.println("Usage: ParseCmdLine [-alg] Algorithm Name [-quantum] Intger [-input] filename");
            }
        }    
        
        Double_Linked_List<PCB_Structure> ready_q = new Double_Linked_List<PCB_Structure>();
        Double_Linked_List<PCB_Structure> IO_q = new Double_Linked_List<PCB_Structure>();
        
        
        Thread read_input = new Thread(new Read_Input_File(ready_q, algo, inputfile));
        Thread cpu_sch1 = new Thread(new CPU_Scheduler(ready_q, IO_q, algo, quantum));
        Thread cpu_sch2 = new Thread(new IO_Scheduler(ready_q, IO_q, "FIFO", quantum));

        long start = System.currentTimeMillis();
       
        read_input.start();
        
        
        cpu_sch1.start();
        cpu_sch2.start();
        read_input.join();
        cpu_sch1.join();
        cpu_sch2.join();
        
        
         for(PCB_Structure proc: Read_Input_File.procList){
        	//System.out.println("ProcessID: "+proc.processid );
        	 long arrival_time = proc.arrival_time - start;
        	 turnaround_time += (proc.last_spotted -start) - arrival_time;
        	 waiting_time += proc.total_waiting_time;
        	 response_time += (proc.first_spotted -start) - arrival_time;
        	 //proc.print_entity();
        }
//        Double_Linked_List<PCB_Structure> temp = ready_q;
//        temp.clear_iterator();
//        while (temp.iterator != null) {
//        	temp.iterator.element.print_entity();
//        	temp.iterator = temp.iterator.next;
//        }
//        temp.clear_iterator();
        
         System.out.println();
         System.out.println("Input File Name: " + inputfile);
         if(algo.equalsIgnoreCase("RR")){
        	 System.out.println("CPU Scheduling Alg: " + algo + " (" + quantum + ")");
         }
         else{
        	 System.out.println("CPU Scheduling Alg: " + algo);
         }
         System.out.println("CPU utilization " + (((double)total_cpu_busy_time /(double) total_cpu_time) * 100));
         System.out.println("Throughput " +  ((double)PCB_proc_count / (double)total_cpu_time));
         System.out.println("Turnaround time " + ((double)turnaround_time / (double)PCB_proc_count));
         System.out.println("Waiting time " + ((double)waiting_time / (double)PCB_proc_count));
         System.out.println("Response time " + ((double)response_time / (double)PCB_proc_count));
         //System.out.println("total CPU time is " + total_cpu_time);
         //System.out.println("total CPU busy time is " + total_cpu_busy_time);
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Total Execution time is " + formatter.format((end - start) / 1000d) + " seconds");

    }
}
