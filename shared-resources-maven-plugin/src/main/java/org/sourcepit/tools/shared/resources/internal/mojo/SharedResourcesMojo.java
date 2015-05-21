/*
 * Copyright 2014 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class SharedResourcesMojo extends AbstractSharedResourcesMojo {
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

   @Override
   public File _getOutputDirectory() {
      return outputDirectory;
   }

   @Override
   public void setOutputDirectory(File outputDirectory) {
      this.outputDirectory = outputDirectory;
   }

   @Override
   public File getResourcesDirectory() {
      return resourcesDirectory;
   }

   @Override
   public void setResourcesDirectory(File resourcesDirectory) {
      this.resourcesDirectory = resourcesDirectory;
   }

   @Override
   public File getProcessedResourcesDirectory() {
      return processedResourcesDirectory;
   }

   @Override
   public void setProcessedResourcesDirectory(File processedResourcesDirectory) {
      this.processedResourcesDirectory = processedResourcesDirectory;
   }

   @Override
   public String getTargetPath() {
      return targetPath;
   }

   @Override
   public void setTargetPath(String targetPath) {
      this.targetPath = targetPath;
   }

   @Override
   public void setManifestFile(File manifestFile) {
      this.manifestFile = manifestFile;
   }

   @Override
   public File getManifestFile() {
      return manifestFile;
   }

   @Override
   public String getManifestHeaderName() {
      return manifestHeaderName;
   }

   @Override
   public void setManifestHeaderName(String manifestHeaderName) {
      this.manifestHeaderName = manifestHeaderName;
   }
}
