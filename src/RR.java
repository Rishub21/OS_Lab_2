import java.io.File;
import java.util.Comparator;
import java.util.*;
import java.io.File;

public class RR {

    PriorityQueue<Process> readyQueue = new PriorityQueue( new fcfsComparator());
    List<Process>  processList = new ArrayList<>();
    Process curr = null;
    Set<Process> blockedSet = new HashSet<>();
    int numProcesses;
    Scanner randomScanner = getRandomFile();
    boolean verbose;

    public RR (List<Process> processList, boolean verbose ){
        this.processList = processList;
        this.numProcesses = processList.size();
        this.verbose = verbose;

        System.out.print("The original input was: ");
        System.out.print(processList.size());
        for(Process p : processList){
            System.out.print( "(" + p.arrival + " " + p.cpuRandom + " " + p.cpuTotal + " " + p.ioMulti  + ") ");
        }
        System.out.println();

        Collections.sort(processList, new rrComparator());

        for(int i = 0 ; i < processList.size(); i ++){
            this.processList.get(i).index = i;
        }

        System.out.print("The (sorted) input is : ");
        System.out.print(processList.size());
        for(Process p : processList){
            System.out.print( "(" + p.arrival + " " + p.cpuRandom + " " + p.cpuTotal + " " + p.ioMulti  + ") ");
        }
        System.out.println("\n");
        if(this.verbose){
            System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        }




    }

    public void Schedule(){
        int time = 0;
        int terminatedCount = 0;
        int unutilized = 0;
        int unutilizedIO = 0;
        while(terminatedCount < numProcesses){

            if(verbose){
                System.out.print("Before Cycle: " + time);
                for(Process p : processList){
                    System.out.print( " " + p.state + " ");
                    if(p.state == State.blocked){
                        System.out.print(p.ioTime + " ");
                    }else if(p.state == State.running){
                        System.out.print(p.quantum + " ");
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
                curr.quantum -= 1;


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
                }else if(curr.quantum == 0){
                    curr.state = State.ready;
                    curr.queueArrival = time;
                    readyQueue.offer(curr);
                    curr = null;
                }
            }

            if(blockedSet.size() == 0 && time > 0){
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

                    curr = readyQueue.poll();
                    curr.state = State.running;

                    if(curr.cpuBurst == null || curr.cpuBurst == 0){
                        int burstTime = randomOS(curr.cpuRandom);
                        curr.prevCPUBurst = burstTime;
                        curr.cpuBurst = burstTime;
                    }

                    curr.quantum = 2;
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

        System.out.println("The scheduling algorithm used was Round Robin \n");
        summary(time, unutilized, unutilizedIO);

    }


    public void summary(int time, Integer unutilized, Integer unutilizedIO){
        int totalWait = 0;
        int totalTurn = 0;
        int totalCompute = 0;
        for(Process p : processList){
            System.out.println(p);
            totalWait +=  (((p.finishingTime - p.arrival) - p.originalCPUTotal) - (p.ioTotal));
            totalTurn += (p.finishingTime - p.arrival);
            totalCompute += p.originalCPUTotal;

        }

        time -= 1;
        System.out.println("Summary Data");
        System.out.println(" Finishing Time : " + time);
        System.out.println(" CPU Utilization: " +  (totalCompute / (float)time));
        System.out.println(" IO Utilization: "  + ((time - unutilizedIO) / (float)time));
        System.out.println(" Throughput: " +  ((100 / (float)time) * processList.size()) + " processes per hundred cycles");
        System.out.println(" Average turn around time : " + (float)totalTurn / processList.size() );
        System.out.println(" Average wait time : " + (float)totalWait / processList.size() );
    }



    public  int randomOS(Integer cpuRandom){
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
}

class rrComparator implements Comparator<Process> {
    public int compare(Process p1, Process p2){
        if(p1.queueArrival.compareTo(p2.queueArrival) == 0){
            return p1.index.compareTo(p2.index);
        }else{
            return p1.queueArrival.compareTo(p2.queueArrival);
        }
    }
}