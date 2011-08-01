/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import java.util.Properties;


public class SharedResourcesCopierTest extends AbstractWorkspaceTest
{
   public void testReplaceProperty() throws Exception
   {
      SharedResourcesCopier copier = new SharedResourcesCopier();
      copier.setFilter(true);
      copier.setClassLoader(getClass().getClassLoader());
      copier.setEscapeWindowsPaths(true);
      copier.setManifestHeader("Shared-Test-Resources");
      Properties properties = new Properties();
      properties.setProperty("replace.me", "Hello");

      copier.getValueSources().add(ValueSourceUtils.newPropertyValueSource(properties));

      copier.copy("täst.txt", getWorkspace().getDir());
      
      copier.copy("täst/foo/bär.txt", getWorkspace().getDir());
   }
}
