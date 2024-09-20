package glaciar;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

public class ReflexivePenguin 
{
	private static ReflexivePenguin instance;

	private ReflexivePenguin() {

	}
	
	public static String getEntityName(Class<?> clazz)
	{
		PenguinEntity annotation = clazz.getAnnotation(PenguinEntity.class);
		if(annotation != null && annotation.name() != null)
		{			
			return annotation.name();			
		}
		return clazz.getSimpleName();
	}
	
	public static PenguinField[] getEmptyPenguinFields(Class<?> clazz)
	{
		try {
			if(!clazz.isInterface())
			{
				Object obj = clazz.getDeclaredConstructor().newInstance();
				return getPenguinFields(obj);
			}			
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	//TODO: Quiero que si encuentra un array o un elemento Collection como list o set que compruebe si la clase de sus elementos tiene la anotacion PenguinEntity y añadir cada elemento del array por separado a la array final
	public static PenguinObject<?>[] getPenguinEntityFields(Object obj)
	{
		Field[] fields = obj.getClass().getDeclaredFields();

        return Arrays.stream(fields)
            .filter(ReflexivePenguinFilters::filterIgnores)
            .flatMap(field->{
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value == null) {
                        return Stream.empty();
                    }
                    Stream<?> stream = Stream.empty();
                    if (hasPenguinEntityAnnotation(value)) {
                        stream = Stream.concat(stream, Stream.of(value));
                    }
                    // Erorr?/*
                    if (value.getClass().isArray()) {
                    	if (!(value instanceof byte[])) {
                    		stream = Stream.concat(stream, Arrays.stream((Object[]) value));
                        }
                    }

                    // Process collections
                    else if (value instanceof Collection<?>) {
                        stream = Stream.concat(stream, ((Collection<?>) value).stream());
                    }

                    // Convert elements to PenguinObject and return
                    return stream
                        .filter(element -> element != null && hasPenguinEntityAnnotation(element))
                        .map(element->new PenguinObject(element)); // Pass each element to PenguinObject constructor 
                    

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    return Stream.empty();
                } finally {
                    field.setAccessible(false);
                }
            })            
            .toArray(PenguinObject[]::new);
	}
	
	private static boolean hasPenguinEntityAnnotation(Object obj) {
        return obj != null && obj.getClass().isAnnotationPresent(PenguinEntity.class);
    }

	public static PenguinField[] getPenguinFields(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        return Arrays.stream(fields)
            .filter(ReflexivePenguinFilters::filterIgnores)
            .filter(field -> {
            	PenguinEntity annotation = field.getType().getAnnotation(PenguinEntity.class);
                return annotation == null;
            })
            // FILTRO QUE DESCARTA ARRAYS Y COLLECTIONS
            .filter(field -> {
            	try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    field.setAccessible(false);
                    if(value != null && (value.getClass().isArray() || value instanceof Collection<?>) && !(value instanceof byte[]))
                    {
                    	return false;
                    }
                    else {
                    	return true;
                    }
                    
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            })
            .map(field -> {
                field.setAccessible(true);
                PenguinAttribute pa = field.getAnnotation(PenguinAttribute.class);
                PenguinField pf = null;
                try {
	                if(pa != null)
	                {                
	                	pf = new PenguinField(field.getName(), field.get(obj), pa.penguinKey(), pa.unique(), pa.autoIncrement(), pa.foreignKey());	
						
	                }else {
						pf = new PenguinField(field.getName(),field.get(obj));
	                }
                } catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				field.setAccessible(false);
				return pf; // Crea una entrada del mapa
            })
            .toArray(PenguinField[]::new); // Colecciona en un array de PenguinField
    }

	public static PenguinField getPenguinFieldPk(Class<?> clazz, Object id) 
	{
		Field[] fields = clazz.getDeclaredFields();

		return Arrays.stream(fields)
                .filter(field -> {
                    PenguinAttribute annotation = field.getAnnotation(PenguinAttribute.class);
                    return annotation != null && annotation.penguinKey();
                })
                .findFirst()
                .map(field ->{
                	PenguinAttribute annotation = field.getAnnotation(PenguinAttribute.class);
                	return new PenguinField(field.getName(), id,annotation.penguinKey(), annotation.unique(), annotation.autoIncrement(), annotation.foreignKey());
                })
                .orElse(null);
	}

	 public static <T> T[] resultSetToObject(Class<T> clazz, ResultSet rs) {
	        Field[] campos = clazz.getDeclaredFields();
	        List<T> listaObjetos = new ArrayList<>();
	        
	        try {
	            while (rs.next()) {
	                T obj = clazz.getDeclaredConstructor().newInstance();
	                
	                for (Field campo : campos) {
	                    PenguinAttribute anotacion = campo.getAnnotation(PenguinAttribute.class);
	                    if (anotacion == null || !anotacion.ignore()) {
	                        campo.setAccessible(true);	                        
	                        String nombreColumna = campo.getName();
	                        try {
	                        	Object valor = rs.getObject(nombreColumna);
		                        valor = filterValue(valor);
	                        	campo.set(obj, valor);
	                        }catch(Exception e)
	                        {
	                        	//e.printStackTrace();
	                        }
	                        
	                        campo.setAccessible(false);
	                    }
	                }
	                
	                listaObjetos.add(obj);
	            }
	        } catch (SQLException | ReflectiveOperationException e) {
	            e.printStackTrace();
	        }
	        
	        @SuppressWarnings("unchecked")
	        T[] arrayObjetos = (T[]) java.lang.reflect.Array.newInstance(clazz, listaObjetos.size());
	        return listaObjetos.toArray(arrayObjetos);

	    }
	 
	 private static Object filterValue(Object value)
	 {
		 Object newValue = value;
		 
		 if (value instanceof Timestamp) {
             newValue = ((Timestamp) value).toLocalDateTime();
         } else if (value instanceof Date) {
        	 newValue = ((Date) value).toLocalDate().atStartOfDay();
         }
		 
		 return newValue;
	 }

	public static void setPenguinKey(Object obj, Object value) 
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		
		for(Field field:fields)
		{
			if(field.isAnnotationPresent(PenguinAttribute.class)) 
			{
				if(field.getAnnotation(PenguinAttribute.class).penguinKey())
				{
					field.setAccessible(true);
			        try 
			        {
						field.set(obj,value);
						
					} 
			        catch (IllegalArgumentException | IllegalAccessException e) 
			        {
						e.printStackTrace();
					}
			        field.setAccessible(false);
				}
			}
			
		}		
	}

	public static void setForeignKey(Object obj, Class<?> clazz, Object value)
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		
		for(Field field:fields)
		{
			if(field.isAnnotationPresent(PenguinAttribute.class)) 
			{
				if(field.getAnnotation(PenguinAttribute.class).foreignKey().equals(clazz))
				{
					field.setAccessible(true);
			        try 
			        {
						field.set(obj,value);
						
					} 
			        catch (IllegalArgumentException | IllegalAccessException e) 
			        {
						e.printStackTrace();
					}
			        field.setAccessible(false);
				}
			}			
		}			
	}

	public static Class<?>[] getRelationClasses(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return Arrays.stream(fields)
		.filter(ReflexivePenguinFilters::filterIgnores)
		.map(ReflexivePenguinFilters::mapContentEntities)
		.filter(c -> c != null)
		.toArray(Class<?>[]::new);
	}

	public static <T> void addRelations(T obj, T[] objs2) {
		// TODO Añadir el valor al atributo relacion jy
		 
	}	
}
	

	/* Deprecated
	public static String getValueNamesSeparatedByCommas(Object obj)
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		
		return Arrays.stream(fields).filter(field -> {
            PenguinAttribute annotation = field.getAnnotation(PenguinAttribute.class);
            return annotation == null || !annotation.ignore();
        })
		.map(field->{
			try 
			{
				field.setAccessible(true);
				String secretValue = field.get(obj).toString();
				
				if(field.getType() == String.class)
				{
					secretValue = "'"+secretValue+"'";
				}
				
				
				field.setAccessible(false);
				return secretValue;
			} 
			catch (IllegalArgumentException e) {e.printStackTrace();} 
			catch (IllegalAccessException e) {e.printStackTrace();}		
			return null;
			})
		.collect(Collectors.joining(", "));        
        // Obtener el valor del campo privado para la instancia proporcionada        
	}*/
	
	/* Deprecated
	// Método para obtener los nombres de los atributos separados por comas.
	public static String getAttributeNamesSeparatedByCommas(Class<?> clazz) 
	{
		Field[] fields = clazz.getDeclaredFields();
		return Arrays.stream(fields).filter(field -> {
            PenguinAttribute annotation = field.getAnnotation(PenguinAttribute.class);
            return annotation == null || !annotation.ignore();
        }).map(Field::getName).collect(Collectors.joining(", "));
	}*/



