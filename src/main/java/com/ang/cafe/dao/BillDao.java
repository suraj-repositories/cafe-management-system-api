package com.ang.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ang.cafe.POJO.Bill;

public interface BillDao extends JpaRepository<Bill, Integer>{

	List<Bill> getAllBills();
	
	List<Bill> getBillsByUserName(@Param("username") String username);
	
}
