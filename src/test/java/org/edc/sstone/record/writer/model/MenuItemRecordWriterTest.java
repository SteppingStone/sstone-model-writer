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

import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.MenuItemRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class MenuItemRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void test() throws IOException {
        String title = "title";
        String iconPath = "math";

        ScreenRecordWriter<ScreenRecord> srw = new ScreenRecordWriter<ScreenRecord>();
        srw.setTitle(title);
        
        MenuItemRecordWriter<MenuItemRecord> rw = new MenuItemRecordWriter<MenuItemRecord>(srw);

        rw.setSubType(MenuItemRecord.MODULE_HEADER);
        rw.setIconImagePath(iconPath);

        byte[] bytes = rw.toByteArray();

        RecordInputStream ris = getRecordInputStream(bytes);

        MenuItemRecord mir = (MenuItemRecord) ris.readRecord();
        ScreenRecord r = (ScreenRecord) ris.readRecord();

        assertEquals(iconPath, mir.iconImagePath);
        assertEquals(title, r.title);

        ris.close();

    }

}
