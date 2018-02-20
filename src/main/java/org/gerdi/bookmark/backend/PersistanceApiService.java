package org.gerdi.bookmark.backend;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import org.bson.Document;
import org.gerdi.bookmark.backend.route.DeleteCollection;
import org.gerdi.bookmark.backend.route.GetCollections;
import org.gerdi.bookmark.backend.route.GetDocuments;
import org.gerdi.bookmark.backend.route.PostCollection;
import org.gerdi.bookmark.backend.route.PutCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * This class initializes the involved middleware and all REST paths.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public class PersistanceApiService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersistanceApiService.class);

	public static void main(String[] args) {

		// Init MongoDB Connection
		MongoClient mongoClient;
		try {
			if (BookmarkPersistanceConstants.MONGO_DB_PASSWORD == "") {
				mongoClient = new MongoClient(BookmarkPersistanceConstants.MONGO_DB_HOSTNAME,
						BookmarkPersistanceConstants.MONGO_DB_PORT);
			} else {
				mongoClient = new MongoClient(
						new ServerAddress(BookmarkPersistanceConstants.MONGO_DB_HOSTNAME,
								BookmarkPersistanceConstants.MONGO_DB_PORT),
						BookmarkPersistanceConstants.MONGO_DB_CREDENTIAL, MongoClientOptions.builder().build());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to connect to MongoDB at " + BookmarkPersistanceConstants.MONGO_DB_HOSTNAME + ":"
					+ BookmarkPersistanceConstants.MONGO_DB_PORT, e);
			return;
		}
		MongoDatabase db = mongoClient.getDatabase(BookmarkPersistanceConstants.MONGO_DB_DB_NAME);
		MongoCollection<Document> collection = db.getCollection(BookmarkPersistanceConstants.MONGO_DB_COLLECTION_NAME);

		try {
			// Try to connect to MongoDB
			db.listCollectionNames();
		} catch (MongoException e) {
			LOGGER.error("Failed to connect to MongoDB at " + BookmarkPersistanceConstants.MONGO_DB_HOSTNAME + ":"
					+ BookmarkPersistanceConstants.MONGO_DB_PORT, e);
		}

		// Init SparkJava
		port(4567);

		// GET a list of collections of a specific user
		get("/collections/:userId", new GetCollections(collection));

		// GET all documents within a collection
		get("/collections/:userId/:collectionId", new GetDocuments(collection));

		// Create a new collection
		post("/collections/:userId", new PostCollection(collection));

		// Update a collection
		put("/collections/:userId/:collectionId", new PutCollection(collection));

		// DELETE a collection
		delete("/collections/:userId/:collectionId", new DeleteCollection(collection));

	}

}
