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

/**
 * A domain object to encapsulate the properties currently used in module.properties. This is here
 * to give us a typesafe way to manage the properties
 * 
 * @author Greg Orlowski
 */
public class ProjectProperties {

    private static final String imageFileTypeProp = "image.filetype";
    private static final String audioFileTypeProp = "audio.filetype";

    private static final String alphabetLowerCaseProp = "alphabet.lowercase";
    private static final String alphabetUpperCaseProp = "alphabet.uppercase";

    private final Project project;

    public ProjectProperties(Project project) {
        this.project = project;
    }

    public String getImageFileType() {
        return project.getProperty(imageFileTypeProp);
    }

    public void setImageFileType(String imageFileType) {
        project.setProperty(imageFileTypeProp, imageFileType);
    }

    public String getAudioFileType() {
        return project.getProperty(audioFileTypeProp);
    }

    public void setAudioFileType(String val) {
        project.setProperty(audioFileTypeProp, val);
    }

    public String getAlphabetLowerCase() {
        return project.getProperty(alphabetLowerCaseProp);
    }

    public void setAlphabetLowerCase(final String alphabetLowerCase) {
        project.setProperty(alphabetLowerCaseProp, alphabetLowerCase);
    }

    public String getAlphabetUpperCase() {
        return project.getProperty(alphabetUpperCaseProp);
    }

    public void setAlphabetUpperCase(final String alphabetUpperCase) {
        project.setProperty(alphabetUpperCaseProp, alphabetUpperCase);
    }
}
