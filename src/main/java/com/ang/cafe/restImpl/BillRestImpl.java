package com.ang.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ang.cafe.POJO.Bill;
import com.ang.cafe.constants.CafeConstants;
import com.ang.cafe.rest.BillRest;
import com.ang.cafe.service.BillService;
import com.ang.cafe.utils.CafeUtils;

@RestController
public class BillRestImpl implements BillRest{

	@Autowired
	BillService billService;
	
	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		try {
			return billService.getBills();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Bill>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		try {
			return billService.getPdf(requestMap);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<byte[]>(CafeConstants.SOMETHING_WENT_WRONG.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			return billService.deleteBill(id);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
