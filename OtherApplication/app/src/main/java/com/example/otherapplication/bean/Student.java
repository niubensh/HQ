package com.example.otherapplication.bean;

public class Student {
	
	private String name;
	private int age;
	private String introduce;
	
	public Student(String name, int age, String introduce) {
		super();
		this.name = name;
		this.age = age;
		this.introduce = introduce;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
}
