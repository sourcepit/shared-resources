/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.mojo;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @extendsPlugin resources
 * @extendsGoal resources
 */
@Mojo(name = "share-resources", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class SharedResourcesMojo extends AbstractSharedResourcesMojo
{
   /**
    * The output directory into which to copy the resources.
    */
   @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
   private File outputDirectory;

   /**
    * Directory which is containing the resources that should be shared among projects
    */
   @Parameter(defaultValue = "${basedir}/src/main/shared-resources", required = true)
   private File resourcesDirectory;

   /**
    * The mojos working directory.
    */
   @Parameter(defaultValue = "${project.build.directory}/generated-resources/shared-resources/", required = true)
   private File processedResourcesDirectory;

   /**
    * Path of the directory under which the resources will appear in the final build artifact.
    */
   @Parameter(defaultValue = "META-INF/shared-resources")
   private String targetPath;

   @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/MANIFEST.MF", required = true)
   private File manifestFile;

   @Parameter(defaultValue = "Shared-Resources", required = true)
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
