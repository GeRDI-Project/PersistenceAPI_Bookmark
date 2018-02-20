package org.gerdi.bookmark.backend;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.mongodb.MongoCredential;

/**
 * This class contains all the constants required for this software to work
 * 
 * @author Nelson Tavares de Sousa
 *
 */
public final class BookmarkPersistanceConstants {

	// MongoDB Constants
	static final int MONGO_DB_PORT = Integer
			.parseInt(System.getenv().getOrDefault("BOOKMARK_MONGODB_PORT", "27017"));
	static final String MONGO_DB_COLLECTION_NAME = System.getenv()
			.getOrDefault("BOOKMARK_MONGODB_COLLECTION_NAME", "collections");
	static final String MONGO_DB_DB_NAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_DB_NAME", "select");
	static final String MONGO_DB_HOSTNAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_DB_HOSTNAME", "localhost");
	static final String MONGO_DB_ADMIN_DB_NAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_ADMIN_DB_NAME", "admin");
	static final String MONGO_DB_USER = System.getenv().getOrDefault("BOOKMARK_MONGODB_USERNAME", "admin");
	static final String MONGO_DB_PASSWORD = System.getenv().getOrDefault("BOOKMARK_MONGODB_PASSWORD", "");
	static final MongoCredential MONGO_DB_CREDENTIAL = MongoCredential.createCredential(MONGO_DB_USER, MONGO_DB_ADMIN_DB_NAME, MONGO_DB_PASSWORD.toCharArray());

	// Elasticsearch Constants
	static final String GERDI_ES_HOSTNAME = System.getenv().getOrDefault("GERDI_ES_HOSTNAME", "localhost");
	static final String GERDI_ES_INDEXNAME = System.getenv().getOrDefault("GERDI_ES_INDEXNAME", "gerdi");
	static final String GERDI_ES_TYPENAME = System.getenv().getOrDefault("GERDI_ES_TYPENAME", "metadata");
	static final RestHighLevelClient ES_CLIENT = new RestHighLevelClient(
			RestClient.builder(new HttpHost(GERDI_ES_HOSTNAME, 9200, "http")));
	
}
