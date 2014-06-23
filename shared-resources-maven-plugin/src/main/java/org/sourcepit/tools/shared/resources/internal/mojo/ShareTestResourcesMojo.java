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
@Mojo(name = "share-test-resources", defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES, requiresProject = true)
public class ShareTestResourcesMojo extends AbstractSharedResourcesMojo
{
   /**
    * The output directory into which to copy the resources.
    */
   @Parameter(defaultValue = "${project.build.testOutputDirectory}", required = true)
   private File outputDirectory;

   /**
    * Directory which is containing the resources that should be shared among projects
    */
   @Parameter(defaultValue = "${basedir}/src/test/shared-resources", required = true)
   private File resourcesDirectory;

   /**
    * The working directory of the mojo.
    */
   @Parameter(defaultValue = "${project.build.directory}/generated-test-resources/shared-resources/", required = true)
   private File processedResourcesDirectory;

   /**
    * Path of the directory under which the resources will appear in the final build artifact.
    */
   @Parameter(defaultValue = "META-INF/shared-test-resources")
   private String targetPath;

   @Parameter(defaultValue = "${project.build.testOutputDirectory}/META-INF/MANIFEST.MF", required = true)
   private File manifestFile;

   @Parameter(defaultValue = "Shared-Test-Resources", required = true)
   private String manifestHeaderName;

   @Override
   public File _getOutputDirectory()
   {
      return outputDirectory;
   }

   @Override
   public void setOutputDirectory(File outputDirectory)
   {
      this.outputDirectory = outputDirectory;
   }

   @Override
   public File getResourcesDirectory()
   {
      return resourcesDirectory;
   }

   @Override
   public void setResourcesDirectory(File resourcesDirectory)
   {
      this.resourcesDirectory = resourcesDirectory;
   }

   @Override
   public File getProcessedResourcesDirectory()
   {
      return processedResourcesDirectory;
   }

   @Override
   public void setProcessedResourcesDirectory(File processedResourcesDirectory)
   {
      this.processedResourcesDirectory = processedResourcesDirectory;
   }

   @Override
   public String getTargetPath()
   {
      return targetPath;
   }

   @Override
   public void setTargetPath(String targetPath)
   {
      this.targetPath = targetPath;
   }

   @Override
   public void setManifestFile(File manifestFile)
   {
      this.manifestFile = manifestFile;
   }

   @Override
   public File getManifestFile()
   {
      return manifestFile;
   }

   @Override
   public String getManifestHeaderName()
   {
      return manifestHeaderName;
   }

   @Override
   public void setManifestHeaderName(String manifestHeaderName)
   {
      this.manifestHeaderName = manifestHeaderName;
   }
}
