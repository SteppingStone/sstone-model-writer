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
package org.edc.sstone.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Greg Orlowski
 */
public class IOUtil {

    public static void closeGracefully(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void closeGracefully(Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void closeGracefully(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void closeGracefully(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        copyStream(new FileInputStream(source), new FileOutputStream(dest));
    }

    public static void writeZipEntry(ZipOutputStream zout, String filename, InputStream in) throws IOException {
        zout.putNextEntry(new ZipEntry(filename));
        copyStream(in, zout, true, false);
        zout.closeEntry();
    }

    /**
     * Copy all bytes from in to out. Flush out at the end but do not close the streams.
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public static int copyStream(InputStream in, OutputStream out, boolean closeIn, boolean closeOut)
            throws IOException {
        int totalBytes = 0;
        try {
            byte[] buff = new byte[1024 * 4];
            int bytesRead = 0;
            while ((bytesRead = in.read(buff)) != -1) {
                if (bytesRead > 0) {
                    out.write(buff, 0, bytesRead);
                    totalBytes += bytesRead;
                }
            }
            out.flush();
        } finally {
            if (closeIn)
                closeGracefully(in);

            if (closeOut)
                closeGracefully(out);
        }
        return totalBytes;
    }

    public static int copyStream(InputStream in, OutputStream out) throws IOException {
        return copyStream(in, out, true, true);
    }

    /**
     * The core {@link Properties#store(OutputStream, String)} does not properly support UTF8
     * encoding. Therefore, I use this implementation to support UTF8
     * 
     * @param props
     * @param out
     * @throws IOException
     */
    public static void writeProperties(Properties props, OutputStream out) throws IOException {
        PrintStream pstream = new PrintStream(out, true, "UTF-8");
        pstream.println();
        for (Entry<Object, Object> propEntry : props.entrySet()) {
            String key = propEntry.getKey().toString();
            String value = propEntry.getValue().toString();
            pstream.print(key);
            pstream.print('=');
            pstream.println(value);
        }
    }

    public static void deleteGracefully(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
