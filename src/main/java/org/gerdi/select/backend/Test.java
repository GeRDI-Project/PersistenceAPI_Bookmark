package org.gerdi.select.backend;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Test {

	private static final int MONGO_DB_PORT = Integer
			.parseInt(System.getenv().getOrDefault("SELECT_MONGODB_PORT", "27017"));
	private static final String MONGO_DB_COLLECTION_NAME = System.getenv()
			.getOrDefault("SELECT_MONGODB_COLLECTION_NAME", "testCollection");
	private static final String MONGO_DB_DB_NAME = System.getenv().getOrDefault("SELECT_MONGODB_DB_NAME", "sparkTest");
	private static final String MONGO_DB_HOSTNAME = System.getenv().getOrDefault("SELECT_MONGODB_DB_NAME", "10.1.45.1");

	private static final String GERDI_ES_HOSTNAME = System.getenv().getOrDefault("GERDI_ES_HOSTNAME",
			"localhost");
	private static final String GERDI_ES_INDEXNAME = System.getenv().getOrDefault("GERDI_ES_INDEXNAME", "gerdi");
	private static final String GERDI_ES_TYPENAME = System.getenv().getOrDefault("GERDI_ES_TYPENAME", "metadata");
	private static final RestHighLevelClient ES_CLIENT = new RestHighLevelClient(
			RestClient.builder(new HttpHost(GERDI_ES_HOSTNAME, 9200, "http")));

	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {

		// Init MongoDB Connection
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient(MONGO_DB_HOSTNAME, MONGO_DB_PORT);
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

		get("/collections/:userId", (req, res) -> {
			res.type("application/json");
			String userId = req.params("userId");
			
			BasicDBObject query = new BasicDBObject("userId", userId);
			
			FindIterable<Document> myCursor = collection.find(query);
			List<Map<String,String>> collectionsList = new ArrayList<Map<String,String>>();
			for (Document doc : myCursor) {
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("_id", doc.getObjectId("_id").toString());
				temp.put("name", doc.getString("collectionName"));
				collectionsList.add(temp);
			}
			String string = new Gson().toJson(collectionsList).toString();
			return string;
		});
		
		get("/docs/:collectionId", (req, res) -> {
			res.type("application/json");
			String collectionId = req.params("collectionId");
			
			BasicDBObject query = new BasicDBObject("_id", new ObjectId(collectionId));
			FindIterable<Document> result = collection.find(query);
			
			if (result == null) return "[]";

			List<Map<String, Object>> docs = new ArrayList<Map<String, Object>>();
			for (String doc : ((List<String>) result.first().get("docs"))) {
				docs.add(retrieveDocFromElasticsearch(doc)); // TODO: check for empty docs!
			}
			return new Gson().toJson(docs).toString();
		});
		
		post("/docs", (request, response) -> {
			if (request.contentType() != "application/json") halt(405);
			JsonElement jelement = new JsonParser().parse(request.body());

			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> docsList = new Gson().fromJson(jelement.getAsJsonObject().getAsJsonArray("docs"), listType);
			
			Document document = new Document("userId", jelement.getAsJsonObject().getAsJsonPrimitive("userId").getAsString())
		               .append("docs", docsList);
			
			collection.insertOne(document);
			
//			System.out.println(jelement.getAsJsonObject().getAsJsonArray("docs").get(0));
			return "";
		});

	}

	private static Map<String, Object> retrieveDocFromElasticsearch(String docId) throws IOException {
		GetRequest getRequest = new GetRequest(GERDI_ES_INDEXNAME, GERDI_ES_TYPENAME,
				docId);
		GetResponse getResponse = ES_CLIENT.get(getRequest);
		Map<String, Object> retVal = new HashMap<String, Object>();
		retVal.put("_source", getResponse.getSource());
		retVal.put("_id", docId);
		return retVal;
	}

}
