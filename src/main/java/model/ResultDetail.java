package model;

import java.sql.Date;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name="ResultDetail")
public class ResultDetail 
{
	@PenguinAttribute(name="questionId")
	private Question question;
	@PenguinAttribute(name="optionId")
	private Option option;
	
	
	public ResultDetail(Question question, Option option) {
		super();
		this.question = question;
		this.option = option;
	}


	public Question getQuestion() {
		return question;
	}


	public void setQuestion(Question question) {
		this.question = question;
	}


	public Option getOption() {
		return option;
	}


	public void setOption(Option option) {
		this.option = option;
	}
	
	
	

}
