package glaciar;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PenguinObject<T> {
	
	private String name;
	private PenguinField[] fields;
	private PenguinObject<?>[] penguinObjects;
	// Es necesario obtener dos tipos de info, la primera a que clase hace referencia (fácial) la segunda si se trata de una collection,
	// array o la clase en si. es posible que la lista de clases deba contener directamente si es array o collection, al momento de rellenar con esos nuevos datos
	//TODO: Crear un constructor aunque sea factorymethod que permita crear un po con uno generico, de ese modo podrias crearlo sin usar tanto el api.refl
	private Class<?>[] penguinObjectsClasses;
	
	private Class<T> clazz;
	private PenguinField penguinKey;
	private T obj = null;
	
	
	
	// Constructores  #############################################################################
	
	@SuppressWarnings("unchecked")
	public PenguinObject(T obj) {
		super();
		this.obj = obj;
		this.name = ReflexivePenguin.getEntityName(obj.getClass());
		this.fields = ReflexivePenguin.getPenguinFields(obj);
		this.penguinKey = detectPenguinKey();
		this.penguinObjects = ReflexivePenguin.getPenguinEntityFields(obj);
		this.penguinObjectsClasses = ReflexivePenguin.getRelationClasses(obj.getClass());
		
		// Comprobar si fields contiene una entity
		
		
		this.clazz = (Class<T>) obj.getClass();
	}
	
	
	
	public PenguinObject(Class<T> clazz, Object id)
	{
		this.name = ReflexivePenguin.getEntityName(clazz);
		this.fields = new PenguinField[]{ReflexivePenguin.getPenguinFieldPk(clazz,id)};
		this.penguinKey = detectPenguinKey();
		this.clazz = clazz;
		this.penguinObjectsClasses = ReflexivePenguin.getRelationClasses(clazz);
	}
	public PenguinObject(Class<T> clazz)
	{
		this.name = ReflexivePenguin.getEntityName(clazz);
		this.fields = ReflexivePenguin.getEmptyPenguinFields(clazz);
		this.penguinKey = detectPenguinKey();
		this.clazz = clazz;
		this.penguinObjectsClasses = ReflexivePenguin.getRelationClasses(clazz);
	}
	// ##############################################################################################
	
	private PenguinField detectPenguinKey() {
		for(PenguinField field: fields)
		{
			if(field.isPenguinKey())
			{
				return field;
			}
		}
		return null;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setFieldValue(String fieldName, Object value)
	{
		PenguinField field = getFieldByName(fieldName);
		field.setValue(value);
	}
	
	public boolean isPenguinKeyAutoIncrement()
	{
		return penguinKey.isAutoIncrement();
	}
	
	public Object getPenguinKeyValue()
	{
		return penguinKey.getValue();
	}
	
	public void setPenguinKey(Object value)
	{
		penguinKey.setValue(value);
		ReflexivePenguin.setPenguinKey(obj,value);
		// Cambiar el de los hijos su foreignkey (los hijos son los penguinObjects)
		for(PenguinObject<?> po: penguinObjects)
		{
			po.setForeignKey(clazz, value);
		}
	}
	
	private void setForeignKey(Class<?> clazz, Object value)
	{
		for(PenguinField field: fields)
		{
			if(field.getForeignKey().equals(clazz))
			{
				field.setValue(value);
				ReflexivePenguin.setForeignKey(obj,clazz,value);
			}
		}
		
	}
	
	public String getWhereId()
	{
		for(PenguinField field: fields)
		{
			if(field.isPenguinKey())
			{
				return field.getAsignation();
			}			
		}
		return null;
	}
	
	public String getVoidSets()
	{
		StringBuilder msg = new StringBuilder();
	    
	    for (PenguinField field : fields) 
	    {
	        if (msg.length() > 0) 
	        {
	            msg.append(", ");
	        }
	        if (!field.isPenguinKey()) 
	        {
	            msg.append(field.getVoidAsignation());
	        }
	    }	    
	    return msg.toString();
	}
	
	public String getSet()
	{
		StringBuilder msg = new StringBuilder();
	    
	    for (PenguinField field : fields) 
	    {
	        if (msg.length() > 0) 
	        {
	            msg.append(", ");
	        }
	        if (!field.isPenguinKey()) 
	        {
	            msg.append(field.getAsignation());
	        }
	    }	    
	    return msg.toString();
	}
	
	public String getAsignation(String fieldName)
	{
		PenguinField field = getFieldByName(fieldName); 
		return field.getAsignation();
	}
	
	private PenguinField getFieldByName(String fieldName)
	{
		for (PenguinField field : fields) 
	    {
	        if(fieldName.equals(field.getName()))
	        {
	        	return field;
	        }
	    }	 
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> T[] resultSetToObject(ResultSet rs)
	{		
		return (T[]) ReflexivePenguin.resultSetToObject(clazz, rs);
	}
	

	public String getFieldsSeparatedByCommas() {
		return Arrays.stream(fields).map(field->field.getName()).collect(Collectors.joining(", "));
	}
	public String getFieldsSeparatedByCommas(boolean ignoreAutoIncrement) {
		return Arrays.stream(fields).filter(field->!ignoreAutoIncrement || !field.isAutoIncrement()).map(field->field.getName()).collect(Collectors.joining(", "));
	}
	public Object getQuestionMarks(boolean ignoreAutoIncrement) {		
		return Arrays.stream(fields).filter(field->!ignoreAutoIncrement || !field.isAutoIncrement()).map(field->"?").collect(Collectors.joining(", "));
	}
	public Object[] getFieldValues(boolean ignoreAutoIncrement) {		
		return Arrays.stream(fields).filter(field->!ignoreAutoIncrement || !field.isAutoIncrement()).map(field->field.getValue()).toArray();
	}
	
	
	public String getFieldValuesSeparatedByCommas() {
		return Arrays.stream(fields).map(field->field.getValueString()).collect(Collectors.joining(", "));
	}
	
	
	
	public PenguinObject<?>[] getPenguinObjects()
	{
		return penguinObjects;
	}
	
	public Class<?>[] getPenguinObjectsClasses()
	{
		return penguinObjectsClasses;
	}
	
	public Class<T> getClazz()
	{
		return clazz;
	}
	
	
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("Name = ").append(name);
		
		return msg.toString();
	}



	public String getForeignKeyName(Class<?> clazz) {
		for(PenguinField f: fields)
		{
			if(f.foreignKey.equals(clazz))
			{
				return f.getName();
			}
		}
		return null;
	}



	public <T> T[] setContentRelation(T[] objs2) 
	{
		List<PenguinObject<?>> pobjs = new ArrayList<>();
		
		for(T o: objs2)
		{
			PenguinObject<T> po = new PenguinObject<>(o);
			pobjs.add(po);
		}
		
		ReflexivePenguin.addRelations(obj,objs2);
		
		PenguinObject<?>[]  pObjsArray = pobjs.toArray(new PenguinObject[0]);
		penguinObjects = pObjsArray;
		
		return objs2;		
	}

}
