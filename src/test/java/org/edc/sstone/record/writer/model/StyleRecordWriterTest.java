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

import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.model.StyleRecordWriter;
import org.edc.sstone.ui.model.FixedSpacing;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class StyleRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void testNull() throws IOException {
        StyleRecordWriter<StyleRecord> srw = new StyleRecordWriter<StyleRecord>();
        byte[] bytes = srw.toByteArray();

        assertTrue(srw.isNull());
        assertEquals((short) bytes[1], srw.getClassUID());
        assertEquals(RecordHeader.getHeaderSize(), bytes.length);
    }

    @Test
    public void testMargin() throws IOException {
        int red = 0xFF0000;

        StyleRecordWriter<StyleRecord> srw = new StyleRecordWriter<StyleRecord>();
        srw.setFontColor(red);
        assertFalse(srw.isNull());

        StyleRecord sr = readRecord(srw.toByteArray());
        assertNull(sr.margin);

        // Now test that margin will not be null
        srw.setMargin(new FixedSpacing((short) 1));
        sr = readRecord(srw.toByteArray());
        assertEquals(1, sr.margin.getLeft());
    }

    @Test
    public void testLineHeight() throws IOException {
        byte lineHeight = 12;

        float lineHeightFloat = ((float) lineHeight) / 10f;

        StyleRecordWriter<StyleRecord> srw = new StyleRecordWriter<StyleRecord>();
        srw.setLineHeight(lineHeightFloat);

        StyleRecord sr = readRecord(srw.toByteArray());
        assertEquals(lineHeight, sr.lineHeight);
    }

    @Test
    public void test() throws IOException {
        StyleRecordWriter<StyleRecord> srw = new StyleRecordWriter<StyleRecord>();

        srw.setMargin(new FixedSpacing((short) 1, (short) 2, (short) 3, (short) 4));

        srw.setBackgroundColor(0x444444);
        srw.setFontColor(0x333333);

        byte[] bytes = srw.toByteArray();

        assertEquals((short) bytes[1], srw.getClassUID());
        assertEquals((int) bytes[5], bytes.length - 6);

        StyleRecord sr = readRecord(bytes);

        assertEquals(srw.getBackgroundColor(), sr.backgroundColor);
        assertEquals(srw.getFontColor(), sr.fontColor);

        //
        assertEquals(1, sr.margin.getTop());
        assertEquals(srw.getMargin().getTop(), sr.margin.getTop());

        assertEquals(2, sr.margin.getRight());
        assertEquals(srw.getMargin().getRight(), sr.margin.getRight());

        assertEquals(3, sr.margin.getBottom());
        assertEquals(srw.getMargin().getBottom(), sr.margin.getBottom());

        assertEquals(4, sr.margin.getLeft());
        assertEquals(srw.getMargin().getLeft(), sr.margin.getLeft());
    }

}
