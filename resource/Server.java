package resource;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{

	private BlockingQueue<Task> tasks;
	private AtomicInteger waitingPeriod;
	private Boolean cleanup;
	public Server() {
		tasks = new LinkedBlockingQueue<Task>();
		waitingPeriod = new AtomicInteger();
		cleanup = false;
		//initialize queue and waitingPeriod
	}
	

	public Boolean getCleanup() {
		return cleanup;
	}


	public void setCleanup(Boolean cleanup) {
		this.cleanup = cleanup;
	}


	public void addTask(Task newTask) {
		tasks.add(newTask);
		waitingPeriod.addAndGet(newTask.getProcessingTime());
	}
	public AtomicInteger getWaitingPeriod() {
		return waitingPeriod;
	}

	public void setWaitingPeriod(AtomicInteger waitingPeriod) {
		this.waitingPeriod = waitingPeriod;
	}

	public void setTasks(BlockingQueue<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void run() {
		while(!tasks.isEmpty()) {
			try {
				if(cleanup == true) {
					Thread.currentThread().interrupt();
					return;
				}
				if(tasks.peek() != null) {
					Thread.sleep(989);
					waitingPeriod.getAndAdd(-1);
					tasks.peek().setProcessingTime(tasks.peek().getProcessingTime() - 1);
					if(tasks.peek().getProcessingTime() == 0) {
						System.out.println("Removed task " + tasks.peek().getId() + " from queue");
						tasks.remove();
						if(tasks.isEmpty()) {
							Thread.currentThread().interrupt();
						}
						continue;
					}
					
				}
			}
			catch(InterruptedException e) {
			}
		}
	}
	
	public BlockingQueue<Task> getTasks() { 
		
		return tasks;
	}

}
