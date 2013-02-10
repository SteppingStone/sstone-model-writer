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

import org.edc.sstone.record.reader.model.QuestionRecord;
import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.edc.sstone.record.writer.model.QuestionRecordWriter;
import org.edc.sstone.record.writer.model.RecordWriter;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.record.writer.model.ScreenSeriesRecordWriter;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class RecordWriterFactory {

    public IComponentRecordWriter newComponentWriter(ComponentPresentation componentType) {
        byte subtype = componentType.getSubtype();
        IComponentRecordWriter ret = null;
        switch (componentType) {
            case ScreenSeries:
                ret = new ScreenSeriesRecordWriter<ScreenSeriesRecord>();
                break;

            /*
             * Non-menu Screen Types
             */
            case MenuScreen:
            case AudioScreen:
            case AnimatedScreen:
            case QuestionScreen:
            case ContentScreen:
                ret = new ScreenRecordWriter<ScreenRecord>();
                break;

            /*
             * Component Types that can be rendered on a screen
             */
            case TextArea:
            case LetterReader:
            case SyllableReader:
            case WordReader:
                ret = new TextAreaComponentRecordWriter<TextAreaComponentRecord>();
                break;

            case Question:
                ret = new QuestionRecordWriter<QuestionRecord>();
                break;

            case ImagePanel:
                ret = new ResourceComponentRecordWriter<ResourceComponentRecord>();
                break;
        }
        if (ret != null && ret instanceof RecordWriter<?>) {
            ((RecordWriter<?>) ret).setSubType(subtype);
        }
        return ret;

    }
}
