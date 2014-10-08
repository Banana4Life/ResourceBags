package life.banana4.util.resourcebags;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ResourceBag<T> implements Disposable {
    protected final List<T> resources;
    private final Class<T> type;
    private boolean built = false;

    @SuppressWarnings("unchecked")
    protected ResourceBag() {
        Type t = this.getClass().getGenericSuperclass();
        Type param = ((ParameterizedType) t).getActualTypeArguments()[0];
        if (!(param instanceof Class)) {
            throw new IllegalArgumentException("Field to detect our type!");
        }
        this.type = (Class<T>) param;
        this.resources = new ArrayList<T>();
    }

    protected static String fieldToPath(Field field) {
        return fieldNameToPath(field.getName());
    }

    protected static String fieldNameToPath(String fieldName) {
        char[] chars = fieldName.toCharArray();
        if (chars.length == 0) {
            throw new IllegalArgumentException("Empty string is not a valid field name!");
        }

        StringBuilder path = new StringBuilder().append(Character.toLowerCase(chars[0]));

        char c;
        for (int i = 1; i < chars.length; i++) {
            c = chars[i];
            if (Character.isUpperCase(c)) {
                path.append(File.separatorChar);
                c = Character.toLowerCase(c);
            }
            path.append(c);
        }

        return path.toString();
    }

    public List<T> getResources() {
        return Collections.unmodifiableList(this.resources);
    }

    public void build() {
        if (built) {
            return;
        }
        built = true;
        File basedir = fileHandle(getClass().getSimpleName().toLowerCase());
        Field[] fields = this.getClass().getFields();

        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && this.type.isAssignableFrom(field.getType())) {
                try {
                    T resource = load(basedir, field);
                    field.set(this, resource);
                    this.resources.add(resource);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to access a resource field!", e);
                }
            }
        }
    }

    protected abstract T load(File basedir, Field field);

    protected File fileHandle(String path) {
        try {
            URL base = getClass().getClassLoader().getResource(".");
            if (base == null) {
                throw new BrokenResourceBagException("Base path not found!");
            }
            return new File(new File(base.toURI()), path);
        } catch (URISyntaxException e) {
            throw new BrokenResourceBagException(e);
        }
    }

    protected File fieldToFileHandle(Field field, File basedir) {
        String path = fieldToPath(field);
        if (basedir == null) {
            return fileHandle(path);
        }
        return new File(basedir, path);
    }

    public void dispose() {
        if (Disposable.class.isAssignableFrom(this.type)) {
            for (T resource : this.resources) {
                ((Disposable) resource).dispose();
            }
        }
    }

}
