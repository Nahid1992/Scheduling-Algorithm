package assignment02_scheduling;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.org.apache.regexp.internal.recompile;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;

class CPU_Scheduler implements Runnable {

    private Double_Linked_List<PCB_Structure> ready_q;
    private Double_Linked_List<PCB_Structure> IO_q;
    private String algo;
    private Integer quantum;
    //ArrayList<PCB_Structure> proclist;

    public CPU_Scheduler(Double_Linked_List<PCB_Structure> ready_q, Double_Linked_List<PCB_Structure> IO_q, String algo, Integer quantum) {
        this.ready_q = ready_q;
        this.IO_q = IO_q;
        this.algo = algo;
        this.quantum = quantum;
        //System.out.println("Entered in FIFO Scheduler 01_ Thread <Fetched - Ready Queue>");
    }    
     
    
    @Override
    public void run() {        
        try {
        	switch (algo) {
			case "FIFO":
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis();
				fifo_algorithm_ready_q();
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis() - Assignment02_Scheduling.total_cpu_time;							
				break;
			case "RR":
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis();
				rr_algorithm_ready_q();
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis() - Assignment02_Scheduling.total_cpu_time;	
				break;
			case "PR":
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis();
				pr_algorithm_ready_q();
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis() - Assignment02_Scheduling.total_cpu_time;	
				break;
			case "SJF":
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis();
				sjf_algorithm_ready_q();
				Assignment02_Scheduling.total_cpu_time = System.currentTimeMillis() - Assignment02_Scheduling.total_cpu_time;	
				break;	
			default:
				break;
			}
                
                //System.out.println(Thread.currentThread().getName());
           	
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU_Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    synchronized public int get_highest_priority_index() {
        int position = 0;
        int count = 0;
        ready_q.clear_iterator();
        int temp = 0;
        while (ready_q.iterator != null) {
            if (temp > ready_q.iterator.element.priority) {
                position = count;
                temp = ready_q.iterator.element.priority;
            }
            ready_q.iterator = ready_q.iterator.next;
            count++;
        }
        ready_q.clear_iterator();

        return position;
    }
    
    synchronized public int get_shortest_job_index() {
        int position = 0;
        int count = 0;
        ready_q.clear_iterator();
        int temp = ready_q.iterator.element.get_list_cpu(0);
        while (ready_q.iterator != null) {
            if (temp < ready_q.iterator.element.get_list_cpu(0)) {
                position=count;
                temp = ready_q.iterator.element.get_list_cpu(0);
            }
            ready_q.iterator = ready_q.iterator.next;
            count++;
        }
        ready_q.clear_iterator();

        return position;
    }


    
     void fifo_algorithm_ready_q() throws InterruptedException {
    	Integer burst_period;
    	//burst_period = ready_q.head.element.get_list_cpu(0);
    	//System.out.println("Process ID: " + ready_q.head.element.processid + "Time: "+ burst_period);
    	
    	
        while(true){
         //System.out.println(ready_q.);
        	
        	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
    			break;
    		}
        	else if((ready_q.head!=null)){
        		long cpu_usage_time = System.currentTimeMillis();
        		burst_period = ready_q.head.element.get_list_cpu(0);
        		System.out.println("Process ID(CPU): " + ready_q.head.element.processid + " ; Time: "+ burst_period);
        		if(!ready_q.head.element.time_flag){
        			ready_q.head.element.first_spotted = System.currentTimeMillis();
        			ready_q.head.element.time_flag = true;
        		}
        		
        		ready_q.head.element.total_waiting_time += System.currentTimeMillis() - ready_q.head.element.waiting_time; 
        		try {
        			Thread.sleep((long) burst_period);
				} catch (InterruptedException e) {					
					e.printStackTrace();
					break;
				}
        		
        		ready_q.head.element.remove_cpu_burst(0);
        		if(ready_q.head.element.cpu_io.isEmpty()){
        			ready_q.head.element.last_spotted = System.currentTimeMillis();
        			ready_q.removeAtHead();
        			Assignment02_Scheduling.PCB_proc_finished_count++;
        			
        			//continue;
        		}else{
        			
        			ready_q.head.element.waiting_time = System.currentTimeMillis();
        			    IO_q.add(ready_q.head.element);
        				
        				synchronized (IO_q) {
        					IO_q.notifyAll();
						}
        				
        				//System.out.println(IO_q.head.element.processid);
        			
        			ready_q.removeAtHead();
        			//ready_q.head = ready_q.head.next;
        		}
        		Assignment02_Scheduling.total_cpu_busy_time += System.currentTimeMillis() - cpu_usage_time;	
        	}
        	else if(ready_q.isEmpty()){
        		synchronized (ready_q) {
        			ready_q.wait();	
				}
        	}
        	else{
        		continue;
        	}
        }

    }
    
     
     void rr_algorithm_ready_q() throws InterruptedException {
     	Integer burst_period;
     	Integer allowed_burst_period;
    	boolean flag;
     	
     	
         while(true){
          //System.out.println(ready_q.);
        	allowed_burst_period = quantum;
         	flag = false;
         	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
     			break;
     		}
         	else if((ready_q.head!=null)){
         		long cpu_usage_time = System.currentTimeMillis();
         		burst_period = ready_q.head.element.get_list_cpu(0);
         		System.out.println("Process ID(CPU): " + ready_q.head.element.processid + "Time: "+ burst_period);
         		if(burst_period <= quantum){
        			allowed_burst_period = burst_period;
        			flag = true;
        		}
         		
         		if(!ready_q.head.element.time_flag){
        			ready_q.head.element.first_spotted = System.currentTimeMillis();
        			ready_q.head.element.time_flag = true;
        		}
        		
        		ready_q.head.element.total_waiting_time += System.currentTimeMillis() - ready_q.head.element.waiting_time; 
         		
         		try {
         			Thread.sleep((long) allowed_burst_period);
 				} catch (InterruptedException e) {
 					// TODO: handle exception
 					e.printStackTrace();
 					break;
 				}
         		
         		
         		if(flag){
         			ready_q.head.element.remove_cpu_burst(0);
         			if(ready_q.head.element.cpu_io.isEmpty()){
         				ready_q.head.element.last_spotted = System.currentTimeMillis();
         				ready_q.removeAtHead();
         				Assignment02_Scheduling.PCB_proc_finished_count++;
         			//continue;
         			}else{
         				ready_q.head.element.waiting_time = System.currentTimeMillis();
         				IO_q.add(ready_q.head.element);
         				synchronized (IO_q) {
         					IO_q.notifyAll();
 						}
         				
         				//System.out.println(IO_q.head.element.processid);
         			
         				ready_q.removeAtHead();
         			//ready_q.head = ready_q.head.next;
         			}
         		}else{
         			ready_q.head.element.update_cpu_burst(0, (burst_period-quantum));
         			ready_q.head.element.waiting_time = System.currentTimeMillis();
         			ready_q.add(ready_q.head.element);
         			ready_q.removeAtHead();
         		}
         		Assignment02_Scheduling.total_cpu_busy_time += System.currentTimeMillis() - cpu_usage_time;	
         	}
         	else if(ready_q.isEmpty()){
         		synchronized (ready_q) {
         			ready_q.wait();	
 				}
         	}
         	else{
         		continue;
         	}
         }

     }
     
     void pr_algorithm_ready_q() throws InterruptedException {
     	Integer burst_period;
     	int priority_index;
     	
     	//burst_period = ready_q.head.element.get_list_cpu(0);
     	//System.out.println("Process ID: " + ready_q.head.element.processid + "Time: "+ burst_period);
     	
     	
         while(true){
          //System.out.println(ready_q.);
         	
         	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
     			break;
     		}
         	else if((ready_q.head!=null)){
         		long cpu_usage_time = System.currentTimeMillis();
         		priority_index = get_highest_priority_index();
         		//priority_element = ready_q.head.element;
         		//System.out.println("Index: "+ priority_index);
         		ready_q.clear_iterator();
         		for(int i=0;i<priority_index;i++){
         			ready_q.iterator = ready_q.iterator.next;
         		}
         		
         		//if(ready_q.iterator.element.cpu_io.isEmpty()) continue;
         		burst_period = ready_q.iterator.element.get_list_cpu(0);
         		System.out.println("Process ID(CPU): " + ready_q.iterator.element.processid + "Time: "+ burst_period);
         		if(!ready_q.iterator.element.time_flag){
        			ready_q.iterator.element.first_spotted = System.currentTimeMillis();
        			ready_q.iterator.element.time_flag = true;
        		}
        		
        		ready_q.iterator.element.total_waiting_time += System.currentTimeMillis() - ready_q.iterator.element.waiting_time; 
         		
         		try {
         			Thread.sleep((long) burst_period);
 				} catch (InterruptedException e) {
 					// TODO: handle exception
 					e.printStackTrace();
 					break;
 				}
         		
         		ready_q.iterator.element.remove_cpu_burst(0);
         		
         		if(ready_q.iterator.element.cpu_io.isEmpty()){
         			//ready_q.removeAtHead();
         			ready_q.iterator.element.last_spotted = System.currentTimeMillis();
         			ready_q.remove_index(priority_index);
         			Assignment02_Scheduling.PCB_proc_finished_count++;
         			//continue;
         		}else{
         			ready_q.iterator.element.waiting_time = System.currentTimeMillis();
         				IO_q.add(ready_q.iterator.element);
         			    //IO_q.add(priority_element);
         				synchronized (IO_q) {
         					IO_q.notifyAll();
 						}
         				
         				//System.out.println(IO_q.head.element.processid);
         				ready_q.remove_index(priority_index);
         			//ready_q.removeAtHead();
         			//ready_q.head = ready_q.head.next;
         		}
         		
         		ready_q.clear_iterator();
         		Assignment02_Scheduling.total_cpu_busy_time += System.currentTimeMillis() - cpu_usage_time;	
         	}
         	else if(ready_q.isEmpty()){
         		synchronized (ready_q) {
         			ready_q.wait();	
 				}
         	}
         	else{
         		continue;
         	}
         }

     }
     
     void sjf_algorithm_ready_q() throws InterruptedException {
      	Integer burst_period;
      	int priority_index;
      	
          while(true){
           //System.out.println(ready_q.);
          	
          	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
      			break;
      		}
          	else if((ready_q.head!=null)){
          		long cpu_usage_time = System.currentTimeMillis();
          		priority_index = get_shortest_job_index();
          		//priority_element = ready_q.head.element;
          		//System.out.println("Index: "+ priority_index);
          		ready_q.clear_iterator();
          		for(int i=0;i<priority_index;i++){
          			ready_q.iterator = ready_q.iterator.next;
          		}
          		
          		//if(ready_q.iterator.element.cpu_io.isEmpty()) continue;
          		burst_period = ready_q.iterator.element.get_list_cpu(0);
          		System.out.println("Process ID(CPU): " + ready_q.iterator.element.processid + "Time: "+ burst_period);
          		
          		if(!ready_q.iterator.element.time_flag){
        			ready_q.iterator.element.first_spotted = System.currentTimeMillis();
        			ready_q.iterator.element.time_flag = true;
        		}
        		
        		ready_q.iterator.element.total_waiting_time += System.currentTimeMillis() - ready_q.iterator.element.waiting_time; 
          		
          		try {
          			Thread.sleep((long) burst_period);
  				} catch (InterruptedException e) {
  					// TODO: handle exception
  					e.printStackTrace();
  					break;
  				}
          		
          		ready_q.iterator.element.remove_cpu_burst(0);
          		
          		if(ready_q.iterator.element.cpu_io.isEmpty()){
          			//ready_q.removeAtHead();
          			ready_q.iterator.element.last_spotted = System.currentTimeMillis();
          			ready_q.remove_index(priority_index);
          			Assignment02_Scheduling.PCB_proc_finished_count++;
          			//continue;
          		}else{
          			ready_q.iterator.element.waiting_time = System.currentTimeMillis();
          				IO_q.add(ready_q.iterator.element);
          			    //IO_q.add(priority_element);
          				synchronized (IO_q) {
          					IO_q.notifyAll();
  						}
          				
          				//System.out.println(IO_q.head.element.processid);
          				ready_q.remove_index(priority_index);
          			//ready_q.removeAtHead();
          			//ready_q.head = ready_q.head.next;
          		}
          		
          		ready_q.clear_iterator();
          		Assignment02_Scheduling.total_cpu_busy_time += System.currentTimeMillis() - cpu_usage_time;	
          	}
          	else if(ready_q.isEmpty()){
          		synchronized (ready_q) {
          			ready_q.wait();	
  				}
          	}
          	else{
          		continue;
          	}
          }

      }
     
    
}
