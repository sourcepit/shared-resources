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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class PropertyValueFormatter
{
   public String execute(String expression, String value)
   {
      return escapeJavaProperties(value.toString());
   }

   public static String escapeJavaProperties(String string)
   {
      final Properties properties = new Properties();
      properties.put("", string);

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try
      {
         properties.store(out, null);
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }

      final String result;
      try
      {
         result = new String(out.toByteArray(), "8859_1");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new IllegalStateException(e);
      }

      final String propertyLine;
      try
      {
         final BufferedReader br = new BufferedReader(new StringReader(result));
         br.readLine();
         propertyLine = br.readLine();
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }

      return propertyLine.substring(1);
   }

}
