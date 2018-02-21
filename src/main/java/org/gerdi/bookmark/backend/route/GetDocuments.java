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

import static com.mongodb.client.model.Filters.and;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;
import org.gerdi.bookmark.backend.DocumentUtility;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for document retrieval.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class GetDocuments extends AbstractBookmarkRoute
{

    public GetDocuments(final MongoCollection<Document> collection)
    {
        super(collection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(final Request request, final Response response) throws IOException
    {
        response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
        final String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
        final String collectionId = request.params(BookmarkPersistanceConstants.PARAM_COLLECTION_NAME);

        final BasicDBObject queryId = new BasicDBObject("_id", new ObjectId(collectionId));
        final BasicDBObject queryUser = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);
        final FindIterable<Document> result = collection.find( and (queryId, queryUser));

        if (result.first() == null)
            return "[]";

        final List<Map<String, Object>> docs = new ArrayList<>();
        for (final String doc : ((List<String>) result.first().get(BookmarkPersistanceConstants.DB_DOCS_FIELD_NAME)))
            docs.add(DocumentUtility.retrieveDoc(doc));
        return new Gson().toJson(docs).toString();
    }

}
