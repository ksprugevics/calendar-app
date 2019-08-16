package com.example.calendar;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;
import com.example.calendar.models.EventType;

public class EventUnitTest {

	
	@Test
	public void eventConstructorTest()
	{
		Event e = new Event("1. events", "desc desc", LocalDate.now(), LocalTime.now(), EventType.Birthday);
		
		
		assertEquals("1. events", e.getName());
		assertEquals("desc desc", e.getDescription());
		assertEquals(LocalDate.now(), e.getStartDate());
		assertEquals(LocalTime.now().getHour(), e.getStartTime().getHour());
		assertEquals(LocalTime.now().getMinute(), e.getStartTime().getMinute());
	}
	
	@Test
	public void calendarAddToEventTest()
	{
		Calendar c1 = new Calendar();
		Event e = new Event();
		e.setCalendar(c1);
		
		
		assertEquals(c1, e.getCalendar());
	}
}
