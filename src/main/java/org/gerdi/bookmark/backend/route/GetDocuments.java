package org.gerdi.bookmark.backend.route;

import static com.mongodb.client.model.Filters.and;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;
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

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
		String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
		String collectionId = request.params(BookmarkPersistanceConstants.PARAM_COLLECTION_NAME);

		BasicDBObject queryId = new BasicDBObject("_id", new ObjectId(collectionId));
		BasicDBObject queryUser = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);
		FindIterable<Document> result = collection.find(and(queryId, queryUser));

		if (result.first() == null)
			return "[]";

		List<Map<String, Object>> docs = new ArrayList<Map<String, Object>>();
		for (String doc : ((List<String>) result.first().get(BookmarkPersistanceConstants.DB_DOCS_FIELD_NAME))) {
			docs.add(DocumentUtils.retrieveDoc(doc));
		}
		return new Gson().toJson(docs).toString();
	}

}
