package com.example.calendar.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity 
@Table(name = "EventTable")
public class Event{

	@Column(name = "Name")
	private String name;
	
	@Column(name = "Description")
	private String description;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "StartDate")
	private LocalDate startDate;
	
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "StartTime")
	private LocalTime startTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_e")
	private int event_ID;
	
	@Column(name = "EventType")
	private EventType eventType;
	
	@ManyToOne
	@JoinColumn(name = "ID_c")
	private Calendar calendar;

	

	//Constructor, all parameters
	public Event(String name, String description, LocalDate startDate, LocalTime startTime, EventType eventType) {
		setName(name);
		setDescription(description);
		setStartDate(startDate);
		setStartTime(startTime);
		this.eventType = eventType;
	}



	//Set & Get
	public String getName() {
		return name;
	}
	
	//Name can be contain any symbols.
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	//Description can contain any symbols
	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	//Cannot be previous date
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;	
	}


	public int getEvent_ID() {
		return event_ID;
	}

	public EventType getEventType() {
		return eventType;
	}

	//Verification not needed
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}


	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {

			this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "Event [name=" + name + ", description=" + description + ", startDate=" + startDate + ", startTime="
				+ startTime + ", event_ID=" + event_ID + ", eventType=" + eventType + ", isAllDayEvent="
				+ "]";
	}

	public Event() {
		super();
	}

	
    public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

    
}
