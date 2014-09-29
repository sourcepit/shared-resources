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
