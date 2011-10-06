/*
 * Copyright (C) 2011 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.sourcepit.tools.shared.resources.internal.harness;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


public interface IFilteredCopier
{
   void copy(InputStream from, OutputStream to, String encoding, File outFile) throws IOException;

   void copy(Reader from, Writer to, File outFile) throws IOException;
}