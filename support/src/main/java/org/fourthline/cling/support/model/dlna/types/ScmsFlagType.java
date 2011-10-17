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
package org.fourthline.cling.support.model.dlna.types;

/**
 *
 * @author Mario Franco
 */
public class ScmsFlagType {

    private boolean copyright;
    private boolean original;
    
    public ScmsFlagType() {
        this.copyright = true;
        this.original = true;
    }

    public ScmsFlagType(boolean copyright, boolean original) {
        this.copyright = copyright;
        this.original = original;
    }
    
    /**
     * @return the copyright
     */
    public boolean isCopyright() {
        return copyright;
    }

    /**
     * @return the original
     */
    public boolean isOriginal() {
        return original;
    }
}
