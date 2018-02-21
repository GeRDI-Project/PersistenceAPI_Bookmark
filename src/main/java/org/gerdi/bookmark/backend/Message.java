package org.gerdi.bookmark.backend;

import java.util.List;

/**
 * Model class for messages returned to the caller.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class Message
{

    private String message; // NOPMD
    private List<String> docs;
    private String collectionId;
    private boolean acknowledged;

    Message(final String message)
    {
        this.message = message;
    }

    public Message(final boolean acknowledged)
    {
        this.acknowledged = acknowledged;
    }

    Message(final String message, final List<String> docs)
    {
        this(message);
        this.docs = docs;
    }

    Message(final String message, final String collectionId)
    {
        this(message);
        this.collectionId = collectionId;
    }

    Message(final String message, final boolean acknowledged)
    {
        this(message);
        this.setAcknowledged(acknowledged);
    }

    public Message(final String message, final List<String> docs, final boolean acknowledged)
    {
        this(message, docs);
        this.setAcknowledged(acknowledged);
    }

    public Message(final String message, final String collectionId, final boolean acknowledged)
    {
        this(message, collectionId);
        this.setAcknowledged(acknowledged);
    }

    public List<String> getDocs()
    {
        return docs;
    }

    public void setDocs(final List<String> docs)
    {
        this.docs = docs;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    public String getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(final String collectionId)
    {
        this.collectionId = collectionId;
    }

    public boolean isAcknowledged()
    {
        return acknowledged;
    }

    public void setAcknowledged(final boolean acknowledged)
    {
        this.acknowledged = acknowledged;
    }

}
