package com.example.calendar.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@Entity 
@Table(name = "UserTable")
public class User {

	@Column(name = "Username")
	private String username;
	
	@Column(name = "Password")
	private String password;
	
	@Transient
    private String passwordConfirm;
	
	@ManyToMany
	private Set<Role> roles;
	 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_u")
	private int user_ID;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	private Collection<Calendar> calendar = new ArrayList<Calendar>();
	
	@Column(name = "activeCalendar")
	private String activeCalendar = "";
	
	
	public User() {
		
	}
	
	public User(String username, String password) {
		setUsername(username);
		setPassword(password);
	}

	
	public void addCalendar(Calendar newCal)
	{
		newCal.setUser(this);
		calendar.add(newCal);
	}
	
	//Set & get

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getUser_ID() {
		return user_ID;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Collection<Calendar> getCalendar() {
		return calendar;
	}

	public void setCalendar(Collection<Calendar> calendar) {
		this.calendar = calendar;
	}

	public String getActiveCalendar() {
		return activeCalendar;
	}

	public void setActiveCalendar(String activeCalendar) {
		this.activeCalendar = activeCalendar;
	}
	


	@Override
	public String toString() {
		return "ID= " + user_ID +" " +username + " password=" + password + "activeCal: " + activeCalendar;
	}
}
