
package steffen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TempFile {
	private String				tempFolder	= "temp";
	private boolean			oneFile;
	private File				file			= null;
	private FileWriter		writer		= null;
	private BufferedReader	reader		= null;
	
	TempFile(String path) {
		this(path, true, true);
	}
	
	TempFile(String path, boolean oneFile) {
		this(path, oneFile, true);
	}
	
	TempFile(String path, boolean oneFile, boolean delete) {
		file = new File(tempFolder);
		file.mkdirs();
		if (delete) {
			file.deleteOnExit();
		}
		file = new File(tempFolder + "/" + path);
		if (delete) {
			file.deleteOnExit();
		}
		this.oneFile = oneFile;
	}
	
	private void readMode() {
		if (reader == null) {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = null;
			}
			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void writeMode() {
		if (writer == null) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
			try {
				writer = new FileWriter(file, true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void endReadMode() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader = null;
		}
	}
	
	public void add(String line) {
		this.writeMode();
		try {
			writer.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addInteger(Integer integer) {
		String nmbr = String.valueOf(integer);
		if (oneFile) {
			this.add(nmbr + "\n");
		} else {
			File f = new File(tempFolder + "/" + nmbr);
			try {
				f.createNewFile();
				f.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean hasNext() {
		this.readMode();
		boolean ready = false;
		try {
			ready = reader.ready();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ready;
	}
	
	public String next() {
		this.readMode();
		if (this.hasNext()) {
			try {
				if (reader.ready()) {
					return reader.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Integer nextInt() {
		String line = this.next();
		return Integer.valueOf(line);
	}
	
	public boolean contains(String ref) {
		if (oneFile) {
			this.readMode();
			while (this.hasNext()) {
				if (this.next().equals(ref)) {
					return true;
				}
			}
			this.endReadMode();
			return false;
		} else {
			File f = new File(ref);
			return f.exists();
		}
	}
}
