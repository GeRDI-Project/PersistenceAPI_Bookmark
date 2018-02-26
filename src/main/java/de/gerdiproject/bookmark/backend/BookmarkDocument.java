package de.gerdiproject.bookmark.backend;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class BookmarkDocument
{

    @SerializedName(BookmarkPersistenceConstants.RESPONSE_UID_FIELD_NAME)
    private final String collectionId; // NOPMD yes, it could be optimized, but
    // this is a model class
    @SerializedName(BookmarkPersistenceConstants.RESPONSE_SOURCE_FIELD_NAME)
    private final Map<String, Object> source; // NOPMD yes, it could be
    // optimized, but this is a
    // model class

    public BookmarkDocument(final String identifier,
                            final Map<String, Object> source)
    {
        this.collectionId = identifier;
        this.source = source;
    }

}
