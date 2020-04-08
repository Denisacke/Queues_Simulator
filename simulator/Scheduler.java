package simulator;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import resource.Server;
import resource.Task;
import strategy.ConcreteStrategyQueue;
import strategy.ConcreteStrategyTime;
import strategy.Strategy;

public class Scheduler {

	private List<Server> servers;
	private int maxNoServers;
	private int maxTasksPerServer;
	private Strategy strategy;
	
	public Scheduler(int maxNoServers, int maxTasksPerServer) {
		this.maxNoServers = maxNoServers;
		servers = new LinkedList<Server>();
		this.maxTasksPerServer = maxTasksPerServer;
		for(int i = 0; i < maxNoServers; i++) {
			Server server = new Server();
			servers.add(server);
		}
	}
	
	public int getMaxNoServers() {
		return maxNoServers;
	}

	public void setMaxNoServers(int maxNoServers) {
		this.maxNoServers = maxNoServers;
	}

	public int getMaxTasksPerServer() {
		return maxTasksPerServer;
	}

	public void setMaxTasksPerServer(int maxTasksPerServer) {
		this.maxTasksPerServer = maxTasksPerServer;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public void changeStrategy(SelectionPolicy policy) {
		if(policy == SelectionPolicy.SHORTEST_QUEUE) {
			strategy = new ConcreteStrategyQueue();
		}
		if(policy == SelectionPolicy.SHORTEST_TIME) {
			strategy = new ConcreteStrategyTime();
		}
	}
	
	public void dispatchTask(Task t) {
		strategy.addTask(servers, t);
	}
	
	public List<Server> getServers(){
		return servers;
	}
	
}
