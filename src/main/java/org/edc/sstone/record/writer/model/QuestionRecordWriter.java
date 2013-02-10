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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.edc.sstone.record.reader.model.QuestionRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public class QuestionRecordWriter<R extends QuestionRecord> extends ComponentRecordWriter<R> {

    private transient String answer;

    @SuppressWarnings("unchecked")
    public QuestionRecordWriter() {
        this((R) new QuestionRecord());
        if (getRecord().answers == null) {
            getRecord().answers = new String[] {};
        }
    }

    protected QuestionRecordWriter(R record) {
        super(record);
    }

    @Override
    protected Object[] fieldValues() {
        setAnswerIndexFromAnswer();
        return StdLib.mergeArray(super.fieldValues(),
                new Object[] {
                        "question", getQuestion(),
                        "answers", StdLib.arrayToString(getAnswerArray()),
                        "correct-answer-idx", getCorrectAnswerIndex()
                });
    }

    public String getQuestion() {
        return getRecord().question;
    }

    public void setQuestion(String question) {
        getRecord().question = question;
    }

    public List<String> getAnswers() {
        return getRecord().answers == null
                ? new ArrayList<String>()
                : new ArrayList<String>(Arrays.asList(getRecord().answers));
    }

    /**
     * @param answers
     *            the list of answers to the question. For convenience in our DSL, I have this as
     *            List<Object> rather than List<String> so we can specify, e.g., Long literals
     *            rather than strings containing numeric values in our DSL.
     */
    public void setAnswers(List<Object> answers) {
        if (answers == null) {
            getRecord().answers = null;
            setAnswerCount(0);
        } else {
            getRecord().answers = new String[answers.size()];
            for (int i = 0; i < answers.size(); i++) {
                getRecord().answers[i] = answers.get(i).toString();
            }
            setAnswerCount(answers.size());
        }
    }

    public String[] getAnswerArray() {
        return getRecord().answers;
    }

    public void setAnswers(Object... answers) {
        getRecord().answers = new String[answers.length];
        for (int i = 0; i < answers.length; i++) {
            getRecord().answers[i] = answers[i].toString();
        }
        setAnswerCount(answers.length);
    }

    public int getAnswerCount() {
        return getRecord().getAnswerCount();
    }

    void setAnswerCount(int cnt) {
        getRecord().answerInfo = (byte) ((getRecord().answerInfo & 0xF0) | cnt);
    }

    public int getCorrectAnswerIndex() {
        return getRecord().getCorrectAnswerIndex();
    }

    public void setCorrectAnswerIndex(int i) {
        getRecord().answerInfo &= 0x0F;
        getRecord().answerInfo |= (byte) (i << 4);
    }

    /**
     * NOTE: this is zero-indexed
     * 
     * @param idx
     */
    public void setAnswerIndex(int idx) {
        getRecord().answerInfo = (byte) ((getRecord().answerInfo & 0x0F) | (idx << 4));
    }

    private void setAnswerIndexFromAnswer() {
        if (answer != null) {
            String[] answers = getAnswerArray();
            for (int i = 0; i < answers.length; i++) {
                String a = answers[i].trim();
                if (answer.trim().equals(a)) {
                    setAnswerIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);

        setAnswerIndexFromAnswer();

        out.writeByte(getRecord().answerInfo);
        out.writeUTF(getRecord().question);

        String[] answers = getAnswerArray();
        for (int i = 0; i < answers.length; i++) {
            out.writeUTF(answers[i]);
        }
    }

    @Override
    protected int getEstimatedRecordSize() {
        if (getRecord() == null || getRecord().answers == null || getRecord().question == null)
            return 8;

        int ret = getRecord().question.length();
        for (int i = 0; i < getRecord().answers.length; i++)
            ret += getRecord().answers[i].length();

        return ret;
    }

    /**
     * This allows us to set the correct answer by string rather than by index, which might be nice
     * for our DSL.
     */
    public void setCorrectAnswer(String answer) {
        this.answer = answer;
    }

    public String getLabel(int maxLength) {
        return abbreviateString(getQuestion(), maxLength);
    }

    public ComponentPresentation getPresentation() {
        return ComponentPresentation.Question;
    }
}
