package de.gerdiproject.bookmark.backend;

import org.bson.Document;

import com.google.gson.annotations.SerializedName;

public class BookmarkCollection
{

    @SerializedName(BookmarkPersistenceConstants.RESPONSE_UID_FIELD_NAME)
    private final String collectionId;
    @SerializedName(BookmarkPersistenceConstants.RESPONSE_NAME_FIELD_NAME)
    private final String collectionName;

    public BookmarkCollection(final String identifier,
                              final String collectionName)
    {
        this.collectionId = identifier;
        this.collectionName = collectionName;
    }

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
