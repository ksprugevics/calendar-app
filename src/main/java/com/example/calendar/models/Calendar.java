package com.example.calendar.models;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity 
@Table(name = "CalendarTable")
public class Calendar {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_c")
	private int calendar_ID;
	
	@ManyToOne
	@JoinColumn(name="ID_u")
	private User user;
	
	@OneToMany(mappedBy = "calendar")
	private Collection<Event> event;

	@Column(name="Name")
	private String name;

	public Calendar() {

	}
	public Calendar(String name)
	{
		this.name = name;
	}
	
	public void addNewEvent(Event newEvent)
	{
		
		event.add(newEvent);
	}
	
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Collection<Event> getEvent() {
		return event;
	}
	
	public void setEvent(Collection<Event> event) {
		this.event = event;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCalendar_ID() {
		return calendar_ID;
	}

	@Override
	public String toString() {
		return "Cal Name: " + name+ " ID = " + calendar_ID + " user " + user.getUsername() + " events: " + event;
	}
	
}

	

