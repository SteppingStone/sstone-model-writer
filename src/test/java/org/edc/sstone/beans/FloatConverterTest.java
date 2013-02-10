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
package org.edc.sstone.beans;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class FloatConverterTest {

    @Test
    public void testConvertFromBeanProperty() throws Exception {
        FloatConverter fc = new FloatConverter(1);
        assertEquals("1.0", fc.convertFromBeanProperty(1.0123f));
    }

    @Test
    public void testConvertToBeanProperty() throws Exception {
        FloatConverter fc = new FloatConverter(2);
        double d = fc.convertToBeanProperty("1.23");
        assertEquals(1.23, d, 0.01);
    }

}
