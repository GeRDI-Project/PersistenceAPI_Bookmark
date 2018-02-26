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

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

import de.gerdiproject.bookmark.backend.BookmarkPersistenceConstants;
import de.gerdiproject.bookmark.backend.Message;
import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection deletion.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class DeleteCollection extends AbstractBookmarkRoute
{

    /**
     * Initializes the delete collections route.
     *
     * @param collection
     *            A MongoDB collection on which the operations are performed.
     */
    public DeleteCollection(final MongoCollection<Document> collection)
    {
        super(collection);
    }

    @Override
    public Object handle(final Request request, final Response response)
    {
        response.type(BookmarkPersistenceConstants.APPLICATION_JSON);

        final String userId = request
                              .params(BookmarkPersistenceConstants.PARAM_USER_ID_NAME);
        final String collectionId = request
                                    .params(BookmarkPersistenceConstants.PARAM_COLLECTION_NAME);

        final BasicDBObject query1 = new BasicDBObject(
            BookmarkPersistenceConstants.DB_UID_FIELD_NAME,
            new ObjectId(collectionId));
        final BasicDBObject query2 = new BasicDBObject(
            BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, userId);
        final DeleteResult result = collection.deleteOne( and (query1, query2));

        response.status(202);
        return GSON.toJson(new Message(result.wasAcknowledged())).toString();
    }

}
