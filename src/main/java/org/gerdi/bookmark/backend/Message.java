package org.gerdi.bookmark.backend;

import java.util.List;

/**
 * Model class for messages returned to the caller.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public class Message {

	private String message;
	private List<String> docs;
	private String collectionId;
	private boolean acknowledged;

	Message(String message) {
		this.message = message;
	}

	public Message(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	Message(String message, List<String> docs) {
		this(message);
		this.docs = docs;
	}

	Message(String message, String collectionId) {
		this(message);
		this.collectionId = collectionId;
	}

	Message(String message, boolean acknowledged) {
		this(message);
		this.setAcknowledged(acknowledged);
	}

	public Message(String message, List<String> docs, boolean acknowledged) {
		this(message, docs);
		this.setAcknowledged(acknowledged);
	}

	public Message(String message, String collectionId, boolean acknowledged) {
		this(message, collectionId);
		this.setAcknowledged(acknowledged);
	}

	public List<String> getDocs() {
		return docs;
	}

	public void setDocs(List<String> docs) {
		this.docs = docs;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

}
