package org.gerdi.bookmark.backend.route;

import static com.mongodb.client.model.Filters.and;
import static spark.Spark.halt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;
import org.gerdi.bookmark.backend.DocumentUtils;
import org.gerdi.bookmark.backend.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection updates.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public final class PutCollection extends AbstractBookmarkRoute {

	public PutCollection(MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
		if (request.contentType() != BookmarkPersistanceConstants.APPLICATION_JSON)
			halt(405);

		String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
		String collectionId = request.params(BookmarkPersistanceConstants.PARAM_COLLECTION_NAME);

		JsonElement requestBody = new JsonParser().parse(request.body());

		String collectionName = "";

		if (requestBody.getAsJsonObject().has("name")) {
			collectionName = requestBody.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
		}

		// TODO: check if collection exists... don't overwrite name, if empty string
		if (collectionName.isEmpty())
			collectionName = "Collection " + BookmarkPersistanceConstants.DATE_STRING.format(new Date());

		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> docsList = new Gson().fromJson(requestBody.getAsJsonObject().getAsJsonArray("docs"), listType);

		List<String> failedDocs = new ArrayList<String>();

		// Check whether the doc exists in our system
		for (String doc : docsList) {
			if (DocumentUtils.checkIfDocExists(doc) == false) {
				failedDocs.add(doc);
			}
		}
		if (!failedDocs.isEmpty()) {
			response.status(400);
			Message returnMsg = new Message("At least one document is unknown. Request was aborted.", failedDocs,
					false);
			return new Gson().toJson(returnMsg).toString();
		}

		Document document = new Document(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId)
				.append(BookmarkPersistanceConstants.DB_DOCS_FIELD_NAME, docsList)
				.append(BookmarkPersistanceConstants.DB_COLLECTION_FIELD_NAME, collectionName);

		BasicDBObject queryId = new BasicDBObject("_id", new ObjectId(collectionId));
		BasicDBObject queryUser = new BasicDBObject(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId);

		if (collection.find(and(queryId, queryUser)).first() != null) {
			collection.updateOne(and(queryId, queryUser), new Document("$set", document));
		} else {
			collection.insertOne(document.append("_id", new ObjectId(collectionId)));
		}

		response.status(201);
		return new Gson().toJson(new Message("Collection updated.", collectionId, true)).toString();
	}

}
