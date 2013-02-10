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
package org.edc.sstone.file;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.edc.sstone.Constants;
import org.edc.sstone.project.ResourceType;
import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ZipEntryFileTest {

    @Test
    public void testListFiles() throws Exception {
        ZipEntryFile zef = new ZipEntryFile(getZipFile(), ResourceType.LetterAudio.directory);
        Set<String> foundFiles = new HashSet<String>();
        Set<String> foundNames = new HashSet<String>();
        for (File f : zef.listFiles()) {
            foundFiles.add(f.getPath());
            foundNames.add(f.getName());
            // System.out.println("name : " + f.getParentFile());
        }
        assertTrue(foundFiles.contains("audio/letters/g.mp3"));
        assertTrue(foundNames.contains("g.mp3"));
    }

    @Test
    public void testGetParentFile() throws Exception {
        ZipEntryFile zef = new ZipEntryFile(getZipFile(), ResourceType.LetterAudio.directory);
        assertEquals("audio", zef.getParent());
        assertEquals("audio", zef.getParentFile().getPath());
    }

    protected File getZipFile() {
        URL url = getClass().getResource("/modules/mod1." + Constants.PROJECT_FILE_EXT);
        File zipFile = new File(url.getFile());
        return zipFile;
    }

    @Test
    public void testList() throws Exception {
        ZipEntryFile zef = new ZipEntryFile(getZipFile(), ResourceType.LetterAudio.directory);
        Set<String> children = new HashSet<String>(Arrays.asList(zef.list()));
        assertTrue(children.contains("g.mp3"));
    }

}
