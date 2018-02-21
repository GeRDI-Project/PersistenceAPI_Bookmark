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

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;
import org.gerdi.bookmark.backend.Message;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

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

    public DeleteCollection(final MongoCollection<Document> collection)
    {
        super(collection);
    }

    @Override
    public Object handle(final Request request, final Response response)
    {
        response.type(BookmarkPersistanceConstants.APPLICATION_JSON);

        final String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
        final String collectionId = request.params(BookmarkPersistanceConstants.PARAM_COLLECTION_NAME);

        final BasicDBObject query1 = new BasicDBObject("_id", new ObjectId(collectionId));
        final BasicDBObject query2 = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);
        final DeleteResult result = collection.deleteOne( and (query1, query2));

        response.status(202);
        return new Gson().toJson(new Message(result.wasAcknowledged())).toString();
    }

}
