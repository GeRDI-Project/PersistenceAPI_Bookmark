package de.gerdiproject.bookmark.backend;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class BookmarkDocument
{

	@SerializedName(BookmarkPersistenceConstants.RESPONSE_UID_FIELD_NAME)
	private final String collectionId;
	@SerializedName(BookmarkPersistenceConstants.RESPONSE_SOURCE_FIELD_NAME)
	private final Map<String, Object> source;

	public BookmarkDocument(final String identifier,
			final Map<String, Object> source)
	{
		this.collectionId = identifier;
		this.source = source;
	}

}
