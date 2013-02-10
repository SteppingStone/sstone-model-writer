/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class BeanUtilTest {

    @Test
    public void testSetProperty() throws Exception {
        Person p = new Person();
        p.setAddress(new Address(""));
        BeanUtil.setProperty(p, "age", 33);
        BeanUtil.setProperty(p, "firstName", "Greg");
        BeanUtil.setProperty(p, "address.zip", "60647");

        assertEquals("Greg", p.getFirstName());
        assertEquals(33, p.getAge());
        assertEquals("60647", p.getAddress().getZip());

        p = new Person();
        BeanUtil.setProperty(p, "age", Integer.valueOf(33));
        assertEquals(33, p.getAge());

        BeanUtil.setProperty(p, "human", Boolean.FALSE);
        assertEquals(false, p.isHuman());

        BeanUtil.setProperty(p, "favoriteColors[0]", "green");
        assertEquals("green", p.getFavoriteColors()[0]);
        
        BeanUtil.setProperty(p, "recentResidences[1]", "New York");
        assertEquals("New York", p.getRecentResidences().get(1));
    }

    @Test
    public void testGetProperty() throws Exception {
        Person p = new Person();
        p.setAddress(new Address("60647"));
        p.setAge(33);
        p.setFirstName("Greg");

        assertEquals("Greg", BeanUtil.getProperty(p, "firstName"));
        assertEquals(33, BeanUtil.getProperty(p, "age"));
        assertEquals("60647", BeanUtil.getProperty(p, "address.zip"));
        assertEquals(true, BeanUtil.getProperty(p, "human"));

        // Test array notation
        assertEquals("red", BeanUtil.getProperty(p, "favoriteColors[0]"));
        assertEquals("black", BeanUtil.getProperty(p, "favoriteColors[1]"));
        
        // Test List array notation
        assertEquals("Chicago", BeanUtil.getProperty(p, "recentResidences[0]"));
        assertEquals("Boston", BeanUtil.getProperty(p, "recentResidences[1]"));
    }

    /*
     * Note that I test both primitives (int) and Objects (String)
     */
    static class Person {

        private int age;
        boolean human = true;
        private String firstName;
        private Address address;

        private String[] favoriteColors = { "red", "black" };
        private List<String> recentResidences = Arrays.asList(new String[] { "Chicago", "Boston" });

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public boolean isHuman() {
            return human;
        }

        public void setHuman(boolean human) {
            this.human = human;
        }

        public String[] getFavoriteColors() {
            return favoriteColors;
        }

        public List<String> getRecentResidences() {
            return recentResidences;
        }
    }

    static class Address {
        private String zip;

        Address(String zip) {
            this.zip = zip;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }
    }

}
