package com.example.calendar.controller;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;
import com.example.calendar.models.EventType;
import com.example.calendar.models.User;
import com.example.calendar.repo.CalendarRepo;
import com.example.calendar.repo.EventRepo;
import com.example.calendar.repo.UserRepo;
import com.example.calendar.validator.EventValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import javax.validation.Valid;

@Controller
public class EventController {


	
	@Autowired
	EventRepo eventRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	CalendarRepo calendarRepo;
	
	@Autowired
	private EventValidator eventValidator;

	public User findCurrentUser()
	{
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username = "";
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
		
		User currUser = userRepo.findByUsername(username);
		return currUser;
	}
	
	
	
	@GetMapping(value = "/add-new-event")
	public String addNewEventGet(Model models, Event event, String calName)
	{
		List<EventType> allEventTypes = Arrays.asList(EventType.values());
		
		User currUser = findCurrentUser();
		
		//If user doesnt have calendar - redirected to create
		
		try
		{
			calendarRepo.findAllByUser(currUser).get(0);
		}
		catch(Exception e)
		{
			return "redirect:/add-new-calendar";
		}
			
		
		ArrayList<Calendar> allUserCalendars = calendarRepo.findAllByUser(currUser);
		
		JsonArray calendarArray = new JsonArray();
		
		
		for (int i = 0; i < allUserCalendars.size(); i++) {
			JsonObject calendarJson = new JsonObject();
			calendarJson.addProperty("title", allUserCalendars.get(i).getName());
				calendarArray.add(calendarJson);
		}
		
		models.addAttribute("calendarList", calendarArray);
		models.addAttribute("eventTypes", allEventTypes);
		return "addNewEvent";
	}
	
	@PostMapping(value = "/add-new-event")
	public String addNewEventPost(@Valid Event event, BindingResult bindingResult, String calName, Model models)
	{
		
		
		//Gets all the calendars and eventTypes again and sends them to view.
		//--
		List<EventType> allEventTypes = Arrays.asList(EventType.values());
		User currUser = findCurrentUser();
			
		ArrayList<Calendar> allUserCalendars = calendarRepo.findAllByUser(currUser);
		JsonArray calendarArray = new JsonArray();
		
		for (int i = 0; i < allUserCalendars.size(); i++) {
			JsonObject calendarJson = new JsonObject();
			calendarJson.addProperty("title", allUserCalendars.get(i).getName());
				calendarArray.add(calendarJson);
		}
		
		models.addAttribute("calendarList", calendarArray);
		models.addAttribute("eventTypes", allEventTypes);
		
		//--
	
		
		Calendar c1 = calendarRepo.findByNameAndUser(calName, currUser);
		if(c1 == null)
		{
			c1 = calendarRepo.findByNameAndUser(currUser.getActiveCalendar(), currUser);
		}
		event.setCalendar(c1);
	
		eventValidator.validate(event, bindingResult);
		
        if (bindingResult.hasErrors()) {
            return "addNewEvent";
        }
    


		eventRepo.save(event);

		return "redirect:/calendar";
	}

	
}
