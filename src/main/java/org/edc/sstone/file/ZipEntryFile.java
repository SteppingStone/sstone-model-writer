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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.edc.sstone.util.IOUtil;

/**
 * @author Greg Orlowski
 */
public class ZipEntryFile extends File {

    private static final long serialVersionUID = 1010453316573701399L;

    boolean directory = false;
    // long lastModifiedTime;

    private final File zipFile;

    public ZipEntryFile(File zipFile, String path) {
        this(zipFile, path, false);
    }

    public ZipEntryFile(File zipFile, String path, boolean directory) {
        super(path);
        this.zipFile = zipFile;
        this.directory = directory;
    }

    // @Override
    // public String getParent() {
    // super.getParent()
    // return new File(getPath()).getParent();
    // }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public boolean isFile() {
        return !isDirectory();
    }
    
    /**
     * We want the path relative to the root of the zip file, which will be the path that we pass in
     * the ctor as the standard path.
     */
    @Override
    public String getAbsolutePath() {
        return getPath();
    }

    @Override
    public boolean exists() {
        // TODO: I may want to actually implement this for real. For now,
        // assume true always
        return true;
    }

    @Override
    public File getParentFile() {
        // return new ZipEntryFile(zipFile, new File(getPath()).getParent());
        return new ZipEntryFile(zipFile, getParent());
    }

    @Override
    public String[] list() {
        File[] children = listFiles();
        String[] ret = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            ret[i] = children[i].getName();
        }
        return ret;
    }

    @Override
    public File[] listFiles() {
        List<File> ret = new ArrayList<File>();
        ZipFile zf = null;
        String path = getPath();
        try {
            zf = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                File f = new File(ze.getName());
                if (path.equals(f.getParent())) {
                    ret.add(new ZipEntryFile(zipFile, ze.getName(), ze.isDirectory()));
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(zf);
        }

        return ret.toArray(new File[ret.size()]);
    }

    // public static ZipEntryFile getZipEntryFile(File zipFile, String path) {
    // ZipFile zf = null;
    // ZipEntryFile ret = null;
    // try {
    // zf = new ZipFile(zipFile);
    // ZipEntry zipEntry = zf.getEntry(path);
    // if (zipEntry != null) {
    // ret = new ZipEntryFile(zipEntry);
    // }
    // } catch (ZipException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // } finally {
    // IOUtil.closeGracefully(zf);
    // }
    // return ret;
    // }

    // @Override
    // public long lastModified() {
    // return lastModifiedTime;
    // }

    // @Override
    // public boolean isFile() {
    // return !isDirectory();
    // };

    // @Override
    // public boolean isDirectory() {
    // return directory;
    // }

}
