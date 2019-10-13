import java.util.*;
import java.io.File;

public class Solution {
    static Scanner randomScanner;
    public static void main(String [] args){
        String fileName = "";
        boolean verbose = false;
        if(args.length == 1){
            fileName = args[0];
        }else{
            verbose = true;
            fileName = args[1];
        }

        Scanner sc   = new Scanner(System.in);

        try{
            sc = new Scanner(new File(fileName));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        int numProcesses = sc.nextInt();

        List<Process> processList = new ArrayList<Process>();

        for(int i = 0 ; i < numProcesses; i ++){
            int arrival = sc.nextInt();
            int cpuRandom = sc.nextInt();
            int cpuTotal = sc.nextInt();
            int ioMulti = sc.nextInt();
            Process p = new Process(arrival, cpuRandom, cpuTotal, ioMulti, i,State.unstarted);
            processList.add(p);
        }

//        FCFS f = new FCFS(getDeepClone(processList), verbose);
//        f.Schedule();

//        RR r = new RR(getDeepClone(processList), verbose);
//        r.Schedule();
//
//        SJF s = new SJF(getDeepClone(processList), verbose);
//        s.Schedule();
//
        HPRN h = new HPRN(getDeepClone(processList), verbose);
        h.Schedule();
    }

    public static List<Process> getDeepClone(List<Process> processList) {
        List<Process> results = new ArrayList<>();
        for(Process p : processList){
            results.add((Process)p.clone());
        }
        return results;
    }
}




class Process implements Comparable<Process>,Cloneable {
    Integer arrival; // this is the original arrival time
    Integer queueArrival; // this is the updated arrival everytime this get back on queue
    Integer cpuRandom;
    Integer cpuTotal;
    Integer ioMulti;
    Integer index;
    Integer ioTime;
    Integer cpuBurst;
    Integer prevCPUBurst;
    Integer finishingTime;
    Float hprn = 0.0f;

    int ioTotal = 0;
    int originalCPUTotal;
    int quantum = 0;

    State state;


    public Process(int arrival, int cpuRandom, int cpuTotal, int ioMulti, int index, State state){
        this.arrival = arrival;
        this.queueArrival = arrival;
        this.cpuRandom = cpuRandom;
        this.cpuTotal = cpuTotal;
        this.originalCPUTotal = cpuTotal;
        this.ioMulti = ioMulti;
        this.index = index;
        this.state = state;
    }

    public int compareTo(Process other){
        System.out.println("TEST " + other.index);

        return -1;
//                if(this.queueArrival.compareTo(other.queueArrival) == 0){
//            return this.index.compareTo(other.index);
//        }else{
//            return this.queueArrival.compareTo(other.queueArrival);
//        }
    }

    public Object clone()  {

        Process p = new Process(this.arrival, this.cpuRandom, this.cpuTotal, this.ioMulti, this.index, this.state);
        return p;
    }

    public String toString(){

        String  processNum = "Process " + this.index + ":" + '\n';
        String details = " (A,B,C,M) = (" + this.arrival +"," +  this.cpuRandom+ "," + this.originalCPUTotal + "," + this.ioMulti + ")" + '\n';
        String finishingTime = " Finishing Time : " + this.finishingTime + '\n';
        String turnAroundTime = " TurnAround Time: " + (this.finishingTime - this.arrival) + '\n' ;
        String IOTime = " I/O Time: " +  this.ioTotal + '\n' ;
        String waitingTime = " Waiting Time : " + (((this.finishingTime - this.arrival) - this.originalCPUTotal) - (this.ioTotal)) + '\n';
        return processNum + details + finishingTime + turnAroundTime + IOTime + waitingTime;
     }


}

enum State {
    running,ready,blocked,terminated,unstarted;
}
