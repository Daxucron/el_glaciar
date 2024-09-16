package model;

import java.sql.Date;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name="Result")
public class Result 
{
	@PenguinAttribute(penguinKey=true)
	private int resultId;
	@PenguinAttribute(foreignKey=User.class, name = "userId")
	private User user;
	@PenguinAttribute(foreignKey=Exam.class, name = "examId")
	private Exam exam;
	private Date execution_date;
	private float score;
	
	public Result() {}
	public Result(int resultId, User user, Exam exam, Date execution_date, float score) {
		super();
		this.resultId = resultId;
		this.user = user;
		this.exam = exam;
		this.execution_date = execution_date;
		this.score = score;
	}
	
	public int getResultId() {
		return resultId;
	}
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Exam getExam() {
		return exam;
	}
	public void setExam(Exam exam) {
		this.exam = exam;
	}
	public Date getExecution_date() {
		return execution_date;
	}
	public void setExecution_date(Date execution_date) {
		this.execution_date = execution_date;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
	
}
