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

package org.fourthline.cling.bridge.auth;

import org.jboss.resteasy.client.ClientRequest;
import org.seamless.http.Query;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class SecureHashAuthManager implements AuthManager<HashCredentials> {

    final private static Logger log = Logger.getLogger(AuthManager.class.getName());

    public static final String QUERY_PARAM_AUTH = "auth";
    public static final String HEADER_PARAM_AUTH = "X-CLING-BRIDGE-AUTH";

    final protected Random random;
    final protected HashCredentials credentials;

    public SecureHashAuthManager() {
        try {
            random = new SecureRandom();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        random.setSeed(
            random.nextLong() ^
                System.currentTimeMillis() ^
                hashCode() ^
                Runtime.getRuntime().freeMemory()
        );
        credentials = newCredentials();
    }

    protected HashCredentials newCredentials() {
        String hash = null;
        while (hash == null || hash.length() == 0) {
            long r0 = random.nextLong();
            if (r0 < 0)
                r0 = -r0;
            long r1 = random.nextLong();
            if (r1 < 0)
                r1 = -r1;
            hash = Long.toString(r0, 36) + Long.toString(r1, 36);
        }
        return new HashCredentials(hash);
    }

    public HashCredentials getLocalCredentials() {
        return credentials;
    }

    public boolean isAuthenticated(HashCredentials creds) {
        return getLocalCredentials().equals(creds);
    }

    public HashCredentials read(HttpServletRequest request) {
        Query query = new Query(request.getQueryString());
        String hash;
        if ((hash = query.get(QUERY_PARAM_AUTH)) != null && hash.length() > 0) {
            return new HashCredentials(hash);
        } else if ((hash = request.getHeader(HEADER_PARAM_AUTH)) != null && hash.length() > 0) {
            return new HashCredentials(hash);
        }
        return null;
    }

    public void write(HashCredentials credentials, ClientRequest request) {
        log.fine("Adding auth header credentials to request: " + credentials.toString());
        request.getHeaders().putSingle(HEADER_PARAM_AUTH, credentials.toString());
    }

}
