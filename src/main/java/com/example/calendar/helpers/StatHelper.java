package com.example.calendar.helpers;

public class StatHelper {

	private String chosenCalendar;
	private String statMethod;
	private String graphMethod;
	
	
	public StatHelper() {}


	public StatHelper(String chosenCalendar, String statMethod, String graphMethod) {
		this.chosenCalendar = chosenCalendar;
		this.statMethod = statMethod;
		this.graphMethod = graphMethod;
	}


	public String getChosenCalendar() {
		return chosenCalendar;
	}


	public void setChosenCalendar(String chosenCalendar) {
		this.chosenCalendar = chosenCalendar;
	}


	public String getStatMethod() {
		return statMethod;
	}


	public void setStatMethod(String statMethod) {
		this.statMethod = statMethod;
	}


	public String getGraphMethod() {
		return graphMethod;
	}


	public void setGraphMethod(String graphMethod) {
		this.graphMethod = graphMethod;
	}


	@Override
	public String toString() {
		return "StatHelper [chosenCalendar=" + chosenCalendar + ", statMethod=" + statMethod + ", graphMethod="
				+ graphMethod + "]";
	}
	
	
	
}
