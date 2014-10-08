package life.banana4.util.resourcebags;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Scanner;

public class Texts extends ResourceBag<String> {

    public String test;

    @Override
    protected String load(File basedir, Field field) {
        try {
            return new Scanner(new File(basedir, field.getName() + ".txt")).useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
