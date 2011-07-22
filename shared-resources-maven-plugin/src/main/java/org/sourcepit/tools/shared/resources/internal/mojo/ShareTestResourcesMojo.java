/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.mojo;

import java.io.File;

/**
 * @extendsPlugin resources
 * @extendsGoal resources
 * @goal share-test-resources
 * @phase process-test-resources
 * @requiresProject true
 */
public class ShareTestResourcesMojo extends AbstractSharedResourcesMojo
{
   /**
    * The output directory into which to copy the resources.
    * 
    * @parameter default-value="${project.build.testOutputDirectory}"
    * @required
    */
   private File outputDirectory;

   /**
    * Directory which is containing the resources that should be shared among projects
    * 
    * @parameter default-value="${basedir}/src/test/shared-resources"
    * @required
    */
   private File resourcesDirectory;

   /**
    * The working directory of the mojo.
    * 
    * @parameter default-value="${project.build.directory}/generated-test-resources/shared-resources/"
    * @required
    */
   private File processedResourcesDirectory;

   /**
    * Path of the directory under which the resources will appear in the final build artifact.
    * 
    * @parameter default-value="META-INF/shared-test-resources/${project.groupId}/${project.artifactId}"
    */
   private String targetPath;

   /**
    * @parameter expression="${project.build.testOutputDirectory}/META-INF/MANIFEST.MF"
    * @required
    */
   private File manifestFile;
   
   /**
    * @parameter default-value="Shared-Test-Resources"
    * @required
    */
   private String manifestHeaderName;

   public File _getOutputDirectory()
   {
      return outputDirectory;
   }

   public void setOutputDirectory(File outputDirectory)
   {
      this.outputDirectory = outputDirectory;
   }

   public File getResourcesDirectory()
   {
      return resourcesDirectory;
   }

   public void setResourcesDirectory(File resourcesDirectory)
   {
      this.resourcesDirectory = resourcesDirectory;
   }

   public File getProcessedResourcesDirectory()
   {
      return processedResourcesDirectory;
   }

   public void setProcessedResourcesDirectory(File processedResourcesDirectory)
   {
      this.processedResourcesDirectory = processedResourcesDirectory;
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

   public String getManifestHeaderName()
   {
      return manifestHeaderName;
   }

   public void setManifestHeaderName(String manifestHeaderName)
   {
      this.manifestHeaderName = manifestHeaderName;
   }
}