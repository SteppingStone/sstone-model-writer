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

/**
 * @author Greg Orlowski
 */
public enum FontSize {

    NULL(Constants.NUMBER_NOT_SET),
    SMALL(Constants.FONT_SIZE_SMALL),
    MEDIUM(Constants.FONT_SIZE_MEDIUM),
    LARGE(Constants.FONT_SIZE_LARGE);

    private final byte value;

    private FontSize(byte size) {
        this.value = size;
    }

    public byte getValue() {
        return value;
    }

    public static FontSize forStyle(org.edc.sstone.ui.model.FontStyle fontStyle) {
        return fontStyle == null
                ? FontSize.NULL
                : valueOf(fontStyle.getSize());
    }

    public static FontSize valueOf(byte size) {
        FontSize[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == size)
                return values[i];
        }
        return FontSize.NULL;
    }

}
