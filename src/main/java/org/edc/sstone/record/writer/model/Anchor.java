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

import java.util.ArrayList;
import java.util.List;

import org.edc.sstone.Constants;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public enum Anchor {

    /**
     * TODO: Do we need NULL
     */
    NULL(Constants.NUMBER_NOT_SET),

    BASELINE(64),
    BOTTOM(32),

    HCENTER(1),
    LEFT(4),
    RIGHT(8),

    TOP(16),
    VCENTER(2);

    private Anchor(int b) {
        this.b = (byte) b;
    }

    private final byte b;

    public int intValue() {
        return b;
    }

    public static String stringValueOf(byte b) {
        return StdLib.arrayToString(valuesFor(b));
    }

    public static Anchor[] verticalComponentAnchors() {
        return new Anchor[] { TOP, VCENTER, BOTTOM };
    }

    public static Anchor[] verticalTextAlignAnchors() {
        return new Anchor[] { TOP, BASELINE, BOTTOM };
    }

    public static Anchor verticalAnchor(byte b) {
        return selectMatching(b, new Anchor[] { BASELINE, BOTTOM, TOP, VCENTER });
    }

    public static Anchor horizontalAnchor(byte b) {
        return selectMatching(b, horizontalAnchors());
    }

    public static Anchor[] horizontalAnchors() {
        return new Anchor[] { LEFT, HCENTER, RIGHT };
    }

    static Anchor selectMatching(byte b, Anchor... values) {
        if (b == Constants.NUMBER_NOT_SET)
            return Anchor.NULL;
        int i = b;
        for (Anchor anchor : values) {
            if ((i & anchor.intValue()) == anchor.intValue())
                return anchor;
        }
        return Anchor.NULL;
    }

    public static Anchor[] valuesFor(byte b) {

        // We need a special case for -1 b/c it will
        // have ALL bits set. But we want to treat
        // it as null
        if (b == Constants.NUMBER_NOT_SET) {
            return null;
        }

        int i = b;
        List<Anchor> ret = new ArrayList<Anchor>();
        for (Anchor anchor : Anchor.values()) {
            if (anchor != NULL && (i & anchor.intValue()) != 0) {
                ret.add(anchor);
            }
        }
        return ret.toArray(new Anchor[ret.size()]);
    }

}
