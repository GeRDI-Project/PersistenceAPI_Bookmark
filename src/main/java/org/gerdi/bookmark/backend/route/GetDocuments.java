package org.gerdi.bookmark.backend.route;

import static com.mongodb.client.model.Filters.and;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.DocumentUtils;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for document retrieval.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public final class GetDocuments extends AbstractBookmarkRoute {

	public GetDocuments(MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		// TODO Auto-generated method stub
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
			docs.add(DocumentUtils.retrieveDoc(doc));
		}
		return new Gson().toJson(docs).toString();
	}

}
