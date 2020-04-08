package strategy;

import java.util.List;

import resource.Server;
import resource.Task;

public class ConcreteStrategyQueue implements Strategy{

	@Override
	public void addTask(List<Server> servers, Task t) {
	
		int min = Integer.MAX_VALUE;
		int pos = 0;
		for(int i = 0; i < servers.size(); i++) {
			if(servers.get(i).getTasks().size() < min) {
				min = servers.get(i).getTasks().size();
				pos = i;
			}
		}
		System.out.println("Task with id: " + t.getId() + " has been added to queue: " + pos);
		t.setOverallTime(t.getProcessingTime() + servers.get(pos).getWaitingPeriod().get());
		servers.get(pos).addTask(t);
		if(servers.get(pos).getTasks().size() == 1) {
			Thread thread = new Thread(servers.get(pos));
			thread.start();
		}
	}

}
