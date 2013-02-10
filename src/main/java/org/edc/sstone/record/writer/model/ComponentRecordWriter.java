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

import org.edc.sstone.record.reader.model.ComponentRecord;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public abstract class ComponentRecordWriter<R extends ComponentRecord> extends RecordWriter<R> implements
        IComponentRecordWriter {

    protected StyleRecordWriter<StyleRecord> componentStyle;

    protected ComponentRecordWriter(R record) {
        super(record);
        componentStyle = new StyleRecordWriter<StyleRecord>();
    }

    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeRecord(componentStyle);
    }

    @Override
    protected void postRead() {
        componentStyle.setRecord(getRecord().styleRecord);
        componentStyle.initFontStyle();
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(super.fieldValues(),
                new Object[] {
                        "style", getStyle()
                });
    }

    /**
     * @return the component's style.
     */
    public StyleRecordWriter<StyleRecord> getStyle() {
        return componentStyle;
    }

    /**
     * @param style
     *            the style for the component
     */
    public void setStyle(StyleRecordWriter<StyleRecord> style) {
        this.componentStyle = style;
    }

    protected String abbreviateString(String str, int maxLength) {
        if (str == null)
            return "";
        if (str.length() <= maxLength)
            return str;
        return str.substring(0, maxLength - 1) + "…";
    }

    public boolean acceptsChildren() {
        return false;
    }

}
