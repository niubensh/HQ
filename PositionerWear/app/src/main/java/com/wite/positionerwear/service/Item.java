package com.wite.positionerwear.service;

import java.io.Serializable;

public class Item implements Serializable {
	
	private String name;
	private String number;
	
	public void setName(String Name) {
		this.name = Name;
	}

	public void setNumber(String Numbere) {
		this.number = Numbere;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNumber(){
		return number;
	}
}
