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

package org.sourcepit.tools.shared.resources.harness;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;


/**
 * @author Bernd
 */
public class StringInterpolator extends AbstractPropertyInterpolator
{
   public String interpolate(String value)
   {
      StringWriter writer = null;
      Reader reader = null;
      try
      {
         writer = new StringWriter();
         reader = new StringReader(value);
         newCopier().copy(reader, writer, null);
         IOUtils.copy(reader, writer);
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }
      finally
      {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly(writer);
      }
      return writer.toString();
   }
}
