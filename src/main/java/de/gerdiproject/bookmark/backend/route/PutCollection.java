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
package de.gerdiproject.bookmark.backend.route;

import static com.mongodb.client.model.Filters.and;
import static spark.Spark.halt;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import de.gerdiproject.bookmark.backend.BookmarkPersistenceConstants;
import de.gerdiproject.bookmark.backend.DocumentUtility;
import de.gerdiproject.bookmark.backend.Message;
import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection updates.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class PutCollection extends AbstractBookmarkRoute
{

	private static final String UNKNOWN_DOCS = "At least one document is unknown. Request was aborted.";

	/**
	 * Initializes the put collection route.
	 *
	 * @param collection
	 *            A MongoDB collection on which the operations are performed.
	 */
	public PutCollection(final MongoCollection<Document> collection)
	{
		super(collection);
	}

	@Override
	public Object handle(final Request request, final Response response)
			throws IOException
	{
		response.type(BookmarkPersistenceConstants.APPLICATION_JSON);
		if (request
				.contentType() != BookmarkPersistenceConstants.APPLICATION_JSON)
		{
			halt(405);
		}

		final String userId = request
				.params(BookmarkPersistenceConstants.PARAM_USER_ID_NAME);
		final String collectionId = request
				.params(BookmarkPersistenceConstants.PARAM_COLLECTION_NAME);

		final JsonElement requestBody = new JsonParser().parse(request.body());

		String collectionName = "";

		if (requestBody.getAsJsonObject()
				.has(BookmarkPersistenceConstants.REQUEST_NAME_FIELD_NAME))
		{
			collectionName = requestBody.getAsJsonObject().getAsJsonPrimitive(
					BookmarkPersistenceConstants.REQUEST_NAME_FIELD_NAME)
					.getAsString();
		}

		// TODO: check if collection exists... don't overwrite name, if empty
		// string
		if (collectionName.isEmpty())
		{
			collectionName = "Collection " + new SimpleDateFormat(
					BookmarkPersistenceConstants.DATE_STRING, Locale.GERMANY)
							.format(new Date());
		}

		final Type listType = new TypeToken<List<String>>()
		{
		}.getType();
		final List<String> docsList = GSON.fromJson(
				requestBody.getAsJsonObject().getAsJsonArray(
						BookmarkPersistenceConstants.REQUEST_DOCS_FIELD_NAME),
				listType);

		final List<String> failedDocs = new ArrayList<>();

		// Check whether the doc exists in our system
		for (final String doc : docsList)
		{
			if (!DocumentUtility.doesDocumentExist(doc))
			{
				failedDocs.add(doc);
			}
		}
		if (!failedDocs.isEmpty())
		{
			response.status(400);
			final Message returnMsg = new Message(
					UNKNOWN_DOCS,
					failedDocs, false);
			return GSON.toJson(returnMsg).toString();
		}

		final Document document = DocumentUtility.createBookmarkDoc(userId, collectionName,
				docsList);

		final BasicDBObject queryId = new BasicDBObject(
				BookmarkPersistenceConstants.DB_UID_FIELD_NAME,
				new ObjectId(collectionId));
		final BasicDBObject queryUser = new BasicDBObject(
				BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, userId);

		if (collection.find(and(queryId, queryUser)).first() == null)
		{
			collection.insertOne(document.append(
					BookmarkPersistenceConstants.DB_UID_FIELD_NAME,
					new ObjectId(collectionId)));
		} else
		{
			collection.updateOne(and(queryId, queryUser),
					new Document("$set", document));
		}

		response.status(201);
		return GSON
				.toJson(new Message("Collection updated.", collectionId, true))
				.toString();
	}

}
