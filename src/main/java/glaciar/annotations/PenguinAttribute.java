package glaciar.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface PenguinAttribute {

    boolean penguinKey() default false; // Elemento opcional
    String name() default "null";
    boolean autoIncrement() default false;
    Class<?> foreignKey() default Void.class;
    boolean unique() default false;
    boolean ignore() default false;
    
}
