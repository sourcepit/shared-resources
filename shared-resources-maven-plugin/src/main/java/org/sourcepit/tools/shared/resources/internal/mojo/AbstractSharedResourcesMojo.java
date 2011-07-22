/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.ResourcesMojo;
import org.codehaus.plexus.util.ReaderFactory;
import org.sourcepit.common.mf.internal.merge.ManifestMerger;
import org.sourcepit.common.mf.internal.model.Manifest;
import org.sourcepit.common.mf.internal.model.ManifestFactory;
import org.sourcepit.tools.manifest.internal.mojo.MavenProjectManifestMerger;

/**
 * @author Bernd
 */
public abstract class AbstractSharedResourcesMojo extends ResourcesMojo
{
   /**
    * Set whether resources are filtered to replace tokens with parameterised values or not. The values are taken from
    * the <code>properties</code> element and from the properties in the files listed in the <code>filters</code>
    * element. Note: While the type of this field is <code>String</code> for technical reasons, the semantic type is
    * actually <code>Boolean</code>. Default value is <code>false</code>.
    * 
    * @parameter default-value="false"
    */
   private boolean filtering;

   private Properties resourceProperties;

   /**
    * @parameter default-value="${project.build.directory}/shared-resources/"
    * @required
    */
   private File workingDirectory;

   @Override
   public void execute() throws MojoExecutionException
   {
      try
      {
         resourceProperties = null;
         cleanWorkingDirectory();

         // copy
         super.execute();

         final String enc = encoding == null ? ReaderFactory.FILE_ENCODING : encoding;

         final File[] members = new File(workingDirectory, getNormalizedTargetPath()).listFiles();
         if (members != null)
         {
            for (File member : members)
            {
               if (member.isDirectory())
               {
                  final File archive = createArchiveFile(member);
                  ZipUtils.zip(member, archive, enc);
                  FileUtils.deleteDirectory(member);

                  putEncodingProperty(archive.getName(), enc);
               }
               else
               {
                  putEncodingProperty(member.getName(), enc);
               }
            }
            saveResourceProperties();

            FileUtils.copyDirectory(workingDirectory, getProcessedTemplatesDirectory());
            FileUtils.copyDirectory(getProcessedTemplatesDirectory(), _getOutputDirectory());
         }
         FileUtils.deleteDirectory(workingDirectory);

         Manifest manifest = ManifestFactory.eINSTANCE.createManifest();
         manifest.getHeaders().put("Resource-Locations", getTargetPath() == null ? "" : getTargetPath());

         new MavenProjectManifestMerger().merge(project, getManifestFile(), new ManifestMerger(), manifest);
      }
      catch (IOException e)
      {
         throw new MojoExecutionException(e.getLocalizedMessage(), e);
      }
   }

   private void cleanWorkingDirectory() throws IOException
   {
      if (!workingDirectory.exists())
      {
         workingDirectory.mkdirs();
      }
      FileUtils.cleanDirectory(workingDirectory);
   }

   private Object putEncodingProperty(final String path, final String encoding) throws IOException
   {
      return getResourceProperties().put("encoding//" + path, encoding);
   }

   private void saveResourceProperties() throws IOException
   {
      if (resourceProperties != null && !resourceProperties.isEmpty())
      {
         saveResourceProperties(getResourceProperties());
      }
   }

   private void saveResourceProperties(Properties resourceProperties) throws IOException
   {
      final File propsFile = getResourcePropertiesFile();
      final OutputStream out = new FileOutputStream(propsFile);
      try
      {
         resourceProperties.store(out, null);
      }
      finally
      {
         IOUtils.closeQuietly(out);
      }
   }

   private Properties getResourceProperties() throws IOException
   {
      if (resourceProperties == null)
      {
         resourceProperties = loadResourceProperties();
      }
      return resourceProperties;
   }

   private Properties loadResourceProperties() throws IOException, FileNotFoundException
   {
      final File propsFile = getResourcePropertiesFile();
      final InputStream in = new FileInputStream(propsFile);
      try
      {
         final Properties properties = new Properties();
         properties.load(in);
         return properties;
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }

   private File getResourcePropertiesFile() throws IOException
   {
      String path = getNormalizedTargetPath();
      path += "resources.properties";

      final File propsFile = new File(getProcessedTemplatesDirectory(), path);
      if (!propsFile.exists())
      {
         propsFile.getParentFile().mkdirs();
         propsFile.createNewFile();
      }
      return propsFile;
   }

   private String getNormalizedTargetPath()
   {
      String path = "";
      if (getTargetPath() != null)
      {
         path += getTargetPath().replace(File.separatorChar, '/');
         if (!path.endsWith("/"))
         {
            path += "/";
         }
      }
      return path;
   }

   private File createArchiveFile(File sourceDir) throws IOException
   {
      File archive = new File(workingDirectory + "/" + getNormalizedTargetPath() + sourceDir.getName() + ".zip");
      if (archive.exists())
      {
         FileUtils.deleteQuietly(sourceDir);
         archive.createNewFile();
      }
      return archive;
   }

   // HACK we want to force the resources mojo to copy the template resources into our working directory first
   public final File getOutputDirectory()
   {
      return workingDirectory;
   }

   public abstract File _getOutputDirectory();

   public abstract void setOutputDirectory(File outputDirectory);

   private List<Resource> resources;

   public List<Resource> getResources()
   {
      if (resources == null)
      {
         final Resource resource = new Resource();
         resource.setDirectory(getTemplatesDirectory().getAbsolutePath());
         resource.setTargetPath(getTargetPath());
         resource.setFiltering(filtering);
         resources = new ArrayList<Resource>();
         resources.add(resource);
      }
      return resources;
   }

   public abstract File getTemplatesDirectory();

   public abstract void setTemplatesDirectory(File templatesDirectory);

   public abstract File getProcessedTemplatesDirectory();

   public abstract void setProcessedTemplatesDirectory(File processedTemplatesDirectory);

   public abstract String getTargetPath();

   public abstract void setTargetPath(String targetPath);

   public abstract void setManifestFile(File manifestFile);

   public abstract File getManifestFile();
}
