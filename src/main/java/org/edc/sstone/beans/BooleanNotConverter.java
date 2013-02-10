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
public class BooleanNotConverter implements PropertyConverter<Boolean> {

    private static final BooleanNotConverter instance = new BooleanNotConverter();

    private BooleanNotConverter() {
    }

    public Boolean convertToBeanProperty(Object input) {
        Boolean ret = null;
        if (input instanceof Boolean) {
            ret = (Boolean) input;
        } else if (input instanceof String) {
            ret = Boolean.valueOf((String) input);
        }
        return not(ret);
    }

    public Object convertFromBeanProperty(Boolean beanProperty) {
        return not(beanProperty);
    }

    private Boolean not(Boolean b) {
        return (b != null)
                ? Boolean.valueOf(!b.booleanValue())
                : null;
    }

    public static BooleanNotConverter getInstance() {
        return instance;
    }
}
