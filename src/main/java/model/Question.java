package model;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name="Question")
public class Question 
{
	@PenguinAttribute(penguinKey = true, autoIncrement=true)
	private int questionId;	
	private String question;
	private int score;
	private float penalty;
	@PenguinAttribute(foreignKey=Exam.class)
	private int examId;
	
	private Option[] options;
	
	public Question() {}
	public Question(int questionId, String question, int score, float penalty, int examId) {
		super();
		this.questionId = questionId;
		this.question = question;
		this.score = score;
		this.penalty = penalty;
		this.examId = examId;
		this.options = new Option[0];
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public float getPenalty() {
		return penalty;
	}

	public void setPenalty(float penalty) {
		this.penalty = penalty;
	}

	public int getExamId() {
		return examId;
	}

	public void setExamId(int examId) {
		this.examId = examId;
	}
	
	public void setOptions(Option[] options)
	{
		this.options = options;
	}
	
	
	
}
