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
package org.edc.sstone.ui.model;

import org.edc.sstone.Constants;
import org.edc.sstone.record.writer.model.FontFace;
import org.edc.sstone.record.writer.model.FontSize;

/**
 * This is in the same package as FontStyle just to provide a way to set the package-private fields.
 * I'd rather not provide setters or make the fields public.
 * 
 * @author Greg Orlowski
 */
public class FontStyleSetter {

    public static void set(FontStyle fontStyle, FontSize fontSize) {
        fontStyle.size = fontSize.getValue();
    }

    public static void set(FontStyle fontStyle, org.edc.sstone.record.writer.model.FontStyle... textStyle) {
        if (textStyle.length == 0) {
            fontStyle.style = Constants.NUMBER_NOT_SET;
        } else {
            byte val = 0;
            for (org.edc.sstone.record.writer.model.FontStyle fs : textStyle) {
                val |= fs.getValue();
            }
            fontStyle.style = val;
        }
    }

    public static void set(FontStyle fontStyle, FontFace fontFace) {
        fontStyle.face = fontFace.getValue();
    }

    public static void setEnableMagnification(FontStyle fontStyle, boolean enableMagnification) {
        fontStyle.enableMagnification = enableMagnification;
    }
}
