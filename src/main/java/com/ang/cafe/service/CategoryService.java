package com.ang.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ang.cafe.POJO.Category;

public interface CategoryService {

	ResponseEntity<String> addNewCategory(Map<String, String> requestMap);
	
	ResponseEntity<List<Category>> getAllCategory(String filterValue);
	
	ResponseEntity<String> updateCategory(Map<String, String> requestMap);
	 
	ResponseEntity<String> deleteCategory(Integer categoryId);
	
}
