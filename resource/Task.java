package resource;

public class Task {
	
	private int arrivalTime;
	private int processingTime;
	private int id;
	private int overallTime;
	
	public int getOverallTime() {
		return overallTime;
	}
	public void setOverallTime(int overallTime) {
		this.overallTime = overallTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getProcessingTime() {
		return processingTime;
	}
	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}
	
}
