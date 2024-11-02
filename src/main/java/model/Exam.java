package model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name = "Exam")
public class Exam {
	@PenguinAttribute(penguinKey=true, autoIncrement=true)
	private int examId;
	@PenguinAttribute(ignore = true)
	private Category category;
	private int categoryId;
	private int creatorId;
	private LocalDateTime creation_date;
	private String level;
	//TODO: Comprobar que uno de los atributos es una coleccion de elementos tipo PenguinEntity y tratarlos como tal. Enlazar el elemtno con una clave foranea.
	private List<Question> questions = new ArrayList<>();
	//private Question[] questions = null;
	
	public Exam() {}
	public Exam(int examId, Category category, int creatorId, LocalDateTime creation_date, String level) {
		super();
		this.examId = examId;
		this.category = category;
		this.categoryId = category.getCategoryId();
		this.creatorId = creatorId;
		this.creation_date = creation_date;
		this.level = level;
	}
	
	public void addQuestion(Question question)
	{
		questions.add(question);
		question.setExamId(examId);
	}
	
	public int getExamId() {
		return examId;
	}
	public void setExamId(int examId) {
		this.examId = examId;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCategoryId(int value) {
		categoryId = value;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public LocalDateTime getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(LocalDateTime creation_date) {
		this.creation_date = creation_date;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}	
	
	
	
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	public int getCategoryId() {
		return categoryId;
	}
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("ExamId = " + examId);
		
		return msg.toString();
	}

}
