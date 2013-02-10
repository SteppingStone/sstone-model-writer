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

import static org.edc.sstone.record.writer.model.ComponentPresentation.ImagePanel;
import static org.edc.sstone.record.writer.model.ComponentPresentation.LetterReader;
import static org.edc.sstone.record.writer.model.ComponentPresentation.Question;
import static org.edc.sstone.record.writer.model.ComponentPresentation.SyllableReader;
import static org.edc.sstone.record.writer.model.ComponentPresentation.TextArea;
import static org.edc.sstone.record.writer.model.ComponentPresentation.WordReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.Record;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.record.writer.RecordWriterFactory;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter.MenuItemChild;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public class ScreenRecordWriter<R extends ScreenRecord>
        extends TitledComponentRecordWriter<R>
        implements MenuItemChild<ScreenRecordWriter<?>> {

    private List<ComponentRecordWriter<?>> componentRecords = new ArrayList<ComponentRecordWriter<?>>();
    private static final Map<Byte, Set<ComponentPresentation>> POSSIBLE_CHILD_TYPES = new HashMap<Byte, Set<ComponentPresentation>>();
    static {
        POSSIBLE_CHILD_TYPES.put(ScreenRecord.SUBTYPE_MENU_SCREEN,
                EnumSet.of(

                        ComponentPresentation.MenuScreen,
                        ComponentPresentation.ScreenSeries,

                        ComponentPresentation.AudioScreen,
                        ComponentPresentation.AnimatedScreen,
                        ComponentPresentation.QuestionScreen,
                        ComponentPresentation.ContentScreen,

                        TextArea,
                        ImagePanel));

        POSSIBLE_CHILD_TYPES.put(ScreenRecord.SUBTYPE_CONTENT_SCREEN,
                EnumSet.of(TextArea, ImagePanel));

        POSSIBLE_CHILD_TYPES.put(ScreenRecord.SUBTYPE_AUDIO_SCREEN,
                EnumSet.of(TextArea, ImagePanel));

        POSSIBLE_CHILD_TYPES.put(ScreenRecord.SUBTYPE_QUESTION_SCREEN,
                EnumSet.of(TextArea, ImagePanel, Question));

        POSSIBLE_CHILD_TYPES.put(ScreenRecord.SUBTYPE_ANIMATED_SCREEN, EnumSet.of(
                TextArea,
                LetterReader,
                SyllableReader,
                WordReader,
                ImagePanel));
    }

    @SuppressWarnings("unchecked")
    public ScreenRecordWriter() {
        this((R) new ScreenRecord());
    }

    public ScreenRecordWriter(R record) {
        super(record);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        // We do not want the ScreenSeriesRecord to read the components;
        // we want to handle that here b/c we want to eager-load all of them
        getRecord().shouldReadComponentRecords = false;
        super.read(in);
        while (in.hasMoreRecordBytes()) {
            Record rec = in.readRecord();
            componentRecords.add((ComponentRecordWriter<?>) rec);
        }
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeByte(getRecord().navControlByte);
        out.writeUTF(getRecord().resourcePath);
        for (RecordWriter<?> rw : componentRecords)
            out.writeRecord(rw);
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "resourcePath", getResourcePath(),
                        "components", getComponents()
                });
    }

    // NOTE: This only needs to be a rough estimate. This will return a value smaller than the
    // actual buffer size, which is fine.
    @Override
    protected int getEstimatedRecordSize() {
        int ret = getTitleLength() + getStyle().getEstimatedRecordSize();
        for (ComponentRecordWriter<?> rw : componentRecords)
            ret += rw.getEstimatedRecordSize();
        return ret;
    }

    /**
     * TODO: This returns a list with the class ({@link ComponentRecordWriter}) type not the
     * interface ( {@link IComponentRecordWriter}) type. This is a vestige of partially refactoring
     * the inheritance hierarchy when I added the {@link ComponentContainerRecordWriter} interface
     * to polymorphically handle screens in a screen series (within a menu item) and components
     * within a screen. I may pull some more method signatures into the interface and eventually
     * remove the redundant {@link #getComponentWriters()} and {@link #getComponents()} methods.
     * 
     * @return the list of component writers that will be rendered on this screen.
     */
    public List<ComponentRecordWriter<?>> getComponents() {
        return componentRecords;
    }

    /**
     * @param compList
     *            the components to be rendered on the screen
     */
    public void setComponents(List<ComponentRecordWriter<?>> compList) {
        this.componentRecords.clear();
        this.componentRecords.addAll(compList);
    }

    public String getResourcePath() {
        return getRecord().resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        getRecord().resourcePath = resourcePath;
    }

    /**
     * This is a convenience method so we can refer to audio tracks as just "track" in our DSL
     * rather than using the more abstract "resourcePath" accessor name. It would be cleaner to do
     * this in clojure, but it is easier to just add this method. (candidate for removal post v1.0)
     * 
     * @param track
     *            the name of the audio track to play
     */
    public void setTrack(String track) {
        setResourcePath(track);
    }

    public void addComponent(IComponentRecordWriter component) {
        componentRecords.add((ComponentRecordWriter<?>) component);
    }

    public List<? extends IComponentRecordWriter> getComponentWriters() {
        return componentRecords;
    }

    public void insertComponent(IComponentRecordWriter component, int idx) {
        getComponents().add(idx, (ComponentRecordWriter<?>) component);
    }

    public ComponentPresentation getPresentation() {
        switch (getSubType()) {
            case ScreenRecord.SUBTYPE_ANIMATED_SCREEN:
                return ComponentPresentation.AnimatedScreen;
            case ScreenRecord.SUBTYPE_AUDIO_SCREEN:
                return ComponentPresentation.AudioScreen;
            case ScreenRecord.SUBTYPE_MENU_SCREEN:
                return ComponentPresentation.MenuScreen;
            case ScreenRecord.SUBTYPE_QUESTION_SCREEN:
                return ComponentPresentation.QuestionScreen;
        }
        return ComponentPresentation.ContentScreen;
    }

    public boolean acceptsChildren() {
        return true;
    }

    public Set<ComponentPresentation> getPossibleChildTypes() {
        return POSSIBLE_CHILD_TYPES.get(getSubType());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public IComponentRecordWriter newChild(RecordWriterFactory factory, ComponentPresentation componentType) {
        IComponentRecordWriter ret = factory.newComponentWriter(componentType);
        if (getSubType() == ScreenRecord.SUBTYPE_MENU_SCREEN && (ret instanceof MenuItemChild<?>)) {
            ret = new MenuItemRecordWriter((MenuItemChild<?>) ret);
        }
        return ret;
    }

    public boolean isAutoAdvance() {
        return getRecord().isAutoAdvance();
    }

    public void setAutoAdvance(boolean autoAdvance) {
        getRecord().setAutoAdvance(autoAdvance);
    }

    public float getAutoAdvanceDelaySeconds() {
        return ((float) getRecord().getAutoAdvanceDelayIntervals() / (float) ScreenRecord.ADVANCE_DELAY_SECOND_INTERVAL);
    }

    public void setAutoAdvanceDelaySeconds(float val) {
        val = Math.min(val, 16f);
        getRecord().setAutoAdvanceDelayIntervals((int) (val * (float) ScreenRecord.ADVANCE_DELAY_SECOND_INTERVAL));
    }

}
