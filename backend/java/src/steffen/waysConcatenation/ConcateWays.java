
package steffen.waysConcatenation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import steffen.Constants;
import steffen.layer.Layer;

public class ConcateWays {
	private static Layer	layer	= Layer.Motorways;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin concatenation of ways...");
		
		Hashtable<Integer, List<Integer>> ways = new Hashtable<>();
		
		String fileSource = Constants.pathToExternXMLs + "bawu " + ConcateWays.layer.name + ".xml";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileSource)));
		
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.indexOf("<way") >= 0) {
				int id_begin = line.indexOf("id=\"") + 4;
				int id_end = line.indexOf("\"", id_begin);
				if (id_end > id_begin) {
					Integer way_id = Integer.valueOf(line.substring(id_begin, id_end));
					List<Integer> nds = new LinkedList<>();
					do {
						line = reader.readLine();
						if (line.indexOf("nd") >= 0) {
							id_begin = line.indexOf("id=\"") + 4;
							id_end = line.indexOf("\"", id_begin);
							Integer nd_id = Integer.valueOf(line.substring(id_begin, id_end));
							nds.add(nd_id);
						}
					} while (line.indexOf("</way") < 0);
					ways.put(way_id, nds);
				}
			}
		}
		
		reader.close();
		
		boolean concated = false;
		do {
			Enumeration<List<Integer>> elements = ways.elements();
			List<Integer> way = elements.nextElement();
			Integer first = way.get(0);
			Integer last = way.get(way.size() - 1);
			while (elements.hasMoreElements()) {
				List<Integer> nextWay = elements.nextElement();
				Integer nextFirst = nextWay.get(0);
				Integer nextLast = nextWay.get(nextWay.size() - 1);
				if (last.intValue() == nextFirst.intValue()) {
				}
			}
		} while (concated);
		
		reader = new BufferedReader(new FileReader(new File(fileSource)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileSource.replaceFirst(".xml", "2.xml"))));
		
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Constants.lineSeparator
				+ "<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\">"
				+ Constants.lineSeparator);
		
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.indexOf("<node") >= 0) {
				writer.write(line + Constants.lineSeparator);
				if (line.indexOf("/>") < 0) {
					do {
						line = reader.readLine();
						writer.write(line + Constants.lineSeparator);
					} while (line.indexOf("</node") < 0);
				}
			}
		}
		
		reader.close();
		
		Enumeration<Integer> keys = ways.keys();
		while (keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			List<Integer> way = ways.get(key);
			writer.write("<way id=\"" + key + "\">" + Constants.lineSeparator);
			Iterator<Integer> nds = way.iterator();
			while (nds.hasNext()) {
				writer.write("<nd ref=\"" + nds.next().intValue() + "\"/>" + Constants.lineSeparator);
			}
			writer.write("</way>" + Constants.lineSeparator);
		}
		writer.write("</osm>" + Constants.lineSeparator);
		writer.close();
		
		System.out.println("Done");
	}
}
