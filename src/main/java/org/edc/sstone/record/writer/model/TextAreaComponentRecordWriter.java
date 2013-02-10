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

import java.io.IOException;
import java.util.EnumSet;

import org.edc.sstone.record.reader.model.TextAreaComponentRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public class TextAreaComponentRecordWriter<R extends TextAreaComponentRecord>
        extends ComponentRecordWriter<R> {

    @SuppressWarnings("unchecked")
    public TextAreaComponentRecordWriter() {
        this((R) new TextAreaComponentRecord());
    }

    protected TextAreaComponentRecordWriter(R record) {
        super(record);
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeByte(getReadControlByte());
        out.writeByte((byte) getSyllableSeparator().charAt(0));
        out.writeUTF(getText());
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "syllableSeparator", getSyllableSeparator(),
                        "text", getText()
                });
    }

    public String getSyllableSeparator() {
        return Character.toString((char) getRecord().syllableSeparator);
    }

    public void setSyllableSeparator(String s) {
        if (s != null && s.length() > 0) {
            getRecord().syllableSeparator = (byte) s.charAt(0);
        }
    }

    public void setSyllableSeparator(Character ch) {
        getRecord().syllableSeparator = (byte) ch.charValue();
    }

    @Override
    protected int getEstimatedRecordSize() {
        return getText().length();
    }

    /*
     * Accessors
     */
    public String getText() {
        return getRecord().text;
    }

    public void setText(String text) {
        getRecord().text = fixWhiteSpace(text);
    }

    protected String fixWhiteSpace(String input) {
        String ret = input.replaceAll("\\\r\\\n", "\n");
        ret = ret.replaceAll("\\\r", "\n");
        ret = ret.replaceAll("[ \\\t]+", " ");
        return ret;
    }

    public String getLabel(int maxLength) {
        return abbreviateString(getText(), maxLength);
    }

    public ComponentPresentation getPresentation() {
        switch (getSubType()) {
            case TextAreaComponentRecord.SUBTYPE_LETTER_READER:
                return ComponentPresentation.LetterReader;
            case TextAreaComponentRecord.SUBTYPE_SYLLABLE_READER:
                return ComponentPresentation.SyllableReader;
            case TextAreaComponentRecord.SUBTYPE_WORD_READER:
                return ComponentPresentation.WordReader;
        }
        return ComponentPresentation.TextArea;
    }

    public boolean isReader() {
        return EnumSet.of(ComponentPresentation.LetterReader,
                ComponentPresentation.WordReader,
                ComponentPresentation.SyllableReader).contains(getPresentation());
    }

    public boolean isAudioEnabled() {
        return !getRecord().isSuppressAudio();
    }

    public void setAudioEnabled(boolean enabled) {
        byte val = (byte) (getRecord().readControlByte & (~TextAreaComponentRecord.SUPPRESS_AUDIO));
        val |= enabled ? 0 : TextAreaComponentRecord.SUPPRESS_AUDIO;
        setReadControlByte(val);
    }

    public boolean isReadNonLetters() {
        return getRecord().isReadNonLetters();
    }

    public void setReadNonLetters(boolean readNonLetters) {
        byte val = (byte) (getRecord().readControlByte & (~TextAreaComponentRecord.READ_NON_LETTERS));
        val |= readNonLetters ? TextAreaComponentRecord.READ_NON_LETTERS : 0;
        setReadControlByte(val);
    }

    protected byte getReadControlByte() {
        return getRecord().readControlByte;
    }

    protected void setReadControlByte(byte val) {
        getRecord().readControlByte = val;
    }

}
