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

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents a model of a get collection response.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class BookmarkCollection
{

    @SerializedName(BookmarkPersistenceConstants.RESPONSE_UID_FIELD_NAME)
    private final String collectionId;
    @SerializedName(BookmarkPersistenceConstants.RESPONSE_NAME_FIELD_NAME)
    private final String collectionName;

    /**
     * Creates an instance with an identifier and a name.
     *
     * @param identifier
     *            The collection's identifier
     * @param collectionName
     *            The collection name
     */
    public BookmarkCollection(final String identifier,
                              final String collectionName)
    {
        this.collectionId = identifier;
        this.collectionName = collectionName;
    }

    /**
     * Creates an instance by collecting the data from a MongoDB document.
     *
     * @param doc
     *            A MongoDB document representing a collection.
     */
    public BookmarkCollection(final Document doc)
    {
        this.collectionId = doc
                            .getObjectId(BookmarkPersistenceConstants.DB_UID_FIELD_NAME)
                            .toString();
        this.collectionName = doc.getString(
                                  BookmarkPersistenceConstants.DB_COLLECTION_FIELD_NAME);
    }

    public String getCollectionId()
    {
        return collectionId;
    }

    public String getCollectionName()
    {
        return collectionName;
    }
}
