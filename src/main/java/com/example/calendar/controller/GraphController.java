package com.example.calendar.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;
import com.example.calendar.helpers.StatHelper;
import com.example.calendar.models.User;
import com.example.calendar.repo.CalendarRepo;
import com.example.calendar.repo.EventRepo;
import com.example.calendar.repo.UserRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class GraphController {

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	CalendarRepo calendarRepo;
	
	@Autowired
	EventRepo eventRepo;
	
	
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
	
	@GetMapping(value="/stat")
	public String statGet(Model models)
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
			
		
		
		return "statistics";
	}

	
	@GetMapping(value="/graph")
	public String graphGet(Model models)
	{
		
		User currentUser = findCurrentUser();
		
		try
		{
			calendarRepo.findAllByUser(currentUser).get(0);
		}
		catch(Exception e)
		{
			return "redirect:/add-new-calendar";
		}
		
		
		ArrayList<Calendar> calList = calendarRepo.findAllByUser(currentUser);
		ArrayList<Integer> eventCounter = new ArrayList<>();
		
		
		for (Calendar c : calList) {
			int temp = 0;
			if(c.getEvent().isEmpty())
			{
				eventCounter.add(0);
				continue;
			}
			for (@SuppressWarnings("unused") Event event : eventRepo.findAllByCalendar(c)) {
				temp++;

			}
			eventCounter.add(temp);
		}

		JsonArray eventArray = new JsonArray();
		
		for (int i = 0; i < calList.size(); i++) {
			JsonObject eventJson = new JsonObject();
			eventJson.addProperty("name", calList.get(i).getName());
			eventJson.addProperty("eventCount", eventCounter.get(i));
			eventArray.add(eventJson);
		}
		
		
		
		
		models.addAttribute("allEvents", eventArray);
		return "graph";
	}
	
	
	
	
	
	@GetMapping(value = "/choose-cal")
	public String chooseCalGet(Model model, StatHelper statHelper, String calName)
	{
	
		User currUser = findCurrentUser();
		
		//If user doesnt have calendar - redirected to create
		
		try
		{
			calendarRepo.findAllByUser(currUser);
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
		
		
		
		ArrayList<String> graphTypes = new ArrayList<>();
		graphTypes.add("bar");
		graphTypes.add("doughnut");
		graphTypes.add("column");
		graphTypes.add("line");
		
		
		
		ArrayList<String> analysisTypes = new ArrayList<>();
		analysisTypes.add("Analysis by day");
		analysisTypes.add("Analysis by month");
		
		
		
		model.addAttribute("analysisTypes", analysisTypes);
		model.addAttribute("graphTypes", graphTypes);
		model.addAttribute("calendarList", calendarArray);
		return "chooseCal";
	}
	
	@PostMapping(value ="/choose-cal")
	public String chooseCalPost(Model models, StatHelper statHelper,  String calName)
	{	
		statHelper.setChosenCalendar(calName);
		
		
		User currUser = findCurrentUser();
		try
		{
			calendarRepo.findAllByUser(currUser);
		}
		catch(Exception e)
		{
			return "redirect:/add-new-calendar";
		}
		
		
		
		
		
		Calendar chosenCalendar = calendarRepo.findByNameAndUser(calName, currUser);
		//Checks if calendar has events
		try
		{
			eventRepo.findAllByCalendar(chosenCalendar);
		}
		catch(Exception e)
		{
			return "redirect:/choose-cal";
		}

		ArrayList<Event> allEventsInCal = eventRepo.findAllByCalendar(chosenCalendar);
		
		
		if(statHelper.getStatMethod().equals("Analysis by month"))
		{

			ArrayList<Integer> eventCountInMonths = new ArrayList<>();
			LocalDate now = LocalDate.now();
			
			
			for (int i = 1; i <= 12; i++) {
				int temp = 0;
				
				for (Event e : allEventsInCal) {
					if(e.getStartDate().getMonthValue() == i && now.getYear() == e.getStartDate().getYear())
					{
						temp++;
					}
				}
				eventCountInMonths.add(temp);
			}
			
			ArrayList<String> monthNames = new ArrayList<>();
			monthNames.add("January");
			monthNames.add("February");
			monthNames.add("March");
			monthNames.add("April");
			monthNames.add("May");
			monthNames.add("June");
			monthNames.add("July");
			monthNames.add("August");
			monthNames.add("September");
			monthNames.add("October");
			monthNames.add("November");
			monthNames.add("December");
			
			JsonArray eventArray = new JsonArray();
			
			for (int i = 0; i < eventCountInMonths.size(); i++) {
				JsonObject eventJson = new JsonObject();
				eventJson.addProperty("name", monthNames.get(i));
				eventJson.addProperty("eventCount", eventCountInMonths.get(i));
				eventArray.add(eventJson);
			}
			String title = "Event count each month in calendar: " + statHelper.getChosenCalendar();
			models.addAttribute("name", title);
			models.addAttribute("allEvents", eventArray);
			
		}
		else if(statHelper.getStatMethod().equals("Analysis by day"))
		{
			ArrayList<Integer> eventCountThisMonth = new ArrayList<>();
			
			
			LocalDate now = LocalDate.now();
			
			for (int i = 1; i <= now.lengthOfMonth(); i++) {
				int temp = 0;
				
				for (Event e : allEventsInCal) {
					if(e.getStartDate().getDayOfMonth() == i && now.getYear() == e.getStartDate().getYear())
					{
						temp++;
					}
				}
				eventCountThisMonth.add(temp);
			}
			
			JsonArray eventArray = new JsonArray();
			
			for (int i = 0; i < eventCountThisMonth.size(); i++) {
				JsonObject eventJson = new JsonObject();
				eventJson.addProperty("name", i+1);
				eventJson.addProperty("eventCount", eventCountThisMonth.get(i));
				eventArray.add(eventJson);
			}
			
			
			
			String title = "Event count this month by days: " + statHelper.getChosenCalendar();
			models.addAttribute("name", title);		
			models.addAttribute("allEvents", eventArray);
		}
		
		models.addAttribute("graphType", statHelper.getGraphMethod());
		return "stats";
	}
	
}
