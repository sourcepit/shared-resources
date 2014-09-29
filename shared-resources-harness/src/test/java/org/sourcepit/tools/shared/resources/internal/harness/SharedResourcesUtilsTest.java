/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.interpolation.ValueSource;
import org.sourcepit.tools.shared.resources.harness.ValueSourceUtils;

import junit.framework.TestCase;

/**
 * @author Bernd
 */
public class SharedResourcesUtilsTest extends TestCase
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
         SharedResourcesUtils.normalizeResourcesPath(null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }
      assertEquals("", SharedResourcesUtils.normalizeResourcesPath("/"));
      assertEquals("project.zip", SharedResourcesUtils.normalizeResourcesPath("\\project.zip"));
      assertEquals("project.zip", SharedResourcesUtils.normalizeResourcesPath("/project.zip"));
      assertEquals("projects/tests", SharedResourcesUtils.normalizeResourcesPath("projects\\tests\\"));
      assertEquals("projects/tests", SharedResourcesUtils.normalizeResourcesPath("projects/tests/"));
      assertEquals("projects/tests", SharedResourcesUtils.normalizeResourcesPath("\\projects\\tests\\"));
      assertEquals("projects/tests", SharedResourcesUtils.normalizeResourcesPath("/projects/tests/"));
   }

   public void testCreateFullTemplateResourcesPath() throws Exception
   {
      try
      {
         SharedResourcesUtils.createFullResourcesPath("", null);
         fail();
      }
      catch (IllegalArgumentException e)
      {
      }

      assertEquals("project.zip", SharedResourcesUtils.createFullResourcesPath(null, "project.zip"));
      assertEquals("project.zip", SharedResourcesUtils.createFullResourcesPath("\\", "project.zip"));
      assertEquals("project.zip", SharedResourcesUtils.createFullResourcesPath("\\", "/project.zip"));
      assertEquals("test/project.zip", SharedResourcesUtils.createFullResourcesPath("test", "project.zip"));
      assertEquals("test/project.zip", SharedResourcesUtils.createFullResourcesPath("test", "/project.zip"));
      assertEquals("test/foo/project.zip", SharedResourcesUtils.createFullResourcesPath("test", "foo/project.zip"));
      assertEquals("test/foo/project.zip", SharedResourcesUtils.createFullResourcesPath("test", "/foo/project.zip"));
   }

   public void testImportFile() throws Exception
   {
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst.txt", ws.getDir(), false,
         null);
      assertEquals(1, ws.getDir().list().length);
      assertEquals("täst.txt", ws.getDir().list()[0]);
   }

   public void testImportFile_keepArchivePaths() throws Exception
   {
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst.txt", ws.getDir(), true,
         null);
      assertEquals(1, ws.getDir().list().length);
      assertEquals("täst.txt", ws.getDir().list()[0]);
   }

   public void testImportArchive() throws Exception
   {
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst", ws.getDir(), false,
         null);
      assertEquals(2, ws.getDir().list().length);

      File[] members1 = ws.getDir().listFiles();
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
      SharedResourcesUtils
         .copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst", ws.getDir(), true, null);
      assertEquals(1, ws.getDir().list().length);

      File file1 = ws.getDir().listFiles()[0];
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
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst/foo.txt", ws.getDir(),
         false, null);
      assertEquals(1, ws.getDir().list().length);

      File file1 = ws.getDir().listFiles()[0];
      assertEquals("foo.txt", file1.getName());
      assertTrue(file1.isFile());

      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst/foo/bär.txt",
         ws.getDir(), false, null);
   }

   public void testImportFileInArchive_keepArchivePaths() throws Exception
   {
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst/foo.txt", ws.getDir(),
         true, null);
      assertEquals(1, ws.getDir().list().length);

      File file1 = ws.getDir().listFiles()[0];
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
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst/foo", ws.getDir(), false,
         null);
      assertEquals(1, ws.getDir().list().length);

      File file1 = ws.getDir().listFiles()[0];
      assertEquals("bär.txt", file1.getName());
      assertTrue(file1.isFile());
   }

   public void testImportDirInArchive_keepArchivePaths() throws Exception
   {
      SharedResourcesUtils.copy(getClass().getClassLoader(), SHARED_RESOURCES_LOCATION, "täst/foo", ws.getDir(), true,
         null);
      assertEquals(1, ws.getDir().list().length);

      File file1 = ws.getDir().listFiles()[0];
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

   public void testGetPossiblePrefixes() throws Exception
   {
      ValueSource source = ValueSourceUtils.newPropertyValueSource(new Properties());
      String[] actualPrefixes = SharedResourcesUtils.getPossiblePrefixes(source);
      assertNull(actualPrefixes);

      String[] prefixes = new String[2];
      prefixes[0] = "pom";
      prefixes[1] = "project";

      source = ValueSourceUtils.newPrefixedValueSource(prefixes, new Object());

      actualPrefixes = SharedResourcesUtils.getPossiblePrefixes(source);
      assertTrue(Arrays.equals(prefixes, actualPrefixes));
   }
}
