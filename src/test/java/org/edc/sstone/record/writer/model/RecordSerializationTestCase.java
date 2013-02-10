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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.edc.sstone.record.reader.RecordFactory;
import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.IntArrayRecord;
import org.edc.sstone.record.reader.model.MenuItemRecord;
import org.edc.sstone.record.reader.model.QuestionRecord;
import org.edc.sstone.record.reader.model.Record;
import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;

/**
 * This is a base test-case class with some utility functions to reduce boilerplate code in
 * serialization/deserialization tests.
 * 
 * @author Greg Orlowski
 */
public abstract class RecordSerializationTestCase {

    protected static RecordFactory factory(Record[] recordObjects) {
        RecordFactory rf = new RecordFactory();

        for (int i = 0; i < recordObjects.length; i++)
            rf.registerType(recordObjects[i]);

        return rf;
    }

    protected static RecordFactory getRecordFactory() {
        Record[] recordObjects = new Record[] {
                new StyleRecord(),
                new TextAreaComponentRecord(),
                new ScreenRecord(),
                new ScreenSeriesRecord(),
                new MenuItemRecord(),
                new IntArrayRecord(),
                new ResourceComponentRecord(),
                new QuestionRecord()
        };
        return factory(recordObjects);
    }

    protected static RecordFactory getRecordFactoryEager() {
        Record[] recordObjects = new Record[] {
                new StyleRecordWriter<StyleRecord>(),
                new TextAreaComponentRecordWriter<TextAreaComponentRecord>(),
                new ScreenRecordWriter<ScreenRecord>(),
                new ScreenSeriesRecordWriter<ScreenSeriesRecord>(),
                new MenuItemRecordWriter<MenuItemRecord>(),

                /*
                 * NOTE:
                 */
                // new IntArrayRecordWriter(),
                new IntArrayRecord(),

                new ResourceComponentRecordWriter<ResourceComponentRecord>(),
                new QuestionRecordWriter<QuestionRecord>()
        };
        return factory(recordObjects);
    }

    protected static RecordInputStream getRecordInputStream(byte[] bytes, RecordFactory rf) {
        return new RecordInputStream(new ByteArrayInputStream(bytes), rf);
    }

    protected static RecordInputStream getRecordInputStream(byte[] bytes) {
        return new RecordInputStream(new ByteArrayInputStream(bytes), getRecordFactory());
    }

    @SuppressWarnings("unchecked")
    protected static <R extends Record> R readRecord(byte[] bytes) throws IOException {
        RecordInputStream ris = getRecordInputStream(bytes, getRecordFactory());
        R ret = (R) ris.readRecord();
        ris.close();
        return ret;
    }

    @SuppressWarnings("unchecked")
    protected static <R extends Record> R readRecordEager(byte[] bytes) throws IOException {
        RecordInputStream ris = getRecordInputStream(bytes, getRecordFactoryEager());
        R ret = (R) ris.readRecord();
        ris.close();
        return ret;
    }
}
