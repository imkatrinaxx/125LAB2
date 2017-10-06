import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.Comparator;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

class Lab2 {
	public static int quantum = 4;	
	public static void main(String[] args) throws Exception {
		clear();
		new DataSet().Start();
	}

	/* clearing console */
	public static void clear() throws Exception{
	    final String os = System.getProperty("os.name");
	    if (os.contains("Windows"))
	      new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	    else Runtime.getRuntime().exec("clear");
  	}
}

class DataPrinting{

	private static DecimalFormat f = new DecimalFormat("##.0");

	public static void printFinal() {
		System.out.println("\n\t\t-----------------------------------------------------------------------------------------------------------------");
		System.out.println("\t\t|\tProcess" + "\t|\t" + "BT" + "\t|\t"  +"FCFS" + "\t|\t"+ "SJF" + "\t|\t" + "SRPT" + "\t|    " + "Priority" +"\t|\t" + "RR" + "\t|");
		System.out.println("\t\t-----------------------------------------------------------------------------------------------------------------");
		for(int  i = 0; i < DataSet.fscs_datalist.size(); i ++) {
			System.out.println("\t\t|\t"+ DataSet.fscs_datalist.get(i).PROCESS  + "\t|\t" + 
								DataSet.fscs_datalist.get(i).ORIG_BT  + "\t|\t" + 
								DataSet.fscs_datalist.get(i).WAITING_TIME + "\t|\t"+ 
								DataSet.sjf_datalist.get(i).WAITING_TIME + "\t|\t" +
								DataSet.srpt_datalist.get(i).WAITING_TIME + "\t|\t" + 
							 	DataSet.priority_datalist.get(i).WAITING_TIME  + "\t|\t" +  
							 	DataSet.rr_datalist.get(i).WAITING_TIME + "\t|");
			System.out.print("\t\t-----------------------------------------------------------------------------------------------------------------\n");
		}
		System.out.println("\t\t|\t"+ "AVE"  + "\t|\t" + "\t|\t" + f.format(DataSet.ave_fcfs) + 
							 "\t|\t"+ f.format(DataSet.ave_sjf) + "\t|\t" + f.format(DataSet.ave_srpt) + "\t|\t" + f.format(DataSet.ave_priority) + "\t|\t" +
							 f.format(DataSet.ave_rr) + "\t|");
		System.out.print("\t\t-----------------------------------------------------------------------------------------------------------------\n");
	}

	public static void printEvaluation() {
		ArrayList<Float> aves = new ArrayList<Float>();

		aves.add(DataSet.ave_fcfs);
		aves.add(DataSet.ave_sjf);
		aves.add(DataSet.ave_srpt);
		aves.add(DataSet.ave_priority);
		aves.add(DataSet.ave_rr);

		Collections.sort(aves);
		System.out.println("\n\t\tFastest Algo:\t" + DataSet.getAlgoUsedByAve(aves.get(0)) + "(" + f.format(aves.get(0))
							+ ") \n\t\tFollowed by:\t"  + DataSet.getAlgoUsedByAve(aves.get(1)) + "(" + f.format(aves.get(1)) + ")," 
							+ DataSet.getAlgoUsedByAve(aves.get(2)) + "(" + f.format(aves.get(2)) + ") and then "
							+ DataSet.getAlgoUsedByAve(aves.get(3)) + "(" + f.format(aves.get(3)) 
							+ ") \n\t\tThe Slowest:\t" + DataSet.getAlgoUsedByAve(aves.get(4)) + "(" + f.format(aves.get(4)) + ")");
	}
}

class Process {
	protected int PROCESS;
	protected int ARRIVAL;
	protected int BURST_TIME; /* used for the iteration */
	protected int ORIG_BT;	  /* used for printing purposes */
	protected int PRIORITY;
	protected int WAITING_TIME;
	protected int RR = 0;	  /* used for Round Robin Computation (quantum * RR) */
	protected int COMP_TIME = 0;
    
    public Process(int process) {
    	this.PROCESS = process;
    }

	public Process(ArrayList<Integer> values){
		this.PROCESS = values.get(0);
		this.ARRIVAL = values.get(1);
		this.BURST_TIME = values.get(2);
		this.ORIG_BT = values.get(2);
		this.PRIORITY = values.get(3);
	}

	public int getBurstTime(){
		return this.BURST_TIME;
	}

	public int getPriority() {
		return this.PRIORITY;
	}

	public int getArrival() {
		return this.ARRIVAL;
	}

	public int getProcess() {
		return this.PROCESS;
	}

    public static Comparator<Process> BurstTime = new Comparator<Process>() {
	public int compare(Process s1, Process s2) {
	   int arr1 = s1.getBurstTime();
	   int arr2 = s2.getBurstTime();
	   return arr1-arr2;
   }};

    public static Comparator<Process> PriorityNum = new Comparator<Process>() {
	public int compare(Process s1, Process s2) {
	   int arr1 = s1.getPriority();
	   int arr2 = s2.getPriority();
	   return arr1-arr2;
   }};

    public static Comparator<Process> ArrivalTime = new Comparator<Process>() {
	public int compare(Process s1, Process s2) {
	   int arr1 = s1.getArrival();
	   int arr2 = s2.getArrival();
	   return arr1-arr2;
   }};

    public static Comparator<Process> ProcessNum = new Comparator<Process>() {
	public int compare(Process s1, Process s2) {
	   int arr1 = s1.getProcess();
	   int arr2 = s2.getProcess();
	   return arr1-arr2;
   }};
}

class DataSet{
	public static BufferedReader SCAN;
	/* created datalist for each algorithm for the purpose of printing (so that the output can be printed at once) and computation */
	public static ArrayList<Process> fscs_datalist, srpt_datalist, sjf_datalist, priority_datalist, rr_datalist;
	public static float ave_fcfs = 0, ave_sjf = 0, ave_srpt = 0, ave_priority = 0, ave_rr = 0;
	Scanner s = new Scanner(System.in);
	String filename = "";
	public static int type = 0;


	public void Start() throws Exception {
		FileReadingChoices();
	}

	public static String getAlgoUsedByAve(float ave) {
		if(ave == ave_fcfs) return "FCFS";
		if(ave == ave_sjf)	return "SJF";
		if(ave == ave_srpt)	return "SRPT";
		if(ave == ave_priority)	return "PRIORITY";
		if(ave == ave_rr)	return "ROUND ROBIN";
		return null;
	}

	public void FileReadingChoices() throws Exception{
		System.out.println("\n\t\t------------------------------------------");
		System.out.print("\t\t||\t   Select File To Read\t\t||\n");
		System.out.print("\t\t||\t\t\t\t\t||\n");
		System.out.print("\t\t||\t  [1]process1.txt \t\t||\n");
		System.out.print("\t\t||\t  [2]process2.txt \t\t||\n");
		System.out.print("\t\t||\t  [3]process3.txt \t\t||");
		System.out.println("\n\t\t------------------------------------------\n\n");


		System.out.print("\t\tEnter your choice: ");			
		int ch = s.nextInt();	

		if(ch == 1) {
			filename = "process1";
			type = 1;
		} else if(ch == 2){
			filename = "process2";
			type = 2;			
		} else if(ch == 3){
			filename = "process3";
			type = 3;
		}

		ReadContent();
	}
	//reading contents from file
	protected void ReadContent() throws Exception {		
		fscs_datalist = new ArrayList<Process>();
		srpt_datalist = new ArrayList<Process>();
		sjf_datalist = new ArrayList<Process>();
		priority_datalist = new ArrayList<Process>();
		rr_datalist = new ArrayList<Process>();
		
		FileReader file = null;

		try{
			file = new FileReader(filename+".txt");
            SCAN = new BufferedReader(file);
            String linecontent = "";
			String label = SCAN.readLine();

            while((linecontent = SCAN.readLine()) != null){   
				String[] content = linecontent.split("\\s");
				ArrayList<Integer> values = new ArrayList<Integer>();

				for(int i = 0; i < content.length; i++) {
					if(!content[i].equals("")){
						values.add(Integer.parseInt(content[i]));
					}
				}
				fscs_datalist.add(new Process(values));
				sjf_datalist.add(new Process(values));				
				srpt_datalist.add(new Process(values));
				priority_datalist.add(new Process(values));
				rr_datalist.add(new Process(values));
            }    
            solveAlgo();     
		} catch (FileNotFoundException ex) {
           	System.out.println("File not found");
        } 
	}

	public void solveAlgo() throws Exception{
		//FCFS
		ComputeWaitingTime(fscs_datalist,"FCFS");
		//SJF
		Collections.sort(sjf_datalist, Process.BurstTime);
		ComputeWaitingTime(sjf_datalist,"SJF");

		//SRPT
		Collections.sort(srpt_datalist, Process.ArrivalTime);
		ComSRPT(srpt_datalist);

		//PRIORITY
		Collections.sort(priority_datalist, Process.PriorityNum);
		ComputeWaitingTime(priority_datalist,"PRIORITY");

		//ROUND ROBIN
		ComputeRoundRobin(rr_datalist);

		SortByProcessName();		
		DataPrinting.printFinal();
		DataPrinting.printEvaluation();
		ContinueExecution();
	}

	private void SortByProcessName() {
		Collections.sort(fscs_datalist, Process.ProcessNum);
		Collections.sort(sjf_datalist, Process.ProcessNum);
		Collections.sort(srpt_datalist, Process.ProcessNum);
		Collections.sort(priority_datalist, Process.ProcessNum);
		Collections.sort(rr_datalist, Process.ProcessNum);
	}

	public void ComputeRoundRobin(ArrayList<Process> processdata) throws Exception{
		int wait = 0;
		int prev = 0;
		
		while(!allBurst0(processdata)){
			for (Process p : processdata) {
				if(p.BURST_TIME != 0){					
					p.WAITING_TIME = wait;
					if(p.BURST_TIME <= Lab2.quantum){
						wait += p.BURST_TIME;
						p.BURST_TIME = 0;
					} else {
						if(prev != p.PROCESS){
							wait += Lab2.quantum;
							p.BURST_TIME = p.BURST_TIME - Lab2.quantum;
							p.RR++;
						} else {
							p.BURST_TIME = p.BURST_TIME - Lab2.quantum;	
						}
						prev = p.PROCESS;
					}
				}
			}
		}

		float avewt = 0;
		int subt;
		for (Process p : processdata) {			
			subt = p.WAITING_TIME - (p.RR * Lab2.quantum);
			avewt += subt;
			p.WAITING_TIME = subt;
		}

		ave_rr = avewt/processdata.size();
	}

	public void ComputeWaitingTime(ArrayList<Process> processdata, String label)throws Exception {
		
		int wait = 0;
		for(Process p: processdata) {
			p.WAITING_TIME = wait;
			wait += p.BURST_TIME;
		}

		float avewt = 0;
		for (Process p : processdata) {
			avewt += p.WAITING_TIME;
		}

		if(label.equals("FCFS")) {
			ave_fcfs = avewt/processdata.size();
		} else if(label.equals("SJF")) {
			ave_sjf = avewt/processdata.size();
		} else if(label.equals("PRIORITY")) {
			ave_priority = avewt/processdata.size();
		}
	}

	public void ContinueExecution() throws Exception{
		System.out.println("\n\t\t(Choose Execution)\t[1]Choose File\t[ANY]Exit");
		System.out.print("\t\tEnter your choice: ");
		int cont = s.nextInt();
		if(cont == 1) {
			Lab2.clear();
			FileReadingChoices();
		} else if(cont == 2) {
			return;
		}
	}

	public boolean allBurst0(ArrayList<Process> processdata) {
		for (Process p : processdata) {
			if(p.BURST_TIME > 0)
				return false;
		}
		return true;
	}

	public void ComSRPT(ArrayList<Process> processdata) throws Exception {
		Lab2.clear();
		ArrayList<Process> queue = new ArrayList<Process>();
		int lasttime = processdata.size();
		for(int i = 0 ; i < processdata.size(); i++) {
			queue.addAll(newProcessArrived(i, processdata));
			Collections.sort(queue, Process.BurstTime);
			Process _lowestB = getleastBurst(queue);
			_lowestB.BURST_TIME--;
			if(_lowestB.BURST_TIME == 0) {
				_lowestB.COMP_TIME = i+1;
			}
		}

		Collections.sort(queue, Process.BurstTime);
		if(!allBurst0(processdata)){
			for ( Process p: queue) {
				if(p.BURST_TIME != 0) {
					p.COMP_TIME = p.BURST_TIME + lasttime;
					lasttime = p.COMP_TIME;
				} 
			}
		}

		float avewt = 0;
		for (Process p: processdata ) {
			p.WAITING_TIME = p.COMP_TIME - p.ORIG_BT - p.ARRIVAL;
			avewt += p.WAITING_TIME;
		}

		ave_srpt = avewt/processdata.size();
	}

	public ArrayList<Process> newProcessArrived(int time, ArrayList<Process> processdata){
		ArrayList<Process> toAdd = new ArrayList<Process>();
		for (Process p : processdata) {
			if(p.ARRIVAL > time) {
				break;
			} else{
				if(p.ARRIVAL == time) {
					toAdd.add(p);
				}
			}
		}
		return toAdd;
	}

	public Process getleastBurst(ArrayList<Process> processdata) {
		for (Process p: processdata) {
			if(p.BURST_TIME != 0){
				return p;
			}
		}
		return null;
	}
}

