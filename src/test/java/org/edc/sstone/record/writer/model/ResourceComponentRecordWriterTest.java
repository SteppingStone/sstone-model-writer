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

import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ResourceComponentRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void test() throws IOException {

        String resourcePath = "logo_image";
        ResourceComponentRecordWriter<ResourceComponentRecord> rw = new ResourceComponentRecordWriter<ResourceComponentRecord>();
        rw.setSubType(ResourceComponentRecord.SUBTYPE_IMAGE_PANEL);
        rw.setFile(resourcePath);
        
        byte[] recordBytes = rw.toByteArray();
        
        ResourceComponentRecord r = readRecord(recordBytes);
        
        assertEquals(resourcePath, r.resourcePath);
        assertEquals(ResourceComponentRecord.SUBTYPE_IMAGE_PANEL, r.subType);

    }

}
