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
        randomScanner = getRandomFile();
        int numProcesses = sc.nextInt();

        List<Process> processList = new ArrayList<Process>();
        PriorityQueue<Process> readyQueue = new PriorityQueue();
        Set<Process> blockedSet = new HashSet<>();
        Process curr = null;

        for(int i = 0 ; i < numProcesses; i ++){
            int arrival = sc.nextInt();
            int cpuRandom = sc.nextInt();
            int cpuTotal = sc.nextInt();
            int ioMulti = sc.nextInt();
            Process p = new Process(arrival, cpuRandom, cpuTotal, ioMulti, i,State.unstarted);

            processList.add(p);
        }

        Collections.sort(processList);
        for(int i = 0 ; i < processList.size(); i ++){
            processList.get(i).index = i;
        }
        // now our arrival map is
        int time = 0;
        int terminatedCount = 0;
        int unutilized = 0;
        int unutilizedIO = 0;
        while(terminatedCount < numProcesses){
            if(time < 10000 ){
                System.out.print("Before Cycle: " + time);
                for(Process p : processList){
                    System.out.print( " " + p.state + " ");
                    if(p.state == State.blocked){
                        System.out.print(p.ioTime + " ");
                    }else if(p.state == State.running){
                        System.out.print(p.cpuBurst + " ");
                    } else{
                        System.out.print(0 + " ");
                    }
                }
                System.out.println();
            }


            Set<Process> newBlockedSet = new HashSet<>();



            for(Process p : blockedSet ){
                p.ioTime --;
                if(p.ioTime == 0){
                    p.state = State.ready;

                        p.queueArrival = time;
                    readyQueue.offer(p);
                }else{
                    newBlockedSet.add(p);
                }
            }

            blockedSet = newBlockedSet;

            boolean hasCurr = false;
            if(curr != null){
                hasCurr = true;
                curr.cpuBurst -= 1;
                curr.cpuTotal -= 1;
                if(curr.cpuTotal == 0){

                    curr.state = State.terminated;
                    curr.finishingTime = time;
                    terminatedCount ++;
                    curr = null;
                }else if(curr.cpuBurst == 0 ){
                    curr.state = State.blocked;

                    curr.ioTime = curr.ioMulti * curr.prevCPUBurst;
                    curr.ioTotal += curr.ioTime;
                    blockedSet.add(curr);
                    curr = null;
                }
            }

            if(blockedSet.size() == 0 && time > 0){
                //System.out.println("NO IO " + time );
                unutilizedIO ++;
            }


            for(Process p : processList){
                if(p.arrival == time){
                    readyQueue.offer(p);
                    p.state = State.ready;
                }
            }

            if(curr == null){

                if(readyQueue.size() > 0){
//                    if(time == 228){
//                        //System.out.println(readyQueue.size());
//                        System.out.println(processList.get(1).queueArrival);
//                        System.out.println(processList.get(2).queueArrival);
//                        System.out.println(processList.get(1).compareTo(processList.get(2)));
//                    }
                    curr = readyQueue.poll();
                    curr.state = State.running;

                    int burstTime = randomOS(curr.cpuRandom);
                    curr.prevCPUBurst = burstTime;
                    curr.cpuBurst = burstTime;
                }
            }
            if(time > 0){
                boolean addOn = false;
                for(Process p : processList){
                    if(p.state != State.blocked && p.state != State.unstarted){
                        addOn = true;
                    }
                }
                if(addOn){
                    unutilized ++;
                }
            }
            time ++;
        }
        int totalWait = 0;
        int totalTurn = 0;
        for(Process p : processList){
            System.out.println(p);
            totalWait +=  (((p.finishingTime - p.arrival) - p.originalCPUTotal) - (p.ioTotal));
            totalTurn += (p.finishingTime - p.arrival);
        }
        time -= 1;
        System.out.println(unutilized + " " + unutilizedIO);
        System.out.println("Summary Data");
        System.out.println("Finishing Time : " + time);
        System.out.println("CPU Utilization: " +  ((time - unutilized) / (float)time));
        System.out.println("IO Utilization: "  + ((time - unutilizedIO) / (float)time));
        System.out.println("Throughput: " +  ((100 / (float)time) * processList.size()) + " processes per hundred cycles");
        System.out.println("Average turn around time : " + (float)totalTurn / processList.size() );
        System.out.println("Average wait time : " + (float)totalWait / processList.size() );



    }

    public static int randomOS(Integer cpuRandom){
        return (1 + (randomScanner.nextInt() % cpuRandom));
    }

    public static Scanner getRandomFile(){
        try {
            File file = new File("random-numbers.txt");
            return new Scanner(file);
        }catch(Exception e){
            return null;
        }
    }

    public static void printSummary(){


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
        if(this.queueArrival.compareTo(other.queueArrival) == 0){
            return this.index.compareTo(other.index);
        }else{
            return this.queueArrival.compareTo(other.queueArrival);
        }
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
