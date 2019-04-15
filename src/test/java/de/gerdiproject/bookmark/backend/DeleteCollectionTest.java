package de.gerdiproject.bookmark.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import de.gerdiproject.bookmark.backend.route.DeleteCollection;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;
import spark.Request;
import spark.Response;

import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DeleteCollectionTest {

    @Test
    public void testDelete(){

        // Arguments
        Request req = mock(Request.class);
        when(req.params(BookmarkPersistenceConstants.PARAM_USER_ID_NAME)).thenReturn("testUser");
        String objectId = new ObjectId().toString();
        when(req.params(BookmarkPersistenceConstants.PARAM_COLLECTION_NAME)).thenReturn(objectId);
        Response res = mock(Response.class);

        // MongoDB Mockup
        MongoCollection<Document> collection = mock(MongoCollection.class);
        DeleteResult deleteResult = mock(DeleteResult.class);
        when(deleteResult.wasAcknowledged()).thenReturn(true);
        when(collection.deleteOne(argThat(Objects::nonNull))).thenReturn(deleteResult);

        // Invoke handler
        DeleteCollection deleteColl = new DeleteCollection(collection);
        deleteColl.handle(req, res);

        ArgumentCaptor<Bson> captor = ArgumentCaptor.forClass(Bson.class);
        verify(collection).deleteOne(captor.capture());

        final BasicDBObject query1 = new BasicDBObject(
                BookmarkPersistenceConstants.DB_UID_FIELD_NAME,
                new ObjectId(objectId));
        final BasicDBObject query2 = new BasicDBObject(
                BookmarkPersistenceConstants.DB_USER_ID_FIELD_NAME, "testUser");

        System.out.println(captor.getValue().toString().equals(and (query1, query2).toString()));
    }

}
