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

import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public class ResourceComponentRecordWriter<R extends ResourceComponentRecord> extends ComponentRecordWriter<R> {

    @SuppressWarnings("unchecked")
    public ResourceComponentRecordWriter() {
        this((R) new ResourceComponentRecord());
    }

    protected ResourceComponentRecordWriter(R record) {
        super(record);
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeUTF(getFile());
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "file", getFile()
                });
    }

    @Override
    protected int getEstimatedRecordSize() {
        return getFile().length();
    }

    /*
     * Accessors
     */
    public String getFile() {
        return getRecord().resourcePath;
    }

    public void setFile(String resourcePath) {
        getRecord().resourcePath = resourcePath;
    }

    public String getLabel(int maxLength) {
        String filename = getFile();
        return abbreviateString(filename, maxLength);
    }

    public ComponentPresentation getPresentation() {
        return getSubType() == ResourceComponentRecord.SUBTYPE_IMAGE_PANEL
                ? ComponentPresentation.ImagePanel
                : null;
    }

}
