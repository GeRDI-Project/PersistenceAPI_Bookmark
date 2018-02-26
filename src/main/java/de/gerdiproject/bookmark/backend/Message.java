/**
 * Copyright 2018 Nelson Tavares de Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gerdiproject.bookmark.backend;

import java.util.List;

/**
 * Model class for messages returned to the caller.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class Message
{

	private final String message; // NOPMD ignore the same name for the class
									// and the
									// field here
	private final List<String> docs;
	private final String collectionId;
	private final boolean acknowledged;

	/**
	 * Initializes the class with only a acknowledged info.
	 *
	 * @param acknowledged
	 *            A boolean value indicating the acknowledgment.
	 */
	public Message(final boolean acknowledged)
	{
		this.message = null;
		this.collectionId = null;
		this.acknowledged = acknowledged;
		this.docs = null;
	}

	/**
	 * Initializes the class with a message string, a list of relevant
	 * documents, and an acknowledgement flag.
	 *
	 * @param message
	 *            A string containing a message.
	 * @param docs
	 *            A list of relevant documents.
	 * @param acknowledged
	 *            A boolean value indicating the acknowledgment.
	 */
	public Message(final String message, final List<String> docs,
			final boolean acknowledged)
	{
		this.message = message;
		this.collectionId = null;
		this.acknowledged = acknowledged;
		this.docs = docs;
	}

	/**
	 * Initializes the class with a message string, a collection name, and an
	 * acknowledgement flag.
	 *
	 * @param message
	 *            A string containing a message.
	 * @param collectionId
	 *            A string containing the name of the collection.
	 * @param acknowledged
	 *            A boolean value indicating the acknowledgment.
	 */
	public Message(final String message, final String collectionId,
			final boolean acknowledged)
	{
		this.message = message;
		this.collectionId = collectionId;
		this.acknowledged = acknowledged;
		this.docs = null;
	}

	public List<String> getDocs()
	{
		return docs;
	}

	public String getMessage()
	{
		return message;
	}

	public String getCollectionId()
	{
		return collectionId;
	}

	public boolean isAcknowledged()
	{
		return acknowledged;
	}

}
