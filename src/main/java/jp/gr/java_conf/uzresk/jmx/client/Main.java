package jp.gr.java_conf.uzresk.jmx.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.gr.java_conf.uzresk.jmx.client.util.StringUtils;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger("default");

	private static final Logger SIMPLE_LOG = LoggerFactory.getLogger("simple");

	private MBeanServerConnection mbsc = null;

	private boolean init = true;

	private List<String> header = new ArrayList<String>();

	/**
	 * args[0] URL args[1] ObjectName args[2] AttributeName args[3] interval
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (StringUtils.isBlank(args[0])) {
			LOG.error("URL is not null.");
			System.exit(1);
		}
		String url = args[0];

		List<ObjectNameAttribute> metrics = load();

		// Bad only if the file or either argument
		if (!metrics.isEmpty() && args.length >= 3) {
			LOG.error("Bad only if the file or either argument.");
			System.exit(1);
		}

		String intervalStr = null;

		boolean isShowDomains = false;

		if (metrics.isEmpty()) {

			if (args.length == 1) {
				isShowDomains = true;
			} else if (args.length == 3) {
				metrics.add(new ObjectNameAttribute(args[1], args[2]));
			} else if (args.length == 4) {
				metrics.add(new ObjectNameAttribute(args[1], args[2]));
				intervalStr = args[3];
			}
		} else {
			if (args.length == 2) {
				intervalStr = args[1];
			}
		}

		int interval = 0;
		if (StringUtils.isNotBlank(intervalStr)) {
			try {
				interval = Integer.parseInt(intervalStr);
			} catch (NumberFormatException e) {
				LOG.error("polling interval must be numeric.");
				System.exit(1);
			}
		}

		try {
			new Main().collect(url, isShowDomains, metrics, interval);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			System.exit(2);
		}
		System.exit(0);
	}

	public void collect(String url, boolean isShowDomains, List<ObjectNameAttribute> metrics, int interval) {

		JMXServiceURL jmxServiceUrl = null;
		try {
			jmxServiceUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		JMXConnector connector = null;
		try {
			connector = JMXConnectorFactory.connect(jmxServiceUrl);

			mbsc = connector.getMBeanServerConnection();

			if (isShowDomains) {
				outputObjectNames();
			} else {
				// headerを取るために１回空振りさせる
				outputAttribute(metrics);
				init = false;
				showHeader();

				if (interval != 0) {
					while (true) {
						try {
							LOG.info(outputAttribute(metrics));
							Thread.sleep(interval * 1000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				} else {
					LOG.info(outputAttribute(metrics));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot connect jmx server [" + url + "]", e);
		} finally {
			if (connector != null) {
				try {
					connector.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}

	private static List<ObjectNameAttribute> load() {
		String path = System.getProperty("path");
		if (StringUtils.isBlank(path)) {
			return new ArrayList<ObjectNameAttribute>();
		}

		File file = new File(path);
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found. [" + path + "]", e);
		}
		BufferedReader br = null;
		String str = null;

		List<ObjectNameAttribute> values = new ArrayList<ObjectNameAttribute>();
		try {
			br = new BufferedReader(fr);
			str = br.readLine();
			str = str != null ? str.substring(1, str.length() - 1) : null;

			while (str != null) {
				String[] splitStr = str.split("\" \"");
				if (splitStr.length != 2) {
					throw new RuntimeException(
							"Either it has not been specified in the ObjectName or attribute. [" + str + "]");
				}
				values.add(new ObjectNameAttribute(splitStr[0], splitStr[1]));
				str = br.readLine();
				str = str != null ? str.substring(1, str.length() - 1) : null;
			}
		} catch (IOException e) {
			throw new RuntimeException("file cannot be read. [" + path + "]");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return values;
	}

	private void showHeader() {
		int i = 0;
		String headerLine = "";
		for (String s : header) {
			if (i == 0) {
				headerLine = s;
			} else {
				headerLine = headerLine + "," + s;
			}
			i++;
		}
		SIMPLE_LOG.info(headerLine);
	}

	private void outputObjectNames() {
		Set<ObjectName> names = null;
		try {
			names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));
		} catch (IOException e) {
			throw new RuntimeException("can't retrieve mbean.", e);
		}

		for (ObjectName name : names) {
			System.out.println(name.toString());
		}

	}

	private String outputAttribute(List<ObjectNameAttribute> metrics) {

		String collect = "";
		int i = 0;

		for (ObjectNameAttribute objectNameAttribute : metrics) {

			if (i == 0) {
				collect = prettyPrintAttribute(objectNameAttribute);
			} else {
				collect = collect + "," + prettyPrintAttribute(objectNameAttribute);
			}
			i++;
		}
		return collect;
	}

	private String prettyPrintAttribute(ObjectNameAttribute objectNameAttribute) {

		ObjectName objectName;
		try {
			objectName = new ObjectName(objectNameAttribute.getObjectName());
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException("cannot find object name [" + objectNameAttribute.getObjectName() + "]", e);
		}

		Object obj = null;
		String attribute = objectNameAttribute.getAttribute();
		try {
			obj = mbsc.getAttribute(objectName, attribute);
		} catch (Exception e) {
			throw new RuntimeException("cannot find attribute [" + attribute + "]", e);
		}

		if (obj instanceof CompositeDataSupport) {
			CompositeDataSupport data = (CompositeDataSupport) obj;
			Set<String> keys = data.getCompositeType().keySet();

			String value = "";
			int i = 0;
			for (String key : keys) {
				if (init) {
					header.add(attribute + "@" + key);
				}
				if (i == 0) {
					value = data.get(key).toString();
				} else {
					value = value + "," + data.get(key).toString();
				}
				i++;
			}
			return value;
		} else {
			if (init) {
				header.add(attribute);
			}
			return obj.toString();
		}
	}
}
