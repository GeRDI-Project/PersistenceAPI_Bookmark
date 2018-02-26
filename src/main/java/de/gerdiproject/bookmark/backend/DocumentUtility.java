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

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;

/**
 * A utility class for document retrieval.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class DocumentUtility
{

	private DocumentUtility()
	{
	}

	/**
	 * This method checks whether or not a document exists in our system.
	 *
	 * @param docId
	 *            The ID of the document to be checked
	 * @return true if the document exists, false otherwise
	 * @throws IOException
	 *             Thrown by the Elasticsearch client upon failed connection.
	 */
	public static boolean doesDocumentExist(final String docId)
			throws IOException
	{
		final GetRequest getRequest = new GetRequest(
				BookmarkPersistenceConstants.GERDI_ES_INDEXNAME,
				BookmarkPersistenceConstants.GERDI_ES_TYPENAME, docId);
		return BookmarkPersistenceConstants.ES_CLIENT.get(getRequest)
				.isExists();
	}

	/**
	 * This method retrieves a document and returns it.
	 *
	 * @param docId
	 *            The ID of the document to be retrieved
	 * @return A Map containing the document id and the source data. The value
	 *         of the source data may be null if no such document can be found.
	 * @throws IOException
	 *             Thrown by the Elasticsearch client upon failed connection.
	 */
	public static BookmarkDocument retrieveDoc(final String docId)
			throws IOException
	{
		final GetRequest getRequest = new GetRequest(
				BookmarkPersistenceConstants.GERDI_ES_INDEXNAME,
				BookmarkPersistenceConstants.GERDI_ES_TYPENAME, docId);
		final GetResponse getResponse = BookmarkPersistenceConstants.ES_CLIENT
				.get(getRequest);
		return new BookmarkDocument(docId, getResponse.getSource());
	}

	/**
	 * Creates a MongoDB document representing a collection.
	 * 
	 * @param userId
	 *            The user's identifier
	 * @param collectionName
	 *            The name of the collection
	 * @param docsList
	 *            A list of documents to be saved
	 * @return A new MongoDB document representing the given attributes
	 */
	public static Document createBookmarkDoc(final String userId,
			final String collectionName, final List<String> docsList)
	{
		return new Document(
				BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, userId)
						.append(BookmarkPersistenceConstants.DB_DOCS_FIELD_NAME,
								docsList)
						.append(BookmarkPersistenceConstants.DB_COLLECTION_FIELD_NAME,
								collectionName);
	}
}
