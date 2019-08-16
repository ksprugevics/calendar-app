package com.example.calendar.repo;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.User;

@Repository
public interface CalendarRepo extends CrudRepository<Calendar, Integer>{
	ArrayList<Calendar> findAllByUser(User user);
	Calendar findByNameAndUser(String name, User user);
	Calendar findByName(String name);
}
