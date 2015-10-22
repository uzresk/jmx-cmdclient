package jp.gr.java_conf.uzresk.jmx.client;

public class ObjectNameAttribute {

	private String objectName;

	private String attribute;

	public ObjectNameAttribute(String objectName, String attribute) {
		this.objectName = objectName;
		this.attribute = attribute;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public String getAttribute() {
		return this.attribute;
	}

	@Override
	public String toString() {
		return "ObjectNameAttribute [objectName=" + objectName + ", attribute=" + attribute + "]";
	}

}
