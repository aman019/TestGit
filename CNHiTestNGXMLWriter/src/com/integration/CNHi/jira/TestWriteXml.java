package com.integration.CNHi.jira;


import java.util.Map;



public class TestWriteXml {

	public static void main (String args[]) {
		XMLWriter xmlWriter = new XMLWriter();
		XmlParser xmlParser = new XmlParser();

		xmlParser.setFilePath(System.getProperty("user.dir")+"\\JiraXml"+"\\JiraXML.xml");

		Map map = xmlParser.parseXML(); //use setter for this
		System.out.println(System.getProperty("user.dir")+"\\testNGXML");

		xmlWriter.setFilePath(System.getProperty("user.dir")+"\\testNGXML"+"\\testNG.xml");

		xmlWriter.writeXml(map);
	}

}
