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
import java.util.List;
import java.util.Set;

import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.MenuItemRecord;
import org.edc.sstone.record.reader.model.Record;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.RecordOutputStream;
import org.edc.sstone.record.writer.RecordWriterFactory;
import org.edc.sstone.util.StdLib;

public class MenuItemRecordWriter<R extends MenuItemRecord> extends TitledComponentRecordWriter<R>
        implements ComponentContainerRecordWriter {

    protected StyleRecordWriter<StyleRecord> branchStyle = new StyleRecordWriter<StyleRecord>();
    protected MenuItemChild<?> child;

    @SuppressWarnings("unchecked")
    public MenuItemRecordWriter() {
        this((R) new MenuItemRecord());
    }

    @SuppressWarnings("unchecked")
    public MenuItemRecordWriter(MenuItemChild<?> child) {
        this((R) new MenuItemRecord());
        setChild(child);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(RecordInputStream in) throws IOException {
        super.read(in);

        // read the child
        Record childRecord = in.readRecord();
        switch (childRecord.getClassUID()) {
            case ScreenRecord.CLASS_UID:
                setChild((ScreenRecordWriter<ScreenRecord>) childRecord);
                break;
            case ScreenSeriesRecord.CLASS_UID:
                setChild((ScreenSeriesRecordWriter<ScreenSeriesRecord>) childRecord);
                break;
        }
    }

    @Override
    protected void postRead() {
        super.postRead();
        branchStyle.setRecord(getRecord().branchStyleRecord);
    }

    // @SuppressWarnings("unchecked")
    // public MenuItemRecordWriter() {
    // this((R) new MenuItemRecord());
    // }

    protected MenuItemRecordWriter(R record) {
        super(record);
    }

    @Override
    protected void writeData(RecordOutputStream out) throws IOException {
        super.writeData(out);
        out.writeUTF(getIconImagePath());
        out.writeRecord(branchStyle);
        out.writeUTF(getBranchTitle());
        out.write(child.toByteArray());
    }

    @Override
    protected Object[] fieldValues() {
        return StdLib.mergeArray(
                super.fieldValues(),
                new Object[] {
                        "icon", getIconImagePath(),
                        "branchStyle", getBranchStyle(),
                        "branchTitle", getBranchTitle(),
                        "child", getChild()
                });
    }

    // TODO: I could optimize, but it doesn't really matter.
    // @Override
    // protected int getEstimatedRecordSize() {
    // return super.getEstimatedRecordSize();
    // }

    /*
     * Accessors
     */
    public String getIconImagePath() {
        return getRecord().iconImagePath;
    }

    public void setIconImagePath(String path) {
        getRecord().iconImagePath = path;
    }

    // convenience method name for DSL
    public void setIcon(String path) {
        setIconImagePath(path);
    }

    public StyleRecordWriter<StyleRecord> getBranchStyle() {
        return branchStyle;
    }

    public void setBranchStyle(StyleRecordWriter<StyleRecord> branchStyle) {
        this.branchStyle = branchStyle;
    }

    public String getBranchTitle() {
        return getRecord().branchTitle;
    }

    public void setBranchTitle(String branchTitle) {
        this.getRecord().branchTitle = branchTitle;
    }

    @SuppressWarnings("unchecked")
    public <C extends MenuItemChild<?>> C getChild() {
        return (C) child;
    }

    /**
     * Set the child screen or screen series that will be displayed when this menu item is selected.
     * 
     * @param child
     */
    public void setChild(MenuItemChild<?> child) {
        this.child = child;
        getRecord().subType = isModuleHeader() ? MenuItemRecord.MODULE_HEADER : 0;
        if (child instanceof ScreenRecordWriter) {
            getRecord().subType |= MenuItemRecord.SCREEN_POINTER;
        } else if (child instanceof ScreenSeriesRecordWriter) {
            getRecord().subType |= MenuItemRecord.SCREEN_SERIES_POINTER;
        }
    }

    public boolean isModuleHeader() {
        return isSubTypeMaskSet(MenuItemRecord.MODULE_HEADER);
    }

    /*
     * TODO: I think these methods will come in handy when we're programming the sstone-DAT. If we
     * don't need them then remove them.
     */
    public boolean isScreenPointer() {
        return (child instanceof ScreenRecordWriter);
    }

    public boolean isScreenSeriesPointer() {
        return (child instanceof ScreenSeriesRecordWriter);
    }

    /*
     * OTHER
     */
    /**
     * For now this is just a marker interface that tells us that a {@link RecordWriter} can be the
     * child of a {@link MenuItemRecordWriter}.
     * 
     * @author Greg Orlowski
     */
    public static interface MenuItemChild<C extends RecordWriter<?>> extends IRecordWriter,
            ComponentContainerRecordWriter {
    }

    public List<? extends IComponentRecordWriter> getComponentWriters() {
        return getChild().getComponentWriters();
    }

    // @SuppressWarnings({ "unchecked", "rawtypes" })
    // public void insertComponent(IComponentRecordWriter component, int idx) {
    // List componentWriters = getComponentWriters();
    // componentWriters.add(idx, component);
    // }

    public void insertComponent(IComponentRecordWriter component, int idx) {
        getChild().insertComponent(component, idx);
    }

    public ComponentPresentation getPresentation() {
        if (getChild() instanceof IComponentRecordWriter) {
            return ((IComponentRecordWriter) getChild()).getPresentation();
        }
        return null;
    }

    public boolean acceptsChildren() {
        return true;
    }

    public Set<ComponentPresentation> getPossibleChildTypes() {
        if (getChild() instanceof ComponentContainerRecordWriter) {
            return getChild().getPossibleChildTypes();
        }
        return null;
    }

    public IComponentRecordWriter newChild(RecordWriterFactory factory, ComponentPresentation componentType) {
        if (getChild() instanceof ComponentContainerRecordWriter) {
            return getChild().newChild(factory, componentType);
        }
        return null;
    }

}
