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

import java.io.InputStream;
import java.net.URL;

public class FileRef {
    private final ClassLoader classLoader;
    private final String path;

    private FileRef(ClassLoader loader, String path) {
        this.classLoader = loader;
        this.path = path;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getPath() {
        return path;
    }

    public FileRef child(String relativePath) {
        String path = this.path;
        if (!path.endsWith("/")) {
            path += "/";
        }
        return new FileRef(this.classLoader, path + relativePath);
    }

    public InputStream getInputStream() {
        return this.classLoader.getResourceAsStream(this.path);
    }

    public URL getURL() {
        return this.classLoader.getResource(this.path);
    }

    public static FileRef from(ClassLoader loader) {
        return new FileRef(loader, ".");
    }

    public static FileRef from(ClassLoader loader, String relative) {
        return from(loader).child(relative);
    }
}
