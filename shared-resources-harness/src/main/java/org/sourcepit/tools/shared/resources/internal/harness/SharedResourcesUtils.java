/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
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
      boolean keepArchivePaths, IFilteredCopier copier) throws FileNotFoundException, IOException
   {
      copy(classLoader, resourcesLocation, resourcesPath, targetDir, keepArchivePaths, copier, IFilterStrategy.TRUE);
   }

   public static void copy(ClassLoader classLoader, String resourcesLocation, String resourcesPath, File targetDir,
      boolean keepArchivePaths, IFilteredCopier copier, IFilterStrategy strategy) throws FileNotFoundException,
      IOException
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
         importFile(classLoader, createFullResourcesPath(resourcesLocation, path), path, encoding, targetDir, copier,
            strategy);
      }
      else
      {
         importArchive(classLoader, createFullResourcesPath(resourcesLocation, archiveName), path,
            archiveName.substring(0, archiveName.length() - 4), encoding, targetDir, keepArchivePaths, copier, strategy);
      }
   }

   private static void importArchive(ClassLoader classLoader, String archivePath, String archiveEntry, String dirName,
      String encoding, File targetDir, boolean keepArchivePaths, IFilteredCopier copier, IFilterStrategy strategy)
      throws FileNotFoundException, IOException
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
         importArchive(zipIn, archiveEntry, outDir, keepArchivePaths, encoding, copier, strategy);
      }
      finally
      {
         IOUtils.closeQuietly(zipIn);
      }
   }

   private static void importArchive(ZipArchiveInputStream zipIn, String archiveEntry, File outDir,
      boolean keepArchivePaths, String encoding, IFilteredCopier copier, IFilterStrategy strategy) throws IOException
   {
      boolean found = false;

      ArchiveEntry entry = zipIn.getNextEntry();
      while (entry != null)
      {
         final String entryName = entry.getName();

         if (archiveEntry == null || entryName.startsWith(archiveEntry + "/") || entryName.equals(archiveEntry))
         {
            found = true;

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
                  if (copier != null && strategy.filter(fileName))
                  {
                     copy(zipIn, out, encoding, copier, file);
                  }
                  else
                  {
                     IOUtils.copy(zipIn, out);
                  }
               }
               finally
               {
                  IOUtils.closeQuietly(out);
               }
            }
         }
         entry = zipIn.getNextEntry();
      }

      if (!found)
      {
         throw new FileNotFoundException(archiveEntry);
      }
   }

   private static void importFile(ClassLoader classLoader, String path, String fileName, String encoding,
      File targetDir, IFilteredCopier copier, IFilterStrategy strategy) throws FileNotFoundException, IOException
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
            if (copier != null && strategy.filter(fileName))
            {
               copy(in, out, encoding, copier, outFile);
            }
            else
            {
               IOUtils.copy(in, out);
            }
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


   private static void copy(InputStream from, OutputStream to, String encoding, IFilteredCopier copier, File outFile)
      throws IOException
   {
      copier.copy(from, to, encoding, outFile);
   }


   public static Reader createFilterReader(Reader reader, LinkedHashSet<String> delimiters,
      Collection<ValueSource> valueSources, String escapeString, final boolean escapeWindowsPaths,
      final InterpolationPostProcessor postProcessor)
   {
      final MultiDelimiterStringSearchInterpolator interpolator = new MultiDelimiterStringSearchInterpolator();
      interpolator.setDelimiterSpecs(delimiters);
      interpolator.setEscapeString(escapeString);


      interpolator.addPostProcessor(new InterpolationPostProcessor()
      {
         public Object execute(String expression, Object value)
         {
            if (escapeWindowsPaths && value instanceof String)
            {
               value = FilteringUtils.escapeWindowsPath((String) value);
            }

            if (postProcessor != null)
            {
               final Object newValue = postProcessor.execute(expression, value);
               if (newValue != null)
               {
                  value = newValue;
               }
            }
            return value;
         }
      });

      final Set<String> prefixes = new HashSet<String>();
      for (ValueSource valueSource : valueSources)
      {
         interpolator.addValueSource(valueSource);
         String[] _prefixes = SharedResourcesUtils.getPossiblePrefixes(valueSource);
         if (_prefixes != null)
         {
            Collections.addAll(prefixes, _prefixes);
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
   protected static String[] getPossiblePrefixes(ValueSource source)
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

               return (String[]) field.get(pp);
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
