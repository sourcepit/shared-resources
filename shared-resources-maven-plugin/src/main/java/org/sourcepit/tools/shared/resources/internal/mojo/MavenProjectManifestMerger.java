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
import java.io.IOException;

import org.apache.maven.project.MavenProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.sourcepit.common.manifest.Manifest;
import org.sourcepit.common.manifest.ManifestFactory;
import org.sourcepit.common.manifest.merge.ManifestMerger;
import org.sourcepit.common.manifest.resource.ManifestResourceImpl;

public class MavenProjectManifestMerger {
   public void merge(MavenProject project, File manifestFile, ManifestMerger merger, Manifest manifest)
      throws IOException {
      Manifest current = loadManifest(manifestFile);
      merger.merge(current, manifest);
      current.eResource().save(null);
      project.getProperties().setProperty("jar.useDefaultManifestFile", String.valueOf(true));
   }

   private Manifest loadManifest(File manifestFile) throws IOException {
      final Resource resource = new ManifestResourceImpl();
      resource.setURI(URI.createFileURI(manifestFile.getAbsolutePath()));
      if (manifestFile.canRead()) {
         resource.load(null);
      }
      if (resource.getContents().isEmpty()) {
         resource.getContents().add(ManifestFactory.eINSTANCE.createManifest());
      }
      return (Manifest) resource.getContents().get(0);
   }
}
