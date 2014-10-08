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
