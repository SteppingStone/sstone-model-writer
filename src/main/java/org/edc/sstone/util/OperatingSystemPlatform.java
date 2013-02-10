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
 * I do not use the AppHelper and PlatformType classes from BSAF because I want this code to live in
 * the writer (not coupled to the UI) and I do not want the writer module to depend on BSAF. This is
 * pretty simple and should work fine.
 * 
 * @author Greg Orlowski
 * 
 */
public enum OperatingSystemPlatform {

    WINDOWS,
    OSX,
    LINUX,
    OTHER;

    public static OperatingSystemPlatform get() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("win") == 0) {
            return WINDOWS;
        }

        // We do support mac classic (<= OSX)
        if (osName.indexOf("mac") == 0) {
            return OSX;
        }

        if (osName.indexOf("linux") == 0) {
            return LINUX;
        }

        return OTHER;
    }
}
