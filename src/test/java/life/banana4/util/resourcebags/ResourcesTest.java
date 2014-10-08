package life.banana4.util.resourcebags;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourcesTest {

    private TestResources resources;

    @Before
    public void setUp() throws Exception {
        this.resources = new TestResources();
    }

    @Test
    public void testBuild() throws Exception {
        this.resources.build();

        System.out.println();

        assertEquals(trim(this.resources.texts.test), "Hello World");
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    @After
    public void tearDown() throws Exception {
        this.resources.dispose();
        this.resources = null;
    }
}
