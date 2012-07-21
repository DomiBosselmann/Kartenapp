
package steffen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KillCrap {
	private static String	fileSource	= "bawu.xml";
	private static String	fileTarget	= "bawu2.xml";
	
	/*
	 * public static void mains(String[] args) throws ParserConfigurationException, SAXException, IOException,
	 * TransformerFactoryConfigurationError, TransformerException {
	 * 
	 * DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	 * 
	 * File oldFile = new File(Constants.pathToExternXMLs + "test.xml"); Document document = documentBuilder.parse(oldFile);
	 * 
	 * Element osmElement = document.getDocumentElement();
	 * 
	 * NodeList relations = osmElement.getElementsByTagName("relation"); for (int i = 0; i < relations.getLength(); i++) {
	 * Node node = relations.item(i); osmElement.removeChild(node); }
	 * 
	 * NodeList nodes = osmElement.getElementsByTagName("node"); for (int i = 0; i < nodes.getLength(); i++) {
	 * removeNotNeededAttributes(nodes.item(i).getAttributes()); }
	 * 
	 * NodeList tags = osmElement.getElementsByTagName("tag"); for (int i = 0; i < tags.getLength(); i++) { Node tag =
	 * tags.item(i); NamedNodeMap attributes = tag.getAttributes(); for (int j = 0; j < attributes.getLength(); j++) { Node
	 * attribute = attributes.item(j); if (attribute.getNodeName().equals("k")) { if
	 * ((attribute.getNodeValue().indexOf("created_by") >= 0) || (attribute.getNodeValue().indexOf("TMC") >= 0)) { //
	 * osmElement.removeChild(tag); break; } } } }
	 * 
	 * File newFile = new File(Constants.pathToExternXMLs + "test2.xml"); Transformer transformer =
	 * TransformerFactory.newInstance().newTransformer(); transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	 * transformer.setOutputProperty(OutputKeys.INDENT, "no"); transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	 * transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); transformer.transform(new DOMSource(document),
	 * new StreamResult(newFile)); }
	 * 
	 * private static void removeNotNeededAttributes(NamedNodeMap attributes) { if (attributes.getNamedItem("user") != null) {
	 * attributes.removeNamedItem("user"); } if (attributes.getNamedItem("uid") != null) { attributes.removeNamedItem("uid");
	 * } if (attributes.getNamedItem("timestamp") != null) { attributes.removeNamedItem("timestamp"); } if
	 * (attributes.getNamedItem("visible") != null) { attributes.removeNamedItem("visible"); } if
	 * (attributes.getNamedItem("version") != null) { attributes.removeNamedItem("version"); } if
	 * (attributes.getNamedItem("changeset") != null) { attributes.removeNamedItem("changeset"); } }
	 */
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin killing crap...");
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.pathToExternXMLs + KillCrap.fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.pathToExternXMLs + KillCrap.fileTarget)));
		
		String line = null;
		while (reader.ready()) {
			line = reader.readLine();
			if (line.indexOf("<relation") < 0) {
				if (line.indexOf("<tag") < 0) {
					if (line.indexOf("<osm") >= 0) {
						writer.write("<osm>" + Constants.lineSeparator);
					} else {
						line = KillCrap.deleteAttribute("user", line);
						line = KillCrap.deleteAttribute("uid", line);
						line = KillCrap.deleteAttribute("timestamp", line);
						line = KillCrap.deleteAttribute("visible", line);
						line = KillCrap.deleteAttribute("version", line);
						line = KillCrap.deleteAttribute("changeset", line);
						line = KillCrap.cleanLine(line);
						writer.write(line + Constants.lineSeparator);
					}
				} else {
					// Tag checks
					if (line.indexOf("k=\"TMC") < 0) {
						if (line.indexOf("k=\"created_by\"") < 0) {
							line = KillCrap.cleanLine(line);
							writer.write(line + Constants.lineSeparator);
						}
					}
				}
			} else {
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
					} while (line.indexOf("</relation") < 0);
				}
			}
		}
		reader.close();
		writer.close();
		
		System.out.println("Done");
	}
	
	private static String cleanLine(String input) {
		input = input.trim();
		while (input.indexOf("  ") >= 0) {
			input = input.replaceAll("  ", " ");
		}
		input = input.replaceAll(" >", ">");
		input = input.replaceAll(" />", "/>");
		return input;
	}
	
	private static String deleteAttribute(String attribute, String input) {
		attribute += "=\"";
		int first = input.indexOf(attribute);
		int second = input.indexOf("\"", first + attribute.length());
		if (first >= 0 && second >= first) {
			input = input.substring(0, first) + input.substring(second + 1, input.length());
		}
		return input;
	}
}
