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
package de.gerdiproject.bookmark.backend;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents a model of a get document response.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class BookmarkDocument
{

    @SerializedName(BookmarkPersistenceConstants.RESPONSE_UID_FIELD_NAME)
    private final String collectionId; // NOPMD yes, it could be optimized, but
    // this is a model class
    @SerializedName(BookmarkPersistenceConstants.RESPONSE_SOURCE_FIELD_NAME)
    private final Map<String, Object> source; // NOPMD yes, it could be
    // optimized, but this is a
    // model class

    /**
     * Creates a instance with an identifier and a original metadata document.
     *
     * @param identifier
     *            The identifier of the metadata document.
     * @param source
     *            The original metadata document.
     */
    public BookmarkDocument(final String identifier,
                            final Map<String, Object> source)
    {
        this.collectionId = identifier;
        this.source = source;
    }

}
