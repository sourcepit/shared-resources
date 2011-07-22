/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.mojo;

import java.io.File;

/**
 * @extendsPlugin resources
 * @extendsGoal resources
 * @goal package-test-templates
 * @phase process-test-resources
 * @requiresProject true
 */
public class PackageTestTemplatesMojo extends AbstractPackageTemplatesMojo
{
   /**
    * The output directory into which to copy the resources.
    * 
    * @parameter default-value="${project.build.testOutputDirectory}"
    * @required
    */
   private File outputDirectory;

   /**
    * The working directory under which the mojo copies, filters amd archives the templates.
    * 
    * @parameter default-value="${basedir}/src/test/templates"
    * @required
    */
   private File templatesDirectory;

   /**
    * The working directory under which the mojo copies, filters amd archives the templates.
    * 
    * @parameter default-value="${project.build.directory}/generated-resources/test-templates/"
    * @required
    */
   private File processedTemplatesDirectory;

   /**
    * Path of the directory under which the templates will appear in the final build artifact.
    * 
    * @parameter default-value="META-INF/test-templates"
    */
   private String targetPath;

   /**
    * @parameter expression="${project.build.testOutputDirectory}/META-INF/MANIFEST.MF"
    * @required
    */
   private File manifestFile;

   public File _getOutputDirectory()
   {
      return outputDirectory;
   }

   public void setOutputDirectory(File outputDirectory)
   {
      this.outputDirectory = outputDirectory;
   }

   public File getTemplatesDirectory()
   {
      return templatesDirectory;
   }

   public void setTemplatesDirectory(File templatesDirectory)
   {
      this.templatesDirectory = templatesDirectory;
   }

   public File getProcessedTemplatesDirectory()
   {
      return processedTemplatesDirectory;
   }

   public void setProcessedTemplatesDirectory(File processedTemplatesDirectory)
   {
      this.processedTemplatesDirectory = processedTemplatesDirectory;
   }

   public String getTargetPath()
   {
      return targetPath;
   }

   public void setTargetPath(String targetPath)
   {
      this.targetPath = targetPath;
   }

   public void setManifestFile(File manifestFile)
   {
      this.manifestFile = manifestFile;
   }

   public File getManifestFile()
   {
      return manifestFile;
   }
}
