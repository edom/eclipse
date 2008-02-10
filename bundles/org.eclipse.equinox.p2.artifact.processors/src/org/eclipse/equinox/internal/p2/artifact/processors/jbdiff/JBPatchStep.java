/*******************************************************************************
 * Copyright (c) 2007 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	compeople AG (Stefan Liebig) - initial API and implementation
 * 	IBM Corporation - ongoing development
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.artifact.processors.jbdiff;

import ie.wombat.jbdiff.JBPatch;
import java.io.*;
import org.eclipse.equinox.internal.p2.artifact.processors.AbstractDeltaProcessorStep;
import org.eclipse.equinox.internal.p2.core.helpers.FileUtils;
import org.eclipse.equinox.internal.p2.sar.DirectByteArrayOutputStream;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.ArtifactDescriptor;

/**
 * The JBPatchStep patches a JBDiff based data.   
 */
public class JBPatchStep extends AbstractDeltaProcessorStep {

	public JBPatchStep() {
		super();
	}

	protected OutputStream createIncomingStream() throws IOException {
		return new DirectByteArrayOutputStream();
	}

	protected void performProcessing() throws IOException {
		DirectByteArrayOutputStream predecessor = fetchPredecessorBytes(new ArtifactDescriptor(key));
		DirectByteArrayOutputStream current = (DirectByteArrayOutputStream) incomingStream;
		byte[] result = JBPatch.bspatch(predecessor.getBuffer(), predecessor.getBufferLength(), current.getBuffer(), current.getBufferLength());
		// free up the memory as soon as possible.
		predecessor = null;
		current = null;
		incomingStream = null;

		// copy the result of the optimization to the destination.
		FileUtils.copyStream(new ByteArrayInputStream(result), true, destination, false);
	}

	private DirectByteArrayOutputStream fetchPredecessorBytes(ArtifactDescriptor artifactDescriptor) throws IOException {
		DirectByteArrayOutputStream result = new DirectByteArrayOutputStream();
		status = repository.getArtifact(artifactDescriptor, result, monitor);
		if (!status.isOK())
			throw (IOException) new IOException(status.getMessage()).initCause(status.getException());
		return result;
	}
}