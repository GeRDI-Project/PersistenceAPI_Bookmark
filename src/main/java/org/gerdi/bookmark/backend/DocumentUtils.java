package org.gerdi.bookmark.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;

public class DocumentUtils {

	/**
	 * This method check whether or not a document exists in our system. The current
	 * implementation checks if Elasticsearch holds such document, but this needs to
	 * be changed once this service has an own persistance layer.
	 * 
	 * @param docId
	 *            The ID of the document to be checked
	 * @return true if the document exists, false otherwise
	 * @throws IOException
	 */
	public static boolean checkIfDocExists(String docId) throws IOException {
		GetRequest getRequest = new GetRequest(BookmarkPersistanceConstants.GERDI_ES_INDEXNAME, BookmarkPersistanceConstants.GERDI_ES_TYPENAME, docId);
		return BookmarkPersistanceConstants.ES_CLIENT.get(getRequest).isExists();
	}

	
	/**
	 * This method retrieves a document and returns it in a specific format.
	 * In the first version, we use the persistance capabilities of Elasticsearch. This method needs to be changed once a dedicated persistance layer exists.
	 * 
	 * @param docId The ID of the document to be retrieved
	 * @return A Map containing the document id and the source data. The value of the source data may be null if no such document can be found.
	 * @throws IOException
	 */
	public static Map<String, Object> retrieveDoc(String docId) throws IOException {
		GetRequest getRequest = new GetRequest(BookmarkPersistanceConstants.GERDI_ES_INDEXNAME, BookmarkPersistanceConstants.GERDI_ES_TYPENAME, docId);
		GetResponse getResponse = BookmarkPersistanceConstants.ES_CLIENT.get(getRequest);
		Map<String, Object> retVal = new HashMap<String, Object>();
		retVal.put("_source", getResponse.getSource());
		retVal.put("_id", docId);
		return retVal;
	}
	
}
