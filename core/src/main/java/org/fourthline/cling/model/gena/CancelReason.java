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

package org.fourthline.cling.model.gena;

/**
 * The reason why a {@link org.fourthline.cling.model.gena.GENASubscription} has ended unexpectedly.
 *
 * @author Christian Bauer
 */
public enum CancelReason {

    RENEWAL_FAILED,
    DEVICE_WAS_REMOVED,
    UNSUBSCRIBE_FAILED,
    EXPIRED
}
