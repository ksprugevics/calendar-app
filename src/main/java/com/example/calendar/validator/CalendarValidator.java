package com.example.calendar.validator;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.calendar.models.Calendar;
import com.example.calendar.models.Event;
import com.example.calendar.models.User;
import com.example.calendar.repo.CalendarRepo;
import com.example.calendar.repo.UserRepo;

@Component
public class CalendarValidator implements Validator {
	

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
        Calendar c = (Calendar) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        
        //Get current user
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username = "";
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
        
        User u1 = userRepo.findByUsername(username);
       
        ArrayList<Calendar> allUserCals = calendarRepo.findAllByUser(u1);
        if(allUserCals.isEmpty())
        {
        	return;
        }
        
        for (Calendar calendar : allUserCals) {
			if(calendar.getName().equals(c.getName()))
			{
				errors.rejectValue("name", "CalendarExists");
			}
        }
    }
}