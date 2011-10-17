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

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.ErrorCode;

/**
 * Thrown (or encapsulated in {@link org.fourthline.cling.model.action.ActionInvocation} when an action execution failed.
 *
 * @author Christian Bauer
 */
public class ActionException extends Exception {

    private int errorCode;

    public ActionException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ActionException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ActionException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDescription());
    }

    public ActionException(ErrorCode errorCode, String message) {
        this(errorCode, message, true);
    }

    public ActionException(ErrorCode errorCode, String message, boolean concatMessages) {
        this(errorCode.getCode(), concatMessages ? (errorCode.getDescription() + ". " + message + ".") : message);
    }

    public ActionException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".", cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
}