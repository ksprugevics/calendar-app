package com.example.calendar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.example.calendar.models.Calendar;

public class CalendarUnitTest {

	
	@Test
	public void testCalendarConstructor()
	{
		Calendar cal = new Calendar("test cal");
		
		assertEquals("test cal", cal.getName());
		
	}
}
