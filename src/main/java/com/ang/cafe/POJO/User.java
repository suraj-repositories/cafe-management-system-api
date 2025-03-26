package com.ang.cafe.POJO;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;

@NamedQuery(name = "User.findByEmailId", query="select u from User u WHERE u.email=:email")

@NamedQuery(name = "User.getAllUser", query="select new com.ang.cafe.wrapper.UserWrapper(u.id, u.name, u.email, u.contactNumber, u.status) from User u where u.role='user'")

@NamedQuery(name = "User.updateStatus", query="update User u set u.status=:status where u.id=:id")

@NamedQuery(name = "User.getAllAdmin", query="select u.email from User u where u.role='admin'")

@Data 			// this create the getters, setters and constructor for us
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "contactNumber")
	private String contactNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "role")
	private String role;

}
