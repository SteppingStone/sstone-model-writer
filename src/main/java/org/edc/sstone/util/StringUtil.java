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

/**
 * @author Greg Orlowski
 */
public class StringUtil {

    public static String repeat(String s, int count) {
        StringBuilder ret = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++)
            ret.append(s);
        return ret.toString();
    }

    public static StringBuffer sbuff(String str) {
        return new StringBuffer(str);
    }

    public static String[] toStringArray(Object[] arr) {
        String[] ret = new String[arr.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = arr[i].toString();
        return ret;
    }
}
