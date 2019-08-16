package com.example.calendar.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.calendar.models.User;

@Repository
public interface UserRepo extends CrudRepository<User, Integer>{
	User findByUsername(String username);
}
