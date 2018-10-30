package com.sekhar.pdfextractor.beans;


public class Voter {
	
	
	String voterID;
	String userName;
	String dependentName;
	String dependentType;
	String age;	
	String houseNumber;
	String gender;
	boolean validCard;
	String pageNumber;
	
	

	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public boolean isValidCard() {
		return validCard;
	}
	public void setValidCard(boolean validCard) {
		this.validCard = validCard;
	}
	public String getVoterID() {
		return voterID;
	}
	public void setVoterID(String voterID) {
		this.voterID = voterID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDependentName() {
		return dependentName;
	}
	public void setDependentName(String dependentName) {
		this.dependentName = dependentName;
	}
	public String getDependentType() {
		return dependentType;
	}
	public void setDependentType(String dependentType) {
		this.dependentType = dependentType;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	@Override
	public String toString() {
		return "Voter [voterID=" + voterID + ", userName=" + userName + ", dependentName=" + dependentName
				+ ", dependentType=" + dependentType + ", age=" + age + ", houseNumber=" + houseNumber + ", gender="
				+ gender + ", validCard=" + validCard + "]";
	}
	
	
	
	
}
