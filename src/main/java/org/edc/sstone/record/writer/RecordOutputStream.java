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
package org.edc.sstone.record.writer;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.edc.sstone.record.writer.model.IRecordWriter;

/**
 * @author Greg Orlowski
 */
public class RecordOutputStream extends FilterOutputStream implements DataOutput {

    public RecordOutputStream(OutputStream out) {
        super(wrap(out));
    }

    protected DataOutputStream out() {
        return (DataOutputStream) out;
    }

    private static DataOutputStream wrap(OutputStream out) {
        return out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
    }

    public void writeBoolean(boolean v) throws IOException {
        out().writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        out().writeByte(v);

    }

    public void writeShort(int v) throws IOException {
        out().writeShort(v);
    }

    public void writeChar(int v) throws IOException {
        out().writeChar(v);
    }

    public void writeInt(int v) throws IOException {
        out().writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        out().writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        out().writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        out().writeDouble(v);
    }

    @Deprecated
    public void writeBytes(String s) throws IOException {
        throw new UnsupportedOperationException("Use writeUTF() instead");
    }

    @Deprecated
    public void writeChars(String s) throws IOException {
        throw new UnsupportedOperationException("Use writeUTF() instead");
    }

    public void writeUTF(String s) throws IOException {
        out().writeUTF(s == null ? "" : s);
    }

    public void writeRecord(IRecordWriter rw) throws IOException {
        out.write(rw.toByteArray());
    }

    /*
     * Some convenience methods
     */

    public final void writeBytes(byte... values) throws IOException {
        write(values);
    }

    public final void writeInts(int... values) throws IOException {
        for (int i = 0; i < values.length; i++)
            writeInt(values[i]);
    }

    public final void writeShorts(short... values) throws IOException {
        for (int i = 0; i < values.length; i++)
            writeShort(values[i]);
    }

}
