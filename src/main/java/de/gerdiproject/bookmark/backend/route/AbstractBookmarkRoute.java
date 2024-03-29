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
package de.gerdiproject.bookmark.backend.route;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jwt.profile.JwtProfile;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Optional;

/**
 * This abstract class introduces a minimal framework for the needed routes.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public abstract class AbstractBookmarkRoute implements Route
{

    protected static final Gson GSON = new Gson();

    protected final MongoCollection<Document> collection;

    /**
     * Used to initialize the MongoDB collection field.
     *
     * @param collection
     *            A MongoDB collection on which the operations are performed.
     */
    protected AbstractBookmarkRoute(final MongoCollection<Document> collection)
    {
        this.collection = collection;
    }

    protected final JwtProfile getUserProfile(Request req, Response res) {
        WebContext context = new SparkWebContext(req, res);
        ProfileManager manager = new ProfileManager(context);
        Optional<CommonProfile> profile = manager.get(true);
        JwtProfile jwtProfile = (JwtProfile) profile.get();
        return jwtProfile;
    }

    protected final String getProfileSubject(Request req, Response res){
        return getUserProfile(req, res).getSubject();
    }

}
