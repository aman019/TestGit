package com.integration.CNHi.jira;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is parsing the xml file exported from Jira and fetching the Label
 * tags out of that into a Map object.
 * <p>
 * User can refer to this class by making the object of the type
 * <code>XmlParser</code>
 * <p>
 * <b>Usage:</b><br>
 * <code>XmlParser xmlParser = new XmlParser();</code>
 *
 * @author Vishal Kumar
 *
 */
public class XmlParser {

	private static Logger logger = LogManager.getLogger(XmlParser.class.getName());
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Document document;
	Element rootElement;
	Map map = new HashMap<String, String>();
	ArrayList list = new ArrayList<String>();
	static HashSet<String> hashSet;
	String filePath;

	/**
	 * Returns the file path for the xml file exported from Jira.
	 * <p>
	 * <b>Usage:</b><br>
	 * <code>
	 * XmlParser xmlParser = new XmlParser();<br>
	 * xmlParser.getFilePath();</code>
	 * <p>
	 *
	 * @return
	 *
	 */
	private String getFilePath() {
		return filePath;
	}

	/**
	 * Initializes and holds the file path of the xml exported from Jira.
	 * <p>
	 * <b>Usage:</b><br>
	 * <code>
	 * XmlParser xmlParser = new XmlParser();<br>
	 * xmlParser.setFilePath();</code>
	 * <p>
	 *
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Returns the map object containing labels extracted from the xml file.
	 *
	 * @param filePath
	 *            Path of the xml file to be parsed.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map parseXML() {
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(getFilePath()));
			rootElement = document.getDocumentElement();

		} catch (Exception e) {
			logger.error("Exception occured is: " + e.getMessage());
		}

		Map map = returnMap("label", rootElement);

		return map;

	}

	/**
	 * Returns the map object of label tags from the parsed xml file.
	 *
	 * @param tagName
	 *            XML tag name which needs to be extracted.
	 * @param element
	 *            Root element of the xml file.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map returnMap(String tagName, Element element) {

		try {
			NodeList nodeList = element.getElementsByTagName(tagName);
			logger.info("NodeList list length: " + nodeList.getLength());

			if (nodeList.getLength() >= 5) {

				for (int i = 0; i < nodeList.getLength(); i++) {

					NodeList subList = nodeList.item(i).getChildNodes();

					String rowLabel = subList.item(0).getNodeValue();
					String params[] = rowLabel.toString().split(":");

					for (String s : params) {
						if (params[0].equals("method")) {
							list.add(params[1]);
						} else {
							map.put(params[0], params[1]);

						}
					}

				}
				hashSet = new HashSet<String>(list);

				if (validationCheck(map, list) == true) {
					return map;
				} else {
					logger.info("Incorrect label names");
					System.exit(0);

				}
			} else {
				logger.info("Label count is less than 5");
				System.exit(0);
			}
		} catch (Exception e) {
			logger.error("Exception is: " + e.getMessage());
		}

		return map;

	}

	/**
	 * Returns true if label names contained in map object are appropriate as
	 * per standard, false otherwise.
	 *
	 * @param map
	 *            Map object having 'label' tags from the parsed xml file.
	 * @return
	 */
	private boolean validationCheck(Map map, ArrayList list) {

		if (list.size() > 0 && map.containsKey("appType") && map.containsKey("browser")
				&& map.containsKey("browserVersion") && map.containsKey("platform")) {

			return true;
		}
		return false;
	}
}
