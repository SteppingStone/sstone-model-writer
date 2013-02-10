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

import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.reader.model.TitledComponentRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public abstract class TitledComponentRecordWriter<R extends TitledComponentRecord> extends ComponentRecordWriter<R> {

    protected TitledComponentRecordWriter(R record) {
        super(record);
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeUTF(getTitle());
    }

    // protected void readTitledComponentRecord(RecordInputStream in) throws IOException {
    // TitledComponentRecordExt r = new TitledComponentRecordExt();
    // r.read(in);
    // record.subType = r.subType;
    // record.styleRecord = r.styleRecord;
    // record.title = r.title;
    // }

    // protected static class TitledComponentRecordExt extends TitledComponentRecord {
    // @Override
    // public short getClassUID() {
    // return -1;
    // }
    // }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "title", getTitle()
                });
    }

    // TODO: I could create a null-safe string-length util method...
    protected int getTitleLength() {
        String title = getTitle();
        return title == null ? 0 : title.length();
    }

    @Override
    protected int getEstimatedRecordSize() {
        int ret = RecordHeader.getHeaderSize();
        ret += (componentStyle != null && !componentStyle.isNull()) ? componentStyle.getEstimatedRecordSize() : ret;
        ret += getTitleLength();
        return ret;
    }

    /*
     * Accessors
     */
    public String getTitle() {
        return getRecord().title;
    }

    public void setTitle(String title) {
        getRecord().title = title;
    }

    public String getLabel(int maxLength) {
        String title = getTitle();
        return title == null ? "" : abbreviateString(title, maxLength);
    }

}
