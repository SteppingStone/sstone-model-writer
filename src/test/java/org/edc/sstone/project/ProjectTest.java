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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.edc.sstone.Constants;
import org.edc.sstone.record.reader.model.MenuItemRecord;
import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.record.writer.model.ScreenSeriesRecordWriter;
import org.edc.sstone.util.IOUtil;
import org.junit.Test;

/*
 * TODO: make sure all temp files are cleaned up when the tests run
 */
/**
 * @author Greg Orlowski
 */
public class ProjectTest {

    @Test
    public void testLoadProperties() throws Exception {
        Project project = new Project();
        URL url = getClass().getResource("/modules/mod1." + Constants.PROJECT_FILE_EXT);
        File projectFile = null;
        if ("file".equals(url.getProtocol())) {
            projectFile = new File(url.getFile());
        }
        
        Properties props = project.loadProperties(projectFile);
        String alphabet = props.getProperty("alphabet.lowercase");
        assertEquals("abcdefghijklmnopqrstuvwxyz", alphabet);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadIndex() throws Exception {
        Project project = new Project();

        InputStream indexFileIn = getClass().getResourceAsStream("/modules/mod1/index.mod");
        MenuItemRecordWriter<MenuItemRecord> rootNode = project.loadIndex(indexFileIn);

        assertEquals("Module One", rootNode.getTitle());

        ScreenRecordWriter<ScreenRecord> mainMenu = rootNode.getChild();
        assertEquals("Stepping Stone", mainMenu.getTitle());
        assertEquals(4, mainMenu.getComponentWriters().size());

        assertEquals(4, mainMenu.getComponentWriters().size());

        ResourceComponentRecordWriter<ResourceComponentRecord> logoImageComponent = (ResourceComponentRecordWriter<ResourceComponentRecord>) mainMenu
                .getComponentWriters().get(0);

        assertEquals("ss_logo", logoImageComponent.getFile());

        MenuItemRecordWriter<MenuItemRecord> mathSeriesWriterMenuItem = (MenuItemRecordWriter<MenuItemRecord>) mainMenu
                .getComponentWriters().get(2);

        ScreenSeriesRecordWriter<ScreenSeriesRecord> mathSeries = mathSeriesWriterMenuItem.getChild();

        assertEquals(2, mathSeries.getScreens().size());
        assertEquals("math_screen1", mathSeries.getScreens().get(0).getTitle());

    }

    @Test
    public void testLoad() throws Exception {
        Project project = testMod1Project();
        assertEquals("Module One", project.getIndex().getTitle());
        assertEquals("abcdefghijklmnopqrstuvwxyz", project.getProperty("alphabet.lowercase"));

        List<String> letterAudioClips = project.listResources(ResourceType.LetterAudio);
        for (String s : new String[] { "a.mp3", "z.mp3", "p.mp3" }) {
            assertTrue(letterAudioClips.contains(s));
        }

        List<String> syllableAudioClips = project.listResources(ResourceType.SyllableAudio);

        for (String s : new String[] { "component/3.mp3", "component/2.mp3", "syllable/2.mp3" }) {
            assertTrue(syllableAudioClips.contains(s));
        }

    }

    protected Project testMod1Project() throws IOException, ProjectLoadException {
        URL url = getClass().getResource("/modules/mod1." + Constants.PROJECT_FILE_EXT);
        File projectFile = null;
        if ("file".equals(url.getProtocol())) {
            projectFile = new File(url.getFile());
        }

        File tempFile = File.createTempFile("mod1." + Constants.PROJECT_FILE_EXT + ".", ".tmp");

        // Comment this out to inspect the temp file by hand after the test runs
        tempFile.deleteOnExit();

        File tempProjectFile = File.createTempFile("mod1_projectFile." + Constants.PROJECT_FILE_EXT + ".", ".tmp");
        IOUtil.copyFile(projectFile, tempProjectFile);

        Project project = Project.load(tempProjectFile, tempFile);
        return project;
    }

    @Test
    public void testNewProject() throws Exception {
        Project project = Project.newProject();

        // it would be better to test the contents, but at least verify that the file
        // exists as a normal file
        assertTrue(project.tempFile.isFile());
        assertTrue(project.tempFile.length() > 0);
        assertEquals("abcdefghijklmnopqrstuvwxyz", project.getProperty("alphabet.lowercase"));
    }

    @Test
    public void testSaveAs() throws Exception {
        Project project = null;
        File projectFile = null;

        try {
            projectFile = File.createTempFile("sstone_test_saveas.", ".tmp." + Constants.PROJECT_FILE_EXT);
            projectFile.deleteOnExit();
            project = Project.newProject();
            project.saveAs(projectFile);
            assertTrue(projectFile.length() > 0);
        } finally {
            cleanupFiles(project);
        }

        URL url = getClass().getResource("/modules/mod1." + Constants.PROJECT_FILE_EXT);
        projectFile = null;
        if ("file".equals(url.getProtocol())) {
            projectFile = new File(url.getFile());
        }
        File tempFile = File.createTempFile("mod1." + Constants.PROJECT_FILE_EXT + ".", ".tmp");
        tempFile.deleteOnExit();

        try {
            project = Project.load(projectFile, tempFile);
            File saveAsFile = File.createTempFile("sstone_resave_loaded_file.", ".tmp." + Constants.PROJECT_FILE_EXT);
            saveAsFile.deleteOnExit();
            project.saveAs(saveAsFile);
            assertTrue(saveAsFile.length() > 0);
        } finally {
            cleanupFiles(project);
        }
    }

    private void cleanupFiles(Project project) {
        if (project != null) {
            if (!("mod1." + Constants.PROJECT_FILE_EXT).equals(project.tempFile.getName()))
                IOUtil.deleteGracefully(project.tempFile);

            if (!("mod1." + Constants.PROJECT_FILE_EXT).equals(project.projectFile.getName()))
                IOUtil.deleteGracefully(project.projectFile);
        }
    }

    @Test
    public void testAddResourcesToMod1() throws Exception {
        Project project = testMod1Project();
        File f1 = getBinTempFile();
        project.addResources(ResourceType.AudioTrack, f1);
        project.save();

        List<String> tracks = project.listResources(ResourceType.AudioTrack);
        assertEquals(3, tracks.size());
        assertTrue(tracks.contains(f1.getName()));
    }

    protected File getBinTempFile() throws IOException {
        File f1 = File.createTempFile("xyz", ".tmp");
        f1.deleteOnExit();

        FileOutputStream out = new FileOutputStream(f1);
        out.write(new byte[] { 1, 2, 3 });
        out.close();
        return f1;
    }

    @Test
    public void testAddResources() throws Exception {
        File f1 = getBinTempFile();

        Project project = Project.newProject();
        project.addResources(ResourceType.AudioTrack, f1);

        List<String> tracks = project.listResources(ResourceType.AudioTrack);
        assertEquals(1, tracks.size());
        assertEquals(f1.getName(), tracks.get(0));
    }
}
