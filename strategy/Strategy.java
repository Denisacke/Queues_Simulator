package strategy;

import java.util.List;

import resource.Server;
import resource.Task;

public interface Strategy {

	public void addTask(List<Server> servers, Task t);
}
