import java.util.*;
import java.io.File;

public class Solution {
    static Scanner randomScanner;
    public static void main(String [] args){
        String fileName = args[0];
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

        FCFS f = new FCFS(processList);
        f.Schedule();

    }
}




class Process implements Comparable<Process> {
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
    int ioTotal = 0;
    int originalCPUTotal;
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

    public String toString(){

        String  processNum = "Process " + this.index + ":" + '\n';
        String details = "(A,B,C,M) = (" + this.arrival +"," +  this.cpuRandom+ "," + this.originalCPUTotal + "," + this.ioMulti + ")" + '\n';
        String finishingTime = "Finishing Time : " + this.finishingTime + '\n';
        String turnAroundTime = "TurnAround Time: " + (this.finishingTime - this.arrival) + '\n' ;
        String IOTime = "I/O Time: " +  this.ioTotal + '\n' ;
        String waitingTime = "Waiting Time : " + (((this.finishingTime - this.arrival) - this.originalCPUTotal) - (this.ioTotal)) + '\n';
        return processNum + details + finishingTime + turnAroundTime + IOTime + waitingTime;
     }


}

enum State {
    running,ready,blocked,terminated,unstarted;
}
