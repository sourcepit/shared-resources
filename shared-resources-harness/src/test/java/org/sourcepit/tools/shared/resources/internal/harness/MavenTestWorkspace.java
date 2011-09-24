/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

public class MavenTestWorkspace
{
   private final TestCase testCase;
   private final boolean deleteOnTearDown;
   private File ws;
   private String sharedResourcesLocation = "META-INF/shared-test-resources";

   public MavenTestWorkspace(TestCase testCase, boolean deleteOnTearDown)
   {
      this.testCase = testCase;
      this.deleteOnTearDown = deleteOnTearDown;
   }

   public void setSharedResourcesLocation(String sharedResourcesLocation)
   {
      this.sharedResourcesLocation = sharedResourcesLocation;
   }

   public File getDir()
   {
      return ws;
   }

   public void startUp()
   {
      ws = create();
      ws.mkdirs();
      Assert.assertTrue(ws.exists());
      Assert.assertEquals(0, ws.list().length);
   }

   public File importResources(String path)
   {

      return importResources(path, null);
   }

   public File importResources(String sourcePath, String targetPath)
   {
      try
      {
         File outDir;
         if (targetPath == null)
         {
            outDir = getDir();
         }
         else
         {
            outDir = new File(getDir(), targetPath);
            outDir.mkdirs();
         }
         SharedResourcesUtils.copy(testCase.getClass().getClassLoader(), sharedResourcesLocation, sourcePath, outDir,
            false, null);
         return outDir;
      }
      catch (IOException e)
      {
         throw new IllegalStateException(e);
      }
   }

   protected File create()
   {
      final File ws = new File("target/test-workspaces/" + testCase.getClass().getSimpleName() + "/"
         + testCase.getName());
      if (ws.exists())
      {
         Assert.assertTrue(FileUtils.deleteQuietly(ws));
      }
      return ws;
   }

   public void tearDown()
   {
      if (deleteOnTearDown)
      {
         delete();
      }
      ws = null;
   }

   public void delete()
   {
      if (ws != null && ws.exists())
      {
         Assert.assertTrue(FileUtils.deleteQuietly(ws));
      }
   }
}
