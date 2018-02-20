package org.gerdi.bookmark.backend.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection retrieval.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public class GetCollections extends AbstractBookmarkRoute {

	public GetCollections(MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
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
	}

}
