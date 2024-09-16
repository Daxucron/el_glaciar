package glaciar;

public class PenguinField {
	
	private String name;
	private Object value;
	private boolean penguinKey;
	private boolean unique;
	
    boolean autoIncrement;
    Class<?> foreignKey;
    //boolean isEntity;
    
	
	public PenguinField(String name, Object value, boolean penguinKey, boolean unique, boolean autoincrement, Class<?> foreignKey) {
		super();
		this.name = name;
		this.value = value;
		this.penguinKey = penguinKey;
		this.unique = unique;
		this.autoIncrement = autoincrement;
		this.foreignKey = foreignKey;
		
		//System.out.println(toString());
		//this.isEntity = isEntity;
	}
	public PenguinField(String name, Object value) {
		this.name = name;
		this.value = value;
		this.penguinKey = false;
		this.unique = false;
		this.autoIncrement = false;
		this.foreignKey = Void.class;
		//this.isEntity = false;
	}
	
	public String getAsignation()
	{
		return name + " = " + getValueString();
	}
	
	public String getVoidAsignation()
	{
		return name + " = ?";
	}

	public String getName() {
		return name;
	}

	public boolean isPenguinKey() {
		return penguinKey;
	}
	
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isUnique() {
		return unique;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getValueString() {
		if (value instanceof String)
		{
			return "'"+value+"'";
		}
		return value.toString();
	}
	
	public Class<?> getForeignKey()
	{
		return foreignKey;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPenguinKey(boolean penguinKey) {
		penguinKey = penguinKey;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("\nNombre = ").append(name);
		msg.append("\nValue = ").append(value);
		msg.append("\nPenguinKey = ").append(penguinKey);
		msg.append("\nunique = ").append(unique);
		msg.append("\nautoincrement = ").append(autoIncrement);
		msg.append("\nfk = ").append(foreignKey);
		
		return msg.toString();
	}	

}
