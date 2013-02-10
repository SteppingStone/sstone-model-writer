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
package org.edc.sstone.project;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ResourceTypeTest {

    @Test
    public void testGetPaths() throws Exception {
        List<String> paths = ResourceType.getPaths();
        assertEquals("audio", paths.get(0));
        assertEquals(ResourceType.PanelImage.directory, paths.get(paths.size() - 1));
    }

    // @Test
    // public void testGetNonLeafPaths() throws Exception {
    // List<String> paths = ResourceType.getNonLeafPaths();
    // assertTrue(paths.size() > 0);
    // assertTrue(paths.size() < ResourceType.getPaths().size());

    // This would be a bad assertion. A path can be non-leaf and still
    // contain a '/' as long as it has at least 1 child
    // for(String p: paths) {
    // assertFalse(p.contains("/"));
    // }
    // }

    @Test
    public void testGetNonLeafPathsSuffix() {
        String[] parents = ResourceType.AudioTrack.getParentPaths();
        assertEquals(1, parents.length);
        assertEquals("audio", parents[0]);

    }

    @Test
    public void testGetPathParts() {
        assertEquals("tracks", ResourceType.AudioTrack.getBasename());
    }

}
