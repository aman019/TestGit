package com.integration.CNHi.jira;

import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to write the testNGXML file from the exported xml from
 * Jira with required labels to run the test cases.
 * <p>
 * User can refer to this class by making the object of the type
 * <code>XMLWriter</code>
 * <p>
 * <b>Usage:</b><br>
 * <code>XMLWriter xmlWriter = new XMLWriter();</br>
 * xmlWriter.writeXML(map);</code>
 *
 * @author Vishal Kumar
 *
 */
public class XMLWriter {

	private static Logger logger = LogManager.getLogger(XMLWriter.class.getName());

	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
	Document doc;
	Element element;
	String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Writes the testNGXML xml file using labels from the xml exported from
	 * Jira.
	 *
	 * @param map
	 *            Map object received from the XmlParser class.
	 */
	public void writeXml(Map map) {
		logger.debug("[XMLWriter:writeXml()] To write the testNGXML out of the labels in map");
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			dbFactory.setValidating(false);
			doc = dBuilder.newDocument();

			Comment comment = doc.createComment("DOCTYPE suite SYSTEM" + "'http://testng.org/testng-1.0.dtd'");
			Element suite = doc.createElement("suite");
			suite.setAttribute("thread-count", "1");
			suite.setAttribute("name", "CNHi_TestSuite");
			suite.setAttribute("parallel", "none");

			Element test = doc.createElement("test");
			test.setAttribute("name", "CNHi Test");
			Element parameter = doc.createElement("parameter");
			Element parameter2 = doc.createElement("parameter");
			Element parameter3 = doc.createElement("parameter");
			Element parameter4 = doc.createElement("parameter");
			parameter.setAttribute("name", "appType");
			parameter.setAttribute("value", map.get("appType").toString());
			parameter2.setAttribute("name", "browser");
			parameter2.setAttribute("value", map.get("browser").toString());
			parameter3.setAttribute("name", "browserVersion");
			parameter3.setAttribute("value", map.get("browserVersion").toString());
			parameter4.setAttribute("name", "platform");
			parameter4.setAttribute("value", map.get("platform").toString());

			Element classes = doc.createElement("classes");
			Element class1 = doc.createElement("class");
			class1.setAttribute("name", "com.test.CNHi.testSuite.TestPlan");

			Element methods = doc.createElement("methods");

			for (String values : XmlParser.hashSet) {
				Element include = doc.createElement("include");
				include.setAttribute("name", values);

				methods.appendChild(include);
			}

			class1.appendChild(methods);
			classes.appendChild(class1);

			test.appendChild(parameter);
			test.appendChild(parameter2);
			test.appendChild(parameter3);
			test.appendChild(parameter4);
			test.appendChild(classes);

			suite.appendChild(test);

			doc.appendChild(comment);

			doc.appendChild(suite);

			prettyPrint(doc);

		} catch (Exception e) {
			logger.error("Exception is: " + e.getMessage());
		}
	}

	/**
	 * Actual writing of the xml document.
	 *
	 * @param doc
	 *            Object of DocumentBuilder class.
	 */
	private void prettyPrint(Document doc) {
		logger.debug("[XMLWriter:prettyPrint()] To write the contents in the file ");
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult file = new StreamResult(new File(getFilePath()));
			tf.transform(source, file);
		} catch (Exception e) {
			logger.error("Exception is: " + e.getMessage());
		}
	}
}
