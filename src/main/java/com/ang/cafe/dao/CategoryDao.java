package com.ang.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ang.cafe.POJO.Category;

public interface CategoryDao extends JpaRepository<Category, Integer>{

	List<Category> getAllCategory();
}
