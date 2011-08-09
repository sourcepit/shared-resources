/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;
import org.sourcepit.tools.shared.resources.internal.harness.SharedResourcesUtils;

public class SharedResourcesCopier extends AbstractPropertyInterpolator
{
   private String manifestHeader = "Shared-Resources";

   private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

   private boolean filter = false;

   public boolean isFilter()
   {
      return filter;
   }

   public void setFilter(boolean filter)
   {
      this.filter = filter;
   }

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

      final FilterWrapper filterWrapper = isFilter() ? newFilterWrapper() : null;

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
}
