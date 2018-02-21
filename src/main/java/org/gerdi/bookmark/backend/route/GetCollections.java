package org.gerdi.bookmark.backend.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;

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

	public GetCollections(final MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(final Request request, final Response response) {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
		final String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);

		final BasicDBObject query = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);

		final FindIterable<Document> myCursor = collection.find(query);
		final List<Map<String, String>> collectionsList = new ArrayList<>();
		for (final Document doc : myCursor) {
			final HashMap<String, String> temp = new HashMap<>();
			temp.put("_id", doc.getObjectId("_id").toString());
			temp.put("name", doc.getString(BookmarkPersistanceConstants.DB_COLLECTION_FIELD_NAME));
			collectionsList.add(temp);
		}
		return new Gson().toJson(collectionsList).toString();
	}

}
