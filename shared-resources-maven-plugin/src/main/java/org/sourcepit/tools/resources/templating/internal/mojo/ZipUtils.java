/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.resources.templating.internal.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

public final class ZipUtils
{
   private ZipUtils()
   {
      super();
   }

   public static void zip(File directory, File archive, String encoding) throws IOException
   {
      createFileOnDemand(archive);
      final int pathOffset = getAbsolutePathLength(directory);
      final ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(archive);
      zipOut.setEncoding(encoding);
      try
      {
         for (File file : directory.listFiles())
         {
            appendFileOrDirectory(pathOffset, zipOut, file);
         }
      }
      finally
      {
         IOUtils.closeQuietly(zipOut);
      }
   }

   private static void createFileOnDemand(File archive) throws IOException
   {
      if (archive.isDirectory())
      {
         throw new IllegalArgumentException("Archive must not be a directory: " + archive.getAbsolutePath());
      }
      if (!archive.exists())
      {
         if (!archive.getParentFile().exists())
         {
            archive.getParentFile().mkdirs();
         }
         archive.createNewFile();
      }
   }

   private static void appendFileOrDirectory(final int pathOffset, final ZipArchiveOutputStream zipOut,
      File fileOrDirectory) throws IOException, FileNotFoundException
   {
      final String entryName = getZipEntryName(pathOffset, fileOrDirectory.getAbsolutePath());
      final ArchiveEntry zipEntry = zipOut.createArchiveEntry(fileOrDirectory, entryName);
      zipOut.putArchiveEntry(zipEntry);

      final boolean isDirectory = fileOrDirectory.isDirectory();
      try
      {
         if (!isDirectory)
         {
            final InputStream fis = new FileInputStream(fileOrDirectory);
            try
            {
               IOUtils.copy(fis, zipOut);
            }
            finally
            {
               IOUtils.closeQuietly(fis);
            }
         }
      }
      finally
      {
         zipOut.closeArchiveEntry();
      }

      if (isDirectory)
      {
         for (File file : fileOrDirectory.listFiles())
         {
            appendFileOrDirectory(pathOffset, zipOut, file);
         }
      }
   }

   private static String getZipEntryName(int pathOffset, String absolutePath)
   {
      return absolutePath.substring(pathOffset).replace(File.separatorChar, '/');
   }

   private static int getAbsolutePathLength(File file)
   {
      final String absolutePath = file.getAbsolutePath();
      int length = absolutePath.length();
      if (file.isDirectory() && !absolutePath.endsWith(File.separator))
      {
         length++;
      }
      return length;
   }
}
