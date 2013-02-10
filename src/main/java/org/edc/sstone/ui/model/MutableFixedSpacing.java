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

/**
 * @author Greg Orlowski
 */
public class MutableFixedSpacing extends FixedSpacing {

    public MutableFixedSpacing(Spacing spacing) {
        this(spacing.getTop(), spacing.getRight(), spacing.getBottom(), spacing.getLeft());
    }

    public MutableFixedSpacing(int spacing) {
        super((short) spacing);
    }

    public MutableFixedSpacing(int spacingY, int spacingX) {
        super((short) spacingY, (short) spacingX);
    }

    public MutableFixedSpacing(int top, int right, int bottom, int left) {
        super((short) top, (short) right, (short) bottom, (short) left);
    }

    public void setTop(int top) {
        this.top = (short) top;
    }

    public void setRight(int right) {
        this.right = (short) right;
    }

    public void setBottom(int bottom) {
        this.bottom = (short) bottom;
    }

    public void setLeft(int left) {
        this.left = (short) left;
    }

}
