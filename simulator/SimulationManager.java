package simulator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import resource.Task;

public class SimulationManager implements Runnable{

	private int timeLimit = 30; //maximum processing time
	private int maxProcessingTime = 10;
	private int minProcessingTime = 2;
	private int maxArrivalTime = 10;
	private int minArrivalTime = 2;
	private int numberOfServers = 2;
	private int numberOfClients = 6;
	private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
	private FileWriter myWriter;
	
	public SelectionPolicy getSelectionPolicy() {
		return selectionPolicy;
	}

	public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
		this.selectionPolicy = selectionPolicy;
	}

	private Scheduler scheduler;
	private List<Task> generatedTasks;
	
	public SimulationManager() {
		generatedTasks = new LinkedList<Task>();
		generateNRandomTasks();
		scheduler = new Scheduler(numberOfServers, numberOfClients);
		scheduler.changeStrategy(selectionPolicy);
	}
	
	public SimulationManager(int timeLimit, int minProcessingTime, int maxProcessingTime, int minArrivalTime, int maxArrivalTime, int numberOfServers, int numberOfClients, FileWriter myWriter) {
		
		this.timeLimit = timeLimit;
		this.minProcessingTime = minProcessingTime;
		this.maxProcessingTime = maxProcessingTime;
		this.minArrivalTime = minArrivalTime;
		this.maxArrivalTime = maxArrivalTime;
		this.numberOfServers = numberOfServers;
		this.numberOfClients = numberOfClients;
		this.myWriter = myWriter;
		generatedTasks = new LinkedList<Task>();
		generateNRandomTasks();
		scheduler = new Scheduler(numberOfServers, numberOfClients);
		scheduler.changeStrategy(selectionPolicy);
	}
	
	private void generateNRandomTasks() {
		Random rand = new Random();
		for(int i = 0; i < numberOfClients; i++) {
			Task newTask = new Task();
			newTask.setId(i);
			newTask.setArrivalTime(rand.nextInt(maxArrivalTime) + minArrivalTime);
			newTask.setProcessingTime(rand.nextInt(maxProcessingTime) + minProcessingTime);
			generatedTasks.add(newTask);
		}
		
		for(int i = 0; i < numberOfClients; i++) {
			System.out.println("Client with id: " + i);
			System.out.println("Processing time: " + generatedTasks.get(i).getProcessingTime());
			System.out.println("Arrival time: " + generatedTasks.get(i).getArrivalTime() + "\n");
		}
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<Task> getGeneratedTasks() {
		return generatedTasks;
	}

	public void setGeneratedTasks(List<Task> generatedTasks) {
		this.generatedTasks = generatedTasks;
	}

	public void interruptThreads() {
		for(int i = 0; i < scheduler.getServers().size(); i++) {
			scheduler.getServers().get(i).setCleanup(true);
		}
	}
	
	public void printQueues() {
		try {
			for(int i = 0; i < scheduler.getServers().size(); i++) {
				myWriter.write("Queue " + i + ": ");
				Iterator<Task> list = scheduler.getServers().get(i).getTasks().iterator();
				for(int j = 0; j < scheduler.getServers().get(i).getTasks().size(); j++) {
					if(list.hasNext()) {
						Task task = list.next();
						if(task.getProcessingTime() > 0)
							myWriter.write("(" + task.getId() + "," + task.getArrivalTime() + "," + task.getProcessingTime() + "),");
					}
					
				}
				myWriter.write("\n");
			}
		}
		catch(IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	
	public Boolean noRemainingTask() {
		for(int i = 0; i < scheduler.getServers().size(); i++) {
			if(!scheduler.getServers().get(i).getTasks().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void printTasks() {
		try {
			for(int i = 0; i < generatedTasks.size(); i++) {
				myWriter.write("(" + generatedTasks.get(i).getId() + "," + generatedTasks.get(i).getArrivalTime() + "," + generatedTasks.get(i).getProcessingTime()
						+ "),");
			}
			myWriter.write("\n");
		}
		catch(IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	@Override
	public void run() {
		try {
			int currentTime = 0;
			double sum = 0;
			int size = 0;
			while(currentTime < timeLimit) {
				if(generatedTasks.size() == 0) {
					if(noRemainingTask() == true) {
						System.out.println("The avg is " + sum / size);
						myWriter.write("Average waiting time " + sum / size);
						interruptThreads();
						Thread.currentThread().interrupt();
						myWriter.close();
						return;
					}
				}
				System.out.println("Current time is " + currentTime);
				myWriter.write("\nTime " + Integer.toString(currentTime));
				myWriter.write("\nRemaining clients: ");
				for(int i = 0; i < generatedTasks.size(); i++) {
					if(generatedTasks.get(i).getArrivalTime() == currentTime) {
						scheduler.dispatchTask(generatedTasks.get(i));
						sum += generatedTasks.get(i).getOverallTime();
						size++;
						generatedTasks.remove(i);
						i--;
					}
				}
				printTasks();
				printQueues();
				currentTime++;
				if(currentTime == timeLimit) {
					System.out.println("The avg is " + sum / size);
					myWriter.write("Average waiting time " + sum / size);
					interruptThreads();
					Thread.currentThread().interrupt();
					myWriter.close();
					return;
				}
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
				}
			}
		}
		catch(FileNotFoundException e) {
			System.out.println("An error occurred. Couldn't find the file");
		} catch (IOException e) {
			System.out.println("IO Error");
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		
		try {
		      File myObj = new File(args[0]);
		      FileWriter myWriter = new FileWriter(args[1]);
		      Scanner myReader = new Scanner(myObj);
		      int clients = myReader.nextInt();
		      int servers = myReader.nextInt();
		      int maxSimulation = myReader.nextInt();
		      String time = myReader.nextLine();
		      time = myReader.nextLine();
		      String[] vals = time.split(",");
		      int minArrival = Integer.parseInt(vals[0]);
		      int maxArrival = Integer.parseInt(vals[1]);
		      time = myReader.nextLine();
		      vals = time.split(",");
		      int minProcess = Integer.parseInt(vals[0]);
		      int maxProcess = Integer.parseInt(vals[1]);
		      SimulationManager gen = new SimulationManager(maxSimulation, minProcess, maxProcess, minArrival, maxArrival, servers, clients, myWriter);
			  Thread t = new Thread(gen);
			  t.start();
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred. Couldn't find the file");
		      e.printStackTrace();
		    } catch (IOException e) {
				System.out.println("IO Error");
				e.printStackTrace();
			}
	}
}
