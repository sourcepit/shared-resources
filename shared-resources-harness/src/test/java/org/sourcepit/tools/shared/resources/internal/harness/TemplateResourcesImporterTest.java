/*
 * Copyright (C) 2007 Innovations Softwaretechnologie GmbH, Immenstaad, Germany. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

/**
 * @author Bernd
 */
public class TemplateResourcesImporterTest extends TestCase
{
   private static final String SHARED_RESOURCES_LOCATION = "META-INF/shared-test-resources/org.sourcepit.tools/shared-resources-harness";
   private File ws;

   @Override
   protected void setUp() throws Exception
   {
      ws = resetWs();
      super.setUp();
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      deleteWs();
   }

   public void testNormalizeTemplateResourcesPath() throws Exception
   {
      try
      {
         TemplateResourcesImporter.normalizeTemplateResourcesPath(null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }
      assertEquals("", TemplateResourcesImporter.normalizeTemplateResourcesPath("/"));
      assertEquals("project.zip", TemplateResourcesImporter.normalizeTemplateResourcesPath("\\project.zip"));
      assertEquals("project.zip", TemplateResourcesImporter.normalizeTemplateResourcesPath("/project.zip"));
      assertEquals("projects/tests", TemplateResourcesImporter.normalizeTemplateResourcesPath("projects\\tests\\"));
      assertEquals("projects/tests", TemplateResourcesImporter.normalizeTemplateResourcesPath("projects/tests/"));
      assertEquals("projects/tests", TemplateResourcesImporter.normalizeTemplateResourcesPath("\\projects\\tests\\"));
      assertEquals("projects/tests", TemplateResourcesImporter.normalizeTemplateResourcesPath("/projects/tests/"));
   }

   public void testCreateFullTemplateResourcesPath() throws Exception
   {
      try
      {
         TemplateResourcesImporter.createFullTemplateResourcesPath("", null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }

      assertEquals("project.zip", TemplateResourcesImporter.createFullTemplateResourcesPath(null, "project.zip"));
      assertEquals("project.zip", TemplateResourcesImporter.createFullTemplateResourcesPath("\\", "project.zip"));
      assertEquals("project.zip", TemplateResourcesImporter.createFullTemplateResourcesPath("\\", "/project.zip"));
      assertEquals("test/project.zip", TemplateResourcesImporter.createFullTemplateResourcesPath("test", "project.zip"));
      assertEquals("test/project.zip",
         TemplateResourcesImporter.createFullTemplateResourcesPath("test", "/project.zip"));
      assertEquals("test/foo/project.zip",
         TemplateResourcesImporter.createFullTemplateResourcesPath("test", "foo/project.zip"));
      assertEquals("test/foo/project.zip",
         TemplateResourcesImporter.createFullTemplateResourcesPath("test", "/foo/project.zip"));
   }

   public void testImportFile() throws Exception
   {
      System.out.println(getName());
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst.txt", ws, false);
      assertEquals(1, ws.list().length);
      assertEquals("täst.txt", ws.list()[0]);
   }

   public void testImportFile_keepArchivePaths() throws Exception
   {
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst.txt", ws, true);
      assertEquals(1, ws.list().length);
      assertEquals("täst.txt", ws.list()[0]);
   }

   public void testImportArchive() throws Exception
   {
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst",
         ws, false);
      assertEquals(2, ws.list().length);

      File[] members1 = ws.listFiles();
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
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst",
         ws, true);
      assertEquals(1, ws.list().length);

      File file1 = ws.listFiles()[0];
      assertEquals("täst", file1.getName());
      assertTrue(file1.isDirectory());

      File[] members1 = file1.listFiles();
      assertEquals(2, members1.length);

      File file1_1 = members1[0];
      assertEquals("foo", file1_1.getName());
      assertTrue(file1_1.isDirectory());

      File file1_2 = members1[1];
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
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo.txt", ws, false);
      assertEquals(1, ws.list().length);

      File file1 = ws.listFiles()[0];
      assertEquals("foo.txt", file1.getName());
      assertTrue(file1.isFile());
   }

   public void testImportFileInArchive_keepArchivePaths() throws Exception
   {
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo.txt", ws, true);
      assertEquals(1, ws.list().length);

      File file1 = ws.listFiles()[0];
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
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo", ws, false);
      assertEquals(1, ws.list().length);

      File file1 = ws.listFiles()[0];
      assertEquals("bär.txt", file1.getName());
      assertTrue(file1.isFile());
   }

   public void testImportDirInArchive_keepArchivePaths() throws Exception
   {
      new TemplateResourcesImporter().importResources(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION,
         "täst/foo", ws, true);
      assertEquals(1, ws.list().length);

      File file1 = ws.listFiles()[0];
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

   private static File resetWs()
   {
      final File ws = deleteWs();
      ws.mkdirs();
      assertTrue(ws.exists());
      assertEquals(0, ws.list().length);
      return ws;
   }

   private static File deleteWs()
   {
      final File ws = new File("target/test-resources");
      if (ws.exists())
      {
         assertTrue(FileUtils.deleteQuietly(ws));
      }
      return ws;
   }
}
