package com.ang.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ang.cafe.JWT.CustomerUserDetailService;
import com.ang.cafe.JWT.JwtFilter;
import com.ang.cafe.JWT.JwtUtil;
import com.ang.cafe.POJO.User;
import com.ang.cafe.constants.CafeConstants;
import com.ang.cafe.dao.UserDao;
import com.ang.cafe.service.UserService;
import com.ang.cafe.utils.CafeUtils;
import com.ang.cafe.utils.EmailUtils;
import com.ang.cafe.wrapper.UserWrapper;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailService customerUserDetailService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailUtils emailUtils;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("inside signup {}", requestMap);

		try {
			if (validateSignUpMap(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email"));

				if (Objects.isNull(user)) {
					User mappedUser = getUserFromMap(requestMap);
					mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
					userDao.save(mappedUser);
					return CafeUtils.getResponseEntity("Successfully Registered!", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("Email already exists!", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
				&& requestMap.containsKey("password")) {
			return true;
		}
		return false;

	}

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("inside login {}", requestMap);
		
		try {
		 
		    Authentication auth = authenticationManager.authenticate(
		            new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
		    
		    if (auth.isAuthenticated()) {
		       
		        com.ang.cafe.POJO.User userDetail = customerUserDetailService.getUserDetail(requestMap.get("email"));
		        if (userDetail != null && "true".equalsIgnoreCase(userDetail.getStatus())) {
		           
		            return new ResponseEntity<String>("{\"token\" : \"" + 
		                    jwtUtil.generateToken(userDetail.getEmail(), userDetail.getRole()) + "\"}", HttpStatus.OK);
		        } else {
		            return CafeUtils.getResponseEntity("Wait for admin approval!", HttpStatus.BAD_REQUEST);
		        }
		    }
		} catch (Exception ex) {
		    log.error("{}", ex);
		}

		
		return CafeUtils.getResponseEntity("Wrong Credentials!", HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<List<UserWrapper>>(userDao.getAllUser(), HttpStatus.OK);
			}else {
				return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		log.info("exception occure");
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(), userDao.getAllAdmin());
					return CafeUtils.getResponseEntity("User Status Updated Successfully!", HttpStatus.OK);
				}else {
					CafeUtils.getResponseEntity("User Id doesn't Exist!", HttpStatus.OK);
				}
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String email, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		
		if(status!=null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER : " + email + "\n is approved by \n ADMIN : " + jwtFilter.getCurrentUser(), allAdmin);
		}else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER : " + email + "\n is disabled by \n ADMIN : " + jwtFilter.getCurrentUser(), allAdmin);
		}
		
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmail(jwtFilter.getCurrentUser());
			if(!user.equals(null)) {
				if(passwordEncoder.matches(requestMap.get("oldPassword"), user.getPassword())) {
					user.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
					userDao.save(user);
					return CafeUtils.getResponseEntity("Password Updated Successfully!", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
	    try {
	        User user = userDao.findByEmail(requestMap.get("email"));

	        if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
	            String password = String.format("%06d", new Random().nextInt(1_000_000));
	            user.setPassword(passwordEncoder.encode(password));

	            emailUtils.forgotMail(
	                user.getEmail(),
	                "Credentials For Cafe Management System",
	                password
	            );
	            userDao.save(user);

	                return CafeUtils.getResponseEntity("Check your email for credentials!", HttpStatus.OK);
	           
	        }

	        return CafeUtils.getResponseEntity("Invalid email address.", HttpStatus.BAD_REQUEST);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	

}
