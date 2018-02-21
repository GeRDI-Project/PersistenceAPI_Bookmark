/**
 * Copyright 2018 Nelson Tavares de Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
@SuppressWarnings("PMD.LongVariable")
public final class BookmarkPersistanceConstants {

	// MongoDB Constants
	static final int MONGO_DB_PORT = Integer.parseInt(System.getenv().getOrDefault("BOOKMARK_MONGODB_PORT", "27017"));
	static final String MONGO_DB_COLLECTION_NAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_COLLECTION_NAME",
			"collections");
	static final String MONGO_DB_DB_NAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_DB_NAME", "select");
	static final String MONGO_DB_HOSTNAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_DB_HOSTNAME", "localhost");
	static final String MONGO_DB_ADMIN_DB_NAME = System.getenv().getOrDefault("BOOKMARK_MONGODB_ADMIN_DB_NAME",
			"admin");
	static final String MONGO_DB_USER = System.getenv().getOrDefault("BOOKMARK_MONGODB_USERNAME", "admin");
	static final String MONGO_DB_PASSWORD = System.getenv().getOrDefault("BOOKMARK_MONGODB_PASSWORD", "");
	static final MongoCredential MONGO_DB_CREDENTIAL = MongoCredential.createCredential(MONGO_DB_USER,
			MONGO_DB_ADMIN_DB_NAME, MONGO_DB_PASSWORD.toCharArray());

	// Elasticsearch Constants
	static final String GERDI_ES_HOSTNAME = System.getenv().getOrDefault("GERDI_ES_HOSTNAME", "localhost");
	static final String GERDI_ES_INDEXNAME = System.getenv().getOrDefault("GERDI_ES_INDEXNAME", "gerdi");
	static final String GERDI_ES_TYPENAME = System.getenv().getOrDefault("GERDI_ES_TYPENAME", "metadata");
	static final int GERDI_ES_PORT = Integer.parseInt(System.getenv().getOrDefault("GERDI_ES_PORT", "9200"));
	static final RestHighLevelClient ES_CLIENT = new RestHighLevelClient(
			RestClient.builder(new HttpHost(GERDI_ES_HOSTNAME, GERDI_ES_PORT, "http")));

	// Other stuff
	public static final String APPLICATION_JSON = "application/json";
	public static final String DATE_STRING = "yyyy-MM-dd HH:mm:ss";
	public static final String PATH_PREFIX = "/api/v1/collections";

	// MongoDB Field Names
	public static final String DB_COLLECTION_FIELD_NAME = "collectionName";
	public static final String DB_USER_ID_FIELD_NAME = "userId";
	public static final String DB_DOCS_FIELD_NAME = "docs";

	// Param Constants
	public static final String PARAM_USER_ID_NAME = "userId";
	public static final String PARAM_COLLECTION_NAME = "collectionId";

	private BookmarkPersistanceConstants() {

	}
}
