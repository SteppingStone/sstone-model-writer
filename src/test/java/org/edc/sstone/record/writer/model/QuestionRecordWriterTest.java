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

import org.edc.sstone.record.reader.model.QuestionRecord;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class QuestionRecordWriterTest extends RecordSerializationTestCase {

    @Test
    public void testBitImpl() {
        QuestionRecordWriter<QuestionRecord> rec = new QuestionRecordWriter<QuestionRecord>();

        Object[] answers = new String[] { "a", "b", "c", "d" };
        rec.setAnswers(answers);
        rec.setAnswerIndex(2);

        assertEquals(answers.length, rec.getAnswerCount());
        assertEquals(2, rec.getCorrectAnswerIndex());

        assertEquals("c", rec.getAnswerArray()[rec.getCorrectAnswerIndex()]);
        assertEquals(answers.length, rec.getAnswerArray().length);
    }

    @Test
    public void test() throws IOException {
        QuestionRecordWriter<QuestionRecord> rw = new QuestionRecordWriter<QuestionRecord>();

        String question = "What is the capital of Mali?";

        rw.setSubType((byte) 1);
        rw.setQuestion(question);

        int answerIdx = 1;
        Object[] answers = new String[] {
                "Timbuktu",
                "Bamako",
                "Gao",
                "Kidal",
                "Segou",
                "Sikasso"
        };

        // test setting the answer by value rather than by index
        // rw.setAnswerIndex(answerIdx);

        rw.setCorrectAnswer("Bamako");
        rw.setAnswers(answers);

        byte[] bytes = rw.toByteArray();

        QuestionRecord r = readRecord(bytes);
        assertNull(r.styleRecord);

        assertEquals(question, r.question);
        assertEquals(answerIdx, r.getCorrectAnswerIndex());
        assertEquals(answers.length, r.getAnswerCount());

        for (int i = 0; i < answers.length; i++) {
            assertEquals(answers[i], r.answers[i]);
        }
    }

}
