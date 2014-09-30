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

import java.util.Arrays;
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

   public static ValueSource newPrefixedValueSource(String[] possiblePrefixes, Object root)
   {
      return newPrefixedValueSource(Arrays.asList(possiblePrefixes), root);
   }

   public static ValueSource newPrefixedValueSource(List<String> possiblePrefixes, Object root)
   {
      return new PrefixedObjectValueSource(possiblePrefixes, root, true);
   }
}
