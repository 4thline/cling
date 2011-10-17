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

package org.fourthline.cling.support.shared.log;

import org.seamless.swing.logging.LogCategory;
import org.seamless.swing.logging.LogMessage;
import org.fourthline.cling.support.shared.View;

import java.util.List;

/**
 * @author Christian Bauer
 */
public interface LogView extends View<LogView.Presenter> {

    public interface Presenter {

        void init();

        void onExpand(LogMessage logMessage);

        void pushMessage(LogMessage logMessage);
    }

    public interface LogCategories extends List<LogCategory> {
    }

    void pushMessage(LogMessage logMessage);

    void dispose();
}
