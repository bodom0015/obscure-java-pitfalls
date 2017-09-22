package org.nationaldataservice.elasticsearch.rocchio.test.unit;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class TestObscureJava {
    // Create some int/Integers with the same value (arbitrary value)
    int primitive = 300;
    Integer a = new Integer(primitive);
    Integer b = new Integer(primitive);
    
    // Example class that incorrectly overrides .equals() without .hashCode()
    class IncorrectHashcode {
        int value;
        
        public IncorrectHashcode(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CorrectHashcode) {
                CorrectHashcode that = (CorrectHashcode) obj;
                return this.value == that.getValue();
            }
            return false;
        }
    }

    // The same example class, but correctly overriding .hashCode()
    class CorrectHashcode extends IncorrectHashcode {
        public CorrectHashcode(int value) {
            super(value);
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(this.value);
        }
    }
    
    // When dealing with any Objects, it is always bezt to rely on that Object's .equals() method
    // Reference-equality works when at least one value is a primitive, but is not guaranteed to work for Objects
    @Test
    public void testEqualsEquals() {
        // Reference-equality works with primitives, since they do not have a .equals() method
        assertTrue(a == primitive);
        assertTrue(b == primitive);
        assertFalse(a == b);   // Reference-equality is not reliable when comparing two Objects
        
        // Object-equality behaves as expected
        assertTrue(a.equals(primitive));
        assertTrue(b.equals(primitive));
        assertTrue(a.equals(b));

        // Value comparison also behaves as expected
        assertEquals(0, Integer.compare(a, primitive));
        assertEquals(0, Integer.compare(b, primitive));
        assertEquals(0, Integer.compare(a, b));
    }

    // Boxing => converting primitives to Object wrappers
    // Unboxing => converting Object wrappers back to primitives
    // This uses the valueOf() method to coerce primitives to Objects
    // This happens on an as-needed basis (for example, passing int into a method that accepts an Integer)
    // NOTE: Heavy reliance on autoboxing can negatively impact performance, as it is creating implicit objects in memory 
    @Test
    public void testAutoboxing() {
        // Only boxed values that have been cached will truly be reference-equal
        assertTrue(Integer.valueOf("127") == Integer.valueOf("127"));

        // By default, JVM only caches Integers between -128 and 127
        assertFalse(Integer.valueOf("128") == Integer.valueOf("128"));
    }

    // When overriding .equals(), make sure you also override .hashCode()
    // Not doing so will break the API contract and can cause undefined behavior
    // This test shows what happens when you fail to do so...
    @Test
    public void testIncorrectHashCode() {
        // These items should all be equal, but generate different hash codes
        IncorrectHashcode hash1 = new IncorrectHashcode(primitive);
        IncorrectHashcode hash2 = new IncorrectHashcode(primitive);
        IncorrectHashcode hash3 = new IncorrectHashcode(primitive);
        
        HashMap<IncorrectHashcode, Integer> map = new HashMap<>();
        map.put(hash1, 1);
        map.put(hash2, 2);
        map.put(hash3, 3);

        // Different hashCodes => different keys are overwritten
        assertEquals(3, map.keySet().size());
        assertEquals(1, map.get(hash1).intValue());
        assertEquals(2, map.get(hash2).intValue());
        assertEquals(3, map.get(hash3).intValue());
    }

    // When overriding .equals(), make sure you also override .hashCode()
    // Not doing so will break the API contract and can cause undefined behavior
    // This test shows what happens when you correctly do so...
    @Test
    public void testCorrectHashCode() {
        // These items are all equal, with equal hash codes
        CorrectHashcode hash1 = new CorrectHashcode(primitive);
        CorrectHashcode hash2 = new CorrectHashcode(primitive);
        CorrectHashcode hash3 = new CorrectHashcode(primitive);
        
        HashMap<CorrectHashcode, Integer> map = new HashMap<>();
        map.put(hash1, 1);
        map.put(hash2, 2);
        map.put(hash3, 3);
        
        // Same hashCode => same key is overwritten
        assertEquals(1, map.keySet().size());
        assertEquals(3, map.get(hash1).intValue());
        assertEquals(3, map.get(hash2).intValue());
        assertEquals(3, map.get(hash3).intValue());
    }
}
