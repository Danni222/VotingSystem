package com.qa.ui;

public class XMLParse {
	public static String formatXml(String xml) {
		xml = xml.replaceAll("&lt;", "<");
		xml = xml.replaceAll("&gt;", ">");
		xml = xml.replace("\r", "");
		xml = xml.replace("\n", "");
		xml = xml
				.replace(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">",
						"<string>");
		xml = xml
				.replace(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://www.zlsoft.com\">",
						"<string>");
		xml = xml
				.replace(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://www.zlsoft.com\" />",
						"<string></string>");

		xml = xml
				.replace(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\" />",
						"<string></string>");
		xml=xml.replace("><?xml version=\"1.0\" encoding=\"UTF-8\"?>", ">");
		return xml;
	}
}