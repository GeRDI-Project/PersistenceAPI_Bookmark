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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;

/**
 * Utility class for document retrival
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class DocumentUtility
{

    private DocumentUtility()
    {
    }

    /**
     * This method check whether or not a document exists in our system.
     *
     * @param docId
     *            The ID of the document to be checked
     * @return true if the document exists, false otherwise
     * @throws IOException
     */
    public static boolean checkIfDocExists(final String docId) throws IOException
    {
        final GetRequest getRequest = new GetRequest(BookmarkPersistanceConstants.GERDI_ES_INDEXNAME,
                                                     BookmarkPersistanceConstants.GERDI_ES_TYPENAME, docId);
        return BookmarkPersistanceConstants.ES_CLIENT.get(getRequest).isExists();
    }

    /**
     * This method retrieves a document and returns it in a specific format.
     *
     * @param docId
     *            The ID of the document to be retrieved
     * @return A Map containing the document id and the source data. The value of
     *         the source data may be null if no such document can be found.
     * @throws IOException
     */
    public static Map<String, Object> retrieveDoc(final String docId) throws IOException
    {
        final GetRequest getRequest = new GetRequest(BookmarkPersistanceConstants.GERDI_ES_INDEXNAME,
                                                     BookmarkPersistanceConstants.GERDI_ES_TYPENAME, docId);
        final GetResponse getResponse = BookmarkPersistanceConstants.ES_CLIENT.get(getRequest);
        final Map<String, Object> retVal = new HashMap<>();
        retVal.put("_source", getResponse.getSource());
        retVal.put("_id", docId);
        return retVal;
    }

}
