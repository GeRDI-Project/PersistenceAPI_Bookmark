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

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.gerdiproject.bookmark.backend.route.DeleteCollection;
import de.gerdiproject.bookmark.backend.route.GetCollections;
import de.gerdiproject.bookmark.backend.route.GetDocuments;
import de.gerdiproject.bookmark.backend.route.PostCollection;
import de.gerdiproject.bookmark.backend.route.PutCollection;

/**
 * This class initializes the involved middleware and all REST paths.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class PersistenceApiService
{

    private static final Logger LOGGER = LoggerFactory
                                         .getLogger(PersistenceApiService.class);
    private static final String CONNECTION_FAILED = "Failed to connect to MongoDB at %s:%d";

    private PersistenceApiService()
    {
    }

    @SuppressWarnings("resource")
    public static void main(final String[] args)
    {

        // Init MongoDB Connection
        final MongoClient mongoClient;
        if (BookmarkPersistenceConstants.MONGO_DB_PASSWORD.isEmpty()) {
            mongoClient = new MongoClient(
                BookmarkPersistenceConstants.MONGO_DB_HOSTNAME,
                BookmarkPersistenceConstants.MONGO_DB_PORT);
        } else {
            mongoClient = new MongoClient(
                new ServerAddress(
                    BookmarkPersistenceConstants.MONGO_DB_HOSTNAME,
                    BookmarkPersistenceConstants.MONGO_DB_PORT),
                BookmarkPersistenceConstants.MONGO_DB_CREDENTIAL,
                MongoClientOptions.builder().build());
        }
        final MongoDatabase database = mongoClient
                                       .getDatabase(BookmarkPersistenceConstants.MONGO_DB_DB_NAME);
        try {
            // Try to connect to MongoDB
            database.listCollectionNames();
        } catch (final MongoException e) {
            LOGGER.error(String.format(CONNECTION_FAILED,
                                       BookmarkPersistenceConstants.MONGO_DB_HOSTNAME,
                                       BookmarkPersistenceConstants.MONGO_DB_PORT), e);
            throw e;
        }
        final MongoCollection<Document> collection = database.getCollection(
                                                         BookmarkPersistenceConstants.MONGO_DB_COLLECTION_NAME);

        // Init SparkJava
        port(4567);

        // Just to make the code below shorter
        final String paramCollName = BookmarkPersistenceConstants.PARAM_COLLECTION_NAME;
        final String paramUserId = BookmarkPersistenceConstants.PARAM_USER_ID_NAME;
        final String pathPrefix = BookmarkPersistenceConstants.PATH_PREFIX;

        // GET a list of collections of a specific user
        get(pathPrefix + "/:" + paramUserId, new GetCollections(collection));

        // GET all documents within a collection
        get(pathPrefix + "/:" + paramUserId + "/:" + paramCollName,
            new GetDocuments(collection));

        // Create a new collection
        post(pathPrefix + "/:" + paramUserId, new PostCollection(collection));

        // Update a collection
        put(pathPrefix + "/:" + paramUserId + "/:" + paramCollName,
            new PutCollection(collection));

        // DELETE a collection
        delete (pathPrefix + "/:" + paramUserId + "/:" + paramCollName,
                new DeleteCollection(collection));

    }

}
