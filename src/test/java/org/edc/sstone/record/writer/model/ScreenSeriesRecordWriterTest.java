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

import static org.junit.Assert.*;

import java.io.IOException;

import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.IntArrayRecord;
import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.record.writer.model.ScreenSeriesRecordWriter;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ScreenSeriesRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void test() throws IOException {
        ScreenSeriesRecordWriter<ScreenSeriesRecord> rw = new ScreenSeriesRecordWriter<ScreenSeriesRecord>();

        String screen1Title = "first screen";
        ScreenRecordWriter<ScreenRecord> screen1 = new ScreenRecordWriter<ScreenRecord>();
        screen1.setTitle(screen1Title);

        rw.getScreens().add(screen1);

        byte[] recordBytes = rw.toByteArray();

        RecordInputStream ris = getRecordInputStream(recordBytes);
        ScreenSeriesRecord r = (ScreenSeriesRecord) ris.readRecord();

        assertEquals(1, r.screenRecordLengths.length());
        assertEquals(r.readEndPos, ris.getPos());

        ScreenRecord sr = (ScreenRecord) ris.readRecord();
        assertEquals(screen1Title, sr.title);
        assertEquals(r.screenRecordLengths.intValues[0], sr.getHeader().getRecordTotalLen());

        ris.close();
    }

    @Test
    public void testRead() throws Exception {
        ScreenSeriesRecordWriter<ScreenSeriesRecord> rw = new ScreenSeriesRecordWriter<ScreenSeriesRecord>();

        String screen1Title = "first screen";
        ScreenRecordWriter<ScreenRecord> screen1 = new ScreenRecordWriter<ScreenRecord>();
        screen1.setTitle(screen1Title);
        
        rw.getScreens().add(screen1);
        
        byte[] recordBytes = rw.toByteArray();

        RecordInputStream ris = getRecordInputStream(recordBytes, getRecordFactoryEager());
        @SuppressWarnings("unchecked")
        ScreenSeriesRecordWriter<ScreenSeriesRecord> eagerSSWR = (ScreenSeriesRecordWriter<ScreenSeriesRecord>) ris.readRecord();
        
        assertEquals(screen1Title, eagerSSWR.getScreens().get(0).getTitle());
    }

    @Test
    public void testIntArrayRecordWriterRead() throws IOException {
        int[] values = new int[] { 1, 2, 3, 4 };
        ScreenSeriesRecordWriter.IntArrayRecordWriter rw = new ScreenSeriesRecordWriter.IntArrayRecordWriter(values);

        byte[] recordBytes = rw.toByteArray();

        assertEquals(RecordHeader.getHeaderSize() + 5 + (values.length * 4), recordBytes.length);

        IntArrayRecord r = (IntArrayRecord) getRecordInputStream(recordBytes).readRecord();

        for (int i = 0; i < values.length; i++)
            assertEquals(values[i], r.intValues[i]);
    }
}
