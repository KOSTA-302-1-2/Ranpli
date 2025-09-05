package user.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
	private String sessionId;
	
	private Map<String, Object> attributes;
	
	public Session() {};
	
	public Session(String sessionId) {
		this.sessionId = sessionId;
		attributes = new HashMap<>();
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setAttribute(String name, Object value) {
		attributes.put(name,value);
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("sessionId=");
		builder.append(sessionId);
		builder.append(", attributes= \n");
		builder.append(attributes);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return sessionId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Session other = (Session) obj;
		return sessionId.equals(other.sessionId);
	}
}
