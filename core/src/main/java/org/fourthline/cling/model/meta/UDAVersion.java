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

package org.fourthline.cling.model.meta;




import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;

import java.util.List;
import java.util.ArrayList;

/**
 * Version of the UPnP Device Architecture (UDA), defaults to 1.0.
 *
 * @author Christian Bauer
 */
public class UDAVersion implements Validatable {
    
    private int major = 1;
    private int minor = 0;

    public UDAVersion() {
    }

    public UDAVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList();

        if (getMajor() != 1) {
            errors.add(new ValidationError(
                    getClass(),
                    "major",
                    "UDA major spec version must be 1"
            ));
        }
        if (getMajor() < 0) {
            errors.add(new ValidationError(
                    getClass(),
                    "minor",
                    "UDA minor spec version must be equal or greater 0"
            ));
        }

        return errors;
    }
}
