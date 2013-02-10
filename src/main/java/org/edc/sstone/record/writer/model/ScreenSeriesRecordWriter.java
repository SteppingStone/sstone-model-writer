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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.IntArrayRecord;
import org.edc.sstone.record.reader.model.RecordHeader;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.record.writer.RecordWriterFactory;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter.MenuItemChild;
import org.edc.sstone.util.StdLib;

public class ScreenSeriesRecordWriter<R extends ScreenSeriesRecord>
        extends RecordWriter<R>
        implements MenuItemChild<RecordWriter<?>>, ComponentContainerRecordWriter, IComponentRecordWriter {

    private static final Set<ComponentPresentation> POSSIBLE_CHILD_TYPES = EnumSet.of(
            ComponentPresentation.AudioScreen,
            ComponentPresentation.AnimatedScreen,
            ComponentPresentation.QuestionScreen,
            ComponentPresentation.ContentScreen
            );

    @SuppressWarnings("unchecked")
    public ScreenSeriesRecordWriter() {
        this((R) new ScreenSeriesRecord());
    }

    protected ScreenSeriesRecordWriter(R record) {
        super(record);
    }

    private List<ScreenRecordWriter<? extends ScreenRecord>> screens = new ArrayList<ScreenRecordWriter<? extends ScreenRecord>>();

    @Override
    @SuppressWarnings("unchecked")
    public void read(RecordInputStream in) throws IOException {
        super.read(in);
        for (int i = 0; i < getRecord().screenRecordLengths.length(); i++) {
            ScreenRecordWriter<ScreenRecord> srw = (ScreenRecordWriter<ScreenRecord>) in.readRecord();
            screens.add(srw);
        }
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);

        int numRecords = screens.size();
        int[] lengths = new int[numRecords];
        byte[][] screenBytes = new byte[numRecords][];

        for (int i = 0; i < numRecords; i++) {
            screenBytes[i] = screens.get(i).toByteArray();
            lengths[i] = screenBytes[i].length;
        }

        // First we need to write the lengths index:
        out.writeRecord(new IntArrayRecordWriter(lengths));

        // Now we can write the records
        for (int i = 0; i < numRecords; i++)
            out.write(screenBytes[i]);
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "screens", getScreens()
                });
    }

    @Override
    protected int getEstimatedRecordSize() {
        // int ret = getTitleLength() + getStyle().getEstimatedRecordSize();
        int ret = 0;
        for (ScreenRecordWriter<?> rw : screens)
            ret += rw.getEstimatedRecordSize();
        return ret;
    }

    public List<ScreenRecordWriter<? extends ScreenRecord>> getScreens() {
        return screens;
    }

    public void setScreens(List<ScreenRecordWriter<ScreenRecord>> screens) {
        this.screens.clear();
        this.screens.addAll(screens);
    }

    /**
     * This is only intended to be used by {@link ScreenSeriesRecordWriter} when writing out a
     * screen series. When reading record writers, register IntArrayRecord with the factory.
     * 
     * @author Greg Orlowski
     */
    protected static class IntArrayRecordWriter extends RecordWriter<IntArrayRecord> {

        protected IntArrayRecordWriter() {
            super(new IntArrayRecord());
        }

        protected IntArrayRecordWriter(int[] values) {
            this();
            setValues(values);
        }

        public void setValues(int[] values) {
            getRecord().intValues = values;
        }

        protected void writeData(RecordOutputStream out) throws IOException {
            // NOTE: we will NOT write the subtype byte
            super.writeData(out);
            out.writeInts(getRecord().intValues);
        }

        @Override
        protected int getEstimatedRecordSize() {
            return RecordHeader.getHeaderSize() + (getRecord().intValues.length * 4);
        }

    }

    public List<? extends IComponentRecordWriter> getComponentWriters() {
        return getScreens();
    }

    public void insertComponent(IComponentRecordWriter component, int idx) {
        getScreens().add(idx, (ScreenRecordWriter<?>) component);
    }

    // This should never be called. I could split up the interfaces, but since
    // it would just be for this one case, I'll just create this dummy implementation
    public String getLabel(int maxLength) {
        return "screen series";
    }

    public ComponentPresentation getPresentation() {
        return ComponentPresentation.ScreenSeries;
    }

    public boolean acceptsChildren() {
        return true;
    }

    public Set<ComponentPresentation> getPossibleChildTypes() {
        return POSSIBLE_CHILD_TYPES;
    }

    public IComponentRecordWriter newChild(RecordWriterFactory factory, ComponentPresentation componentType) {
        return factory.newComponentWriter(componentType);
    }
}
