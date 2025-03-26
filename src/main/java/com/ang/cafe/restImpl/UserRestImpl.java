package com.ang.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ang.cafe.constants.CafeConstants;
import com.ang.cafe.rest.UserRest;
import com.ang.cafe.service.UserService;
import com.ang.cafe.utils.CafeUtils;
import com.ang.cafe.wrapper.UserWrapper;

@RestController
public class UserRestImpl implements UserRest{

	@Autowired
	UserService userService;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		try {
			return userService.signUp(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {

		try {
			return userService.login(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			return userService.getAllUser();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		
		try {
			return userService.update(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> checkToken() {
		try {
			return userService.checkToken();
		}catch(Exception ex) {
			ex.printStackTrace();
		}	
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try{
			return userService.changePassword(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<String> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap){
		try {
			return userService.forgetPassword(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}	
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
