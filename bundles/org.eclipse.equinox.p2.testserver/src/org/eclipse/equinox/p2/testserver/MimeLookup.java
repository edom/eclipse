/*******************************************************************************
 * Copyright (c) 2009, 2018 Cloudsmith Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cloudsmith Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.p2.testserver;

import java.util.HashMap;
import java.util.Map;

public class MimeLookup {

	public static final String JAR_PACK_GZ = "jar.pack.gz"; //$NON-NLS-1$

	public static String getMimeType(String name) {
		if (extMap == null)
			initNameToMime();

		// treat names with multiple "." in a special way (currently only one)
		if (name.endsWith(JAR_PACK_GZ))
			return extMap.get(JAR_PACK_GZ);
		int dot = name.lastIndexOf("."); //$NON-NLS-1$
		// get the suffix, use "a" for empty as this gives the default
		// "application/octet-stream"
		String tmp = dot == -1 ? "a" : name.substring(dot + 1); //$NON-NLS-1$
		tmp = extMap.get(tmp);
		return tmp == null ? "application/octet-stream" : tmp; //$NON-NLS-1$
	}

	private static Map<String, String> extMap;
	private static Map<String, String> mimeMap;

	public static void initNameToMime() {
		extMap = new HashMap<>(data.length);
		for (int i = 0; i < data.length; i += 2) {
			String key = data[i];
			String val = extMap.get(key);
			if (val == null)
				extMap.put(key, data[i]);
		}

	}

	public static void initMimeToExt() {
		mimeMap = new HashMap<>(data.length);
		for (int i = 0; i < data.length; i += 2) {
			String key = data[i + 1];
			String val = mimeMap.get(key);
			if (val == null)
				mimeMap.put(key, data[i]);
		}

	}

	public final static String[] data = { //
			// --- typical java related mime types - higher priority
			"bat", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"batfrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"class", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"css", "text/css", //$NON-NLS-1$ //$NON-NLS-2$
			"cssfrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"exe", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"gif", "image/gif", //$NON-NLS-1$ //$NON-NLS-2$
			"html", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"htmlfrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"jad", "text/vnd.sun.j2me.app-descriptor", //$NON-NLS-1$ //$NON-NLS-2$
			"jar", "application/java-archive", //$NON-NLS-1$ //$NON-NLS-2$
			"jar.pack.gz", "application/x-java-pack200", //$NON-NLS-1$ //$NON-NLS-2$
			"jardiff", "application/x-java-archive-diff", //$NON-NLS-1$ //$NON-NLS-2$
			"java", "text/x-java-source", //$NON-NLS-1$ //$NON-NLS-2$
			"javafrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"jnlp", "application/x-java-jnlp-file", //$NON-NLS-1$ //$NON-NLS-2$
			"jpg", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"js", "application/x-javascript", //$NON-NLS-1$ //$NON-NLS-2$
			"mp3", "audio/mp3", //$NON-NLS-1$ //$NON-NLS-2$
			"png", "image/png", //$NON-NLS-1$ //$NON-NLS-2$
			"rss", "application/rss+xml", //$NON-NLS-1$ //$NON-NLS-2$
			"ser", "application/x-java-serialized-object", //$NON-NLS-1$ //$NON-NLS-2$
			"sql", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"sqlfrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"swf", "application/x-shockwave-flash", //$NON-NLS-1$ //$NON-NLS-2$
			"txt", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"wav", "audio/wav", //$NON-NLS-1$ //$NON-NLS-2$
			"xml", "application/xml", //$NON-NLS-1$ //$NON-NLS-2$
			"xmlfrag", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"zip", "application/zip", //$NON-NLS-1$ //$NON-NLS-2$

			// --- misc definitions
			"3dm", "x-world/x-3dmf", //$NON-NLS-1$ //$NON-NLS-2$
			"3dmf", "x-world/x-3dmf", //$NON-NLS-1$ //$NON-NLS-2$

			"a", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"aab", "application/x-authorware-bin", //$NON-NLS-1$ //$NON-NLS-2$
			"aam", "application/x-authorware-map", //$NON-NLS-1$ //$NON-NLS-2$
			"aas", "application/x-authorware-seg", //$NON-NLS-1$ //$NON-NLS-2$
			"abc", "text/vnd.abc", //$NON-NLS-1$ //$NON-NLS-2$
			"acgi", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"afl", "video/animaflex", //$NON-NLS-1$ //$NON-NLS-2$
			"ai", "application/postscript", //$NON-NLS-1$ //$NON-NLS-2$
			"aif", "audio/aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aif", "audio/x-aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aifc", "audio/aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aifc", "audio/x-aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aiff", "audio/aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aiff", "audio/x-aiff", //$NON-NLS-1$ //$NON-NLS-2$
			"aim", "application/x-aim", //$NON-NLS-1$ //$NON-NLS-2$
			"aip", "text/x-audiosoft-intra", //$NON-NLS-1$ //$NON-NLS-2$
			"ani", "application/x-navi-animation", //$NON-NLS-1$ //$NON-NLS-2$
			"aps", "application/mime", //$NON-NLS-1$ //$NON-NLS-2$
			"arc", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"arj", "application/arj", //$NON-NLS-1$ //$NON-NLS-2$
			"arj", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"art", "image/x-jg", //$NON-NLS-1$ //$NON-NLS-2$
			"asf", "video/x-ms-asf", //$NON-NLS-1$ //$NON-NLS-2$
			"asm", "text/x-asm", //$NON-NLS-1$ //$NON-NLS-2$
			"asp", "text/asp", //$NON-NLS-1$ //$NON-NLS-2$
			"asx", "application/x-mplayer2", //$NON-NLS-1$ //$NON-NLS-2$
			"asx", "video/x-ms-asf", //$NON-NLS-1$ //$NON-NLS-2$
			"asx", "video/x-ms-asf-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"au", "audio/basic", //$NON-NLS-1$ //$NON-NLS-2$
			"au", "audio/x-au", //$NON-NLS-1$ //$NON-NLS-2$
			"avi", "video/avi", //$NON-NLS-1$ //$NON-NLS-2$
			"avi", "application/x-troff-msvideo", //$NON-NLS-1$ //$NON-NLS-2$
			"avi", "video/msvideo", //$NON-NLS-1$ //$NON-NLS-2$
			"avi", "video/x-msvideo", //$NON-NLS-1$ //$NON-NLS-2$
			"avs", "video/avs-video", //$NON-NLS-1$ //$NON-NLS-2$

			"bcpio", "application/x-bcpio", //$NON-NLS-1$ //$NON-NLS-2$
			"bin", "application/mac-binary", //$NON-NLS-1$ //$NON-NLS-2$
			"bin", "application/macbinary", //$NON-NLS-1$ //$NON-NLS-2$
			"bin", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"bin", "application/x-binary", //$NON-NLS-1$ //$NON-NLS-2$
			"bin", "application/x-macbinary", //$NON-NLS-1$ //$NON-NLS-2$
			"bm", "image/bmp", //$NON-NLS-1$ //$NON-NLS-2$
			"bmp", "image/bmp", //$NON-NLS-1$ //$NON-NLS-2$
			"bmp", "image/x-windows-bmp", //$NON-NLS-1$ //$NON-NLS-2$
			"boo", "application/book", //$NON-NLS-1$ //$NON-NLS-2$
			"book", "application/book", //$NON-NLS-1$ //$NON-NLS-2$
			"boz", "application/x-bzip2", //$NON-NLS-1$ //$NON-NLS-2$
			"bsh", "application/x-bsh", //$NON-NLS-1$ //$NON-NLS-2$
			"bz", "application/x-bzip", //$NON-NLS-1$ //$NON-NLS-2$
			"bz2", "application/x-bzip2", //$NON-NLS-1$ //$NON-NLS-2$
			"c", "text/x-c", //$NON-NLS-1$ //$NON-NLS-2$
			"c", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"c++", "text/x-c", //$NON-NLS-1$ //$NON-NLS-2$
			"c++", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"cat", "application/vnd.ms-pki.seccat", //$NON-NLS-1$ //$NON-NLS-2$
			"cc", "text/x-c", //$NON-NLS-1$ //$NON-NLS-2$
			"cc", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"ccad", "application/clariscad", //$NON-NLS-1$ //$NON-NLS-2$
			"cco", "application/x-cocoa", //$NON-NLS-1$ //$NON-NLS-2$
			"cdf", "application/cdf", //$NON-NLS-1$ //$NON-NLS-2$
			"cdf", "application/x-cdf", //$NON-NLS-1$ //$NON-NLS-2$
			"cdf", "application/x-netcdf", //$NON-NLS-1$ //$NON-NLS-2$
			"cer", "application/pkix-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"cer", "application/x-x509-ca-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"cha", "application/x-chat", //$NON-NLS-1$ //$NON-NLS-2$
			"chat", "application/x-chat", //$NON-NLS-1$ //$NON-NLS-2$
			"class", "application/java", //$NON-NLS-1$ //$NON-NLS-2$
			"class", "application/java-byte-code", //$NON-NLS-1$ //$NON-NLS-2$
			"class", "application/x-java-class", //$NON-NLS-1$ //$NON-NLS-2$
			"com", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"com", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"conf", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"cpio", "application/x-cpio", //$NON-NLS-1$ //$NON-NLS-2$
			"cpp", "text/x-c", //$NON-NLS-1$ //$NON-NLS-2$
			"cpt", "application/mac-compactpro", //$NON-NLS-1$ //$NON-NLS-2$
			"cpt", "application/x-compactpro", //$NON-NLS-1$ //$NON-NLS-2$
			"cpt", "application/x-cpt", //$NON-NLS-1$ //$NON-NLS-2$
			"crl", "application/pkcs-crl", //$NON-NLS-1$ //$NON-NLS-2$
			"crl", "application/pkix-crl", //$NON-NLS-1$ //$NON-NLS-2$
			"crt", "application/pkix-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"crt", "application/x-x509-ca-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"crt", "application/x-x509-user-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"csh", "application/x-csh", //$NON-NLS-1$ //$NON-NLS-2$
			"csh", "text/x-script.csh", //$NON-NLS-1$ //$NON-NLS-2$
			"css", "text/css", //$NON-NLS-1$ //$NON-NLS-2$
			"cxx", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"css", "application/x-pointplus", //$NON-NLS-1$ //$NON-NLS-2$

			"dcr", "application/x-director", //$NON-NLS-1$ //$NON-NLS-2$
			"deepv", "application/x-deepv", //$NON-NLS-1$ //$NON-NLS-2$
			"der", "application/x-x509-ca-cert", //$NON-NLS-1$ //$NON-NLS-2$
			"def", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"dif", "video/x-dv", //$NON-NLS-1$ //$NON-NLS-2$
			"dir", "application/x-director", //$NON-NLS-1$ //$NON-NLS-2$
			"dl", "video/dl", //$NON-NLS-1$ //$NON-NLS-2$
			"dl", "video/x-dl", //$NON-NLS-1$ //$NON-NLS-2$
			"doc", "application/msword", //$NON-NLS-1$ //$NON-NLS-2$
			"dot", "application/msword", //$NON-NLS-1$ //$NON-NLS-2$
			"dp", "application/commonground", //$NON-NLS-1$ //$NON-NLS-2$
			"drw", "application/drafting", //$NON-NLS-1$ //$NON-NLS-2$
			"dump", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"dv", "video/x-dv", //$NON-NLS-1$ //$NON-NLS-2$
			"dvi", "application/x-dvi", //$NON-NLS-1$ //$NON-NLS-2$
			"dwf", "model/vnd.dwf", //$NON-NLS-1$ //$NON-NLS-2$
			"dwg", "application/acad", //$NON-NLS-1$ //$NON-NLS-2$
			"dwg", "image/vnd.dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"dwg", "image/x-dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"dxf", "application/dxf", //$NON-NLS-1$ //$NON-NLS-2$
			"dxf", "image/vnd.dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"dxf", "image/x-dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"dxr", "application/x-director", //$NON-NLS-1$ //$NON-NLS-2$

			"elc", "application/x-elc", //$NON-NLS-1$ //$NON-NLS-2$
			"env", "application/x-envoy", //$NON-NLS-1$ //$NON-NLS-2$
			"eps", "application/postscript", //$NON-NLS-1$ //$NON-NLS-2$
			"es", "application/x-esrehber", //$NON-NLS-1$ //$NON-NLS-2$
			"etx", "text/x-setext", //$NON-NLS-1$ //$NON-NLS-2$
			"exe", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"f", "text/x-fortran", //$NON-NLS-1$ //$NON-NLS-2$
			"f", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"f77", "text/x-fortran", //$NON-NLS-1$ //$NON-NLS-2$
			"f90", "text/x-fortran", //$NON-NLS-1$ //$NON-NLS-2$
			"f90", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"fdf", "application/vnd.fdf", //$NON-NLS-1$ //$NON-NLS-2$
			"fli", "video/fli", //$NON-NLS-1$ //$NON-NLS-2$
			"fli", "video/x-fli", //$NON-NLS-1$ //$NON-NLS-2$
			"for", "text/x-fortran", //$NON-NLS-1$ //$NON-NLS-2$
			"for", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"fpx", "image/vnd.fpx", //$NON-NLS-1$ //$NON-NLS-2$
			"fpx", "image/vnd.net-fpx", //$NON-NLS-1$ //$NON-NLS-2$
			"frl", "application/freeloader", //$NON-NLS-1$ //$NON-NLS-2$
			"funk", "audio/make", //$NON-NLS-1$ //$NON-NLS-2$

			"g", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"g3", "image/g3fax", //$NON-NLS-1$ //$NON-NLS-2$
			"gif", "image/gif", //$NON-NLS-1$ //$NON-NLS-2$
			"gl", "video/gl", //$NON-NLS-1$ //$NON-NLS-2$
			"gl", "video/x-gl", //$NON-NLS-1$ //$NON-NLS-2$
			"gsd", "audio/x-gsm", //$NON-NLS-1$ //$NON-NLS-2$
			"gsm", "audio/x-gsm", //$NON-NLS-1$ //$NON-NLS-2$
			"gsp", "application/x-gsp", //$NON-NLS-1$ //$NON-NLS-2$
			"gss", "application/x-gss", //$NON-NLS-1$ //$NON-NLS-2$
			"gtar", "application/x-gtar", //$NON-NLS-1$ //$NON-NLS-2$
			"gz", "application/x-compressed", //$NON-NLS-1$ //$NON-NLS-2$
			"gz", "application/x-gzip", //$NON-NLS-1$ //$NON-NLS-2$
			"gzip", "application/x-gzip", //$NON-NLS-1$ //$NON-NLS-2$
			"gzip", "multipart/x-gzip", //$NON-NLS-1$ //$NON-NLS-2$

			"h", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"h", "text/x-h", //$NON-NLS-1$ //$NON-NLS-2$
			"hdf", "application/x-hdf", //$NON-NLS-1$ //$NON-NLS-2$
			"help", "application/x-helpfile", //$NON-NLS-1$ //$NON-NLS-2$
			"hgl", "application/vnd.hp-hpgl", //$NON-NLS-1$ //$NON-NLS-2$
			"hh", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"hh", "text/x-h", //$NON-NLS-1$ //$NON-NLS-2$
			"hlb", "text/x-script", //$NON-NLS-1$ //$NON-NLS-2$
			"hlp", "application/hlp", //$NON-NLS-1$ //$NON-NLS-2$
			"hlp", "application/x-helpfile", //$NON-NLS-1$ //$NON-NLS-2$
			"hlp", "application/x-winhelp", //$NON-NLS-1$ //$NON-NLS-2$
			"hpg", "application/vnd.hp-hpgl", //$NON-NLS-1$ //$NON-NLS-2$
			"hpgl", "application/vnd.hp-hpgl", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/binhex", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/binhex4", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/mac-binhex", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/mac-binhex40", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/x-binhex40", //$NON-NLS-1$ //$NON-NLS-2$
			"hqx", "application/x-mac-binhex40", //$NON-NLS-1$ //$NON-NLS-2$
			"hta", "application/hta", //$NON-NLS-1$ //$NON-NLS-2$
			"htc", "text/x-component", //$NON-NLS-1$ //$NON-NLS-2$
			"htm", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"html", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"htmls", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"htt", "text/webviewhtml", //$NON-NLS-1$ //$NON-NLS-2$
			"htx", "text/html", //$NON-NLS-1$ //$NON-NLS-2$

			"ice", "x-conference/x-cooltalk", //$NON-NLS-1$ //$NON-NLS-2$
			"ico", "image/x-icon", //$NON-NLS-1$ //$NON-NLS-2$
			"idc", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"ief", "image/ief", //$NON-NLS-1$ //$NON-NLS-2$
			"iefs", "image/ief", //$NON-NLS-1$ //$NON-NLS-2$
			"iges", "application/iges", //$NON-NLS-1$ //$NON-NLS-2$
			"iges", "model/iges", //$NON-NLS-1$ //$NON-NLS-2$
			"igs", "application/iges", //$NON-NLS-1$ //$NON-NLS-2$
			"igs", "model/iges", //$NON-NLS-1$ //$NON-NLS-2$
			"ima", "application/x-ima", //$NON-NLS-1$ //$NON-NLS-2$
			"imap", "application/x-httpd-imap", //$NON-NLS-1$ //$NON-NLS-2$
			"inf", "application/inf", //$NON-NLS-1$ //$NON-NLS-2$
			"ins", "application/x-internett-signup", //$NON-NLS-1$ //$NON-NLS-2$
			"ip", "application/x-ip2", //$NON-NLS-1$ //$NON-NLS-2$
			"isu", "video/x-isvideo", //$NON-NLS-1$ //$NON-NLS-2$
			"it", "audio/it", //$NON-NLS-1$ //$NON-NLS-2$
			"iv", "application/x-inventor", //$NON-NLS-1$ //$NON-NLS-2$
			"ivr", "i-world/i-vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"ivy", "application/x-livescreen", //$NON-NLS-1$ //$NON-NLS-2$

			"jam", "audio/x-jam", //$NON-NLS-1$ //$NON-NLS-2$
			"jav", "text/x-java-source", //$NON-NLS-1$ //$NON-NLS-2$
			"jav", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"java", "text/x-java-source", //$NON-NLS-1$ //$NON-NLS-2$
			"java", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"jcm", "application/x-java-commerce", //$NON-NLS-1$ //$NON-NLS-2$
			"jfif", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jfif", "image/pjpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jfif-tbnl", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpe", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpe", "image/pjpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpeg", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpeg", "image/pjpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpg", "image/jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jpg", "image/pjpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"jps", "image/x-jps", //$NON-NLS-1$ //$NON-NLS-2$
			"js", "application/x-javascript", //$NON-NLS-1$ //$NON-NLS-2$
			"jut", "image/jutvision", //$NON-NLS-1$ //$NON-NLS-2$

			"kar", "audio/midi", //$NON-NLS-1$ //$NON-NLS-2$
			"kar", "music/x-karaoke", //$NON-NLS-1$ //$NON-NLS-2$
			"ksh", "application/x-ksh", //$NON-NLS-1$ //$NON-NLS-2$
			"ksh", "text/x-script.ksh", //$NON-NLS-1$ //$NON-NLS-2$

			"la", "audio/nspaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"la", "audio/x-nspaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"lam", "audio/x-liveaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"latex", "application/x-latex", //$NON-NLS-1$ //$NON-NLS-2$
			"lha", "application/lha", //$NON-NLS-1$ //$NON-NLS-2$
			"lha", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"lha", "application/x-lha", //$NON-NLS-1$ //$NON-NLS-2$
			"lhx", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"list", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"lma", "audio/nspaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"lma", "audio/x-nspaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"log", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"lsp", "application/x-lisp", //$NON-NLS-1$ //$NON-NLS-2$
			"lsp", "text/x-script.lisp", //$NON-NLS-1$ //$NON-NLS-2$
			"lst", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"lsx", "text/x-la-asf", //$NON-NLS-1$ //$NON-NLS-2$
			"ltx", "application/x-latex", //$NON-NLS-1$ //$NON-NLS-2$
			"lzh", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"lzh", "application/x-lzh", //$NON-NLS-1$ //$NON-NLS-2$
			"lzx", "application/lzx", //$NON-NLS-1$ //$NON-NLS-2$
			"lzx", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"lzx", "application/x-lzx", //$NON-NLS-1$ //$NON-NLS-2$

			"m", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"m", "text/x-m", //$NON-NLS-1$ //$NON-NLS-2$
			"m1v", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"m2a", "audio/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"m2v", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"m3u", "audio/x-mpequrl", //$NON-NLS-1$ //$NON-NLS-2$
			"man", "application/x-troff-man", //$NON-NLS-1$ //$NON-NLS-2$
			"map", "application/x-navimap", //$NON-NLS-1$ //$NON-NLS-2$
			"mar", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"mbd", "application/mbedlet", //$NON-NLS-1$ //$NON-NLS-2$
			"mcd", "application/mcad", //$NON-NLS-1$ //$NON-NLS-2$
			"mcd", "application/x-mathcad", //$NON-NLS-1$ //$NON-NLS-2$
			"mcf", "image/vasa", //$NON-NLS-1$ //$NON-NLS-2$
			"mcf", "text/mcf", //$NON-NLS-1$ //$NON-NLS-2$
			"mcp", "application/netmc", //$NON-NLS-1$ //$NON-NLS-2$
			"me", "application/x-troff-me", //$NON-NLS-1$ //$NON-NLS-2$
			"mht", "message/rfc822", //$NON-NLS-1$ //$NON-NLS-2$
			"mhtml", "message/rfc822", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "audio/midi", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "application/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "audio/x-mid", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "audio/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "music/crescendo", //$NON-NLS-1$ //$NON-NLS-2$
			"mid", "x-music/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "audio/midi", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "application/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "audio/x-mid", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "audio/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "music/crescendo", //$NON-NLS-1$ //$NON-NLS-2$
			"midi", "x-music/x-midi", //$NON-NLS-1$ //$NON-NLS-2$
			"mif", "application/x-frame", //$NON-NLS-1$ //$NON-NLS-2$
			"mif", "application/x-mif", //$NON-NLS-1$ //$NON-NLS-2$
			"mime", "www/mime", //$NON-NLS-1$ //$NON-NLS-2$
			"mime", "message/rfc822", //$NON-NLS-1$ //$NON-NLS-2$
			"mjpg", "video/x-motion-jpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mm", "application/base64", //$NON-NLS-1$ //$NON-NLS-2$
			"mm", "application/x-meme", //$NON-NLS-1$ //$NON-NLS-2$
			"mme", "application/base64", //$NON-NLS-1$ //$NON-NLS-2$
			"mod", "audio/mod", //$NON-NLS-1$ //$NON-NLS-2$
			"mod", "audio/x-mod", //$NON-NLS-1$ //$NON-NLS-2$
			"moov", "video/quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"mov", "video/quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"movie", "video/x-sgi-movie", //$NON-NLS-1$ //$NON-NLS-2$
			"mp2", "audio/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mp2", "audio/x-mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mp2", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mp2", "video/x-mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mp2", "video/x-mpeq2a", //$NON-NLS-1$ //$NON-NLS-2$
			"mp3", "audio/mpeg3", //$NON-NLS-1$ //$NON-NLS-2$
			"mp3", "audio/x-mpeg-3", //$NON-NLS-1$ //$NON-NLS-2$
			"mp3", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mp3", "video/x-mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpa", "audio/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpa", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpc", "application/x-project", //$NON-NLS-1$ //$NON-NLS-2$
			"mpe", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpeg", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpg", "audio/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpg", "video/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpga", "audio/mpeg", //$NON-NLS-1$ //$NON-NLS-2$
			"mpp", "application/vnd.ms-project", //$NON-NLS-1$ //$NON-NLS-2$
			"mpt", "application/x-project", //$NON-NLS-1$ //$NON-NLS-2$
			"mpv", "application/x-project", //$NON-NLS-1$ //$NON-NLS-2$
			"mpx", "application/x-project", //$NON-NLS-1$ //$NON-NLS-2$
			"mrc", "application/marc", //$NON-NLS-1$ //$NON-NLS-2$
			"ms", "application/x-troff-ms", //$NON-NLS-1$ //$NON-NLS-2$
			"mv", "video/x-sgi-movie", //$NON-NLS-1$ //$NON-NLS-2$
			"my", "audio/make", //$NON-NLS-1$ //$NON-NLS-2$
			"mzz", "application/x-vnd.audioexplosion.mzz", //$NON-NLS-1$ //$NON-NLS-2$

			"nap", "image/naplps", //$NON-NLS-1$ //$NON-NLS-2$
			"naplps", "image/naplps", //$NON-NLS-1$ //$NON-NLS-2$
			"nc", "application/x-netcdf", //$NON-NLS-1$ //$NON-NLS-2$
			"ncm", "application/vnd.nokia.configuration-message", //$NON-NLS-1$ //$NON-NLS-2$
			"nif", "image/x-niff", //$NON-NLS-1$ //$NON-NLS-2$
			"niff", "image/x-niff", //$NON-NLS-1$ //$NON-NLS-2$
			"nix", "application/x-mix-transfer", //$NON-NLS-1$ //$NON-NLS-2$
			"nsc", "application/x-conference", //$NON-NLS-1$ //$NON-NLS-2$
			"nvd", "application/x-navidoc", //$NON-NLS-1$ //$NON-NLS-2$
			"o", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"oda", "application/oda", //$NON-NLS-1$ //$NON-NLS-2$
			"omc", "application/x-omc", //$NON-NLS-1$ //$NON-NLS-2$
			"omcd", "application/x-omcdatamaker", //$NON-NLS-1$ //$NON-NLS-2$
			"omcr", "application/x-omcregerator", //$NON-NLS-1$ //$NON-NLS-2$

			"p", "text/x-pascal", //$NON-NLS-1$ //$NON-NLS-2$
			"p10", "application/pkcs10", //$NON-NLS-1$ //$NON-NLS-2$
			"p10", "application/x-pkcs10", //$NON-NLS-1$ //$NON-NLS-2$
			"p12", "application/pkcs-12", //$NON-NLS-1$ //$NON-NLS-2$
			"p12", "application/x-pkcs12", //$NON-NLS-1$ //$NON-NLS-2$
			"p7a", "application/x-pkcs7-signature", //$NON-NLS-1$ //$NON-NLS-2$
			"p7c", "application/pkcs7-mime", //$NON-NLS-1$ //$NON-NLS-2$
			"p7c", "application/x-pkcs7-mime", //$NON-NLS-1$ //$NON-NLS-2$
			"p7m", "application/pkcs7-mime", //$NON-NLS-1$ //$NON-NLS-2$
			"p7m", "application/x-pkcs7-mime", //$NON-NLS-1$ //$NON-NLS-2$
			"p7r", "application/x-pkcs7-certreqresp", //$NON-NLS-1$ //$NON-NLS-2$
			"p7s", "application/pkcs7-signature", //$NON-NLS-1$ //$NON-NLS-2$
			"part", "application/pro_eng", //$NON-NLS-1$ //$NON-NLS-2$
			"pas", "text/pascal", //$NON-NLS-1$ //$NON-NLS-2$
			"pbm", "image/x-portable-bitmap", //$NON-NLS-1$ //$NON-NLS-2$
			"pcl", "application/vnd.hp-pcl", //$NON-NLS-1$ //$NON-NLS-2$
			"pcl", "application/x-pcl", //$NON-NLS-1$ //$NON-NLS-2$
			"pct", "image/x-pict", //$NON-NLS-1$ //$NON-NLS-2$
			"pcx", "image/x-pcx", //$NON-NLS-1$ //$NON-NLS-2$
			"pdb", "chemical/x-pdb", //$NON-NLS-1$ //$NON-NLS-2$
			"pdf", "application/pdf", //$NON-NLS-1$ //$NON-NLS-2$
			"pfunk", "audio/make", //$NON-NLS-1$ //$NON-NLS-2$
			"pfunk", "audio/make.my.funk", //$NON-NLS-1$ //$NON-NLS-2$
			"pgm", "image/x-portable-graymap", //$NON-NLS-1$ //$NON-NLS-2$
			"pgm", "image/x-portable-greymap", //$NON-NLS-1$ //$NON-NLS-2$
			"pic", "image/pict", //$NON-NLS-1$ //$NON-NLS-2$
			"pict", "image/pict", //$NON-NLS-1$ //$NON-NLS-2$
			"pkg", "application/x-newton-compatible-pkg", //$NON-NLS-1$ //$NON-NLS-2$
			"pko", "application/vnd.ms-pki.pko", //$NON-NLS-1$ //$NON-NLS-2$
			"pl", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"pl", "text/x-script.perl", //$NON-NLS-1$ //$NON-NLS-2$
			"plx", "application/x-pixclscript", //$NON-NLS-1$ //$NON-NLS-2$
			"pm", "image/x-xpixmap", //$NON-NLS-1$ //$NON-NLS-2$
			"pm", "text/x-script.perl-module", //$NON-NLS-1$ //$NON-NLS-2$
			"pm4", "application/x-pagemaker", //$NON-NLS-1$ //$NON-NLS-2$
			"pm5", "application/x-pagemaker", //$NON-NLS-1$ //$NON-NLS-2$
			"png", "image/png", //$NON-NLS-1$ //$NON-NLS-2$
			"pnm", "application/x-portable-anymap", //$NON-NLS-1$ //$NON-NLS-2$
			"pnm", "image/x-portable-anymap", //$NON-NLS-1$ //$NON-NLS-2$
			"pot", "application/mspowerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"pot", "application/vnd.ms-powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"pov", "model/x-pov", //$NON-NLS-1$ //$NON-NLS-2$
			"ppa", "application/vnd.ms-powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppm", "image/x-portable-pixmap", //$NON-NLS-1$ //$NON-NLS-2$
			"pps", "application/mspowerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"pps", "application/vnd.ms-powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppt", "application/mspowerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppt", "application/powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppt", "application/vnd.ms-powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppt", "application/x-mspowerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"ppz", "application/mspowerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"pre", "application/x-freelance", //$NON-NLS-1$ //$NON-NLS-2$
			"prt", "application/pro_eng", //$NON-NLS-1$ //$NON-NLS-2$
			"ps", "application/postscript", //$NON-NLS-1$ //$NON-NLS-2$
			"psd", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"pvu", "paleovu/x-pv", //$NON-NLS-1$ //$NON-NLS-2$
			"pwz", "application/vnd.ms-powerpoint", //$NON-NLS-1$ //$NON-NLS-2$
			"py", "text/x-script.phyton", //$NON-NLS-1$ //$NON-NLS-2$
			"pyc", "applicaiton/x-bytecode.python", //$NON-NLS-1$ //$NON-NLS-2$
			"qcp", "audio/vnd.qcelp", //$NON-NLS-1$ //$NON-NLS-2$
			"qd3", "x-world/x-3dmf", //$NON-NLS-1$ //$NON-NLS-2$
			"qd3d", "x-world/x-3dmf", //$NON-NLS-1$ //$NON-NLS-2$
			"qif", "image/x-quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"qt", "video/quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"qtc", "video/x-qtc", //$NON-NLS-1$ //$NON-NLS-2$
			"qti", "image/x-quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"qtif", "image/x-quicktime", //$NON-NLS-1$ //$NON-NLS-2$
			"ra", "audio/x-pn-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"ra", "audio/x-pn-realaudio-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"ra", "audio/x-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"ram", "audio/x-pn-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"ras", "application/x-cmu-raster", //$NON-NLS-1$ //$NON-NLS-2$
			"ras", "image/cmu-raster", //$NON-NLS-1$ //$NON-NLS-2$
			"ras", "image/x-cmu-raster", //$NON-NLS-1$ //$NON-NLS-2$
			"rast", "image/cmu-raster", //$NON-NLS-1$ //$NON-NLS-2$
			"rexx", "text/x-script.rexx", //$NON-NLS-1$ //$NON-NLS-2$
			"rf", "image/vnd.rn-realflash", //$NON-NLS-1$ //$NON-NLS-2$
			"rgb", "image/x-rgb", //$NON-NLS-1$ //$NON-NLS-2$
			"rm", "application/vnd.rn-realmedia", //$NON-NLS-1$ //$NON-NLS-2$
			"rm", "audio/x-pn-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"rmi", "audio/mid", //$NON-NLS-1$ //$NON-NLS-2$
			"rmm", "audio/x-pn-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"rmp", "audio/x-pn-realaudio", //$NON-NLS-1$ //$NON-NLS-2$
			"rmp", "audio/x-pn-realaudio-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"rng", "application/ringing-tones", //$NON-NLS-1$ //$NON-NLS-2$
			"rng", "application/vnd.nokia.ringing-tone", //$NON-NLS-1$ //$NON-NLS-2$
			"rnx", "application/vnd.rn-realplayer", //$NON-NLS-1$ //$NON-NLS-2$
			"roff", "application/x-troff", //$NON-NLS-1$ //$NON-NLS-2$
			"rp", "image/vnd.rn-realpix", //$NON-NLS-1$ //$NON-NLS-2$
			"rpm", "audio/x-pn-realaudio-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"rt", "text/richtext", //$NON-NLS-1$ //$NON-NLS-2$
			"rt", "text/vnd.rn-realtext", //$NON-NLS-1$ //$NON-NLS-2$
			"rtf", "application/rtf", //$NON-NLS-1$ //$NON-NLS-2$
			"rtf", "application/x-rtf", //$NON-NLS-1$ //$NON-NLS-2$
			"rtf", "text/richtext", //$NON-NLS-1$ //$NON-NLS-2$
			"rtx", "application/rtf", //$NON-NLS-1$ //$NON-NLS-2$
			"rtx", "text/richtext", //$NON-NLS-1$ //$NON-NLS-2$
			"rv", "video/vnd.rn-realvideo", //$NON-NLS-1$ //$NON-NLS-2$

			"s", "text/x-asm", //$NON-NLS-1$ //$NON-NLS-2$
			"s3m", "audio/s3m", //$NON-NLS-1$ //$NON-NLS-2$
			"saveme", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"sbk", "application/x-tbook", //$NON-NLS-1$ //$NON-NLS-2$
			"scm", "application/x-lotusscreencam", //$NON-NLS-1$ //$NON-NLS-2$
			"scm", "text/x-script.guile", //$NON-NLS-1$ //$NON-NLS-2$
			"scm", "text/x-script.scheme", //$NON-NLS-1$ //$NON-NLS-2$
			"scm", "video/x-scm", //$NON-NLS-1$ //$NON-NLS-2$
			"sdml", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"sdp", "application/sdp", //$NON-NLS-1$ //$NON-NLS-2$
			"sdp", "application/x-sdp", //$NON-NLS-1$ //$NON-NLS-2$
			"sdr", "application/sounder", //$NON-NLS-1$ //$NON-NLS-2$
			"sea", "application/sea", //$NON-NLS-1$ //$NON-NLS-2$
			"sea", "application/x-sea", //$NON-NLS-1$ //$NON-NLS-2$
			"set", "application/set", //$NON-NLS-1$ //$NON-NLS-2$
			"sgm", "text/sgml", //$NON-NLS-1$ //$NON-NLS-2$
			"sgm", "text/x-sgml", //$NON-NLS-1$ //$NON-NLS-2$
			"sgml", "text/sgml", //$NON-NLS-1$ //$NON-NLS-2$
			"sgml", "text/x-sgml", //$NON-NLS-1$ //$NON-NLS-2$
			"sh", "application/x-bsh", //$NON-NLS-1$ //$NON-NLS-2$
			"sh", "application/x-sh", //$NON-NLS-1$ //$NON-NLS-2$
			"sh", "application/x-shar", //$NON-NLS-1$ //$NON-NLS-2$
			"sh", "text/x-script.sh", //$NON-NLS-1$ //$NON-NLS-2$
			"shar", "application/x-bsh", //$NON-NLS-1$ //$NON-NLS-2$
			"shar", "application/x-shar", //$NON-NLS-1$ //$NON-NLS-2$
			"shtml", "text/html", //$NON-NLS-1$ //$NON-NLS-2$
			"shtml", "text/x-server-parsed-html", //$NON-NLS-1$ //$NON-NLS-2$
			"sid", "audio/x-psid", //$NON-NLS-1$ //$NON-NLS-2$
			"sit", "application/x-sit", //$NON-NLS-1$ //$NON-NLS-2$
			"sit", "application/x-stuffit", //$NON-NLS-1$ //$NON-NLS-2$
			"skd", "application/x-koan", //$NON-NLS-1$ //$NON-NLS-2$
			"skm", "application/x-koan", //$NON-NLS-1$ //$NON-NLS-2$
			"skp", "application/x-koan", //$NON-NLS-1$ //$NON-NLS-2$
			"skt", "application/x-koan", //$NON-NLS-1$ //$NON-NLS-2$
			"sl", "application/x-seelogo", //$NON-NLS-1$ //$NON-NLS-2$
			"smi", "application/smil", //$NON-NLS-1$ //$NON-NLS-2$
			"smil", "application/smil", //$NON-NLS-1$ //$NON-NLS-2$
			"snd", "audio/basic", //$NON-NLS-1$ //$NON-NLS-2$
			"snd", "audio/x-adpcm", //$NON-NLS-1$ //$NON-NLS-2$
			"sol", "application/solids", //$NON-NLS-1$ //$NON-NLS-2$
			"spc", "application/x-pkcs7-certificates", //$NON-NLS-1$ //$NON-NLS-2$
			"spc", "text/x-speech", //$NON-NLS-1$ //$NON-NLS-2$
			"spl", "application/futuresplash", //$NON-NLS-1$ //$NON-NLS-2$
			"spr", "application/x-sprite", //$NON-NLS-1$ //$NON-NLS-2$
			"sprite", "application/x-sprite", //$NON-NLS-1$ //$NON-NLS-2$
			"src", "application/x-wais-source", //$NON-NLS-1$ //$NON-NLS-2$
			"ssi", "text/x-server-parsed-html", //$NON-NLS-1$ //$NON-NLS-2$
			"ssm", "application/streamingmedia", //$NON-NLS-1$ //$NON-NLS-2$
			"sst", "application/vnd.ms-pki.certstore", //$NON-NLS-1$ //$NON-NLS-2$
			"step", "application/step", //$NON-NLS-1$ //$NON-NLS-2$
			"stl", "application/sla", //$NON-NLS-1$ //$NON-NLS-2$
			"stl", "application/vnd.ms-pki.stl", //$NON-NLS-1$ //$NON-NLS-2$
			"stl", "application/x-navistyle", //$NON-NLS-1$ //$NON-NLS-2$
			"stp", "application/step", //$NON-NLS-1$ //$NON-NLS-2$
			"sv4cpio", "application/x-sv4cpio", //$NON-NLS-1$ //$NON-NLS-2$
			"sv4crc", "application/x-sv4crc", //$NON-NLS-1$ //$NON-NLS-2$
			"svf", "image/vnd.dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"svf", "image/x-dwg", //$NON-NLS-1$ //$NON-NLS-2$
			"svr", "application/x-world", //$NON-NLS-1$ //$NON-NLS-2$
			"svr", "x-world/x-svr", //$NON-NLS-1$ //$NON-NLS-2$
			"swf", "application/x-shockwave-flash", //$NON-NLS-1$ //$NON-NLS-2$

			"t", "application/x-troff", //$NON-NLS-1$ //$NON-NLS-2$
			"talk", "text/x-speech", //$NON-NLS-1$ //$NON-NLS-2$
			"tar", "application/x-tar", //$NON-NLS-1$ //$NON-NLS-2$
			"tbk", "application/toolbook", //$NON-NLS-1$ //$NON-NLS-2$
			"tbk", "application/x-tbook", //$NON-NLS-1$ //$NON-NLS-2$
			"tcl", "application/x-tcl", //$NON-NLS-1$ //$NON-NLS-2$
			"tcl", "text/x-script.tcl", //$NON-NLS-1$ //$NON-NLS-2$
			"tcsh", "text/x-script.tcsh", //$NON-NLS-1$ //$NON-NLS-2$
			"tex", "application/x-tex", //$NON-NLS-1$ //$NON-NLS-2$
			"texi", "application/x-texinfo", //$NON-NLS-1$ //$NON-NLS-2$
			"texinfo", "application/x-texinfo", //$NON-NLS-1$ //$NON-NLS-2$
			"text", "application/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"text", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$
			"tgz", "application/gnutar", //$NON-NLS-1$ //$NON-NLS-2$
			"tgz", "application/x-compressed", //$NON-NLS-1$ //$NON-NLS-2$
			"tif", "image/tiff", //$NON-NLS-1$ //$NON-NLS-2$
			"tif", "image/x-tiff", //$NON-NLS-1$ //$NON-NLS-2$
			"tiff", "image/tiff", //$NON-NLS-1$ //$NON-NLS-2$
			"tiff", "image/x-tiff", //$NON-NLS-1$ //$NON-NLS-2$
			"tr", "application/x-troff", //$NON-NLS-1$ //$NON-NLS-2$
			"tsi", "audio/tsp-audio", //$NON-NLS-1$ //$NON-NLS-2$
			"tsp", "application/dsptype", //$NON-NLS-1$ //$NON-NLS-2$
			"tsp", "audio/tsplayer", //$NON-NLS-1$ //$NON-NLS-2$
			"tsv", "text/tab-separated-values", //$NON-NLS-1$ //$NON-NLS-2$
			"turbot", "image/florian", //$NON-NLS-1$ //$NON-NLS-2$
			"txt", "text/plain", //$NON-NLS-1$ //$NON-NLS-2$

			"uil", "text/x-uil", //$NON-NLS-1$ //$NON-NLS-2$
			"uni", "text/uri-list", //$NON-NLS-1$ //$NON-NLS-2$
			"unis", "text/uri-list", //$NON-NLS-1$ //$NON-NLS-2$
			"unv", "application/i-deas", //$NON-NLS-1$ //$NON-NLS-2$
			"uri", "text/uri-list", //$NON-NLS-1$ //$NON-NLS-2$
			"uris", "text/uri-list", //$NON-NLS-1$ //$NON-NLS-2$
			"ustar", "application/x-ustar", //$NON-NLS-1$ //$NON-NLS-2$
			"ustar", "multipart/x-ustar", //$NON-NLS-1$ //$NON-NLS-2$
			"uu", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"uu", "text/x-uuencode", //$NON-NLS-1$ //$NON-NLS-2$
			"uue", "text/x-uuencode", //$NON-NLS-1$ //$NON-NLS-2$

			"vcd", "application/x-cdlink", //$NON-NLS-1$ //$NON-NLS-2$
			"vcs", "text/x-vcalendar", //$NON-NLS-1$ //$NON-NLS-2$
			"vda", "application/vda", //$NON-NLS-1$ //$NON-NLS-2$
			"vdo", "video/vdo", //$NON-NLS-1$ //$NON-NLS-2$
			"vew", "application/groupwise", //$NON-NLS-1$ //$NON-NLS-2$
			"viv", "video/vivo", //$NON-NLS-1$ //$NON-NLS-2$
			"viv", "video/vnd.vivo", //$NON-NLS-1$ //$NON-NLS-2$
			"vivo", "video/vivo", //$NON-NLS-1$ //$NON-NLS-2$
			"vivo", "video/vnd.vivo", //$NON-NLS-1$ //$NON-NLS-2$
			"vmd", "application/vocaltec-media-desc", //$NON-NLS-1$ //$NON-NLS-2$
			"vmf", "application/vocaltec-media-file", //$NON-NLS-1$ //$NON-NLS-2$
			"voc", "audio/voc", //$NON-NLS-1$ //$NON-NLS-2$
			"voc", "audio/x-voc", //$NON-NLS-1$ //$NON-NLS-2$
			"vos", "video/vosaic", //$NON-NLS-1$ //$NON-NLS-2$
			"vox", "audio/voxware", //$NON-NLS-1$ //$NON-NLS-2$
			"vqe", "audio/x-twinvq-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"vqf", "audio/x-twinvq", //$NON-NLS-1$ //$NON-NLS-2$
			"vql", "audio/x-twinvq-plugin", //$NON-NLS-1$ //$NON-NLS-2$
			"vrml", "application/x-vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"vrml", "model/vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"vrml", "x-world/x-vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"vrt", "x-world/x-vrt", //$NON-NLS-1$ //$NON-NLS-2$
			"vsd", "application/x-visio", //$NON-NLS-1$ //$NON-NLS-2$
			"vst", "application/x-visio", //$NON-NLS-1$ //$NON-NLS-2$
			"vsw", "application/x-visio", //$NON-NLS-1$ //$NON-NLS-2$

			"w60", "application/wordperfect6.0", //$NON-NLS-1$ //$NON-NLS-2$
			"w61", "application/wordperfect6.1", //$NON-NLS-1$ //$NON-NLS-2$
			"w6w", "application/msword", //$NON-NLS-1$ //$NON-NLS-2$
			"wav", "audio/wav", //$NON-NLS-1$ //$NON-NLS-2$
			"wav", "audio/x-wav", //$NON-NLS-1$ //$NON-NLS-2$
			"wb1", "application/x-qpro", //$NON-NLS-1$ //$NON-NLS-2$
			"wbmp", "image/vnd.wap.wbmp", //$NON-NLS-1$ //$NON-NLS-2$
			"web", "application/vnd.xara", //$NON-NLS-1$ //$NON-NLS-2$
			"wiz", "application/msword", //$NON-NLS-1$ //$NON-NLS-2$
			"wk1", "application/x-123", //$NON-NLS-1$ //$NON-NLS-2$
			"wmf", "windows/metafile", //$NON-NLS-1$ //$NON-NLS-2$
			"wml", "text/vnd.wap.wml", //$NON-NLS-1$ //$NON-NLS-2$
			"wmlc", "application/vnd.wap.wmlc", //$NON-NLS-1$ //$NON-NLS-2$
			"wmls", "text/vnd.wap.wmlscript", //$NON-NLS-1$ //$NON-NLS-2$
			"wmlsc", "application/vnd.wap.wmlscriptc", //$NON-NLS-1$ //$NON-NLS-2$
			"word", "application/msword", //$NON-NLS-1$ //$NON-NLS-2$
			"wpd", "application/x-wpwin", //$NON-NLS-1$ //$NON-NLS-2$
			"wq1", "application/x-lotus", //$NON-NLS-1$ //$NON-NLS-2$
			"wri", "application/mswrite", //$NON-NLS-1$ //$NON-NLS-2$
			"wri", "application/x-wri", //$NON-NLS-1$ //$NON-NLS-2$
			"wrl", "application/x-world", //$NON-NLS-1$ //$NON-NLS-2$
			"wrl", "model/vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"wrl", "x-world/x-vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"wrz", "model/vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"wrz", "x-world/x-vrml", //$NON-NLS-1$ //$NON-NLS-2$
			"wsc", "text/scriplet", //$NON-NLS-1$ //$NON-NLS-2$
			"wsrc", "application/x-wais-source", //$NON-NLS-1$ //$NON-NLS-2$
			"wtk", "application/x-wintalk", //$NON-NLS-1$ //$NON-NLS-2$
			"xbm", "image/x-xbitmap", //$NON-NLS-1$ //$NON-NLS-2$
			"xbm", "image/x-xbm", //$NON-NLS-1$ //$NON-NLS-2$
			"xbm", "image/xbm", //$NON-NLS-1$ //$NON-NLS-2$
			"xdr", "video/x-amt-demorun", //$NON-NLS-1$ //$NON-NLS-2$
			"xgz", "xgl/drawing", //$NON-NLS-1$ //$NON-NLS-2$
			"xif", "image/vnd.xiff", //$NON-NLS-1$ //$NON-NLS-2$
			"xl", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xla", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xla", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xla", "application/x-msexcel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlb", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlb", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlb", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlc", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlc", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlc", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xld", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xld", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlk", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlk", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xll", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xll", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xll", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlm", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlm", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlm", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xls", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xls", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xls", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xls", "application/x-msexcel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlt", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlt", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlv", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlv", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlw", "application/excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlw", "application/vnd.ms-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlw", "application/x-excel", //$NON-NLS-1$ //$NON-NLS-2$
			"xlw", "application/x-msexcel", //$NON-NLS-1$ //$NON-NLS-2$
			"xm", "audio/xm", //$NON-NLS-1$ //$NON-NLS-2$
			"xml", "application/xml", //$NON-NLS-1$ //$NON-NLS-2$
			"xml", "text/xml", //$NON-NLS-1$ //$NON-NLS-2$
			"xmz", "xgl/movie", //$NON-NLS-1$ //$NON-NLS-2$
			"xpix", "application/x-vnd.ls-xpix", //$NON-NLS-1$ //$NON-NLS-2$
			"xpm", "image/x-xpixmap", //$NON-NLS-1$ //$NON-NLS-2$
			"xpm", "image/xpm", //$NON-NLS-1$ //$NON-NLS-2$
			"x-png", "image/png", //$NON-NLS-1$ //$NON-NLS-2$
			"xsr", "video/x-amt-showrun", //$NON-NLS-1$ //$NON-NLS-2$
			"xwd", "image/x-xwd", //$NON-NLS-1$ //$NON-NLS-2$
			"xwd", "image/x-xwindowdump", //$NON-NLS-1$ //$NON-NLS-2$
			"xyz", "chemical/x-pdb", //$NON-NLS-1$ //$NON-NLS-2$

			"z", "application/x-compress", //$NON-NLS-1$ //$NON-NLS-2$
			"z", "application/x-compressed", //$NON-NLS-1$ //$NON-NLS-2$
			"zip", "application/x-compressed", //$NON-NLS-1$ //$NON-NLS-2$
			"zip", "application/x-zip-compressed", //$NON-NLS-1$ //$NON-NLS-2$
			"zip", "application/zip", //$NON-NLS-1$ //$NON-NLS-2$
			"zip", "multipart/x-zip", //$NON-NLS-1$ //$NON-NLS-2$
			"zoo", "application/octet-stream", //$NON-NLS-1$ //$NON-NLS-2$
			"zsh", "text/x-script.zsh", //$NON-NLS-1$ //$NON-NLS-2$
	};
}