package model;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name="PossibleOption")
public class Option
{
	@PenguinAttribute(penguinKey=true, autoIncrement=true)
	private int PossibleOptionId;
	private String optionText;
	private boolean isCorrect;
	@PenguinAttribute(foreignKey = Question.class)
	private int questionId;
	
	public Option(int possibleOptionId, String optionText, boolean isCorrect, int questionId) {
		super();
		PossibleOptionId = possibleOptionId;
		this.optionText = optionText;
		this.isCorrect = isCorrect;
		this.questionId = questionId;
	}


	public int getPossibleOptionId() {
		return PossibleOptionId;
	}


	public void setPossibleOptionId(int possibleOptionId) {
		PossibleOptionId = possibleOptionId;
	}


	public String getOptionText() {
		return optionText;
	}


	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}


	public boolean isCorrect() {
		return isCorrect;
	}


	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}


	public int getQuestionId() {
		return questionId;
	}


	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	
	
	
	
}
