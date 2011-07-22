/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.mojo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sourcepit.tools.shared.resources.internal.mojo.ZipUtils;

public class ZipUtilsTest extends TestCase
{
   private File targetFolder;
   private File testResources;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      targetFolder = new File("target");
      assertTrue(targetFolder.exists() && targetFolder.isDirectory());
      testResources = new File("src/test/resources");
      assertTrue(testResources.exists() && testResources.isDirectory());
   }

   public void testZip() throws Exception
   {
      final File directory = new File(testResources, "ZipUtilsTest");
      assertTrue(directory.exists() && directory.isDirectory());

      final String encoding = System.getProperty("project.build.sourceEncoding", "UTF-8");

      final File archive = new File(targetFolder, "testResources/ZipUtilsTest/" + getName() + ".zip");
      FileUtils.deleteQuietly(archive);
      assertFalse(archive.exists());

      ZipUtils.zip(directory, archive, encoding);
   }

   public void testWriteReadeFile() throws Exception
   {
      final File utf8 = new File(getWorkingDir(), "utf8.txt");
      utf8.createNewFile();

      OutputStream out = new FileOutputStream(utf8);
      try
      {
         IOUtils.copy(new ByteArrayInputStream("ü漢字 / 汉字".getBytes("UTF-8")), out);
      }
      finally
      {
         IOUtils.closeQuietly(out);
      }

      final File latin1 = new File(getWorkingDir(), "latin1.txt");
      latin1.createNewFile();

      InputStream in = new FileInputStream(utf8);
      try
      {
         out = new FileOutputStream(latin1);
         convert(in, "UTF-8", out, "ISO-8859-1");
      }
      finally
      {
         IOUtils.closeQuietly(out);
         IOUtils.closeQuietly(in);
      }
   }

   public void testXml() throws Exception
   {
      final File latin1 = new File(getWorkingDir(), "latin1.xml");
      latin1.createNewFile();

      File xmlFile = new File(testResources, "utf8.xml");

      InputStream in = new FileInputStream(xmlFile);
      OutputStream out = null;
      try
      {
         out = new FileOutputStream(latin1);
         convert(in, "ISO-8859-1", out, "ISO-8859-1");
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }

   }

   private static void convert(InputStream input, String inputCharset, OutputStream output, String outputCharset)
      throws IOException, UnsupportedEncodingException
   {
      InputStreamReader reader = new InputStreamReader(input, inputCharset);
      OutputStreamWriter writer = new OutputStreamWriter(output, outputCharset);
      IOUtils.copy(reader, writer);
      writer.flush();
   }

   private File getWorkingDir()
   {
      final File ws = new File(targetFolder, "testWorkspaces/" + getClass().getSimpleName() + "/" + getName());
      if (!ws.exists())
      {
         ws.mkdirs();
      }
      return ws;
   }
}
