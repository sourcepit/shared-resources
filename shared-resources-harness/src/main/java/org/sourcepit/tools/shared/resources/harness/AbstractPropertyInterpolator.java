/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;
import org.sourcepit.tools.shared.resources.internal.harness.AbstractStreamCopier;
import org.sourcepit.tools.shared.resources.internal.harness.IFilteredCopier;
import org.sourcepit.tools.shared.resources.internal.harness.SharedResourcesUtils;

/**
 * @author Bernd
 */
public abstract class AbstractPropertyInterpolator
{
   private LinkedHashSet<String> delimiters;

   private List<ValueSource> valueSources;

   private String escapeString;

   private boolean escapeWindowsPaths = true;

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

   public List<ValueSource> getValueSources()
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

   protected IFilteredCopier newCopier()
   {
      final IFilteredCopier copier = new AbstractStreamCopier()
      {
         @Override
         protected FilterWrapper getFilterWrapper(final InterpolationPostProcessor postProcessor)
         {
            final FileUtils.FilterWrapper filterWrapper = new FileUtils.FilterWrapper()
            {
               @Override
               public Reader getReader(Reader reader)
               {
                  return SharedResourcesUtils.createFilterReader(reader, getDelimiters(), getValueSources(),
                     getEscapeString(), isEscapeWindowsPaths(), postProcessor);
               }
            };
            return filterWrapper;
         }
      };
      return copier;
   }
}
