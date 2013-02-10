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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ScreenRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void test() throws IOException {
        String title = "title";
        ScreenRecordWriter<ScreenRecord> rw = new ScreenRecordWriter<ScreenRecord>();
        rw.setTitle(title);

        TextAreaComponentRecordWriter<TextAreaComponentRecord> textComponent1 = new TextAreaComponentRecordWriter<TextAreaComponentRecord>();

        String text = "Hola Mundo!";
        textComponent1.setText(text);

        rw.setSubType(ScreenRecord.SUBTYPE_MENU_SCREEN);
        rw.getComponents().add(textComponent1);

        byte[] bytes = rw.toByteArray();

        ScreenRecord r = readRecord(bytes);
        assertEquals(title, r.title);
        assertEquals(ScreenRecord.SUBTYPE_MENU_SCREEN, r.subType);

        // components should have been read.
        TextAreaComponentRecord tr = (TextAreaComponentRecord) r.componentRecords.elementAt(0);
        assertEquals(text, tr.text);
    }

    @Test
    public void testComponents() throws IOException {
        ScreenRecordWriter<ScreenRecord> rw = new ScreenRecordWriter<ScreenRecord>();

        String imageFile = "image_file";
        ResourceComponentRecordWriter<ResourceComponentRecord> imageWriter = createTestImageComponent(imageFile);

        TextAreaComponentRecordWriter<TextAreaComponentRecord> textComponent1 = new TextAreaComponentRecordWriter<TextAreaComponentRecord>();

        String text = "Hola Mundo!";
        textComponent1.setText(text);

        rw.setSubType(ScreenRecord.SUBTYPE_MENU_SCREEN);

        rw.getComponents().add(imageWriter);
        rw.getComponents().add(textComponent1);

        byte[] bytes = rw.toByteArray();

        ScreenRecord r = readRecord(bytes);
        assertEquals(ScreenRecord.SUBTYPE_MENU_SCREEN, r.subType);

        // components should have been read.
        ResourceComponentRecord rr = (ResourceComponentRecord) r.componentRecords.elementAt(0);
        assertEquals(imageFile, rr.resourcePath);

        TextAreaComponentRecord tr = (TextAreaComponentRecord) r.componentRecords.elementAt(1);
        assertEquals(text, tr.text);
    }

    private ResourceComponentRecordWriter<ResourceComponentRecord> createTestImageComponent(String filename) {
        ResourceComponentRecordWriter<ResourceComponentRecord> imageWriter = new ResourceComponentRecordWriter<ResourceComponentRecord>();
        imageWriter.setSubType(ResourceComponentRecord.SUBTYPE_IMAGE_PANEL);
        imageWriter.setFile(filename);
        return imageWriter;
    }

    @Test
    public void testRead() throws Exception {
        String title = "hello";
        ScreenRecordWriter<ScreenRecord> rw = new ScreenRecordWriter<ScreenRecord>();
        rw.setTitle(title);

        // Test with no components
        ScreenRecordWriter<ScreenRecord> readRecord = readRecordEager(rw.toByteArray());
        assertEquals(title, readRecord.getTitle());

        String imageFile = "image_file";
        ResourceComponentRecordWriter<ResourceComponentRecord> imageWriter = createTestImageComponent(imageFile);

        rw.getComponents().add(imageWriter);
        readRecord = readRecordEager(rw.toByteArray());
        assertEquals(title, readRecord.getTitle());
        assertEquals(1, readRecord.getComponentWriters().size());

        @SuppressWarnings("unchecked")
        ResourceComponentRecordWriter<ResourceComponentRecord> readImage = (ResourceComponentRecordWriter<ResourceComponentRecord>) readRecord
                .getComponentWriters().get(0);
        
        assertEquals(imageFile, readImage.getFile());
    }
    
}
