/*******************************************************************************
 * Copyright (c) 2018 Till Brychcy and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Till Brychcy - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.buildpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jdt.testplugin.JavaProjectHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionRelevance;
import org.eclipse.ui.ide.IDE;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.internal.launching.JREContainerInitializer;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BuildpathProblemQuickFixTest extends TestCase {

	private IJavaProject fJavaProject1;

	private IJavaProject fJavaProject2;

	public BuildpathProblemQuickFixTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(BuildpathProblemQuickFixTest.class);
	}

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
		if (fJavaProject1 != null) {
			JavaProjectHelper.delete(fJavaProject1);
			fJavaProject1= null;
		}
		if (fJavaProject2 != null) {
			JavaProjectHelper.delete(fJavaProject2);
			fJavaProject2= null;
		}
	}

	public IPath addFile(IPath root, String fileName, String contents) throws CoreException, IOException {
		IPath filePath= root.append(fileName);
		createFile(filePath, contents.getBytes("UTF8")); //$NON-NLS-1$
		return filePath;
	}

	private IFile createFile(IPath path, byte[] contents) throws CoreException, IOException {
		IFile file= ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		try (ByteArrayInputStream is= new ByteArrayInputStream(contents)) {
			if (file.exists()) {
				file.setContents(is, true, false, null);
			} else {
				file.create(is, true, null);
			}
			return file;
		}
	}

	public static IMarkerResolution[] sortResolutions(IMarkerResolution[] resolutions) {
		IMarkerResolution[] result= resolutions.clone();
		Arrays.sort(result, (e1, e2) -> {
			int relevanceMarker1= (e1 instanceof IMarkerResolutionRelevance)
					? ((IMarkerResolutionRelevance) e1).getRelevanceForResolution()
					: 0;
			int relevanceMarker2= (e2 instanceof IMarkerResolutionRelevance)
					? ((IMarkerResolutionRelevance) e2).getRelevanceForResolution()
					: 0;
			if (relevanceMarker1 != relevanceMarker2) {
				return Integer.valueOf(relevanceMarker2).compareTo(Integer.valueOf(relevanceMarker1));
			}
			return e1.getLabel().compareTo(
					e2.getLabel());
		});
		return result;
	}

	public void test1Incomplete() throws CoreException, IOException {
		fJavaProject1= JavaProjectHelper.createJavaProject("1_Incomplete", "bin");
		fJavaProject1.getProject().getFolder("src").create(true, true, null);

		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/1_MissingProject\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>");
		String classpath= sb.toString();
		addFile(fJavaProject1.getPath(), ".classpath", classpath);
		fJavaProject1.getProject().getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		IMarker[] markers= fJavaProject1.getResource().findMarkers("org.eclipse.jdt.core.buildpath_problem", true, IResource.DEPTH_INFINITE);
		assertEquals("Project '1_Incomplete' is missing required Java project: '1_MissingProject'", (String) markers[0].getAttribute(IMarker.MESSAGE));
		assertEquals(1, markers.length);
		IMarkerResolution[] resolutions= sortResolutions(IDE.getMarkerHelpRegistry().getResolutions(markers[0]));
		assertEquals(3, resolutions.length);
		assertEquals("Open required project '1_MissingProject'", resolutions[0].getLabel());
		assertEquals("Configure build path...", resolutions[1].getLabel());
		assertEquals("Configure problem severity", resolutions[2].getLabel());
	}

	public void test2Cyclic() throws CoreException, IOException {
		fJavaProject1= JavaProjectHelper.createJavaProject("2_CyclicA", "bin");
		fJavaProject1.getProject().getFolder("src").create(true, true, null);

		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/2_CyclicB\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath= sb.toString();
		addFile(fJavaProject1.getPath(), ".classpath", classpath);
		fJavaProject2= JavaProjectHelper.createJavaProject("2_CyclicB", "bin");
		fJavaProject2.getProject().getFolder("src").create(true, true, null);

		sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/2_CyclicA\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath2= sb.toString();
		addFile(fJavaProject2.getPath(), ".classpath", classpath2);
		fJavaProject1.getProject().getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		IMarker[] markers= fJavaProject1.getResource().findMarkers("org.eclipse.jdt.core.buildpath_problem", true, IResource.DEPTH_INFINITE);
		assertEquals("A cycle was detected in the build path of project '2_CyclicA'. The cycle consists of projects {2_CyclicA, 2_CyclicB}", (String) markers[0].getAttribute(IMarker.MESSAGE));
		assertEquals(1, markers.length);
		IMarkerResolution[] resolutions= sortResolutions(IDE.getMarkerHelpRegistry().getResolutions(markers[0]));
		assertEquals(2, resolutions.length);
		assertEquals("Configure build path...", resolutions[0].getLabel());
		assertEquals("Configure problem severity", resolutions[1].getLabel());
	}

	public void test3RequiredBinaryLevel() throws CoreException, IOException {
		IPath container= new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7");
		IVMInstall vm= JREContainerInitializer.resolveVM(container);
		if (vm == null) {
			return;
		}
		if (vm instanceof IVMInstall2) {
			String version= ((IVMInstall2) vm).getJavaVersion();
			if (version == null || !version.startsWith(JavaCore.VERSION_1_7)) {
				// higher version instead of JavaSE 1.7 not found: 
				// skip test as error against vm's class files would be reported
				return;
			}
		} else {
			return;
		}


		fJavaProject1= JavaProjectHelper.createJavaProject("/3_JDKLevelLow", "bin");
		IFolder src1= fJavaProject1.getProject().getFolder("src");
		src1.create(true, true, null);

		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/3_JDKLevelHigh\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath= sb.toString();
		addFile(fJavaProject1.getPath(), ".classpath", classpath);
		addFile(src1.getFullPath(), "LowClass.java", "public class LowClass{HighClass x;}");
		fJavaProject1.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		fJavaProject1.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		fJavaProject1.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		fJavaProject1.setOption(JavaCore.CORE_INCOMPATIBLE_JDK_LEVEL, JavaCore.ERROR);


		fJavaProject2= JavaProjectHelper.createJavaProject("3_JDKLevelHigh", "bin");
		IFolder src2= fJavaProject2.getProject().getFolder("src");
		src2.create(true, true, null);

		sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath2= sb.toString();
		addFile(fJavaProject2.getPath(), ".classpath", classpath2);
		addFile(src2.getFullPath(), "HighClass.java", "public class HighClass{}");
		fJavaProject2.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		fJavaProject2.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		fJavaProject2.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);

		fJavaProject1.getProject().getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		IMarker[] markers= fJavaProject1.getResource().findMarkers("org.eclipse.jdt.core.buildpath_problem", true, IResource.DEPTH_INFINITE);
		assertEquals(
				"Incompatible .class files version in required binaries. Project '3_JDKLevelLow' is targeting a 1.7 runtime, but is compiled against '3_JDKLevelHigh' which requires a 1.8 runtime",
				(String) markers[0].getAttribute(IMarker.MESSAGE));
		assertEquals(1, markers.length);
		IMarkerResolution[] resolutions= sortResolutions(IDE.getMarkerHelpRegistry().getResolutions(markers[0]));
		assertEquals(2, resolutions.length);
		assertEquals("Configure build path...", resolutions[0].getLabel());
		assertEquals("Configure problem severity", resolutions[1].getLabel());
	}

	public void test4OutOverlap() throws CoreException, IOException {
		fJavaProject1= JavaProjectHelper.createJavaProject("4_OutOverlap", "bin");
		fJavaProject1.getProject().getFolder("src").create(true, true, null);
		fJavaProject1.getProject().getFolder("other").create(true, true, null);

		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" output=\"other\" path=\"src\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"other\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath= sb.toString();
		addFile(fJavaProject1.getPath(), ".classpath", classpath);
		fJavaProject1.getProject().getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		IMarker[] markers= fJavaProject1.getResource().findMarkers("org.eclipse.jdt.core.buildpath_problem", true, IResource.DEPTH_INFINITE);
		assertEquals("Source folder 'src' in project '4_OutOverlap' cannot output to distinct source folder 'other'", (String) markers[0].getAttribute(IMarker.MESSAGE));
		assertEquals(1, markers.length);
		IMarkerResolution[] resolutions= sortResolutions(IDE.getMarkerHelpRegistry().getResolutions(markers[0]));
		assertEquals(2, resolutions.length);
		assertEquals("Configure build path...", resolutions[0].getLabel());
		assertEquals("Configure problem severity", resolutions[1].getLabel());
	}

	public void test7Cyclic() throws CoreException, IOException {
		fJavaProject1= JavaProjectHelper.createJavaProject("7_OnlyMain", "bin");
		fJavaProject1.getProject().getFolder("src").create(true, true, null);

		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\"/>\n");
		sb.append("	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/7_OnlyTest\"/>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath= sb.toString();
		addFile(fJavaProject1.getPath(), ".classpath", classpath);
		fJavaProject2= JavaProjectHelper.createJavaProject("7_OnlyTest", "bin");
		fJavaProject2.getProject().getFolder("src").create(true, true, null);

		sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<classpath>\n");
		sb.append("	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		sb.append("	<classpathentry kind=\"src\" path=\"src\">\n");
		sb.append("		<attributes>\n");
		sb.append("			<attribute name=\"test\" value=\"true\"/>\n");
		sb.append("		</attributes>\n");
		sb.append("	</classpathentry>\n");
		sb.append("	<classpathentry kind=\"output\" path=\"bin\"/>\n");
		sb.append("</classpath>\n");
		sb.append("");
		String classpath2= sb.toString();
		addFile(fJavaProject2.getPath(), ".classpath", classpath2);
		fJavaProject1.getProject().getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		IMarker[] markers= fJavaProject1.getResource().findMarkers("org.eclipse.jdt.core.buildpath_problem", true, IResource.DEPTH_INFINITE);
		assertEquals("Project has only main sources but depends on project '7_OnlyTest' which has only test sources.", (String) markers[0].getAttribute(IMarker.MESSAGE));
		assertEquals(1, markers.length);
		IMarkerResolution[] resolutions= sortResolutions(IDE.getMarkerHelpRegistry().getResolutions(markers[0]));
		assertEquals(2, resolutions.length);
		assertEquals("Configure build path...", resolutions[0].getLabel());
		assertEquals("Configure problem severity", resolutions[1].getLabel());
	}

}
