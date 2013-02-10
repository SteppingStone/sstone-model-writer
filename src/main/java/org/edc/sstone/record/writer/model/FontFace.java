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
package org.edc.sstone.record.writer.model;

import org.edc.sstone.Constants;
import org.edc.sstone.ui.model.FontStyle;

/**
 * @author Greg Orlowski
 */
public enum FontFace {

    NULL(Constants.NUMBER_NOT_SET),
    MONOSPACE(Constants.FONT_FACE_MONOSPACE),
    PROPORTIONAL(Constants.FONT_FACE_PROPORTIONAL),

    /**
     * Serif has the same value as {@link #PROPORTIONAL}
     */
    SERIF(Constants.FONT_FACE_PROPORTIONAL),
    SYSTEM(Constants.FONT_FACE_SYSTEM);

    private final byte value;

    private FontFace(byte face) {
        this.value = face;
    }

    public byte getValue() {
        return value;
    }

    public static FontFace forStyle(FontStyle fontStyle) {
        return fontStyle == null
                ? NULL
                : valueOf(fontStyle.getFace());
    }

    public static FontFace valueOf(byte size) {
        FontFace[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == size)
                return values[i];
        }
        return NULL;
    }
}
