package com.ang.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ang.cafe.POJO.Category;
import com.ang.cafe.constants.CafeConstants;
import com.ang.cafe.rest.CategoryRest;
import com.ang.cafe.service.CategoryService;
import com.ang.cafe.utils.CafeUtils;

@RestController
public class CategoryRestImpl implements CategoryRest{

	@Autowired
	private CategoryService categoryService;
	
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		
		try {
			return categoryService.addNewCategory(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			return categoryService.getAllCategory(filterValue);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			return categoryService.updateCategory(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> deleteCategory(Integer categoryId) {
		try {
			System.out.println(categoryId);
			return categoryService.deleteCategory(categoryId);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
