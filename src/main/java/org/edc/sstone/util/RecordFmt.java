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

import java.util.List;

import org.edc.sstone.Constants;
import org.edc.sstone.record.writer.model.RecordWriter;
import org.edc.sstone.ui.model.FontStyle;
import org.edc.sstone.ui.model.Spacing;

/**
 * @author Greg Orlowski
 */
public class RecordFmt {

    static final String NULL_STR = "null";

    private static final int TAB_SPACES = 4;

    static private int indentLevel = 1;

    static void indent(StringBuilder sb) {
        for (int i2 = 0; i2 < indentLevel * TAB_SPACES; i2++)
            sb.append(' ');
    }

    public synchronized static String toString(Object o, Object... args) {
        StringBuilder sb = new StringBuilder("\n");

        int origIndentLevel = indentLevel;

        indent(sb);
        sb.append("<").append(o.getClass().getSimpleName()).append(">{ \n");

        indentLevel++;

        for (int i = 0; i < args.length; i += 2) {
            indent(sb);
            sb.append(String.format("%-30s: ", args[i]));
            if (args[i + 1] instanceof RecordWriter<?>) {
                indentLevel++;
                if (((RecordWriter<?>) args[i + 1]).isNull()) {
                    sb.append("<null record>");
                } else {
                    sb.append(args[i + 1]);
                }
                indentLevel--;
            } else if (args[i + 1] instanceof List<?>) {
                if (((List<?>) (args[i + 1])).isEmpty()) {
                    sb.append("[]");
                } else {
                    // sb.append("[\n");
                    sb.append("[");
                    indentLevel++;
                    List<?> items = (List<?>) (args[i + 1]);
                    for (int j = 0; j < items.size(); j++) {
                        indent(sb);
                        sb.append(items.get(j));
                        if (j == items.size() - 1)
                            sb.append('\n');
                    }
                    indentLevel--;
                    indent(sb);
                    sb.append("]");
                }
            } else {
                sb.append(args[i + 1]);
            }
            // if (i < args.length - 2)
            // if (sb.charAt(sb.length() - 1) != ']') {
            sb.append("\n");
            // }
        }
        // if (sb.charAt(sb.length() - 1) != ']') {
        // indent(sb);
        // }

        // At the end, we want the } to align witht the start of the record
        indentLevel = origIndentLevel;
        indent(sb);
        sb.append("}");

        return sb.toString();
    }

    public static String toString(Spacing s) {
        if (s == null)
            return NULL_STR;
        int[] m = { s.getTop(), s.getRight(), s.getBottom(), s.getLeft() };
        return toString(m);
    }

    public static String toString(FontStyle fontStyle) {
        if (fontStyle == null)
            return NULL_STR;
        StringBuilder sb = new StringBuilder("{");
        String faceName = NULL_STR;
        StringBuilder styleName = new StringBuilder();
        String sizeName = NULL_STR;

        switch (fontStyle.getFace()) {
            case Constants.FONT_FACE_MONOSPACE:
                faceName = "monospace";
                break;
            case Constants.FONT_FACE_PROPORTIONAL:
                faceName = "serif";
                break;
        }

        if (fontStyle.getStyle() == Constants.FONT_STYLE_PLAIN) {
            styleName.append("PLAIN");
        } else {
            byte style = fontStyle.getStyle();
            if ((style & Constants.FONT_STYLE_BOLD) != 0)
                styleName.append("BOLD");
            if ((style & Constants.FONT_STYLE_ITALIC) != 0) {
                styleName.append(styleName.length() > 0 ? "|" : "");
                styleName.append("ITALIC");
            }
            if ((style & Constants.FONT_STYLE_UNDERLINED) != 0) {
                styleName.append(styleName.length() > 0 ? "|" : "");
                styleName.append("UNDERLINE");
            }
        }

        switch (fontStyle.getSize()) {
            case Constants.FONT_SIZE_SMALL:
                sizeName = "small";
                break;
            case Constants.FONT_SIZE_MEDIUM:
                sizeName = "medium";
                break;
            case Constants.FONT_SIZE_LARGE:
                sizeName = "large";
                break;
        }

        sb.append("face: ").append(faceName).append(", ");
        sb.append("style: ").append(styleName).append(", ");
        sb.append("size: ").append(sizeName).append("}");
        return sb.toString();
    }

    public static String toString(int[] vals) {
        if (vals == null)
            return NULL_STR;
        if (vals.length == 0)
            return "[]";

        StringBuilder sb = new StringBuilder(vals.length * 2);
        sb.append("[");
        for (int i = 0; i < vals.length - 1; i++)
            sb.append(vals[i]).append(", ");
        sb.append(vals[vals.length - 1]);
        return sb.append("]").toString();
    }
}
