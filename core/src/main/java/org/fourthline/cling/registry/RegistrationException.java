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

package org.fourthline.cling.registry;

import org.fourthline.cling.model.ValidationError;

import java.util.List;

/**
 * A recoverable error, thrown if device metadata could not be registered.
 *
 * @author Christian Bauer
 */
public class RegistrationException extends RuntimeException {

    public List<ValidationError> errors;

    public RegistrationException(String s) {
        super(s);
    }

    public RegistrationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RegistrationException(String s, List<ValidationError> errors) {
        super(s);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}