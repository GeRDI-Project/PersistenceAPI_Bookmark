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
package org.gerdi.bookmark.backend.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection retrieval.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class GetCollections extends AbstractBookmarkRoute {

	public GetCollections(final MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(final Request request, final Response response) {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
		final String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);

		final BasicDBObject query = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);

		final FindIterable<Document> myCursor = collection.find(query);
		final List<Map<String, String>> collectionsList = new ArrayList<>();
		for (final Document doc : myCursor) {
			final HashMap<String, String> temp = new HashMap<>();
			temp.put("_id", doc.getObjectId("_id").toString());
			temp.put("name", doc.getString(BookmarkPersistanceConstants.DB_COLLECTION_FIELD_NAME));
			collectionsList.add(temp);
		}
		return new Gson().toJson(collectionsList).toString();
	}

}
