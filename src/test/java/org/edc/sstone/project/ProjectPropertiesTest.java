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

import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class ProjectPropertiesTest {

    @Test
    public void testProjectProps() throws Exception {
        Project project = Project.newProject();
        ProjectProperties props = new ProjectProperties(project);

        final String alphabetLowerCase = "abcdefghijklmnopqrstuvwxyz";
        final String alphabetUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        final String imageFileType = "png";
        final String audioFileType = "mp3";

        props.setAlphabetLowerCase(alphabetLowerCase);
        props.setAlphabetUpperCase(alphabetUpperCase);

        props.setAudioFileType(audioFileType);
        props.setImageFileType(imageFileType);

        assertEquals(alphabetLowerCase, props.getAlphabetLowerCase());
        assertEquals(alphabetUpperCase, props.getAlphabetUpperCase());

        assertEquals(audioFileType, props.getAudioFileType());
        assertEquals(imageFileType, props.getImageFileType());
    }
}
