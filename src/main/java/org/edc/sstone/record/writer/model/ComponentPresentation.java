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

import java.util.EnumSet;

import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;

/**
 * This represents how a component presents itself to the user (in a UI). The mapping between
 * {@link RecordWriter} classes and presentations is not 1:1. It can also depend on the subtype and
 * other characteristics of the writer. Therefore, we'll have the {@link RecordWriter} classes
 * determine and return their presentation, and the UI will map presentation enum instances to icons
 * and/or other visual cues.
 * 
 * @author Greg Orlowski
 */
public enum ComponentPresentation {

    /*
     * Navigation Types
     */
    MenuScreen(ScreenRecord.SUBTYPE_MENU_SCREEN),
    ScreenSeries,

    /*
     * Non-menu Screen Types
     */
    AudioScreen(ScreenRecord.SUBTYPE_AUDIO_SCREEN),
    AnimatedScreen(ScreenRecord.SUBTYPE_ANIMATED_SCREEN),
    QuestionScreen(ScreenRecord.SUBTYPE_QUESTION_SCREEN),
    ContentScreen(ScreenRecord.SUBTYPE_CONTENT_SCREEN),

    /*
     * Component Types that can be rendered on a screen
     */
    TextArea(TextAreaComponentRecord.SUBTYPE_TEXT_AREA),
    LetterReader(TextAreaComponentRecord.SUBTYPE_LETTER_READER),
    SyllableReader(TextAreaComponentRecord.SUBTYPE_SYLLABLE_READER),
    WordReader(TextAreaComponentRecord.SUBTYPE_WORD_READER),
    Question,
    ImagePanel(ResourceComponentRecord.SUBTYPE_IMAGE_PANEL);

    private ComponentPresentation(byte subtype) {
        this.subtype = subtype;
    }

    private ComponentPresentation() {
        this((byte) 0);
    }

    private final byte subtype;

    public byte getSubtype() {
        return subtype;
    }

    public static boolean isScreen(ComponentPresentation cp) {
        return EnumSet.of(MenuScreen, AudioScreen, AnimatedScreen, QuestionScreen, ContentScreen).contains(cp);
    }
}
