/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.update;

import java.io.*;
import java.util.StringTokenizer;
import javax.xml.parsers.*;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Parser for platform.xml files. 
 * 
 * @since 1.0
 */
public class ConfigurationParser implements ConfigurationConstants {

	/*
	 * Parse the given file handle which points to a platform.xml file and a configuration object.
	 * Returns null if the file doesn't exist.
	 */
	public static Configuration parse(File file) throws ProvisionException {
		return new ConfigurationParser().internalParse(file);
	}

	/*
	 * Create a feature object based on the given DOM node. 
	 * Return the new feature.
	 */
	private Feature createFeature(Node node, Site site) {
		Feature result = new Feature(site);
		String id = getAttribute(node, ATTRIBUTE_ID);
		if (id != null)
			result.setId(id);
		String url = getAttribute(node, ATTRIBUTE_URL);
		if (url != null)
			result.setUrl(url);
		String version = getAttribute(node, ATTRIBUTE_VERSION);
		if (version != null)
			result.setVersion(version);
		return result;
	}

	/*
	 * Create the features from the given DOM node.
	 */
	private void createFeatures(Node node, Site site) {
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!ELEMENT_FEATURE.equalsIgnoreCase(child.getNodeName()))
				continue;
			Feature feature = createFeature(child, site);
			if (feature != null)
				site.addFeature(feature);
		}
	}

	/*
	 * Create a site based on the given DOM node.
	 */
	private Site createSite(Node node) {
		Site result = new Site();
		String policy = getAttribute(node, ATTRIBUTE_POLICY);
		if (policy != null)
			result.setPolicy(policy);
		String enabled = getAttribute(node, ATTRIBUTE_ENABLED);
		if (enabled != null)
			result.setEnabled(Boolean.valueOf(enabled).booleanValue());
		String updateable = getAttribute(node, ATTRIBUTE_UPDATEABLE);
		if (updateable != null)
			result.setUpdateable(Boolean.valueOf(updateable).booleanValue());
		String url = getAttribute(node, ATTRIBUTE_URL);
		if (url != null)
			result.setUrl(url);
		String linkFile = getAttribute(node, ATTRIBUTE_LINKFILE);
		if (linkFile != null)
			result.setLinkFile(linkFile);
		String list = getAttribute(node, ATTRIBUTE_LIST);
		if (list != null)
			for (StringTokenizer tokenizer = new StringTokenizer(list, ","); tokenizer.hasMoreTokens();) //$NON-NLS-1$
				result.addPlugin(tokenizer.nextToken());
		createFeatures(node, result);
		return result;
	}

	/*
	 * Return the attribute with the given name, or null if it does
	 * not exist.
	 */
	private String getAttribute(Node node, String name) {
		NamedNodeMap attributes = node.getAttributes();
		Node temp = attributes.getNamedItem(name);
		return temp == null ? null : temp.getNodeValue();
	}

	/*
	 * Load the given file into a DOM document.
	 */
	private Document load(InputStream input) throws ParserConfigurationException, IOException, SAXException {
		// load the feature xml
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		input = new BufferedInputStream(input);
		try {
			return builder.parse(input);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
		}
	}

	/*
	 * Parse the given file handle which points to a platform.xml file and a configuration object.
	 * Returns null if the file doesn't exist.
	 */
	private Configuration internalParse(File file) throws ProvisionException {
		if (!file.exists())
			return null;
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			Document document = load(input);
			return process(document);
		} catch (IOException e) {
			throw new ProvisionException("An error occurred reading the platform configuration file: " + file, e);
		} catch (ParserConfigurationException e) {
			throw new ProvisionException("An error occurred reading the platform configuration", e);
		} catch (SAXException e) {
			throw new ProvisionException("An error occurred reading the platform configuration", e);
		}
	}

	/*
	 * Process the given DOM document and create the appropriate
	 * site objects.
	 */
	private Configuration process(Document document) {
		Node node = getConfigElement(document);
		if (node == null)
			return null;
		Configuration configuration = createConfiguration(node);
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!ELEMENT_SITE.equalsIgnoreCase(child.getNodeName()))
				continue;
			Site site = createSite(child);
			if (site != null)
				configuration.add(site);
		}
		return configuration;
	}

	private Configuration createConfiguration(Node node) {
		Configuration result = new Configuration();
		String value = getAttribute(node, ATTRIBUTE_DATE);
		if (value != null)
			result.setDate(value);
		value = getAttribute(node, ATTRIBUTE_TRANSIENT);
		if (value != null)
			result.setTransient(Boolean.valueOf(value).booleanValue());
		value = getAttribute(node, ATTRIBUTE_SHARED_UR);
		if (value != null)
			result.setSharedUR(value);
		value = getAttribute(node, ATTRIBUTE_VERSION);
		if (value != null)
			result.setVersion(value);
		return result;
	}

	private Node getConfigElement(Document doc) {
		NodeList children = doc.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (ELEMENT_CONFIG.equalsIgnoreCase(child.getNodeName()))
				return child;
		}
		return null;
	}
}
