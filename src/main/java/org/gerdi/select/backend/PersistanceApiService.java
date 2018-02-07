package org.gerdi.select.backend;

import static com.mongodb.client.model.Filters.and;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class PersistanceApiService {

	// MongoDB Constants
	private static final int MONGO_DB_PORT = Integer
			.parseInt(System.getenv().getOrDefault("SELECT_MONGODB_PORT", "27017"));
	private static final String MONGO_DB_COLLECTION_NAME = System.getenv()
			.getOrDefault("SELECT_MONGODB_COLLECTION_NAME", "collections");
	private static final String MONGO_DB_DB_NAME = System.getenv().getOrDefault("SELECT_MONGODB_DB_NAME", "select");
	private static final String MONGO_DB_HOSTNAME = System.getenv().getOrDefault("SELECT_MONGODB_DB_HOSTNAME", "localhost");
	private static final String MONGO_DB_ADMIN_DB_NAME = System.getenv().getOrDefault("SELECT_MONGODB_ADMIN_DB_NAME", "admin");
	private static final String MONGO_DB_USER = System.getenv().getOrDefault("SELECT_MONGODB_ADMIN_DB_NAME", "admin");
	private static final String MONGO_DB_PASSWORD = System.getenv().getOrDefault("SELECT_MONGODB_ADMIN_DB_NAME", "");
	private static final MongoCredential MONGO_DB_CREDENTIAL = MongoCredential.createCredential(MONGO_DB_USER, MONGO_DB_ADMIN_DB_NAME, MONGO_DB_PASSWORD.toCharArray());

	
	// Elasticsearch Constants
	private static final String GERDI_ES_HOSTNAME = System.getenv().getOrDefault("GERDI_ES_HOSTNAME", "localhost");
	private static final String GERDI_ES_INDEXNAME = System.getenv().getOrDefault("GERDI_ES_INDEXNAME", "gerdi");
	private static final String GERDI_ES_TYPENAME = System.getenv().getOrDefault("GERDI_ES_TYPENAME", "metadata");
//	private static final int GERDI_ES_PORT = Integer
//			.parseInt(System.getenv().getOrDefault("GERDI_ES_PORT", "9200"));
	
	private static final RestHighLevelClient ES_CLIENT = new RestHighLevelClient(
			RestClient.builder(new HttpHost(GERDI_ES_HOSTNAME, 9200, "http")));

	private static final Logger LOGGER = LoggerFactory.getLogger(PersistanceApiService.class);

	public static void main(String[] args) {

		// Init MongoDB Connection
		MongoClient mongoClient;
		try {
			if (MONGO_DB_PASSWORD == "") {
				mongoClient = new MongoClient(MONGO_DB_HOSTNAME, MONGO_DB_PORT);
			} else {
				mongoClient = new MongoClient(new ServerAddress(MONGO_DB_HOSTNAME, MONGO_DB_PORT), MONGO_DB_CREDENTIAL, MongoClientOptions.builder().build());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to connect to MongoDB at " + MONGO_DB_HOSTNAME + ":" + MONGO_DB_PORT, e);
			return;
		}
		MongoDatabase db = mongoClient.getDatabase(MONGO_DB_DB_NAME);
		MongoCollection<Document> collection = db.getCollection(MONGO_DB_COLLECTION_NAME);

		try {
			// Try to connect to MongoDB
			db.listCollectionNames();
		} catch (MongoException e) {
			LOGGER.error("Failed to connect to MongoDB at " + MONGO_DB_HOSTNAME + ":" + MONGO_DB_PORT, e);
		}

		// Init SparkJava
		port(4567);

		// GET a list of collections of a specific user
		get("/collections/:userId", (request, response) -> {
			response.type("application/json");
			String userId = request.params("userId");

			BasicDBObject query = new BasicDBObject("userId", userId);

			FindIterable<Document> myCursor = collection.find(query);
			List<Map<String, String>> collectionsList = new ArrayList<Map<String, String>>();
			for (Document doc : myCursor) {
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("_id", doc.getObjectId("_id").toString());
				temp.put("name", doc.getString("collectionName"));
				collectionsList.add(temp);
			}
			return new Gson().toJson(collectionsList).toString();
		});

		// GET all documents within a collection
		get("/collections/:userId/:collectionId", (request, response) -> {
			response.type("application/json");
			String userId = request.params("userId");
			String collectionId = request.params("collectionId");

			BasicDBObject queryId = new BasicDBObject("_id", new ObjectId(collectionId));
			BasicDBObject queryUser = new BasicDBObject("userId", userId);
			FindIterable<Document> result = collection.find(and(queryId, queryUser));

			if (result.first() == null)
				return "[]";

			List<Map<String, Object>> docs = new ArrayList<Map<String, Object>>();
			for (String doc : ((List<String>) result.first().get("docs"))) {
				docs.add(retrieveDoc(doc)); // TODO: check for empty docs!
			}
			return new Gson().toJson(docs).toString();
		});

		// Create a new collection
		post("/collections/:userId", (request, response) -> {
			response.type("application/json");
			if (request.contentType() != "application/json")
				halt(405);
			String userId = request.params("userId");
			JsonElement jelement = new JsonParser().parse(request.body());

			String collectionName = "";

			if (jelement.getAsJsonObject().has("name")) {
				collectionName = jelement.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
			}

			if (collectionName == "")
				collectionName = "Collection " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> docsList = new Gson().fromJson(jelement.getAsJsonObject().getAsJsonArray("docs"), listType);

			List<String> failedDocs = new ArrayList<String>();

			// Check whether the doc exists in our system
			for (String doc : docsList) {
				if (checkIfDocExists(doc) == false) {
					failedDocs.add(doc);
				}
			}
			if (!failedDocs.isEmpty()) {
				response.status(400);
				Message returnMsg = new Message("At least one document is unknown. Request was aborted.", failedDocs,
						false);
				return new Gson().toJson(returnMsg).toString();
			}

			Document document = new Document("userId", userId).append("docs", docsList).append("collectionName",
					collectionName);

			collection.insertOne(document);

			response.status(201);
			return new Gson().toJson(new Message("Collection created.", document.get("_id").toString(), true))
					.toString();
		});

		// Update a collection
		put("/collections/:userId/:collectionId", (request, response) -> {
			response.type("application/json");
			if (request.contentType() != "application/json")
				halt(405);


			String userId = request.params("userId");
			String collectionId = request.params("collectionId");
			
			JsonElement jelement = new JsonParser().parse(request.body());

			String collectionName = "";

			if (jelement.getAsJsonObject().has("name")) {
				collectionName = jelement.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
			}

			if (collectionName == "")
				collectionName = "Collection " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> docsList = new Gson().fromJson(jelement.getAsJsonObject().getAsJsonArray("docs"), listType);

			List<String> failedDocs = new ArrayList<String>();

			// Check whether the doc exists in our system
			for (String doc : docsList) {
				if (checkIfDocExists(doc) == false) {
					failedDocs.add(doc);
				}
			}
			if (!failedDocs.isEmpty()) {
				response.status(400);
				Message returnMsg = new Message("At least one document is unknown. Request was aborted.", failedDocs,
						false);
				return new Gson().toJson(returnMsg).toString();
			}

			Document document = new Document("userId", userId).append("docs", docsList).append("collectionName",
					collectionName);

			BasicDBObject queryId = new BasicDBObject("_id", new ObjectId(collectionId));
			BasicDBObject queryUser = new BasicDBObject("userId", userId);

			if (collection.find(and(queryId, queryUser)).first() != null) {
				collection.updateOne(and(queryId, queryUser), new Document("$set", document));
			} else {
				collection.insertOne(document.append("_id", new ObjectId(collectionId)));
			}

			response.status(201);
			return new Gson().toJson(new Message("Collection updated.", collectionId, true))
					.toString();
		});
		
		// DELETE a collection
		delete("/collections/:userId/:collectionId", (request, response) -> {
			response.type("application/json");
			
			String userId = request.params("userId");
			String collectionId = request.params("collectionId");
			
			BasicDBObject query1 = new BasicDBObject("_id", new ObjectId(collectionId));
			BasicDBObject query2 = new BasicDBObject("userId", userId);
			DeleteResult result = collection.deleteOne(and(query1, query2));
			
			response.status(202);
			return new Gson().toJson(new Message(result.wasAcknowledged()))
					.toString();
		});

	}

	/**
	 * This method check whether or not a document exists in our system. The current
	 * implementation checks if Elasticsearch holds such document, but this needs to
	 * be changed once this service has an own persistance layer.
	 * 
	 * @param docId
	 *            The ID of the document to be checked
	 * @return true if the document exists, false otherwise
	 * @throws IOException
	 */
	private static boolean checkIfDocExists(String docId) throws IOException {
		GetRequest getRequest = new GetRequest(GERDI_ES_INDEXNAME, GERDI_ES_TYPENAME, docId);
		return ES_CLIENT.get(getRequest).isExists();
	}

	
	/**
	 * This method retrieves a document and returns it in a specific format.
	 * In the first version, we use the persistance capabilities of Elasticsearch. This method needs to be changed once a dedicated persistance layer exists.
	 * 
	 * @param docId The ID of the document to be retrieved
	 * @return A Map containing the document id and the source data. The value of the source data may be null if no such document can be found.
	 * @throws IOException
	 */
	private static Map<String, Object> retrieveDoc(String docId) throws IOException {
		GetRequest getRequest = new GetRequest(GERDI_ES_INDEXNAME, GERDI_ES_TYPENAME, docId);
		GetResponse getResponse = ES_CLIENT.get(getRequest);
		Map<String, Object> retVal = new HashMap<String, Object>();
		retVal.put("_source", getResponse.getSource());
		retVal.put("_id", docId);
		return retVal;
	}

}
