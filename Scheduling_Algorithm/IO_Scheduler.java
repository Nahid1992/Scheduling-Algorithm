package assignment02_scheduling;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;

class IO_Scheduler implements Runnable {


	private Double_Linked_List<PCB_Structure> ready_q;
    private Double_Linked_List<PCB_Structure> IO_q;
    private String algo;
    private Integer quantum;

    public IO_Scheduler(Double_Linked_List<PCB_Structure> ready_q, Double_Linked_List<PCB_Structure> IO_q, String algo, Integer quantum) {
        this.IO_q = IO_q;
        this.ready_q = ready_q;
        this.algo = algo;
        this.quantum = quantum;
    }

    @Override
    public void run() {
        //System.out.println("ERROR!!");
    	//System.out.println(Thread.currentThread().getName());
        try {
            
            switch (algo) {
			case "FIFO":
				fifo_algorithm_IO_q();
				break;
			case "RR":
				rr_algorithm_IO_q();
				break;
			case "PR":
				pr_algorithm_IO_q();
				break;
			case "SJF":
				sjf_algorithm_IO_q();
				break;	
			default:
				break;
			}
 
        } catch (InterruptedException ex) {
            Logger.getLogger(IO_Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    synchronized public int get_highest_priority_io_burst() {
        IO_q.clear_iterator();
        int temp = IO_q.iterator.element.priority;
        int burst_period = 0;
        while (IO_q.iterator != null) {
            if (temp < IO_q.iterator.element.priority) {
                temp = IO_q.iterator.element.priority;
                burst_period = IO_q.iterator.element.get_list_cpu(0);
            }
            IO_q.iterator = IO_q.iterator.next;
        }
        IO_q.clear_iterator();
        return burst_period;
    }
    
    synchronized public int get_shortest_job_io_burst() {
    	IO_q.clear_iterator();
       
        int burst_period =IO_q.iterator.element.get_list_cpu(0);
        while (IO_q.iterator != null) {
            if (burst_period < IO_q.iterator.element.get_list_cpu(0)) {
                burst_period = IO_q.iterator.element.get_list_cpu(0);
            }
            IO_q.iterator = IO_q.iterator.next;
        }
        IO_q.clear_iterator();
        return burst_period;
    }
    
    synchronized public int get_highest_priority_index() {
        int position = 0;
        int count = 0;
        IO_q.clear_iterator();
        int temp = 0;
        while (IO_q.iterator != null) {
            if (temp > IO_q.iterator.element.priority) {
                position = count;
                temp = IO_q.iterator.element.priority;
            }
            IO_q.iterator = IO_q.iterator.next;
            count++;
        }
        IO_q.clear_iterator();

        return position;
    }
    
    synchronized public int get_shortest_job_index() {
        int position = 0;
        int count = 0;
        IO_q.clear_iterator();
        int temp = IO_q.iterator.element.get_list_cpu(0);
        while (IO_q.iterator != null) {
            if (temp < IO_q.iterator.element.get_list_cpu(0)) {
                position = count;
                temp = IO_q.iterator.element.get_list_cpu(0);
            }
            IO_q.iterator = IO_q.iterator.next;
            count++;
        }
        IO_q.clear_iterator();

        return position;
    }
    
    void fifo_algorithm_IO_q() throws InterruptedException {
    	Integer burst_period;
    	
    	
        while(true){
        	//System.out.println(Thread.currentThread().getState());
        	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
    			break;
    		}
        	else if(IO_q.head!=null){
        		burst_period = IO_q.head.element.get_list_cpu(0);
        		System.out.println("Process ID(IO): " + IO_q.head.element.processid + "Time: "+ burst_period);
        		if(!IO_q.head.element.time_flag){
        			IO_q.head.element.first_spotted = System.currentTimeMillis();
        			IO_q.head.element.time_flag = true;
        		}
        		
        		IO_q.head.element.total_waiting_time+= System.currentTimeMillis() - IO_q.head.element.waiting_time; 
        		
        		try {
        			Thread.sleep((long) burst_period);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				}
        		
        		IO_q.head.element.remove_cpu_burst(0);
        		if(IO_q.head.element.cpu_io.isEmpty()){
        			IO_q.head.element.last_spotted = System.currentTimeMillis();
        			IO_q.removeAtHead();
        			Assignment02_Scheduling.PCB_proc_finished_count++;
        			
        			//continue;
        		}
        		else{
        			IO_q.head.element.waiting_time = System.currentTimeMillis();
        				ready_q.add(IO_q.head.element);
        				synchronized (ready_q) {
        					ready_q.notifyAll();
						}
        				
        				IO_q.removeAtHead();
        			//IO_q.head = IO_q.head.next;
        		}
        	}
        	else if(IO_q.isEmpty()){
        		synchronized (IO_q) {
        			IO_q.wait();	
				}
        	}
        	
        	else{
        		continue;
        	}
        }
    }
    
    void rr_algorithm_IO_q() throws InterruptedException {
    	Integer burst_period;
    	Integer allowed_burst_period;
    	boolean flag;
    	
    	
        while(true){
        	allowed_burst_period = quantum;
        	flag = false;
        	//System.out.println(Thread.currentThread().getState());
        	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
    			break;
    		}
        	else if(IO_q.head!=null){
        		burst_period = IO_q.head.element.get_list_cpu(0);
        		System.out.println("Process ID(IO): " + IO_q.head.element.processid + "Time: "+ burst_period);
        		//synchronized(this){
        		      //Thread.sleep((long) burst_period);
        		//}
        		if(burst_period <= quantum){
        			allowed_burst_period = burst_period;
        			flag = true;
        		}
        		if(!IO_q.head.element.time_flag){
        			IO_q.head.element.first_spotted = System.currentTimeMillis();
        			IO_q.head.element.time_flag = true;
        		}
        		
        		IO_q.head.element.total_waiting_time+= System.currentTimeMillis() - IO_q.head.element.waiting_time; 
        		
        		try {
        			Thread.sleep((long) allowed_burst_period);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				}
        		
        		
        		if(flag){
        			IO_q.head.element.remove_cpu_burst(0);
        			if(IO_q.head.element.cpu_io.isEmpty()){
        				IO_q.head.element.last_spotted = System.currentTimeMillis();
        				IO_q.removeAtHead();
        				Assignment02_Scheduling.PCB_proc_finished_count++;
        			//continue;
        			}
        			else{
        				IO_q.head.element.waiting_time = System.currentTimeMillis();
        				ready_q.add(IO_q.head.element);
        				synchronized (ready_q) {
        					ready_q.notifyAll();
						}
        				
        				IO_q.removeAtHead();
        			//IO_q.head = IO_q.head.next;
        			}
        		}	
        		else{
        			IO_q.head.element.update_cpu_burst(0, (burst_period-quantum));
        			IO_q.head.element.waiting_time = System.currentTimeMillis();
         			IO_q.add(IO_q.head.element);
         			IO_q.removeAtHead();
        		}
        	}
        	else if(IO_q.isEmpty()){
        		synchronized (IO_q) {
        			IO_q.wait();	
				}
        	}
        	
        	else{
        		continue;
        	}
        }
    }
    
    void pr_algorithm_IO_q() throws InterruptedException {
    	Integer burst_period;
    	int priority_index;
     	
    	
        while(true){
        	//System.out.println(Thread.currentThread().getState());
        	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
    			break;
    		}
        	else if(IO_q.head!=null){
        		
         		priority_index = get_highest_priority_index();
         		IO_q.clear_iterator();
         		for(int i=0;i<priority_index;i++){
         			IO_q.iterator = IO_q.iterator.next;
         		}
         		//priority_element = IO_q.get_element(priority_index);
         		burst_period = IO_q.iterator.element.get_list_cpu(0);
        		System.out.println("Process ID(IO): " + IO_q.iterator.element.processid + "Time: "+ burst_period);
        		if(!IO_q.iterator.element.time_flag){
        			IO_q.iterator.element.first_spotted = System.currentTimeMillis();
        			IO_q.iterator.element.time_flag = true;
        		}
        		
        		IO_q.iterator.element.total_waiting_time+= System.currentTimeMillis() - IO_q.iterator.element.waiting_time; 
        		
        		try {
        			Thread.sleep((long) burst_period);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				}
        		
        		IO_q.iterator.element.remove_cpu_burst(0);
        		//priority_element.remove_cpu_burst(0);
        		if(IO_q.iterator.element.cpu_io.isEmpty()){
        			IO_q.iterator.element.last_spotted = System.currentTimeMillis();
        			IO_q.remove_index(priority_index);
        			Assignment02_Scheduling.PCB_proc_finished_count++;
        			//continue;
        		}
        		else{
        			IO_q.iterator.element.waiting_time = System.currentTimeMillis();
        				ready_q.add(IO_q.iterator.element);
        				//ready_q.add(priority_element);
        				synchronized (ready_q) {
        					ready_q.notifyAll();
						}
        				
        				//IO_q.removeAtHead();
        				IO_q.remove_index(priority_index);
        			//IO_q.head = IO_q.head.next;
        		}
        		
        		IO_q.clear_iterator();
        	}
        	else if(IO_q.isEmpty()){
        		synchronized (IO_q) {
        			IO_q.wait();	
				}
        	}
        	
        	else{
        		continue;
        	}
        }
    }
    
    void sjf_algorithm_IO_q() throws InterruptedException {
    	Integer burst_period;
    	int priority_index;
     	
    	
        while(true){
        	//System.out.println(Thread.currentThread().getState());
        	if((Assignment02_Scheduling.PCB_proc_count==Assignment02_Scheduling.PCB_proc_finished_count) && Assignment02_Scheduling.stop_flag){
    			break;
    		}
        	else if(IO_q.head!=null){
        		
         		priority_index = get_shortest_job_index();
         		IO_q.clear_iterator();
         		for(int i=0;i<priority_index;i++){
         			IO_q.iterator = IO_q.iterator.next;
         		}
         		//priority_element = IO_q.get_element(priority_index);
         		burst_period = IO_q.iterator.element.get_list_cpu(0);
        		System.out.println("Process ID(IO): " + IO_q.iterator.element.processid + "Time: "+ burst_period);
        		if(!IO_q.iterator.element.time_flag){
        			IO_q.iterator.element.first_spotted = System.currentTimeMillis();
        			IO_q.iterator.element.time_flag = true;
        		}
        		
        		IO_q.iterator.element.total_waiting_time+= System.currentTimeMillis() - IO_q.iterator.element.waiting_time; 
        		
        		try {
        			Thread.sleep((long) burst_period);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				}
        		
        		IO_q.iterator.element.remove_cpu_burst(0);
        		//priority_element.remove_cpu_burst(0);
        		if(IO_q.iterator.element.cpu_io.isEmpty()){
        			IO_q.iterator.element.last_spotted = System.currentTimeMillis();
        			IO_q.remove_index(priority_index);
        			Assignment02_Scheduling.PCB_proc_finished_count++;
        			//continue;
        		}
        		else{
        				IO_q.iterator.element.waiting_time = System.currentTimeMillis();
        				ready_q.add(IO_q.iterator.element);
        				//ready_q.add(priority_element);
        				synchronized (ready_q) {
        					ready_q.notifyAll();
						}
        				
        				//IO_q.removeAtHead();
        				IO_q.remove_index(priority_index);
        			//IO_q.head = IO_q.head.next;
        		}
        		
        		IO_q.clear_iterator();
        	}
        	else if(IO_q.isEmpty()){
        		synchronized (IO_q) {
        			IO_q.wait();	
				}
        	}
        	
        	else{
        		continue;
        	}
        }
    }
    
    

}
