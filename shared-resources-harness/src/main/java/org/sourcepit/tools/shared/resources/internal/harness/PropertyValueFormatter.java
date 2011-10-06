/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
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
