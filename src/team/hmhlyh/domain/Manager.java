package team.hmhlyh.domain;

import javax.sql.rowset.serial.SerialBlob;

/**
 * Model模型层，Manager实体类(JavaBean)
 * @author 123
 *
 */
public class Manager {

	private Long id;
	private String password;
	private String name;
	private String sex;
	private String province;
	private String telephone;
	private SerialBlob picURL;
	
	public SerialBlob getPicURL() {
		return picURL;
	}
	public void setPicURL(SerialBlob picURL) {
		this.picURL = picURL;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
