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

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// Init MongoDB Connection
		final MongoClient mongoClient;
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
			throw e;
		}
		final MongoDatabase db = mongoClient.getDatabase(BookmarkPersistanceConstants.MONGO_DB_DB_NAME);
		final MongoCollection<Document> collection = db.getCollection(BookmarkPersistanceConstants.MONGO_DB_COLLECTION_NAME);

		try {
			// Try to connect to MongoDB
			db.listCollectionNames();
		} catch (MongoException e) {
			LOGGER.error("Failed to connect to MongoDB at " + BookmarkPersistanceConstants.MONGO_DB_HOSTNAME + ":"
					+ BookmarkPersistanceConstants.MONGO_DB_PORT, e);
			throw e;
		}

		// Init SparkJava
		port(4567);

		// Just to make the code below shorter
		final String paramCollectionName = BookmarkPersistanceConstants.PARAM_COLLECTION_NAME;
		final String paramUserId = BookmarkPersistanceConstants.PARAM_USER_ID_NAME;
		final String pathPrefix = BookmarkPersistanceConstants.PATH_PREFIX;

		// GET a list of collections of a specific user
		get(pathPrefix + "/:" + paramUserId, new GetCollections(collection));

		// GET all documents within a collection
		get(pathPrefix + "/:" + paramUserId + "/:" + paramCollectionName, new GetDocuments(collection));

		// Create a new collection
		post(pathPrefix + "/:" + paramUserId, new PostCollection(collection));

		// Update a collection
		put(pathPrefix + "/:" + paramUserId + "/:" + paramCollectionName, new PutCollection(collection));

		// DELETE a collection
		delete(pathPrefix + "/:" + paramUserId + "/:" + paramCollectionName, new DeleteCollection(collection));

	}

}
