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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Greg Orlowski
 */
public enum ResourceType {

    PanelImage("images/panels"),
    WordAudio("audio/words"),

    /**
     * NOTE: we will need special-handling for syllables because we will store in:
     * /audio/syllables/WORD/SYLLABLE_INDEX.FILE_EXT
     */
    SyllableAudio("audio/syllables"),
    LetterAudio("audio/letters"),
    AudioTrack("audio/tracks");

    public final String directory;

    private ResourceType(String directory) {
        this.directory = directory;
    }

    public String[] getParentPaths() {
        String[] parts = getPathParts();
        return Arrays.copyOf(parts, parts.length - 1);
    }

    public String getBasename() {
        String[] parts = getPathParts();
        return parts[parts.length - 1];
    }

    private String[] getPathParts() {
        return directory.split("/");
    }

    /**
     * @return a unique list of resource paths, including parent directories, sorted first by depth
     *         (parent directories first) and then alphabetically.
     */
    public static List<String> getPaths() {
        Set<String> paths = new HashSet<String>();
        for (ResourceType resourceType : values()) {
            paths.add(new File(resourceType.directory).getParent());
            paths.add(resourceType.directory);
        }

        List<String> ret = new ArrayList<String>(paths);
        Collections.sort(ret, new DepthFirstSorter());
        return ret;
    }

    static class DepthFirstSorter implements Comparator<String> {
        public int compare(String o1, String o2) {
            int ret = o1.split("/").length - o2.split("/").length;
            return ret != 0 ? ret : o1.compareTo(o2);
        }
    }

    public static Set<String> getFilePaths(ResourceType resourceType, String[] names) {
        Set<String> ret = new LinkedHashSet<String>();
        for (String n : names) {
            ret.add(resourceType.directory + '/' + n);
        }
        return ret;
    }

    // public static List<String> getNonLeafPaths() {
    // Set<String> paths = new HashSet<String>();
    // for (String path : getPaths()) {
    // if (path.indexOf('/') == -1) {
    // paths.add(path);
    // } else {
    // paths.add(path.substring(0, path.lastIndexOf('/')));
    // }
    // }
    // List<String> ret = new ArrayList<String>(paths);
    // Collections.sort(ret, new DepthFirstSorter());
    // return ret;
    // }

}
