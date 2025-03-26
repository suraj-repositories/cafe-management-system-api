package com.ang.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.ang.cafe.POJO.User;
import com.ang.cafe.wrapper.UserWrapper;

import jakarta.transaction.Transactional;

public interface UserDao extends JpaRepository<User, Integer>{

	User findByEmailId(@Param("email") String email);
	
	List<UserWrapper> getAllUser();
	
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status") String status, @Param("id") Integer Id);

	List<String> getAllAdmin();
	
	User findByEmail(String email);
}
