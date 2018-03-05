package assignment02_scheduling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Read_Input_File implements Runnable {

    String input_txt;
    String algo;
    private Double_Linked_List<PCB_Structure> ready_q;
    public static ArrayList<PCB_Structure> procList = null;

    

    Read_Input_File(Double_Linked_List<PCB_Structure> ready_q, String algo, String inputtxt) {
        this.ready_q = ready_q;
    	this.algo = algo;
        this.input_txt = inputtxt;
    }

    @Override
    public void run() {
        try {
            work(algo, input_txt);
        } catch (IOException ex) {
            Logger.getLogger(Read_Input_File.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Read_Input_File.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void work(String algo, String input_txt) throws FileNotFoundException, IOException, InterruptedException {

         //ready_q = new Double_Linked_List<PCB_Structure>();
         procList = new ArrayList<PCB_Structure>();

        //Thread cpu_sch; //Thread to handle CPU Scheduler
        //Thread io_sch; // Thread to handle I\O scheduler
        int i = 0;
        int id = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(input_txt))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                int flag = 0;
                String[] s = line.split("\\s"); // \\s = space delimeter.            
                if (s[0].equalsIgnoreCase("proc")) {
                    PCB_Structure proc = new PCB_Structure();
                    proc.processid = id;
                    proc.priority = Integer.parseInt(s[1]);
                    proc.save_list(s);
                    proc.waiting_time = proc.arrival_time = System.currentTimeMillis();
                    proc.first_spotted = 0;
                    proc.last_spotted = 0;
                    //proc.waiting_time = 0;
                    proc.total_waiting_time = 0;
                    proc.time_flag = false;
                    ready_q.add(proc);
                    procList.add(proc);
                    synchronized (ready_q) {
                    	ready_q.notifyAll();
					}
                    Assignment02_Scheduling.PCB_proc_count++;
                    flag = 1;
                    //System.out.println("CHECK!");
                }
                if (s[0].equalsIgnoreCase("sleep")) {
                    //System.out.println("Waiting for " + Integer.parseInt(s[1]) + "ms");
                    Thread.sleep(Integer.parseInt(s[1]));
                    //continue;
                    //break;
                }
                if (s[0].equalsIgnoreCase("stop")) {
                	//System.out.println("After sleep");
                	Assignment02_Scheduling.stop_flag = true;
                   break;
                }
                line = br.readLine();
                if (flag == 1) {
                    id++;
                }
                i++;
            }

            /*ready_q.clear_iterator();
            while (ready_q.iterator != null) {
                ready_q.iterator.element.print_entity();
                ready_q.iterator = ready_q.iterator.next;
            }
            ready_q.clear_iterator();*/
        }

    }

}
