/**
 * 
 */
package gd.main;

import gd.contextnet.MyGroupDefiner;

import gd.util.StaticLibrary;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author meslin
 * 
 * This application creates a core environment with a Processing Node thread and
 * It also creates a consumer to send data to a InterSCity 
 *
 */
public class MyGroupDefinerMain {
	/*
	 * Configuration parameters
	 */
	/** group description region file */
	private static String workDir;
	/** group description file name */
	private static String filename;

	/** A queue to send information (objects) to the ContextNet Processing Node */
	public static ConcurrentLinkedQueue<Object> dataToPNQueue = new ConcurrentLinkedQueue<Object>();

	public static Map<String,String> env = new HashMap<String, String>();

	/**
	 * 
	 */
	public MyGroupDefinerMain() {
	}

	private static void setEnv(Map<String, String> newenv) throws Exception {
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

	private static void setEnvironmentVariables(){
		env.putAll(System.getenv());
		if(System.getenv("gd.one.consumer.topics") == null)
			env.put("gd.one.consumer.topics", "GroupReportTopic");
		if(System.getenv("gd.one.consumer.auto.offset.reset") == null)
			env.put("gd.one.consumer.auto.offset.reset", "latest");
		if(System.getenv("gd.one.consumer.bootstrap.servers") == null)
			env.put("gd.one.consumer.bootstrap.servers", "127.0.0.1:9092");
		if(System.getenv("gd.one.consumer.group.id") == null)
			env.put("gd.one.consumer.group.id", "gw-gd");
		if(System.getenv("gd.one.producer.bootstrap.servers") == null)
			env.put("gd.one.producer.bootstrap.servers", "127.0.0.1:9092");
		if(System.getenv("gd.one.producer.retries") == null)
			env.put("gd.one.producer.retries", "3");
		if(System.getenv("gd.one.producer.enable.idempotence") == null)
			env.put("gd.one.producer.enable.idempotence", "true");
		if(System.getenv("gd.one.producer.linger.ms") == null)
			env.put("gd.one.producer.linger.ms", "1");
	}

	/**
	 * @param args
	 * @throws
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) {

		// Build date
		final Date buildDate = StaticLibrary.getClassBuildTime();
		System.out.println("BenchmarMyCore builed at " + buildDate);
	    System.out.println("Working Directory is " + System.getProperty("user.dir"));
	    
	    System.out.println("Using OSPL_HOME:       " + System.getenv("OSPL_HOME"));
	    System.out.println("Using PATH:            " + System.getenv("PATH"));
	    System.out.println("Using LD_LIBRARY_PATH: " + System.getenv("LD_LIBRARY_PATH"));
	    System.out.println("Using CPATH            " + System.getenv("CPATH"));
	    System.out.println("Using OSPL_URI         " + System.getenv("OSPL_URI"));
	    
		// get command line options
		Options options = new Options();
		Option option;

		option = new Option("h", "force-headless", false, "Run as in a headless environment");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("w", "workdir", true, "Directory where WebContent/WEB-INF/web.xml is located");
		option.setRequired(false);
		options.addOption(option);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Date = " +  new Date());
			formatter.printHelp("MyGroupDefinerMain", options);
			e.printStackTrace();
			return;
		}
		
		// getting command line and init options
		// ContextNet IP address
		if((StaticLibrary.contextNetIPAddress = System.getenv("REGIONALERT_GATEWAYIP")) == null) { 
			StaticLibrary.contextNetIPAddress = StaticLibrary.getInitParameter("gatewayIP");
		}
		
		// group description filename
		if((workDir = System.getenv("REGIONALERT_WORKDIR")) == null) {
			if((workDir = cmd.getOptionValue("workdir")) == null) {
				workDir = StaticLibrary.getInitParameter("workDir");
			}
		}
	    System.out.println("Working Directory set to " + workDir);
	    if((filename = System.getenv("REGIONALERT_GROUPDESCRIPTIONFILENAME")) == null) {
	    	filename = StaticLibrary.getInitParameter("groupDescriptionFilename");	// filename = cmd.getOptionValue("groupfilename");
	    }
	    
		// ContextNet TCP port number
	    try {
			StaticLibrary.contextNetPortNumber = Integer.parseInt(System.getenv("REGIONALERT_GATEWAYPORTNUMBER"));
		}
		catch (Exception e1) {
			try {
				StaticLibrary.contextNetPortNumber = Integer.parseInt(StaticLibrary.getInitParameter("gatewayPort"));
			} catch(Exception e) {
				e.printStackTrace();
				StaticLibrary.contextNetPortNumber = 5500;
			}
		}
		

		/*
		 * Catch Ctrl+C
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	long elapsedTime = StaticLibrary.stopTime - StaticLibrary.startTime;
		    	System.err.println("CTRL+C");
		    	System.err.println("Time: " +  elapsedTime + " (" + StaticLibrary.stopTime + " - " + StaticLibrary.startTime + ") with " + StaticLibrary.nMessages + " messages");
		    }
		});

		System.out.println("\n\nStarting MyGroupDefinerMain using gateway at " + StaticLibrary.contextNetIPAddress + ":" + StaticLibrary.contextNetPortNumber + "\n\n");
		System.out.println("Ready, set...");

		/*
		 * Creating GroupSelector
		 */
		try{
			setEnvironmentVariables();
			setEnv(env);
			new MyGroupDefiner(workDir, filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		System.out.println("\nGO!");
		while(true) {}
	}
}
