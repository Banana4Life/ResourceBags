/**
 * The MIT License
 * Copyright (c) 2014 Banana4Life
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package life.banana4.util.resourcebags;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        FileRef basedir = fileHandle(getClass().getSimpleName().toLowerCase());
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

    protected abstract T load(FileRef basedir, Field field);

    protected FileRef fileHandle(String path) {
        return FileRef.from(getClass().getClassLoader(), path);
    }

    protected FileRef fieldToFileHandle(Field field, FileRef basedir) {
        String path = fieldToPath(field);
        if (basedir == null) {
            return fileHandle(path);
        }
        return basedir.child(path);
    }

    public void dispose() {
        if (Disposable.class.isAssignableFrom(this.type)) {
            for (T resource : this.resources) {
                ((Disposable) resource).dispose();
            }
        }
    }

}
