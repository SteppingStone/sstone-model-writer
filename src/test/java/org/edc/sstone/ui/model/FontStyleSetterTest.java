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
package org.edc.sstone.ui.model;

import static org.junit.Assert.assertEquals;

import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.model.StyleRecordWriter;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class FontStyleSetterTest {

    @Test
    public void testSet() throws Exception {

        // List<org.edc.sstone.record.writer.model.FontStyle> styles = Arrays
        // .asList(new org.edc.sstone.record.writer.model.FontStyle[] {
        // org.edc.sstone.record.writer.model.FontStyle.BOLD,
        // org.edc.sstone.record.writer.model.FontStyle.ITALIC,
        // });
        StyleRecordWriter<StyleRecord> rw = new StyleRecordWriter<StyleRecord>();
        FontStyleSetter.set(rw.getFontStyle(),
                org.edc.sstone.record.writer.model.FontStyle.BOLD,
                org.edc.sstone.record.writer.model.FontStyle.ITALIC);

        assertEquals(org.edc.sstone.record.writer.model.FontStyle.BOLD.getValue(),
                rw.getFontStyle().style & org.edc.sstone.record.writer.model.FontStyle.BOLD.getValue());

        assertEquals(org.edc.sstone.record.writer.model.FontStyle.ITALIC.getValue(),
                rw.getFontStyle().style & org.edc.sstone.record.writer.model.FontStyle.ITALIC.getValue());

        assertEquals(0, rw.getFontStyle().style & org.edc.sstone.record.writer.model.FontStyle.UNDERLINED.getValue());

    }

}
