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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.edc.sstone.Constants;
import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.AbstractRecord;
import org.edc.sstone.record.reader.model.Record;
import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.RecordFmt;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public abstract class RecordWriter<R extends Record> implements IRecordWriter, Record {

    private R record;

    protected RecordWriter(R record) {
        this.setRecord(record);
    }

    /**
     * By default, just let the underlying record read itself from the stream
     */
    public void read(RecordInputStream in) throws IOException {
        getRecord().read(in);
        postRead();
    }

    /**
     * This is called after read. Use this to set up the writer object.
     */
    protected void postRead() {
    }

    public RecordHeader getHeader() {
        return getRecord().getHeader();
    }

    public void setRecordHeader(RecordHeader recordHeader) {
        getRecord().setRecordHeader(recordHeader);
    }

    public short getClassUID() {
        return getRecord().getClassUID();
    }

    protected void writeData(RecordOutputStream out) throws IOException {
        out.writeByte(getSubType());
        out.writeInt(getControlData());
    }

    public boolean isNull() {
        return false;
    }

    /**
     * @return an estimate of the total size of the serialized record, which is used to set the
     *         initial byte buffer size when serializing this record.
     */
    protected abstract int getEstimatedRecordSize();

    // TODO: translate IOException
    public final byte[] toByteArray() {

        int initialSize = isNull() ? RecordHeader.getHeaderSize() : getEstimatedRecordSize();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(initialSize);
        RecordOutputStream ros = new RecordOutputStream(bos);

        byte[] recordBytes = null;
        try {
            writeHeaderWithUnsetLen(ros);
            if (!isNull()) {
                writeData(ros);
            }
            recordBytes = bos.toByteArray();
            ros.close();
        } catch (IOException e) {
            // TODO: catch
        }
        setHeaderLengthField(recordBytes);
        return recordBytes;
    }

    private void setHeaderLengthField(byte[] recordBytes) {
        StdLib.copyIntInto(recordBytes.length - RecordHeader.getHeaderSize(), recordBytes, 2);
    }

    private void writeHeaderWithUnsetLen(RecordOutputStream dos) throws IOException {
        dos.writeShort(getClassUID());
        dos.writeInt((int) Constants.NUMBER_NOT_SET);
    }

    // I could move this ???
    public static boolean isNull(int i) {
        return i == Constants.NUMBER_NOT_SET;
    }

    public static boolean isNull(double d) {
        return d < 0;
    }

    // TODO: what we really should do is subclass DataOutputStream to RecordOutputStream and
    // handle stuff like this transparently
    // protected static final void nullSafeWrite(DataOutputStream out, String str) throws
    // IOException {
    // out.writeUTF(str == null ? "" : str);
    // }

    /*
     * Accessors
     */
    public byte getSubType() {
        return ((AbstractRecord) getRecord()).subType;
    }

    public int getControlData() {
        return ((AbstractRecord) getRecord()).controlData;
    }

    public void setSubType(byte subType) {
        ((AbstractRecord) getRecord()).subType = subType;
    }

    protected boolean isSubTypeMaskSet(byte mask) {
        return (getSubType() & mask) == mask;
    }

    public String toString() {
        return RecordFmt.toString(this, fieldValues());
    }

    protected Object[] fieldValues() {
        return new Object[] {
                "subType", getSubType()
        };
    }

    protected R getRecord() {
        return record;
    }

    protected void setRecord(R record) {
        this.record = record;
    }
}
