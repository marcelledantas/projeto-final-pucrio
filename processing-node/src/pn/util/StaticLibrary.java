package pn.util;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pn.Model.Region;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 
 * @author meslin
 *
 */
public class StaticLibrary {
	/*
	 * constants
	 */
	public static final int DATAHORA = 0;
	public static final int ORDEM = 1;
	public static final int LINHA = 2;
	public static final int LATITUDE = 3;
	public static final int LONGITUDE = 4;
	public static final int VELOCIDADE = 5;

	public static final String USER_AGENT = "Mozilla/5.0";

	
	/*
	 * global command line configuration
	 */
	/** run as in a headless environment */
	public static boolean forceHeadless = false; 
	/** ContextNet IP address */
	public static String contextNetIPAddress;
	/** ContextNet TCP port number */
	public static int contextNetPortNumber;
	
	
	
	/** interval in ms (interval to create a thread */
	public static final long interval = 5000;
	public static long intervalBetweenThreads = 500;
	/** in % (interval variance to create a thread) */
	public static final long variance = 20;		// 

	
	
	/*
	 * statistics
	 */
	public static long nMessages = 0;
	/** start time - negative value means that there is no start time setted yet */
	public static long startTime = -1;
	/** stop time */
	public static long stopTime;
	
	
	
	/*
	 * Global data
	 */
	/** Core UUID */
	public static UUID coreUUID;
	/** número da mensagem */
	public static long sequencial;
	/** passenger group type */
	public static final int PASSENGER_GROUP = 0;

	
	/**
	 * Constructor<br>
	 */
	public StaticLibrary() {
		nMessages = 0;
		startTime = -1;
	}
	
	

	/**
	 * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
	 * 
	 * @return The date if it can be determined, or null if not.
	 */
	public static Date getClassBuildTime() {
		Date d = null;
		Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
		URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
		if (resource != null) {
			if (resource.getProtocol().equals("file")) {
				try {
					d = new Date(new File(resource.toURI()).lastModified());
				} catch (URISyntaxException ignored) {
				}
			} else if (resource.getProtocol().equals("jar")) {
				String path = resource.getPath();
				d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
			} else if (resource.getProtocol().equals("zip")) {
				String path = resource.getPath();
				File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
				// long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
				// Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
				try (JarFile jf = new JarFile(jarFileOnDisk)) {
					ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));	// Skip the ! and the /
					long zeTimeLong = ze.getTime();
					Date zeTimeDate = new Date(zeTimeLong);
					d = zeTimeDate;
				} catch (IOException | RuntimeException ignored) {
				}
			}
		}
		return d;
	}

	
	
	/**
	 * Reads the filenames file<br>
	 * This file has a filename per line<br>
	 * Each filename represents a region (group) on the map<br>
	 * @param workdir work directory ending with a slash
	 * @param filename name of the file with filenames
	 * @return list of regions (without coordinates)
	 */
	public static List<Region> readFilenamesFile(String workdir, String filename) {
		// read the file composed by a filename per line
		BufferedReader br = null;
		List<Region> regions = new ArrayList<Region>();
		
		try {
			br = new BufferedReader(new FileReader(workdir + filename));
		} catch (IOException e0) {
			try {
				Debug.warning("Error while reading " + workdir + filename, e0);
				workdir = "/Users/imarc/IdeaProjects/gd-pn/";
				br = new BufferedReader(new FileReader(workdir + filename));
			}
			catch (IOException e) {
				Debug.warning("Error while reading " + workdir + filename, e);
				Debug.warning("Available files are:");
				File directory = new  File(".");
				File[] files = directory.listFiles();
				for(File file: files) {
					Debug.warning(file.getAbsolutePath());
				}
			}
		}
		
		String line;
		try {
			while((line = br.readLine()) != null)
			{
				String[] data = line.split(",");
				Region region = new Region();
				region.setNumber(Integer.parseInt(data[0]));
				region.setFilename(data[1].trim());
				regions.add(region);
			}
		}
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					Debug.warning("Could not close " + workdir + filename, e);
				}
			}
		}

		return regions;
	}
	
	
	
	/**
	 * Reads a region from a given file<br>
	 * @param filename	name of the file describing a region
	 * @return a region
	 */
	public static List<Coordinate> readRegion(String workdir, String filename) {
		// reads a region. A region is described by an X, Y coordinate per line
		List<Coordinate> points = new ArrayList<Coordinate>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(workdir + filename));
		} catch (IOException e0) {
			try {
				Debug.warning("Could not read region file " + workdir + filename, e0);
				workdir = "/Users/imarc/IdeaProjects/gd-pn/";
				br = new BufferedReader(new FileReader(workdir + filename));
			}
			catch (IOException e) {
				Debug.warning("Could not read region file " + workdir + filename, e);
				Debug.warning("Available files are:");
				File directory = new  File(".");
				File[] files = directory.listFiles();
				for(File file: files) {
					Debug.warning(file.getAbsolutePath());
				}
			}
		}
		String line;
		try {
			while((line = br.readLine()) != null) {
				Coordinate coordinate = new Coordinate(
						Double.parseDouble(line.substring(0, line.indexOf(" ")).trim()),
						Double.parseDouble(line.substring(line.indexOf(" ")).trim())
						);
				points.add(coordinate);
			}
		}
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					System.err.println("Date = " + new Date());
					e.printStackTrace();
				}
			}
		}
		return points;
	}

	
	
	/**
	 * Reads and returns a text file
	 * @param filename
	 * @return text file content
	 */
	public static String readFile(String filename) {
		Debug.warning("Reading file " + filename);
		
		// read the file composed by a filename per line
		BufferedReader br = null;
		String buffer = "";
		
		try
		{
			br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null) {
				buffer += line.trim();
			}
		}
		catch (IOException e)
		{
			System.err.println("Date = " + new Date());
			e.printStackTrace();
		}
		finally {
			if(br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					System.err.println("Date = " + new Date());
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}
	
	
	
	public static String getInitParameter(String name) {
		String workDir = System.getProperty("user.dir");
		String filename = "WebContent/WEB-INF/web.xml";
		String data = null;
		
	    // Instantiate the Factory
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	    try {
	    	// optional, but recommended
	        // process XML securely, avoid attacks like XML External Entities (XXE)
	        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

	        // parse XML file
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new File(workDir + "/" + filename));

	        // optional, but recommended
	        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	        doc.getDocumentElement().normalize();

	        // get <context-param>
	        NodeList list = doc.getElementsByTagName("context-param");
	        for (int i = 0; i < list.getLength(); i++) {
	        	Node node = list.item(i);
        		Element element = (Element) node;
	        	if (node.getNodeType() == Node.ELEMENT_NODE && 
	        		element.getElementsByTagName("param-name").item(0).getTextContent().equals(name)) {
	        		// get text
	                data = element.getElementsByTagName("param-value").item(0).getTextContent();
	        	}
	        }
	    } catch (IOException | ParserConfigurationException | SAXException e) {	
	    	e.printStackTrace();
	    }
	    return data; 
	}

	public static void setEnv(Map<String, String> newenv) throws Exception {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
		}
		catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String, String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}

	}
}
