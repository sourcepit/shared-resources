/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Bernd
 */
public class ResourcesImporterTest extends TestCase
{
   private static final String SHARED_RESOURCES_LOCATION = "META-INF/shared-test-resources/";

   private MavenTestWorkspace ws = new MavenTestWorkspace(this, true);

   @Override
   protected void setUp() throws Exception
   {
      ws.startUp();
      super.setUp();
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      ws.tearDown();
   }

   public void testNormalizeTemplateResourcesPath() throws Exception
   {
      try
      {
         ResourcesImporter.normalizeResourcesPath(null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }
      assertEquals("", ResourcesImporter.normalizeResourcesPath("/"));
      assertEquals("project.zip", ResourcesImporter.normalizeResourcesPath("\\project.zip"));
      assertEquals("project.zip", ResourcesImporter.normalizeResourcesPath("/project.zip"));
      assertEquals("projects/tests", ResourcesImporter.normalizeResourcesPath("projects\\tests\\"));
      assertEquals("projects/tests", ResourcesImporter.normalizeResourcesPath("projects/tests/"));
      assertEquals("projects/tests", ResourcesImporter.normalizeResourcesPath("\\projects\\tests\\"));
      assertEquals("projects/tests", ResourcesImporter.normalizeResourcesPath("/projects/tests/"));
   }

   public void testCreateFullTemplateResourcesPath() throws Exception
   {
      try
      {
         ResourcesImporter.createFullResourcesPath("", null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }

      assertEquals("project.zip", ResourcesImporter.createFullResourcesPath(null, "project.zip"));
      assertEquals("project.zip", ResourcesImporter.createFullResourcesPath("\\", "project.zip"));
      assertEquals("project.zip", ResourcesImporter.createFullResourcesPath("\\", "/project.zip"));
      assertEquals("test/project.zip", ResourcesImporter.createFullResourcesPath("test", "project.zip"));
      assertEquals("test/project.zip",
         ResourcesImporter.createFullResourcesPath("test", "/project.zip"));
      assertEquals("test/foo/project.zip",
         ResourcesImporter.createFullResourcesPath("test", "foo/project.zip"));
      assertEquals("test/foo/project.zip",
         ResourcesImporter.createFullResourcesPath("test", "/foo/project.zip"));
   }

   public void testImportFile() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst.txt", ws.getWs(), false);
      assertEquals(1, ws.getWs().list().length);
      assertEquals("täst.txt", ws.getWs().list()[0]);
   }

   public void testImportFile_keepArchivePaths() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst.txt", ws.getWs(), true);
      assertEquals(1, ws.getWs().list().length);
      assertEquals("täst.txt", ws.getWs().list()[0]);
   }

   public void testImportArchive() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst",
         ws.getWs(), false);
      assertEquals(2, ws.getWs().list().length);

      File[] members1 = ws.getWs().listFiles();
      assertEquals(2, members1.length);

      // map name to file, because on linux we have another file ordering..
      final Map<String, File> nameToFile = new HashMap<String, File>();
      for (int i = 0; i < members1.length; i++)
      {
         nameToFile.put(members1[i].getName(), members1[i]);
      }

      File file1_1 = nameToFile.get("foo");
      assertEquals("foo", file1_1.getName());
      assertTrue(file1_1.isDirectory());

      File file1_2 = nameToFile.get("foo.txt");
      assertEquals("foo.txt", file1_2.getName());
      assertTrue(file1_2.isFile());

      File[] members1_1 = file1_1.listFiles();
      assertEquals(1, members1_1.length);

      File file1_1_1 = members1_1[0];
      assertEquals("bär.txt", file1_1_1.getName());
      assertTrue(file1_1_1.isFile());
   }

   public void testImportArchive_keepArchivePaths() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst",
         ws.getWs(), true);
      assertEquals(1, ws.getWs().list().length);

      File file1 = ws.getWs().listFiles()[0];
      assertEquals("täst", file1.getName());
      assertTrue(file1.isDirectory());

      File[] members1 = file1.listFiles();
      assertEquals(2, members1.length);

      // map name to file, because on linux we have another file ordering..
      final Map<String, File> nameToFile = new HashMap<String, File>();
      for (int i = 0; i < members1.length; i++)
      {
         nameToFile.put(members1[i].getName(), members1[i]);
      }

      File file1_1 = nameToFile.get("foo");
      assertEquals("foo", file1_1.getName());
      assertTrue(file1_1.isDirectory());

      File file1_2 = nameToFile.get("foo.txt");
      assertEquals("foo.txt", file1_2.getName());
      assertTrue(file1_2.isFile());

      File[] members1_1 = file1_1.listFiles();
      assertEquals(1, members1_1.length);

      File file1_1_1 = members1_1[0];
      assertEquals("bär.txt", file1_1_1.getName());
      assertTrue(file1_1_1.isFile());
   }

   public void testImportFileInArchive() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo.txt", ws.getWs(), false);
      assertEquals(1, ws.getWs().list().length);

      File file1 = ws.getWs().listFiles()[0];
      assertEquals("foo.txt", file1.getName());
      assertTrue(file1.isFile());
   }

   public void testImportFileInArchive_keepArchivePaths() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo.txt", ws.getWs(), true);
      assertEquals(1, ws.getWs().list().length);

      File file1 = ws.getWs().listFiles()[0];
      assertEquals("täst", file1.getName());
      assertTrue(file1.isDirectory());

      File[] members1 = file1.listFiles();
      assertEquals(1, members1.length);

      File file1_1 = members1[0];
      assertEquals("foo.txt", file1_1.getName());
      assertTrue(file1_1.isFile());
   }

   public void testImportDirInArchive() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo", ws.getWs(), false);
      assertEquals(1, ws.getWs().list().length);

      File file1 = ws.getWs().listFiles()[0];
      assertEquals("bär.txt", file1.getName());
      assertTrue(file1.isFile());
   }

   public void testImportDirInArchive_keepArchivePaths() throws Exception
   {
      new ResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo", ws.getWs(), true);
      assertEquals(1, ws.getWs().list().length);

      File file1 = ws.getWs().listFiles()[0];
      assertEquals("täst", file1.getName());
      assertTrue(file1.isDirectory());

      File[] members1 = file1.listFiles();
      assertEquals(1, members1.length);

      File file1_1 = members1[0];
      assertEquals("foo", file1_1.getName());
      assertTrue(file1_1.isDirectory());

      File[] members1_1 = file1_1.listFiles();
      assertEquals(1, members1_1.length);

      File file1_1_1 = members1_1[0];
      assertEquals("bär.txt", file1_1_1.getName());
      assertTrue(file1_1_1.isFile());
   }
}
