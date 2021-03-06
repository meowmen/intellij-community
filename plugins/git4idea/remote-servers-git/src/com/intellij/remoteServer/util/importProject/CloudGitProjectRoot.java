/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.intellij.remoteServer.util.importProject;

import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author michael.golubev
 */
public class CloudGitProjectRoot extends DetectedProjectRoot {

  private final String myRootTypeName;
  private final String myJavaSourceRootTypeName;
  private final VirtualFile myRepositoryRoot;
  private final String myApplicationName;

  public CloudGitProjectRoot(String rootTypeName,
                             String javaSourceRootTypeName,
                             @NotNull File directory,
                             @NotNull VirtualFile repositoryRoot,
                             String applicationName) {
    super(directory);
    myRootTypeName = rootTypeName;
    myJavaSourceRootTypeName = javaSourceRootTypeName;
    myRepositoryRoot = repositoryRoot;
    myApplicationName = applicationName;
  }

  @NotNull
  @Override
  public String getRootTypeName() {
    return myRootTypeName;
  }

  @Override
  public boolean canContainRoot(@NotNull DetectedProjectRoot root) {
    return myJavaSourceRootTypeName.equals(root.getRootTypeName());
  }

  public String getApplicationName() {
    return myApplicationName;
  }

  public VirtualFile getRepositoryRoot() {
    return myRepositoryRoot;
  }
}
