package team.hmhlyh.domain;

import java.io.Serializable;

import javax.sql.rowset.serial.SerialBlob;
/**
 * Model模型层，Student实体类(JavaBean)
 * @author 123
 *
 */
public class Student implements Serializable{
	
	private Long id;
	private String name;
	private String password;
	private String sex;
	private String grade;
	private String major;
	private String telephone;
	private String className;
	private String educationLevel;
	private String province;
	private SerialBlob picURL;//图片属性
	
	public SerialBlob getPicURL() {
		return picURL;
	}
	public void setPicURL(SerialBlob picURL) {
		this.picURL = picURL;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getEducationLevel() {
		return educationLevel;
	}
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

}
