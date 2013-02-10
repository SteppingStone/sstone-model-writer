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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.edc.sstone.Constants;
import org.edc.sstone.record.reader.RecordFactory;
import org.edc.sstone.record.reader.RecordInputStream;
import org.edc.sstone.record.reader.model.IntArrayRecord;
import org.edc.sstone.record.reader.model.MenuItemRecord;
import org.edc.sstone.record.reader.model.QuestionRecord;
import org.edc.sstone.record.reader.model.Record;
import org.edc.sstone.record.reader.model.ResourceComponentRecord;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.reader.model.ScreenSeriesRecord;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.reader.model.TextAreaComponentRecord;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter.MenuItemChild;
import org.edc.sstone.record.writer.model.QuestionRecordWriter;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.record.writer.model.ScreenSeriesRecordWriter;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;
import org.edc.sstone.util.IOUtil;
import org.edc.sstone.util.PlatformUtil;

/**
 * Responsible for reading + writing an entire Stepping Stone project bundle from/to a stream or zip
 * file.
 * 
 * @author Greg Orlowski
 */
public class Project {

    private MenuItemRecordWriter<MenuItemRecord> index;
    protected File projectFile;
    protected File tempFile;
    private Properties properties;
    protected RecordFactory recordFactory = new RecordFactory();

    // TODO: replace property strings with constants
    protected static Properties DEFAULT_PROJECT_PROPERTIES = new Properties();
    static {
        DEFAULT_PROJECT_PROPERTIES.put("alphabet.lowercase", "abcdefghijklmnopqrstuvwxyz");
        DEFAULT_PROJECT_PROPERTIES.put("alphabet.uppercase", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        DEFAULT_PROJECT_PROPERTIES.put(Constants.IMAGE_FILETYPE, "png");
        DEFAULT_PROJECT_PROPERTIES.put(Constants.AUDIO_FILETYPE, "mp3");
    }

    protected Project() {
        initRecordFactory();
    }

    /**
     * Load and initialize a Project from a project zip file.
     * 
     * @param projectFile
     *            The zip file to load
     * @return an initialized Project
     * @throws ProjectLoadException
     */
    public static Project load(File projectFile) throws ProjectLoadException {
        return Project.load(projectFile, tempFileFor(projectFile));
    }

    public static File tempFileFor(File projectFile) {
        return PlatformUtil.tempFile(projectFile);
    }

    public static Project recover(File projectFile) throws ProjectLoadException {
        File tempFile = tempFileFor(projectFile);
        Project ret = null;
        try {
            IOUtil.copyFile(tempFile, projectFile);
            ret = load(projectFile);
        } catch (IOException ioe) {
            throw new ProjectLoadException(ioe);
        }
        return ret;
    }

    public static boolean hasUnsavedTempFile(File projectFile) {
        File tempFile = PlatformUtil.tempFile(projectFile);
        if (tempFile != null && tempFile.isFile() && tempFile.lastModified() > projectFile.lastModified()) {
            return true;
        }
        return false;
    }

    public static Project newProject() throws IOException {
        Project project = new Project();
        project.initZipFile();
        project.properties = new Properties();
        project.properties.putAll(DEFAULT_PROJECT_PROPERTIES);

        project.createNewIndex();

        return project;
    }

    protected void createNewIndex() {
        ScreenRecordWriter<ScreenRecord> mainMenuScreen = new ScreenRecordWriter<ScreenRecord>();
        mainMenuScreen.setSubType(ScreenRecord.SUBTYPE_MENU_SCREEN);
        mainMenuScreen.setTitle("Main Menu"); // TODO: i18n main menu title

        index = newRootNode();
        index.setChild(mainMenuScreen);
    }

    protected MenuItemRecordWriter<MenuItemRecord> newRootNode() {
        MenuItemRecordWriter<MenuItemRecord> ret = new MenuItemRecordWriter<MenuItemRecord>();
        ret.setTitle("New Project");// TODO: i18n project title
        return ret;
    }

    public void save() throws IOException {
        File tmp = createTempSaveFile();
        ZipOutputStream zout = null;
        try {
            zout = copyZipContents(tempFile, tmp,
                    set(Constants.MODULE_INDEX_FILENAME, Constants.MODULE_PROPERTIES_FILENAME));
            zout.flush();

            // Write the properties file and index
            IOUtil.writeZipEntry(zout, Constants.MODULE_INDEX_FILENAME,
                    new ByteArrayInputStream(index.toByteArray()));

            zout.putNextEntry(new ZipEntry(Constants.MODULE_PROPERTIES_FILENAME));
            IOUtil.writeProperties(properties, zout);
            zout.flush();
            zout.closeEntry();

            zout.finish();
            IOUtil.closeGracefully(zout);

            IOUtil.copyFile(tmp, tempFile);
            IOUtil.copyFile(tmp, projectFile);
        } finally {
            IOUtil.closeGracefully(zout);
            IOUtil.deleteGracefully(tmp);
        }
    }

    // TODO: throw business exception
    public void saveAs(File file) throws IOException {
        File oldTemp = tempFile;
        if (!file.getName().endsWith('.' + Constants.PROJECT_FILE_EXT)) {
            file = new File(file.getAbsolutePath() + '.' + Constants.PROJECT_FILE_EXT);
        }
        projectFile = file;
        save();

        tempFile = PlatformUtil.tempFile(file);
        IOUtil.copyFile(projectFile, tempFile);

        oldTemp.delete();
    }

    public void deleteTempFile() {
        tempFile.delete();
    }

    protected Set<String> set(String... elements) {
        return new HashSet<String>(Arrays.asList(elements));
    }

    /**
     * 
     * @param sourceZipFile
     *            the source zip file
     * @param destZipFile
     *            the destionation zip file
     * @return the still-open {@link ZipOutputStream} to create destZipFile
     * @throws IOException
     */
    protected ZipOutputStream copyZipContents(File sourceZipFile, File destZipFile, Collection<String> excludedFiles)
            throws IOException {
        ZipOutputStream zout = null;
        ZipInputStream currentZipIn = null;
        byte[] buff = new byte[4096];
        try {
            zout = new ZipOutputStream(new FileOutputStream(destZipFile));
            currentZipIn = new ZipInputStream(new FileInputStream(sourceZipFile));
            for (ZipEntry inputEntry; (inputEntry = currentZipIn.getNextEntry()) != null;) {
                if (excludedFiles == null || !excludedFiles.contains(inputEntry.getName())) {
                    ZipEntry outputEntry = new ZipEntry(inputEntry.getName());
                    zout.putNextEntry(outputEntry);
                    if (!inputEntry.isDirectory()) {
                        int bytesRead = 0;
                        while ((bytesRead = currentZipIn.read(buff)) != -1) {
                            if (bytesRead > 0) {
                                zout.write(buff, 0, bytesRead);
                            }
                        }
                    }
                    zout.closeEntry();
                }
            }
        } finally {
            IOUtil.closeGracefully(currentZipIn);
        }
        return zout;
    }

    public void addResources(ResourceType resourceType, String parentDir, File... files) throws IOException {
        File tmp = createTempSaveFile();
        ZipOutputStream zout = null;
        try {
            zout = copyZipContents(tempFile, tmp, null);
            for (File file : files) {
                String entryName = (parentDir == null || parentDir.isEmpty())
                        ? resourceType.directory + '/' + file.getName()
                        : resourceType.directory + '/' + parentDir + '/' + file.getName();
                IOUtil.writeZipEntry(zout, entryName, new FileInputStream(file));
            }
            zout.flush();
            zout.close();
            IOUtil.copyFile(tmp, tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(zout);
            IOUtil.deleteGracefully(tmp);
        }
    }

    public void addResources(ResourceType resourceType, File... files) throws IOException {
        addResources(resourceType, null, files);
    }

    public void removeResources(ResourceType resourceType, String[] filenames) throws IOException {
        File tmp = createTempSaveFile();
        ZipOutputStream zout = null;
        try {
            zout = copyZipContents(tempFile, tmp, ResourceType.getFilePaths(resourceType, filenames));
            zout.flush();
            zout.close();
            IOUtil.copyFile(tmp, tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(zout);
            IOUtil.deleteGracefully(tmp);
        }
    }

    protected File createTempSaveFile() throws IOException {
        File tmp = File.createTempFile("sstone_project_save.", ".tmp." + Constants.PROJECT_FILE_EXT);
        tmp.deleteOnExit();
        return tmp;
    }

    /**
     * Initialize a new zip file with a skeletal directory structure
     */
    protected void initZipFile() throws IOException {
        tempFile = File.createTempFile("sstone_module." + Constants.PROJECT_FILE_EXT + '.', ".tmp");
        tempFile.deleteOnExit();

        // ZipFile zipFile = new ZipFile(tempFile);
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(tempFile));
            for (String path : ResourceType.getPaths()) {
                if (!path.endsWith("/")) {
                    path = path + "/";
                }
                ZipEntry entry = new ZipEntry(path);
                zipOut.putNextEntry(entry);
                zipOut.closeEntry();
            }
            zipOut.flush();
            zipOut.finish();
        } finally {
            IOUtil.closeGracefully(zipOut);
        }
    }

    public List<String> listResources(ResourceType resourceType) {
        List<String> ret = new ArrayList<String>();
        ZipFile zipFile = null;

        // TODO create a checked exception for this
        try {
            zipFile = new ZipFile(tempFile);
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(resourceType.directory)) {
                    String name = entry.getName().substring(resourceType.directory.length());
                    int i = 0;
                    while (i < name.length()) {
                        char c = name.charAt(i);
                        if (c == '/' || c == '\\') {
                            i++;
                        } else {
                            break;
                        }
                    }
                    ret.add(name.substring(i));
                }
            }
            // rootNode = loadIndex(zipFile.getInputStream(indexFileEntry));
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(zipFile);
        }
        Collections.sort(ret);
        return ret;
    }

    /**
     * This method exists so we can specify the temp file location for unit testing and write to a
     * directory that is not in the build path.
     * 
     * @param projectFile
     *            The zip file to load
     * @param tempFile
     *            The temp file that is used for project resources
     * @return an initialized Project
     * @throws ProjectLoadException
     */
    static Project load(File projectFile, File tempFile) throws ProjectLoadException {
        Project project = new Project();
        project.projectFile = projectFile;
        project.tempFile = tempFile;

        // projectFile.

        try {
            IOUtil.copyFile(project.projectFile, project.tempFile);
            project.index = project.loadIndex(projectFile);
            project.properties = project.loadProperties(projectFile);
        } catch (ZipException e) {
            throw new ProjectLoadException(e);
        } catch (IOException e) {
            throw new ProjectLoadException(e);
        }

        return project;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value.trim());
    }

    public String getProperty(String key) {
        Object ret = properties.get(key);
        return ret != null ? ret.toString() : null;
    }

    public ByteArrayInputStream getPropertyStream() {
        /*
         * We cannot just use Properties#store(out, comments) because it ALWAYS writes output in
         * ISO8859-1 and we need UTF-8.
         */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream ret = null;
        try {
            IOUtil.writeProperties(properties, out);
            ret = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            // TODO: IOException on properties serialization
            throw new RuntimeException(e);
        }
        return ret;
    }

    public String[] validPropertyKeys() {
        Set<Object> propKeys = DEFAULT_PROJECT_PROPERTIES.keySet();
        String[] ret = new String[propKeys.size()];
        int i = 0;
        for (Iterator<Object> e = propKeys.iterator(); e.hasNext();) {
            ret[i++] = e.next().toString();
        }
        return ret;
    }

    protected void initRecordFactory() {
        Record[] recordObjects = new Record[] {

                // We want StyleRecord not StyleRecordWriter
                // new StyleRecordWriter<StyleRecord>(),
                new StyleRecord(),

                new TextAreaComponentRecordWriter<TextAreaComponentRecord>(),
                new ScreenRecordWriter<ScreenRecord>(),
                new ScreenSeriesRecordWriter<ScreenSeriesRecord>(),
                new MenuItemRecordWriter<MenuItemRecord>(),

                // Intentionally NOT IntArrayRecordWriter
                new IntArrayRecord(),

                new ResourceComponentRecordWriter<ResourceComponentRecord>(),
                new QuestionRecordWriter<QuestionRecord>()
        };

        for (Record r : recordObjects) {
            recordFactory.registerType(r);
        }
    }

    protected MenuItemRecordWriter<MenuItemRecord> loadIndex(File projectFile) throws ZipException, IOException {
        MenuItemRecordWriter<MenuItemRecord> rootNode = null;
        ZipFile zipFile = null;

        // ZipInputStream zs = new ZipInputStream(new FileInputStream(projectFile));
        // zs.g

        try {
            zipFile = new ZipFile(projectFile);
            ZipEntry indexFileEntry = zipFile.getEntry(Constants.MODULE_INDEX_FILENAME);
            rootNode = loadIndex(zipFile.getInputStream(indexFileEntry));
        } finally {
            IOUtil.closeGracefully(zipFile);
        }
        return rootNode;
    }

    @SuppressWarnings("unchecked")
    protected MenuItemRecordWriter<MenuItemRecord> loadIndex(InputStream indexFileInputStream) throws IOException {
        MenuItemRecordWriter<MenuItemRecord> rootNode = null;
        RecordInputStream ris = new RecordInputStream(indexFileInputStream, recordFactory);
        try {
            rootNode = (MenuItemRecordWriter<MenuItemRecord>) ris.readRecord();
        } finally {
            IOUtil.closeGracefully(ris);
        }
        return rootNode;
    }

    protected Properties loadProperties(File projectFile) throws ZipException, IOException {
        ZipFile zipFile = null;
        Properties ret = new Properties();
        InputStreamReader utf8InputStreamReader = null;
        try {
            zipFile = new ZipFile(projectFile);
            ZipEntry propFileEntry = zipFile.getEntry(Constants.MODULE_PROPERTIES_FILENAME);
            InputStream propFileStream = zipFile.getInputStream(propFileEntry);
            /*
             * We need to read the properties as "UTF8" not the default ISO 8859-1 encoding.
             */
            utf8InputStreamReader = new InputStreamReader(propFileStream, "UTF8");
            ret.load(utf8InputStreamReader);
        } finally {
            IOUtil.closeGracefully(utf8InputStreamReader);
            IOUtil.closeGracefully(zipFile);
        }
        return ret;
    }

    public MenuItemRecordWriter<MenuItemRecord> getIndex() {
        return index;
    }

    // public void setIndex(MenuItemRecordWriter<MenuItemRecord> index) {
    // this.index = index;
    // }

    @SuppressWarnings("unchecked")
    public void setIndex(IComponentRecordWriter index) {
        if (index instanceof MenuItemRecordWriter<?>) {
            this.index = (MenuItemRecordWriter<MenuItemRecord>) index;
        } else if (index instanceof MenuItemChild<?>) {
            this.index = newRootNode();
            this.index.setChild((MenuItemChild<?>) index);
        }
    }

    public String getFilename() {
        return (projectFile == null || !projectFile.isFile())
                ? null
                : projectFile.getName();
    }

    public File getTempFile() {
        return tempFile;
    }
}
