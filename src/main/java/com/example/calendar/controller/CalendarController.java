package com.example.calendar.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

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
import com.example.calendar.validator.CalendarValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class CalendarController {

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	CalendarRepo calendarRepo;
	
	@Autowired
	EventRepo eventRepo;
	
	@Autowired
	private CalendarValidator calendarValidator;
	

	//Helper method for finding the current User -> Used very often 
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
	
		
	
	
	@GetMapping({"/", "/calendar"})
	public String calendarGet(Model models, Event event)
	{

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
		
		Calendar c1 = calendarRepo.findByNameAndUser(currUser.getActiveCalendar(), currUser);

		
		
		//Get Events By Calendar
		ArrayList<Event> allEvents = eventRepo.findAllByCalendar(c1);
		
		
		//Info to fill out calendar.
		JsonArray eventArray = new JsonArray();
		
		for (int i = 0; i < allEvents.size(); i++) {
			JsonObject eventJson = new JsonObject();
			eventJson.addProperty("title", allEvents.get(i).getName());
			eventJson.addProperty("start", allEvents.get(i).getStartDate() + "T" + allEvents.get(i).getStartTime());
			eventJson.addProperty("description", allEvents.get(i).getDescription());
			eventJson.addProperty("type", allEvents.get(i).getEventType().name());
			eventArray.add(eventJson);
		}
		
		List<EventType> allEventTypes = Arrays.asList(EventType.values());
		
		models.addAttribute("calendarName", c1.getName());
		models.addAttribute("eventTypes", allEventTypes);
		models.addAttribute("currentUser", currUser);
		models.addAttribute("allEvents", eventArray);
		
		return "calendar";
	}
	
	
	@PostMapping(value="/calendar", params="action=edit")
	public String eventEdit(Model model, Event event, String oldDate){
		User currUser = findCurrentUser();

		String[] dateTimeParts = oldDate.split("T");
		LocalDate localDate = LocalDate.parse(dateTimeParts[0]);
		LocalTime localTime = LocalTime.parse(dateTimeParts[1]);
		Calendar c1 = calendarRepo.findByNameAndUser(currUser.getActiveCalendar(), currUser);
		Event e1 = eventRepo.findByCalendarAndStartDateAndStartTime(c1, localDate, localTime);
		
		
		
		
		//--Validations--
		String tempName = event.getName();
		if(tempName.equals(""))
		{
			tempName = e1.getName();
		}
		
		LocalDate tempDate = event.getStartDate();
		LocalTime tempTime = event.getStartTime();
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		
		ArrayList<Event> allEventsInUserCal = eventRepo.findAllByCalendar(c1);
			
		
		//If user has an event that time - keep previous date.
		for (Event e: allEventsInUserCal) {
			
			if(e.getStartDate().equals(tempDate) && e.getStartTime().equals(tempTime))
			{
				tempDate = e1.getStartDate();
				tempTime = e1.getStartTime();
			}
		}
		
		//Checks if date is not in the past.
		if(tempDate.isBefore(currentDate))
		{
			tempDate = e1.getStartDate();
			tempTime= e1.getStartTime();
		}
		else if(tempDate.equals(currentDate)  && tempTime.isBefore(currentTime))
		{
			tempDate = e1.getStartDate();
			tempTime= e1.getStartTime();
		}

		
		e1.setName(tempName);
		e1.setDescription(event.getDescription());
		e1.setStartDate(tempDate);
		e1.setStartTime(tempTime);
		e1.setEventType(event.getEventType());
		eventRepo.save(e1);
		
		return "redirect:/calendar";
	}
	
	@PostMapping(value="/calendar", params="action=delete")
	public String eventDelete(Model model, Event event, String oldDate){
		User currUser = findCurrentUser();

		String[] dateTimeParts = oldDate.split("T");
		LocalDate localDate = LocalDate.parse(dateTimeParts[0]);
		LocalTime localTime = LocalTime.parse(dateTimeParts[1]);
		Calendar c1 = calendarRepo.findByNameAndUser(currUser.getActiveCalendar(), currUser);
		Event e1 = eventRepo.findByCalendarAndStartDateAndStartTime(c1, localDate, localTime);

		eventRepo.delete(e1);
		
		return "redirect:/calendar";
	}
	
	@GetMapping(value="/add-new-calendar")
	public String addNewCalendarGet(Calendar calendar)
	{
		return "addNewCalendar";
	}
	
	@PostMapping(value="/add-new-calendar")
	public String addNewCalendarPost(@Valid Calendar calendar, BindingResult bindingResult)
	{
		calendarValidator.validate(calendar, bindingResult);
        if (bindingResult.hasErrors()) {
            return "addNewCalendar";
        }
        
		
		User currUser = findCurrentUser();
		
		calendar.setUser(currUser);
		calendarRepo.save(calendar);
		
		currUser.setActiveCalendar(calendar.getName());
		userRepo.save(currUser);
		return "redirect:/calendar";
	}
	

	@GetMapping(value = "/view-calendars")
	public String addNewEventGet(Model models, String calName)
	{
		
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
		return "viewCalendars";
	}
	
	@PostMapping(value="/view-calendars", params="action=edit")
	public String editCalendar(Model model, String currName, String newName){

		User currUser = findCurrentUser();
		
		if(newName.equals(""))
		{
			newName = currUser.getUsername() + LocalTime.now().getHour()+ LocalTime.now().getMinute();
		}
		

		
		//If calendar With the name exists
		if(calendarRepo.findByNameAndUser(newName, currUser) != null)
		{
			newName+="(1)";
		}
		
		System.out.println(newName);
		
		Calendar c1 = calendarRepo.findByNameAndUser(currName, currUser);
		System.out.println(c1);
		
		if(c1.getName().equals(currUser.getActiveCalendar()))
		{
			currUser.setActiveCalendar(newName);
			userRepo.save(currUser);
		}
		c1.setName(newName);
		calendarRepo.save(c1);

		return "redirect:/view-calendars";
	}
	
	
	
	@PostMapping(value="/view-calendars", params="action=delete")
	public String deleteCalendar(Model model, String calName){
		
		User currUser = findCurrentUser();
		Calendar c1 = calendarRepo.findByNameAndUser(calName, currUser);
		ArrayList<Event> allEvents = eventRepo.findAllByCalendar(c1);
		
		for (int i = 0; i < allEvents.size(); i++) {
			eventRepo.delete(allEvents.get(i));
		}
		calendarRepo.delete(c1);
		
		ArrayList<Calendar> allUserCals = calendarRepo.findAllByUser(currUser);
		
		currUser.setActiveCalendar(allUserCals.get(0).getName());
		userRepo.save(currUser);

		return "redirect:/view-calendars";
	}
	
	
	
	@PostMapping(value="/view-calendars", params="action=view")
	public String viewCalendar(Model model, String calName){
		User currUser = findCurrentUser();
		
		currUser.setActiveCalendar(calName);
		
		userRepo.save(currUser);
		return "redirect:/calendar";
	}
}
