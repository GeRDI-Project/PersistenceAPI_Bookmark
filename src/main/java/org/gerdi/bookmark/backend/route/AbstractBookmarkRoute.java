package org.gerdi.bookmark.backend.route;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import spark.Route;

/**
 * This abstract class introduces a minimal framework for the needed routes.
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public abstract class AbstractBookmarkRoute implements Route {

	protected final MongoCollection<Document> collection;

	public AbstractBookmarkRoute(MongoCollection<Document> collection) {
		this.collection = collection;
	}

}
