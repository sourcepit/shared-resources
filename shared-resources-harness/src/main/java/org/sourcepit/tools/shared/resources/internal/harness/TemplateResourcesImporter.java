/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author Bernd
 */
public class TemplateResourcesImporter
{
   public void importResources(ClassLoader classLoader, String templatesLocation, String templateResourcesPath,
      File targetDir, boolean keepArchivePaths) throws FileNotFoundException, IOException
   {
      final Properties resources = loadResourcesProperties(classLoader, templatesLocation);

      String encoding = null;
      String archiveName = null;
      String path = null;

      final String resourcesPath = normalizeTemplateResourcesPath(templateResourcesPath);
      final int segmentLength = resourcesPath.indexOf('/');
      if (segmentLength > -1)
      {
         final String _archiveName = resourcesPath.substring(0, segmentLength) + ".zip";
         encoding = resources.getProperty("encoding//" + _archiveName);
         if (encoding != null)
         {
            archiveName = _archiveName;
            path = resourcesPath.substring(segmentLength + 1);
         }
      }
      else
      {
         encoding = resources.getProperty("encoding//" + resourcesPath);
         if (encoding == null)
         {
            final String _archiveName = resourcesPath + ".zip";
            encoding = resources.getProperty("encoding//" + _archiveName);
            if (encoding != null)
            {
               archiveName = _archiveName;
            }
         }
         else
         {
            path = resourcesPath;
         }
      }

      if (archiveName == null)
      {
         importFile(classLoader, createFullTemplateResourcesPath(templatesLocation, path), path, targetDir);
      }
      else
      {
         importArchive(classLoader, createFullTemplateResourcesPath(templatesLocation, archiveName), path,
            archiveName.substring(0, archiveName.length() - 4), encoding, targetDir, keepArchivePaths);
      }
   }

   private void importArchive(ClassLoader classLoader, String archivePath, String archiveEntry, String dirName,
      String encoding, File targetDir, boolean keepArchivePaths) throws FileNotFoundException, IOException
   {
      final InputStream in = classLoader.getResourceAsStream(archivePath);
      if (in == null)
      {
         throw new FileNotFoundException(archivePath);
      }

      final String _dirName = !keepArchivePaths ? "" : dirName;
      final File outDir = new File(targetDir, _dirName);
      if (!outDir.exists())
      {
         outDir.mkdirs();
      }
      final ZipArchiveInputStream zipIn = new ZipArchiveInputStream(in, encoding, true);
      try
      {
         importArchive(zipIn, archiveEntry, outDir, keepArchivePaths);
      }
      finally
      {
         IOUtils.closeQuietly(zipIn);
      }
   }

   private void importArchive(ZipArchiveInputStream zipIn, String archiveEntry, File outDir, boolean keepArchivePaths)
      throws IOException
   {
      ArchiveEntry entry = zipIn.getNextEntry();
      while (entry != null)
      {
         final String entryName = entry.getName();
         if (archiveEntry == null || entryName.equals(archiveEntry) || entryName.startsWith(archiveEntry + "/"))
         {
            final String fileName;

            if (!keepArchivePaths && entryName.startsWith(archiveEntry + "/"))
            {
               fileName = entryName.substring(archiveEntry.length() + 1);
            }
            else
            {
               fileName = entryName;
            }

            final File file = new File(outDir, fileName);
            if (entry.isDirectory())
            {
               file.mkdir();
            }
            else
            {
               file.createNewFile();
               OutputStream out = new FileOutputStream(file);
               try
               {
                  IOUtils.copy(zipIn, out);
               }
               finally
               {
                  IOUtils.closeQuietly(out);
               }
            }
         }
         entry = zipIn.getNextEntry();
      }
   }

   private void importFile(ClassLoader classLoader, String path, String fileName, File targetDir)
      throws FileNotFoundException, IOException
   {
      System.out.println(path);
      final InputStream in = classLoader.getResourceAsStream(path);
      if (in == null)
      {
         throw new FileNotFoundException(path);
      }
      try
      {
         final File outFile = new File(targetDir, fileName);
         if (!outFile.exists())
         {
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();
         }
         final OutputStream out = new FileOutputStream(outFile);
         try
         {
            IOUtils.copy(in, out);
         }
         finally
         {
            IOUtils.closeQuietly(out);
         }
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }

   private Properties loadResourcesProperties(ClassLoader classLoader, String templatesLocation)
   {
      final Properties resourceProperties = new Properties();
      final InputStream in = classLoader.getResourceAsStream(createFullTemplateResourcesPath(templatesLocation,
         "resources.properties"));
      try
      {
         if (in != null)
         {
            resourceProperties.load(in);
         }
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
      return resourceProperties;
   }

   protected static String createFullTemplateResourcesPath(String templatesLocation, String templateResourcesPath)
   {
      System.out.println(templatesLocation);
      System.out.println(templateResourcesPath);
      final StringBuilder sb = new StringBuilder();
      if (templatesLocation != null)
      {
         sb.append(normalizeTemplateResourcesPath(templatesLocation));
      }
      if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/')
      {
         sb.append('/');
      }
      sb.append(normalizeTemplateResourcesPath(templateResourcesPath));
      return sb.toString();
   }

   protected static String normalizeTemplateResourcesPath(final String path)
   {
      if (path == null)
      {
         throw new IllegalArgumentException("Path must not be null.");
      }
      String result = path.replace('\\', '/');
      if (result.startsWith("/"))
      {
         if (result.length() == 1)
         {
            result = "";
         }
         else
         {
            result = result.substring(1);
         }
      }
      if (result.endsWith("/"))
      {
         result = result.substring(0, result.length() - 1);
      }
      return result;
   }
}
