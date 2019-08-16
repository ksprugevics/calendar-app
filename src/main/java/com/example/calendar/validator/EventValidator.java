package com.example.calendar.validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;
import com.example.calendar.repo.CalendarRepo;
import com.example.calendar.repo.EventRepo;
import com.example.calendar.repo.UserRepo;

@Component
public class EventValidator implements Validator {
	
	@Autowired
	EventRepo eventRepo;
	
	@Autowired
	CalendarRepo calendarRepo;
	
	@Autowired
	UserRepo userRepo;
	
	
	
    @Override
    public boolean supports(Class<?> aClass) {
        return Event.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Event e = (Event) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        
        
        if (e.getStartDate() == null)
        {
        	errors.rejectValue("startDate", "DateEmpty");
        	return;
        }
        
        if(e.getStartTime() == null)
        {
        	errors.rejectValue("startTime", "DateInPast");
        	return;
        }
        
        if (e.getStartDate().isBefore(LocalDate.now())) {
            errors.rejectValue("startDate", "DateInPast");
            return;
        }
        

        if(e.getStartDate().equals(LocalDate.now()) && e.getStartTime().isBefore(LocalTime.now()))
		{
			errors.rejectValue("startDate", "DateInPast");
			return;
		}

        
        //Check if there's an event at that time.
		Calendar c1 = e.getCalendar();

        ArrayList<Event> allUserEvents = eventRepo.findAllByCalendar(c1);
        
        for (Event event : allUserEvents) {
			if(e.getStartDate().equals(event.getStartDate()) && e.getStartTime().equals(event.getStartTime()))
			{

				errors.rejectValue("startDate", "DateTaken");
				break;
			}
		}
        
    }
}