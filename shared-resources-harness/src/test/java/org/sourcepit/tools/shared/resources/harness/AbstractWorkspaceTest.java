/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.harness;

import junit.framework.TestCase;

import org.sourcepit.tools.shared.resources.internal.harness.MavenTestWorkspace;

/**
 * @author Bernd
 */
public abstract class AbstractWorkspaceTest extends TestCase
{
   protected MavenTestWorkspace workspace = new MavenTestWorkspace(this, false);

   @Override
   protected void setUp() throws Exception
   {
      workspace.startUp();
      super.setUp();
   }

   public MavenTestWorkspace getWorkspace()
   {
      return workspace;
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      workspace.tearDown();
   }
}
