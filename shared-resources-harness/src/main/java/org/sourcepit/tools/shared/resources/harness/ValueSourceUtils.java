/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.SingleResponseValueSource;
import org.codehaus.plexus.interpolation.ValueSource;

public final class ValueSourceUtils
{
   private ValueSourceUtils()
   {
      super();
   }

   public static ValueSource newPropertyValueSource(Properties properties)
   {
      return new PropertiesBasedValueSource(properties);
   }

   public static ValueSource newSingleValueSource(String expression, Object value)
   {
      return new SingleResponseValueSource(expression, value);
   }

   public static ValueSource newPrefixedValueSource(String prefix, Object root)
   {
      return new PrefixedObjectValueSource(prefix, root);
   }

   public static ValueSource newPrefixedValueSource(List<String> possiblePrefixes, Object root)
   {
      return new PrefixedObjectValueSource(possiblePrefixes, root, true);
   }
}
