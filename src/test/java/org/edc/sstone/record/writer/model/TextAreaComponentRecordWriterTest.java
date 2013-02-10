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

import org.edc.sstone.Constants;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class TextAreaComponentRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void testFixWhiteSpace() {
        String input = "this\r\nhas\rweird    spaces\t\t in it.";
        TextAreaComponentRecordWriter<TextAreaComponentRecord> rw = new TextAreaComponentRecordWriter<TextAreaComponentRecord>();

        assertEquals("this\nhas\nweird spaces in it.", rw.fixWhiteSpace(input));
    }

    @Test
    public void test() throws IOException {
        TextAreaComponentRecordWriter<TextAreaComponentRecord> rw = new TextAreaComponentRecordWriter<TextAreaComponentRecord>();

        String text = "Hola Mundo!";

        rw.setSubType(TextAreaComponentRecord.SUBTYPE_LETTER_READER);
        rw.setText(text);

        byte[] bytes = rw.toByteArray();

        TextAreaComponentRecord r = readRecord(bytes);

        assertEquals(text, r.text);
        assertNull(r.styleRecord);

        // Now verify style:
        int red = 0xFF0000;
        rw.getStyle().setFontColor(red);
        r = readRecord(rw.toByteArray());
        assertEquals(red, rw.componentStyle.getFontColor());
        assertNotNull(rw.componentStyle.getMargin());
        
        assertEquals(Constants.NUMBER_NOT_SET, rw.componentStyle.getMargin().getTop());
    }

}