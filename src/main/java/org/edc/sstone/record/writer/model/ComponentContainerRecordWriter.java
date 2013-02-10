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

import java.util.List;
import java.util.Set;

import org.edc.sstone.record.writer.RecordWriterFactory;

/**
 *  @author Greg Orlowski
 */
public interface ComponentContainerRecordWriter extends IComponentRecordWriter {

    /**
     * @param component
     *            the component record writer to insert into this container&39;s list of components
     * @param idx
     *            the index at which we should insert the component
     */
    public void insertComponent(IComponentRecordWriter component, int idx);

    /**
     * @return a list of this container&#39;s child component writers
     */
    public List<? extends IComponentRecordWriter> getComponentWriters();

    public Set<ComponentPresentation> getPossibleChildTypes();

    public IComponentRecordWriter newChild(RecordWriterFactory factory, ComponentPresentation componentType);

}
