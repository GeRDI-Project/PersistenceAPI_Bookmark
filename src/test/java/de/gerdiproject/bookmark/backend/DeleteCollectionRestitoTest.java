package de.gerdiproject.bookmark.backend;

import com.xebialabs.restito.server.StubServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;

public class DeleteCollectionRestitoTest {

    private StubServer server;

    @Before
    public void start() {
        server = new StubServer(BookmarkPersistenceConstants.MONGO_DB_PORT).run();

    }

    @Test
    public void testDelete(){
        whenHttp(server).match()
    }

    @After
    public void stop(){
        server.stop();
    }

}
