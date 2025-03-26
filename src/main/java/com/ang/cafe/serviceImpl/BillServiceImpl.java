package com.ang.cafe.serviceImpl;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ang.cafe.JWT.JwtFilter;
import com.ang.cafe.POJO.Bill;
import com.ang.cafe.constants.CafeConstants;
import com.ang.cafe.dao.BillDao;
import com.ang.cafe.service.BillService;
import com.ang.cafe.utils.CafeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

	@Autowired
	BillDao billDao;

	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		log.info("inside generate request service impl");
		try {
			String fileName;
			if (validateRequestMap(requestMap)) {
				if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
					fileName = (String) requestMap.get("uuid");
				} else {
					fileName = CafeUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}

				String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: "
						+ requestMap.get("contactNumber") + "\n" + "Email: " + requestMap.get("email") + "\n"
						+ "Payment Method: " + requestMap.get("paymentMethod");
				Document document = new Document();
				PdfWriter.getInstance(document,
						new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));

				document.open();
				setRectangleInPdf(document);

				Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);

				Paragraph paragraph = new Paragraph(data + "\n\n", getFont("Data"));
				document.add(paragraph);

				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeaders(table);

				JSONArray jsonArray = CafeUtils.jsonArrayFromString(((String) requestMap.get("productDetails")).trim());
				
				for (int i = 0; i < jsonArray.length(); i++) {
					addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
				} 
				document.add(table);
				Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
						+ "Thank you for visiting. Please visit again!", getFont("Data"));
				document.add(footer);
				document.close();

				return new ResponseEntity<String>("\"uuid\" : \"" + fileName + "\"", HttpStatus.OK);
			}
			return CafeUtils.getResponseEntity("Required data not found.", HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		log.info("Inside addRows");
		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quality"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("totalAmount")));
	}

	private void addTableHeaders(PdfPTable table) {
		log.info("inside addTableHeaders");
		Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setBorderWidth(2);
			cell.setPhrase(new Phrase(columnTitle));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		});

	}

	private Font getFont(String type) {
		log.info("inside getFont");
		switch (type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
		default:
			return new Font();
		}
	}

	private void setRectangleInPdf(Document document) throws DocumentException {
		log.info("inside setRectangleInPdf");
		Rectangle rect = new Rectangle(577, 825, 18, 15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		document.add(rect);

	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String) requestMap.get("name"));
			bill.setEmail((String) requestMap.get("email"));
			bill.setContactNumber((String) requestMap.get("contactNumber"));
			bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
			bill.setTotalAmount(Integer.parseInt((String) requestMap.get("totalAmount")));
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("paymentMethod")
				&& requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		try {
			List<Bill> bills = new ArrayList<>();
			if(jwtFilter.isAdmin()) {
				bills = billDao.getAllBills();
			}else {
				bills = billDao.getBillsByUserName(jwtFilter.getCurrentUser());
			}
			return new ResponseEntity<List<Bill>>(bills, HttpStatus.OK);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Bill>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		log.info("inside getPdf : requestMap {}", requestMap);
		try {
			byte[] byteArray = new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
				return new ResponseEntity<byte[]>(byteArray, HttpStatus.BAD_REQUEST);
			}
			String filePath = CafeConstants.STORE_LOCATION+"\\"+(String)requestMap.get("uuid") + ".pdf";
			
			if(CafeUtils.isFileExists(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);
			}else {
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<byte[]>(CafeConstants.SOMETHING_WENT_WRONG.getBytes(), HttpStatus.OK);
	}

	private byte[] getByteArray(String filePath) throws Exception {
		return Files.readAllBytes(Paths.get(filePath));
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional<Bill> optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				billDao.deleteById(id);
				return CafeUtils.getResponseEntity("Bill deleted successfully!", HttpStatus.OK);
			}
			return CafeUtils.getResponseEntity("Bill id doesn't exist!", HttpStatus.OK);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
