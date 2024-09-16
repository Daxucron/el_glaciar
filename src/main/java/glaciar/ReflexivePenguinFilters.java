package glaciar;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

public class ReflexivePenguinFilters 
{
    public static boolean filterIgnores(Field field) 
    {
        PenguinAttribute annotation = field.getAnnotation(PenguinAttribute.class);
        return annotation == null || !annotation.ignore();
    }
    
    public static Class<?> mapContentEntities(Field field) 
    {
    	Class<?> fieldClass = field.getType();
    	
		if(fieldClass.isAnnotationPresent(PenguinEntity.class))
		{
			return fieldClass;
		}
		if(fieldClass.isArray())
        {
			Class<?> componentType = fieldClass.getComponentType();
			if(componentType!=null && componentType.isAnnotationPresent(PenguinEntity.class))
			{
				return componentType;
			}
        }
		
		Type fieldType = field.getGenericType();
		if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            
            for (Type typeArgument : actualTypeArguments) {
                // Verificar si el Type es una instancia de Class
                if (typeArgument instanceof Class) {
                    Class<?> typeClass = (Class<?>) typeArgument;
                    if(typeClass.isAnnotationPresent(PenguinEntity.class))
                    {
                    	return typeClass;
                    }
                }
            }
        }
		
		return null;
    }
}
