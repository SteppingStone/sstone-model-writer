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

import java.io.IOException;
import java.util.List;

import org.edc.sstone.Constants;
import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.ui.model.FixedSpacing;
import org.edc.sstone.ui.model.FontStyle;
import org.edc.sstone.ui.model.FontStyleSetter;
import org.edc.sstone.ui.model.MutableFixedSpacing;
import org.edc.sstone.ui.model.Spacing;
import org.edc.sstone.util.RecordFmt;
import org.edc.sstone.util.StdLib;

/**
 * Serializes a {@link StyleRecord}.
 * 
 * To write a null {@link StyleRecord}, set the value of record to null.
 * 
 * @author Greg Orlowski
 */
public class StyleRecordWriter<R extends StyleRecord> extends RecordWriter<R> {
    @SuppressWarnings("unchecked")
    public StyleRecordWriter() {
        this((R) new StyleRecord());
    }

    public StyleRecordWriter(R record) {
        super(record);
        useMutableMargin();
        initFontStyle();
    }

    // make the margin mutable so we can use bean properties to get/set
    protected void useMutableMargin() {
        R record = getRecord();
        if (record != null) {
            record.margin = (record.margin == null)
                    ? new MutableFixedSpacing(Constants.NUMBER_NOT_SET)
                    : new MutableFixedSpacing(getRecord().margin);
        }
    }

    // Return an html-notation hex string for a color (e.g., 0xFF0000 would be #FF0000)
    private static String colorString(int i) {
        if (i == Constants.NUMBER_NOT_SET) {
            return "null";
        }
        return '#' + Integer.toHexString(i).toUpperCase();
    }

    @Override
    public boolean isNull() {
        if (getRecord() == null) {
            return true;
        }
        return isNull(getBackgroundColor())
                && isNull(getFontColor())
                && isNull(getHighlightColor())
                && (getFontStyle() == null || getFontStyle().isNull())
                && isNull(getLineHeightByte())
                && (isNull(getMargin()))
                && isNull(getPadding())

                // anchor and text alignment
                && isNull(getTextAlign().byteValue())
                && isNull(getAnchor().byteValue())

                // animation delay
                && isNull(getAnimationStartDelayShort())
                && isNull(getAnimationPeriodShort());
    }

    private boolean isNull(Spacing margin) {
        return margin == null
                || isNull(margin.getTop())
                || isNull(margin.getRight())
                || isNull(margin.getBottom())
                || isNull(margin.getLeft());
    }

    @Override
    protected void writeData(RecordOutputStream out) throws java.io.IOException {
        /*
         * NOTE: we only write the super-data (subtype + control data) if the record is not null (if
         * at least one style field is set)
         */
        if (!isNull()) {
            super.writeData(out);
            out.writeInts(getRecord().backgroundColor, getRecord().fontColor, getRecord().highlightColor);

            out.writeBytes(getRecord().fontStyle.getFace(), getRecord().fontStyle.getStyleForWrite(),
                    getRecord().fontStyle.getSizeForWrite());

            out.writeByte(getLineHeightByte());

            Spacing spacing = getRecord().margin != null
                    ? getRecord().margin
                    : new FixedSpacing((short) Constants.NUMBER_NOT_SET);
            write(out, spacing);

            out.writeShort(getRecord().padding);

            out.writeBytes(getRecord().textAnchor, getRecord().componentAnchor);

            out.writeShort(getRecord().animationStartDelay);
            out.writeShort(getRecord().animationPeriod);
        }
    }

    private void write(RecordOutputStream out, Spacing spacing) throws IOException {
        out.writeShorts(
                (short) spacing.getTop(),
                (short) spacing.getRight(),
                (short) spacing.getBottom(),
                (short) spacing.getLeft());
    }

    @Override
    protected int getEstimatedRecordSize() {
        return isNull() ? RecordHeader.getHeaderSize() : 40;
    }

    // public String toString() {
    // return RecordFmt.toString(this,

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(super.fieldValues(),
                new Object[] {
                        "background-color", colorString(getBackgroundColor()),
                        "font-color", colorString(getFontColor()),
                        "highlight-color", colorString(getHighlightColor()),
                        "font-style", RecordFmt.toString(getFontStyle()),
                        "line-height", getLineHeight(),
                        "margin", RecordFmt.toString(getMargin()),
                        "padding", getPadding(),

                        // anchors
                        "text-anchor", Anchor.stringValueOf(getRecord().textAnchor),
                        "component-anchor", Anchor.stringValueOf(getRecord().componentAnchor),

                        // animation delay
                        "animation-start-delay", getAnimationStartDelayShort(),
                        "animation-period", getAnimationPeriodShort()
                });
    }

    // }

    /*
     * Accessors
     */
    public int getBackgroundColor() {
        return getRecord().backgroundColor;
    }

    public void setBackgroundColor(int color) {
        getRecord().backgroundColor = color;
    }

    //
    public int getFontColor() {
        return getRecord().fontColor;
    }

    public void setFontColor(int color) {
        getRecord().fontColor = color;
    }

    public int getColor() {
        return getFontColor();
    }

    /**
     * just calls {@link #setFontColor(int)}. I provide this for semantic equivalence with CSS,
     * which uses just &quot;color&quot; to specify font color.
     * 
     */
    public void setColor(int color) {
        setFontColor(color);
    }

    //
    public int getHighlightColor() {
        return getRecord().highlightColor;
    }

    public void setHighlightColor(int color) {
        getRecord().highlightColor = color;
    }

    /**
     * @return the line height of a line of text, measured in em units.
     * @see StyleRecordWriter#setLineHeight(float)
     */
    public float getLineHeight() {
        return ((float) getRecord().lineHeight) / 10.0f;
    }

    /**
     * @param height
     *            the height of a line of text, measured in em units. This should be greater than or
     *            equal to 1.0f. A value of, e.g., 1.2f would set the line height to 1.2 times the
     *            font height (in pixels)
     * 
     * @See <a href='https://en.wikipedia.org/wiki/Em_%28typography%29'>em units</a>
     */
    public void setLineHeight(float height) {
        getRecord().lineHeight = (byte) (10 * height);
    }

    protected byte getLineHeightByte() {
        return getRecord().lineHeight;
    }

    public FontStyle getFontStyle() {
        return getRecord().fontStyle;
    }

    // public void setFontStyle(FontStyle fontStyle) {
    // getRecord().fontStyle = fontStyle;
    // }

    protected void initFontStyle() {
        if (getRecord() != null && getRecord().fontStyle == null) {
            getRecord().fontStyle = new FontStyle();
        }
    }

    public FontSize getFontSize() {
        return FontSize.forStyle(getRecord().fontStyle);
    }

    public void setFontSize(FontSize fontSize) {
        FontStyleSetter.set(getRecord().fontStyle, fontSize);
    }

    public FontFace getFontFace() {
        return FontFace.forStyle(getRecord().fontStyle);
    }

    public void setFontFace(FontFace fontFace) {
        FontStyleSetter.set(getRecord().fontStyle, fontFace);
    }

    public void setFontStyle(List<org.edc.sstone.record.writer.model.FontStyle> fontStyleEnumList) {
        FontStyleSetter.set(getRecord().fontStyle,
                fontStyleEnumList.toArray(new org.edc.sstone.record.writer.model.FontStyle[fontStyleEnumList.size()]));
    }

    public void setFontStyle(org.edc.sstone.record.writer.model.FontStyle fontStyleEnum) {
        FontStyleSetter.set(getRecord().fontStyle, fontStyleEnum);
    }

    public boolean isEnableFontMagnification() {
        return getRecord().fontStyle.isEnableMagnification();
    }

    public void setEnableFontMagnification(boolean enableFontMagnification) {
        FontStyleSetter.setEnableMagnification(getRecord().fontStyle, enableFontMagnification);
    }

    public Spacing getMargin() {
        return getRecord().margin;
    }

    public void setMargin(Spacing margin) {
        getRecord().margin = margin;
    }

    public void setMargin(List<Number> values) {
        Spacing s = null;
        if (values != null) {
            short[] svals = new short[values.size()];
            for (int i = 0; i < svals.length; i++)
                svals[i] = values.get(i).shortValue();
            switch (svals.length) {
                case 1:
                    s = new MutableFixedSpacing(svals[0]);
                    break;
                case 2:
                    s = new MutableFixedSpacing(svals[0], svals[1]);
                    break;
                case 4:
                    s = new MutableFixedSpacing(svals[0], svals[1], svals[2], svals[3]);
                    break;
            }
        }
        getRecord().margin = s;
    }

    // private boolean isValid(Spacing spacing) {
    // return !(isNull(spacing.getTop()) || isNull(spacing.getRight())
    // || isNull(spacing.getBottom()) || isNull(spacing.getLeft()));
    // }

    // private void setMarginIfValid(Spacing s) {
    // if (isValid(s))
    // setMargin(s);
    // }

    // public int getMarginTop() {
    // return getMargin().getTop();
    // }
    //
    // public void setMarginTop(int margin) {
    // setMargin(new FixedSpacing((short) margin, (short) getMarginRight(), (short)
    // getMarginBottom(),
    // (short) getMarginLeft()));
    // }
    //
    // public int getMarginRight() {
    // return getMargin().getRight();
    // }
    //
    // public void setMarginRight(int margin) {
    // setMargin(new FixedSpacing((short) getMarginTop(), (short) margin, (short) getMarginBottom(),
    // (short) getMarginLeft()));
    // }
    //
    // public int getMarginBottom() {
    // return getMargin().getBottom();
    // }
    //
    // public void setMarginBottom(int margin) {
    // setMargin(new FixedSpacing((short) getMarginTop(), (short) getMarginRight(), (short) margin,
    // (short) getMarginLeft()));
    // }
    //
    // public int getMarginLeft() {
    // return getMargin().getLeft();
    // }
    //
    // public void setMarginLeft(int margin) {
    // setMargin(new FixedSpacing((short) getMarginTop(), (short) getMarginRight(), (short)
    // getMarginBottom(),
    // (short) margin));
    // }

    //
    public int getPadding() {
        return getRecord().padding;
    }

    public void setPadding(int padding) {
        getRecord().padding = (short) padding;
    }

    public Byte getAnchor() {
        return getRecord().componentAnchor;
    }

    public void setAnchor(Byte anchor) {
        getRecord().componentAnchor = anchor;
    }

    public void setAnchor(Anchor... anchors) {
        getRecord().componentAnchor = anchorValue(anchors);
    }

    // public Anchor getAnchorX() {
    // return Anchor.horizontalAnchor(getAnchor());
    // }
    //
    // public void setAnchorX(Anchor anchor) {
    // setAnchor(getAnchorY(), anchor);
    // }
    //
    // public Anchor getAnchorY() {
    // return Anchor.verticalAnchor(getAnchor());
    // }
    //
    // public void setAnchorY(Anchor anchor) {
    // setAnchor(getAnchorX(), anchor);
    // }

    public Byte getTextAlign() {
        return getRecord().textAnchor;
    }

    public void setTextAlign(Byte value) {
        getRecord().textAnchor = value;
    }

    public void setTextAlign(Anchor... anchors) {
        getRecord().textAnchor = anchorValue(anchors);
    }

    public short getAnimationStartDelayShort() {
        return getRecord().animationStartDelay;
    }

    public float getAnimationStartDelay() {
        return ((float) getRecord().animationStartDelay) / 10.0f;
    }

    public void setAnimationStartDelay(float v) {
        getRecord().animationStartDelay = toDeciSeconds(v);
    }

    public short getAnimationPeriodShort() {
        return getRecord().animationPeriod;
    }

    public float getAnimationPeriod() {
        return ((float) getRecord().animationPeriod) / 10.0f;
    }

    public void setAnimationPeriod(float v) {
        getRecord().animationPeriod = toDeciSeconds(v);
    }

    protected short toDeciSeconds(float f) {
        return (short) (f * 10);
    }

    protected byte anchorValue(Anchor... anchors) {
        byte a = 0;
        for (int i = 0; i < anchors.length; i++)
            a |= (byte) anchors[i].intValue();
        return a;
    }

    /*
     * Because the record can be null, explicity return StyleRecord.CLASS_UID in that case to avoid
     * an NPE
     */
    public short getClassUID() {
        return getRecord() != null ? super.getClassUID() : StyleRecord.CLASS_UID;
    }

    // @Override
    // protected void postRead() {
    // super.postRead();
    // useMutableMargin();
    // }

    @Override
    protected void setRecord(R record) {
        super.setRecord(record);
        useMutableMargin();
    }

}
