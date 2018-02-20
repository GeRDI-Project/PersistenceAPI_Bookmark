package org.gerdi.bookmark.backend.route;

import static com.mongodb.client.model.Filters.and;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.Message;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

import spark.Request;
import spark.Response;

public final class DeleteCollection extends AbstractBookmarkRoute {

	public DeleteCollection(MongoCollection<Document> collection) {
		super(collection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.type("application/json");
		
		String userId = request.params("userId");
		String collectionId = request.params("collectionId");
		
		BasicDBObject query1 = new BasicDBObject("_id", new ObjectId(collectionId));
		BasicDBObject query2 = new BasicDBObject("userId", userId);
		DeleteResult result = collection.deleteOne(and(query1, query2));
		
		response.status(202);
		return new Gson().toJson(new Message(result.wasAcknowledged()))
				.toString();
	}

}
