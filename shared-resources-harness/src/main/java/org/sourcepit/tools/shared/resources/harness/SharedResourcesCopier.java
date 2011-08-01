/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;
import org.sourcepit.tools.shared.resources.internal.harness.SharedResourcesUtils;

public class SharedResourcesCopier
{
   private String manifestHeader = "Shared-Resources";

   private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

   private boolean filter = false;

   private LinkedHashSet<String> delimiters;

   private Collection<ValueSource> valueSources;

   private String escapeString;

   private boolean escapeWindowsPaths = true;

   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   public void setManifestHeader(String manifestHeader)
   {
      this.manifestHeader = manifestHeader;
   }

   public String getManifestHeader()
   {
      return manifestHeader;
   }

   public boolean isFilter()
   {
      return filter;
   }

   public void setFilter(boolean filter)
   {
      this.filter = filter;
   }

   public LinkedHashSet<String> getDelimiters()
   {
      if (delimiters == null)
      {
         delimiters = new LinkedHashSet<String>();
         delimiters.add("${*}");
         delimiters.add("@");
      }
      return delimiters;
   }

   public Collection<ValueSource> getValueSources()
   {
      if (valueSources == null)
      {
         valueSources = new ArrayList<ValueSource>();
      }
      return valueSources;
   }

   public String getEscapeString()
   {
      return escapeString;
   }

   public void setEscapeString(String escapeString)
   {
      this.escapeString = escapeString;
   }

   public boolean isEscapeWindowsPaths()
   {
      return escapeWindowsPaths;
   }

   public void setEscapeWindowsPaths(boolean escapeWindowsPaths)
   {
      this.escapeWindowsPaths = escapeWindowsPaths;
   }

   public void copy(String resourcePath, File targetDir) throws IOException
   {
      final Collection<String> resourceLocations = new LinkedHashSet<String>();

      final Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements())
      {
         final InputStream inputStream = resources.nextElement().openStream();
         try
         {
            final String _resourceLocations = new Manifest(inputStream).getMainAttributes().getValue(manifestHeader);
            if (_resourceLocations != null)
            {
               for (String resourceLocation : _resourceLocations.split(","))
               {
                  resourceLocations.add(resourceLocation.trim());
               }
            }
         }
         finally
         {
            IOUtils.closeQuietly(inputStream);
         }
      }

      final FilterWrapper filterWrapper = filter ? newFilterWrapper() : null;

      final List<IOException> ioException = new ArrayList<IOException>();

      for (String resourceLocation : resourceLocations)
      {
         try
         {
            SharedResourcesUtils.copy(classLoader, resourceLocation, resourcePath, targetDir, false, filterWrapper);
            return;
         }
         catch (IOException e)
         {
            ioException.add(e);
         }
      }

      if (!ioException.isEmpty())
      {
         throw ioException.get(0);
      }

      throw new FileNotFoundException(resourcePath);
   }

   private FilterWrapper newFilterWrapper()
   {
      return new FileUtils.FilterWrapper()
      {
         @Override
         public Reader getReader(Reader reader)
         {
            return SharedResourcesUtils.createFilterReader(reader, getDelimiters(), getValueSources(),
               getEscapeString(), isEscapeWindowsPaths());
         }
      };
   }
}
