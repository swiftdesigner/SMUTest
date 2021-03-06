package com.smu.service.impl;

import java.util.List;

import com.smu.dao.IStudentDAO;
import com.smu.service.IStudentService;
import com.smu.model.Student;
public class StudentService implements IStudentService{
private IStudentDAO studentDAO;

public IStudentDAO getStudentDAO() {
	return studentDAO;
}

public void setStudentDAO(IStudentDAO studentDAO) {
	this.studentDAO = studentDAO;
}
public Student checkStudent(String s_no){
	return studentDAO.checkStudent(s_no);
}
public List getStudentsByClass(String class_name){
	return studentDAO.getStudentsByClass(class_name);
}
public List getAllStudents(){
	return studentDAO.getAllStudents();
}
}
