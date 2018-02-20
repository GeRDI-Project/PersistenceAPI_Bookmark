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
public final class DeleteCollection extends AbstractBookmarkRoute {

	public DeleteCollection(MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);

		String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
		String collectionId = request.params(BookmarkPersistanceConstants.PARAM_COLLECTION_NAME);

		BasicDBObject query1 = new BasicDBObject("_id", new ObjectId(collectionId));
		BasicDBObject query2 = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);
		DeleteResult result = collection.deleteOne(and(query1, query2));

		response.status(202);
		return new Gson().toJson(new Message(result.wasAcknowledged())).toString();
	}

}
