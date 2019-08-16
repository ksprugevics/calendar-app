package com.example.calendar.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;

@Repository
public interface EventRepo extends CrudRepository<Event, Integer> {
	ArrayList<Event> findAllByCalendar(Calendar calendar);
	ArrayList<Event> findAllByStartDate(LocalDate startDate);
	Event findByCalendarAndStartDateAndStartTime(Calendar calendar, LocalDate startDate, LocalTime startTime);
}
