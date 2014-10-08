package life.banana4.util.resourcebags;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public abstract class Resources implements Disposable {
    private Set<Field> bagFields;

    protected Resources() {
        this.bagFields = new HashSet<Field>();
        for (Field f : getClass().getFields()) {
            if (Modifier.isPublic(f.getModifiers()) && ResourceBag.class.isAssignableFrom(f.getType())) {
                this.bagFields.add(f);
            }
        }
    }

    public void build() {
        for (Field f : this.bagFields) {
            try {
                Object value = f.get(this);
                if (value != null) {
                    ResourceBag.class.cast(value).build();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access a resource bag!", e);
            }
        }
    }

    public void dispose() {
        for (Field f : this.bagFields) {
            try {
                Object value = f.get(this);
                if (value != null && value instanceof Disposable) {
                    Disposable.class.cast(value).dispose();
                }
            } catch (IllegalAccessException e) {
                System.err.println("Failed to access a resource bag!");
                e.printStackTrace(System.err);
            }
        }
    }
}
