package org.gerdi.bookmark.backend.route;

import static spark.Spark.halt;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.gerdi.bookmark.backend.DocumentUtils;
import org.gerdi.bookmark.backend.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;

import spark.Request;
import spark.Response;

public class PostCollection extends AbstractBookmarkRoute {

	public PostCollection(MongoCollection<Document> collection) {
		super(collection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
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

		Document document = new Document("userId", userId).append("docs", docsList).append("collectionName",
				collectionName);

		collection.insertOne(document);

		response.status(201);
		return new Gson().toJson(new Message("Collection created.", document.get("_id").toString(), true))
				.toString();
	}

}
