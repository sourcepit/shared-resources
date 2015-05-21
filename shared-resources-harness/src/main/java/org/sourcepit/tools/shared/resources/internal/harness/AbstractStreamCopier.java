/*
 * Copyright 2014 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;

public abstract class AbstractStreamCopier implements InterpolationPostProcessor, IFilteredCopier {
   protected Map<String, String> extensionToValueConverterMap = new HashMap<String, String>();

   protected ThreadLocal<String> currentFileExt = new ThreadLocal<String>();

   public Object execute(String expression, Object value) {
      if (value instanceof String) {
         final String fileExt = currentFileExt.get();
         if ("properties".equals(fileExt)) {
            return new PropertyValueFormatter().execute(expression, (String) value);
         }
      }
      return null;
   }

   private String getFileExtension(File file) {
      if (file != null) {
         String fileName = file.getName();

         int idx = fileName.lastIndexOf('.');
         if (idx > -1 && fileName.length() > 1) {
            return fileName.substring(idx + 1);
         }
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void copy(InputStream from, OutputStream to, String encoding, File outFile) throws IOException {
      // buffer so it isn't reading a byte at a time!
      Reader reader = new BufferedReader(new InputStreamReader(from, encoding));
      Writer writer = new OutputStreamWriter(to, encoding);
      copy(reader, writer, outFile);
   }

   /**
    * {@inheritDoc}
    */
   public void copy(Reader from, Writer to, File outFile) throws IOException {
      currentFileExt.set(getFileExtension(outFile));
      try {
         Reader reader = getFilterWrapper(this).getReader(from);
         IOUtils.copy(reader, to);
         to.flush();
      }
      finally {
         currentFileExt.set(null);
      }
   }

   protected abstract FilterWrapper getFilterWrapper(InterpolationPostProcessor postProcessor);
}
