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

/**
 * @author Greg Orlowski
 */
public class FloatConverter implements PropertyConverter<Float> {

    private final int precision;

    public FloatConverter(int precision) {
        this.precision = precision;
    }

    public Float convertToBeanProperty(Object a) {
        try {
            return Float.parseFloat(a.toString());
        } catch (NumberFormatException ignore) {
        }
        return 0f;
    }

    public Object convertFromBeanProperty(Float b) {
        return String.format("%." + precision + "f", b.floatValue());
    }

}
