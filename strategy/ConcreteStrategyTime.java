package strategy;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import resource.Server;
import resource.Task;

public class ConcreteStrategyTime implements Strategy{

	@Override
	public void addTask(List<Server> servers, Task t) {
		AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
		int pos = 0;
		for(int i = 0; i < servers.size(); i++) {
			if(servers.get(i).getWaitingPeriod().get() < min.get()) {
				min.set(servers.get(i).getWaitingPeriod().get());
				pos = i;
			}
		}
		System.out.println("Task with id: " + t.getId() + " has been added to queue: " + pos);
		t.setOverallTime(t.getProcessingTime() + servers.get(pos).getWaitingPeriod().get());
		System.out.println("Overall waiting time for client: " + t.getId() + " is " + t.getOverallTime());
		servers.get(pos).addTask(t);
		if(servers.get(pos).getTasks().size() == 1) {
			Thread thread = new Thread(servers.get(pos));
			thread.start();
		}
	}

}
