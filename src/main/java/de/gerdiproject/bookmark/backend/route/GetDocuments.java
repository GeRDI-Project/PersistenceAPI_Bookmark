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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import de.gerdiproject.bookmark.backend.BookmarkDocument;
import de.gerdiproject.bookmark.backend.BookmarkPersistenceConstants;
import de.gerdiproject.bookmark.backend.DocumentUtility;
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

    /**
     * Initializes the get documents route.
     *
     * @param collection
     *            A MongoDB collection on which the operations are performed.
     */
    public GetDocuments(final MongoCollection<Document> collection)
    {
        super(collection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(final Request request, final Response response)
    throws IOException
    {
        response.type(BookmarkPersistenceConstants.APPLICATION_JSON);
        final String userId = request
                              .params(BookmarkPersistenceConstants.PARAM_USER_ID_NAME);
        final String collectionId = request
                                    .params(BookmarkPersistenceConstants.PARAM_COLLECTION_NAME);

        final BasicDBObject queryId = new BasicDBObject(
            BookmarkPersistenceConstants.DB_UID_FIELD_NAME,
            new ObjectId(collectionId));
        final BasicDBObject queryUser = new BasicDBObject(
            BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, userId);
        final FindIterable<Document> result = collection
                                              .find( and (queryId, queryUser));

        if (result.first() == null)
            return "[]";

        final List<BookmarkDocument> docs = new ArrayList<>();
        for (final String doc : ((List<String>) result.first()
                                 .get(BookmarkPersistenceConstants.DB_DOCS_FIELD_NAME)))
            docs.add(DocumentUtility.retrieveDoc(doc));
        return GSON.toJson(docs).toString();
    }

}
