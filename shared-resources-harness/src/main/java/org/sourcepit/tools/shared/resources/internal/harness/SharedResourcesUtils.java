/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.filtering.FilteringUtils;
import org.codehaus.plexus.interpolation.AbstractDelegatingValueSource;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.multi.MultiDelimiterInterpolatorFilterReader;
import org.codehaus.plexus.interpolation.multi.MultiDelimiterStringSearchInterpolator;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;

/**
 * @author Bernd
 */
public final class SharedResourcesUtils
{
   private SharedResourcesUtils()
   {
      super();
   }

   public static void copy(ClassLoader classLoader, String resourcesLocation, String resourcesPath, File targetDir,
      boolean keepArchivePaths, FilterWrapper wrapper) throws FileNotFoundException, IOException
   {
      final Properties resources = loadResourcesProperties(classLoader, resourcesLocation);

      String encoding = null;
      String archiveName = null;
      String path = null;

      final String _resourcesPath = normalizeResourcesPath(resourcesPath);
      final int segmentLength = _resourcesPath.indexOf('/');
      if (segmentLength > -1)
      {
         final String _archiveName = _resourcesPath.substring(0, segmentLength) + ".zip";
         encoding = resources.getProperty("encoding//" + _archiveName);
         if (encoding != null)
         {
            archiveName = _archiveName;
            path = _resourcesPath.substring(segmentLength + 1);
         }
      }
      else
      {
         encoding = resources.getProperty("encoding//" + _resourcesPath);
         if (encoding == null)
         {
            final String _archiveName = _resourcesPath + ".zip";
            encoding = resources.getProperty("encoding//" + _archiveName);
            if (encoding != null)
            {
               archiveName = _archiveName;
            }
         }
         else
         {
            path = _resourcesPath;
         }
      }

      if (archiveName == null)
      {
         if (path == null)
         {
            throw new FileNotFoundException("Unable to resolve path: " + resourcesPath);
         }
         importFile(classLoader, createFullResourcesPath(resourcesLocation, path), path, encoding, targetDir, wrapper);
      }
      else
      {
         importArchive(classLoader, createFullResourcesPath(resourcesLocation, archiveName), path,
            archiveName.substring(0, archiveName.length() - 4), encoding, targetDir, keepArchivePaths, wrapper);
      }
   }

   private static void importArchive(ClassLoader classLoader, String archivePath, String archiveEntry, String dirName,
      String encoding, File targetDir, boolean keepArchivePaths, FilterWrapper wrapper) throws FileNotFoundException,
      IOException
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
         importArchive(zipIn, archiveEntry, outDir, keepArchivePaths, encoding, wrapper);
      }
      finally
      {
         IOUtils.closeQuietly(zipIn);
      }
   }

   private static void importArchive(ZipArchiveInputStream zipIn, String archiveEntry, File outDir,
      boolean keepArchivePaths, String encoding, FilterWrapper wrapper) throws IOException
   {
      ArchiveEntry entry = zipIn.getNextEntry();
      while (entry != null)
      {
         final String entryName = entry.getName();

         if (archiveEntry == null || entryName.startsWith(archiveEntry + "/") || entryName.equals(archiveEntry))
         {
            boolean isDir = entry.isDirectory();

            final String fileName;
            if (archiveEntry == null || keepArchivePaths)
            {
               fileName = entryName;
            }
            else
            {
               if (entryName.startsWith(archiveEntry + "/"))
               {
                  fileName = entryName.substring(archiveEntry.length() + 1);
               }
               else if (!isDir && entryName.equals(archiveEntry))
               {
                  fileName = new File(entryName).getName();
               }
               else
               {
                  throw new IllegalStateException();
               }
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
                  copy(zipIn, out, encoding, wrapper);
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

   private static void importFile(ClassLoader classLoader, String path, String fileName, String encoding,
      File targetDir, FilterWrapper wrapper) throws FileNotFoundException, IOException
   {
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
            copy(in, out, encoding, wrapper);
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

   private static Properties loadResourcesProperties(ClassLoader classLoader, String templatesLocation)
   {
      final Properties resourceProperties = new Properties();

      final String pathToResourceProperties = createFullResourcesPath(templatesLocation, "resources.properties");

      try
      {
         Enumeration<URL> resources = classLoader.getResources(pathToResourceProperties);
         while (resources.hasMoreElements())
         {
            InputStream in = null;
            try
            {
               final URL url = (URL) resources.nextElement();
               in = url.openStream();
               resourceProperties.load(in);
            }
            finally
            {
               IOUtils.closeQuietly(in);
            }
         }
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }

      return resourceProperties;
   }

   protected static String createFullResourcesPath(String templatesLocation, String templateResourcesPath)
   {
      final StringBuilder sb = new StringBuilder();
      if (templatesLocation != null)
      {
         sb.append(normalizeResourcesPath(templatesLocation));
      }
      if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/')
      {
         sb.append('/');
      }
      sb.append(normalizeResourcesPath(templateResourcesPath));
      return sb.toString();
   }

   protected static String normalizeResourcesPath(final String path)
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


   private static void copy(InputStream from, OutputStream to, String encoding, FilterWrapper wrapper)
      throws IOException
   {
      if (wrapper != null)
      {
         // buffer so it isn't reading a byte at a time!
         Reader fileReader = new BufferedReader(new InputStreamReader(from, encoding));
         Writer fileWriter = new OutputStreamWriter(to, encoding);
         Reader reader = wrapper.getReader(fileReader);
         IOUtils.copy(reader, fileWriter);
         fileWriter.flush();
      }
      else
      {
         IOUtils.copy(from, to);
      }
   }


   public static Reader createFilterReader(Reader reader, LinkedHashSet<String> delimiters,
      Collection<ValueSource> valueSources, String escapeString, boolean escapeWindowsPaths)
   {
      final MultiDelimiterStringSearchInterpolator interpolator = new MultiDelimiterStringSearchInterpolator();
      interpolator.setDelimiterSpecs(delimiters);
      interpolator.setEscapeString(escapeString);

      if (escapeWindowsPaths)
      {
         interpolator.addPostProcessor(new InterpolationPostProcessor()
         {
            public Object execute(String expression, Object value)
            {
               if (value instanceof String)
               {
                  return FilteringUtils.escapeWindowsPath((String) value);
               }

               return value;
            }
         });
      }

      final Set<String> prefixes = new HashSet<String>();
      for (ValueSource valueSource : valueSources)
      {
         interpolator.addValueSource(valueSource);
         List<String> _prefixes = SharedResourcesUtils.getPossiblePrefixes(valueSource);
         if (_prefixes != null)
         {
            prefixes.addAll(_prefixes);
         }
      }

      RecursionInterceptor ri = null;
      if (prefixes != null && !prefixes.isEmpty())
      {
         ri = new PrefixAwareRecursionInterceptor(prefixes, true);
      }
      else
      {
         ri = new SimpleRecursionInterceptor();
      }

      MultiDelimiterInterpolatorFilterReader filterReader = new MultiDelimiterInterpolatorFilterReader(reader,
         interpolator, ri);
      filterReader.setRecursionInterceptor(ri);
      filterReader.setDelimiterSpecs(delimiters);

      filterReader.setInterpolateWithPrefixPattern(false);
      filterReader.setEscapeString(escapeString);
      return filterReader;
   }

   @SuppressWarnings("unchecked")
   protected static List<String> getPossiblePrefixes(ValueSource source)
   {
      try
      {
         if (AbstractDelegatingValueSource.class.isAssignableFrom(source.getClass()))
         {
            Field field = AbstractDelegatingValueSource.class.getDeclaredField("delegate");
            field.setAccessible(true);

            Object delegate = field.get(source);
            if (delegate instanceof PrefixedValueSourceWrapper)
            {
               PrefixedValueSourceWrapper pp = (PrefixedValueSourceWrapper) delegate;

               field = pp.getClass().getDeclaredField("possiblePrefixes");
               field.setAccessible(true);

               return (List<String>) field.get(pp);
            }
         }
         return null;
      }
      catch (Exception e)
      {
         throw new IllegalStateException(e);
      }
   }
}
