package model;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name = "Category")
public class Category 
{
	@PenguinAttribute(penguinKey = true, autoIncrement= true)
	private int categoryId;
	private String name;
	
	public Category() {}
	public Category(int categoryId, String name) 
	{
		super();
		this.categoryId = categoryId;
		this.name = name;
	}	
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("id = " + categoryId).append("\n")
		.append("Nombre categoría = " + name).append("\n");
		return msg.toString();
	}

}
