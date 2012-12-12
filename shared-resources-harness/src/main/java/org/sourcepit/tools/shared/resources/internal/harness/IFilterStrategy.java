/**
 * Copyright (c) 2012 Sourcepit.org contributors and others. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.sourcepit.tools.shared.resources.internal.harness;
public interface IFilterStrategy
{
   static final IFilterStrategy TRUE = new IFilterStrategy()
   {
      public boolean filter(String fileName)
      {
         return true;
      }
   };

   boolean filter(String fileName);
}
