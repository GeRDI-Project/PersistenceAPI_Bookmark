package org.gerdi.bookmark.backend.route;

import static spark.Spark.halt;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bson.Document;
import org.gerdi.bookmark.backend.BookmarkPersistanceConstants;
import org.gerdi.bookmark.backend.DocumentUtility;
import org.gerdi.bookmark.backend.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

/**
 * This class implements the handler for collection creation.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class PostCollection extends AbstractBookmarkRoute {

	public PostCollection(final MongoCollection<Document> collection) {
		super(collection);
	}

	@Override
	public Object handle(final Request request, final Response response) throws IOException {
		response.type(BookmarkPersistanceConstants.APPLICATION_JSON);
		if (request.contentType() != BookmarkPersistanceConstants.APPLICATION_JSON) {
			halt(405);
		}
		final String userId = request.params(BookmarkPersistanceConstants.PARAM_USER_ID_NAME);
		final JsonElement requestBody = new JsonParser().parse(request.body());

		String collectionName = "";

		if (requestBody.getAsJsonObject().has("name")) {
			collectionName = requestBody.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
		}

		if (collectionName.isEmpty()) {
			collectionName = "Collection "
					+ new SimpleDateFormat(BookmarkPersistanceConstants.DATE_STRING, Locale.GERMANY).format(new Date());
		}

		final Type listType = new TypeToken<List<String>>() {
		}.getType();
		final List<String> docsList = new Gson().fromJson(requestBody.getAsJsonObject().getAsJsonArray("docs"),
				listType);

		final List<String> failedDocs = new ArrayList<>();

		// Check whether the doc exists in our system
		for (final String doc : docsList) {
			if (!DocumentUtility.checkIfDocExists(doc)) {
				failedDocs.add(doc);
			}
		}
		if (!failedDocs.isEmpty()) {
			response.status(400);
			final Message returnMsg = new Message("At least one document is unknown. Request was aborted.", failedDocs,
					false);
			return new Gson().toJson(returnMsg).toString();
		}

		final Document document = new Document(BookmarkPersistanceConstants.DB_USER_ID_FIELD_NAME, userId)
				.append(BookmarkPersistanceConstants.DB_DOCS_FIELD_NAME, docsList)
				.append(BookmarkPersistanceConstants.DB_COLLECTION_FIELD_NAME, collectionName);

		collection.insertOne(document);

		response.status(201);
		return new Gson().toJson(new Message("Collection created.", document.get("_id").toString(), true)).toString();
	}

}
