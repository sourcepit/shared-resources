/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
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
         reader = newFilterWrapper().getReader(new StringReader(value));
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
