package com.sekhar.pdfextractor.beans;



enum Gender 
{ 
    MALE,FEMALE; 
}

enum DependentType 
{ 
    Father_Name, Husband_Name; 
} 


public class Voter {
	
	
	String voterID;
	String userName;
	String dependentName;
	String dependentType;
	int age;	
	String houseNumber;
	String gender;
	boolean validCard;

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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
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
	
	
	
	
}
