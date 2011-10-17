/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.ExpirationDetails;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * An addressable object, stored, managed, and accessible through the {@link org.fourthline.cling.registry.Registry}.
 *
 * @param <M> The type of the model object.
 *
 * @author Christian Bauer
 */
public class Resource<M> {

    private URI pathQuery;
    private M model;

    /**
     * @param pathQuery The path and (optional) query URI parts of this resource.
     * @param model The model object.
     */
    public Resource(URI pathQuery, M model) {
        try {
            this.pathQuery = new URI(null, null, pathQuery.getPath(), pathQuery.getQuery(), null);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        this.model = model;
        if (model == null) {
            throw new IllegalArgumentException("Model instance must not be null");
        }
    }

    public URI getPathQuery() {
        return pathQuery;
    }

    public M getModel() {
        return model;
    }


    /**
     * @param pathQuery A relative URI.
     * @return <code>true</code> if the given URI path and query matches the resource's path and query.
     */
    public boolean matches(URI pathQuery) {
        return pathQuery.equals(getPathQuery());
    }

    /**
     * Called periodically by the registry to maintain the resource.
     * <p>
     * NOOP by default.
     * </p>
     *
     * @param pendingExecutions Add <code>Runnable</code>'s to this collection if maintenance code has to run in the background.
     * @param expirationDetails The details of this resource's expiration, e.g. when it will expire.
     */
    public void maintain(List<Runnable> pendingExecutions,
                         ExpirationDetails expirationDetails) {
        // Do nothing
    }

    /**
     * Called by the registry when it stops, in the shutdown thread.
     * <p>
     * NOOP by default.
     * </p>
     */
    public void shutdown() {
        // Do nothing
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (!getPathQuery().equals(resource.getPathQuery())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getPathQuery().hashCode();
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") URI: " + getPathQuery();
    }

}
