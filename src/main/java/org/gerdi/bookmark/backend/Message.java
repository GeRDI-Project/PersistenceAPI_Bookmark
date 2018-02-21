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
