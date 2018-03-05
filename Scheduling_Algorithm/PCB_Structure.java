package assignment02_scheduling;

import java.util.ArrayList;

public class PCB_Structure {

    int processid;
    int priority;
    int quantum;
    static int status; // 0 = readyTOrun; 1 = Running; 2 = waiting(ReadyIO - io done); 3 = IOProcess(IO working);
    int total_burst;
    static int current_index;
    ArrayList<Integer> cpu_io = new ArrayList<Integer>();
    long arrival_time;
    long first_spotted;
    long last_spotted;
    long waiting_time;
    long total_waiting_time;
    boolean time_flag;

    void PCB_Structure() {
        this.status = 0; //ready
        this.current_index = 0;

        //System.out.println("Entity Entered!");
    }

    void PCB_Structure(int quantum) {
        this.status = 0; //ready
        this.quantum = quantum;
        this.current_index = 0;
    }

    void set_processid(int x) {
        this.processid = x;
    }

    int get_processid() {
        return this.processid;
    }

    int get_total_burst() {
        return this.total_burst;
    }

    void set_priority(int x) {
        this.priority = x;
    }

    int get_priority() {
        return this.priority;
    }

    synchronized void set_list_cpu(int x) {
        this.cpu_io.add(x);
    }
    
    synchronized void update_cpu_burst(int index, int burst) {
        this.cpu_io.set(index, burst);
    }
    
    synchronized int get_list_cpu(int index_cpu) {
        return this.cpu_io.get(index_cpu);
    }
    
    synchronized int remove_cpu_burst(int index_cpu) {
        return this.cpu_io.remove(index_cpu);
    }

    synchronized void save_list(String[] s) {
        for (int i = 2; i < s.length; i++) {
            set_list_cpu(Integer.parseInt(s[i]));
            total_burst++;
        }
    }

    void print_entity() {
        String temp = null;
        if (this.status == 0) {
            temp = "ready";
        }
        if (this.status == 1) {
            temp = "Running";
        }
        if (this.status == 2) {
            temp = "waiting";
        }
        System.out.println("<--------------->");
        System.out.println("ProcessID = " + this.processid);
        System.out.println("Priority = " + this.priority);
        System.out.println("Status = " + temp);
        System.out.println("CPU => " + cpu_io);
        System.out.println("Total Burst = " + this.total_burst);
        System.out.println("Arrival Time = " + this.arrival_time);
        System.out.println("Response Time = " + this.first_spotted);
        System.out.println("Turnaround Time = " + this.last_spotted);
        System.out.println("Waiting Time = " + this.total_waiting_time);
        System.out.println("<--------------->");
    }

}
