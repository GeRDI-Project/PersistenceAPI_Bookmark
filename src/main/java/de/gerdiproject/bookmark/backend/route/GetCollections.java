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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import de.gerdiproject.bookmark.backend.BookmarkCollection;
import de.gerdiproject.bookmark.backend.BookmarkPersistenceConstants;
import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection retrieval.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class GetCollections extends AbstractBookmarkRoute
{

    /**
     * Initializes the get collections route.
     *
     * @param collection
     *            A MongoDB collection on which the operations are performed.
     */
    public GetCollections(final MongoCollection<Document> collection)
    {
        super(collection);
    }

    @Override
    public Object handle(final Request request, final Response response)
    {
        response.type(BookmarkPersistenceConstants.APPLICATION_JSON);
        final String userId = request
                              .params(BookmarkPersistenceConstants.PARAM_USER_ID_NAME);

        final BasicDBObject query = new BasicDBObject(
            BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, userId);

        final FindIterable<Document> myCursor = collection.find(query);
        final List<BookmarkCollection> collectionsList = new ArrayList<>();
        myCursor.forEach(
            (Consumer<Document>)(final Document doc) -> collectionsList
            .add(new BookmarkCollection(doc)));
        return GSON.toJson(collectionsList).toString();
    }

}
