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

package org.fourthline.cling.support.messagebox.model;

import org.fourthline.cling.support.messagebox.parser.MessageDOM;
import org.fourthline.cling.support.messagebox.parser.MessageDOMParser;
import org.fourthline.cling.support.messagebox.parser.MessageElement;
import org.seamless.xml.ParserException;

import java.util.Random;

/**
 * https://sourceforge.net/apps/mediawiki/samygo/index.php?title=MessageBoxService_request_format
 * 
 * @author Christian Bauer
 */
public abstract class Message implements ElementAppender {

    final protected Random randomGenerator = new Random();

    public enum Category {
        SMS("SMS"),
        INCOMING_CALL("Incoming Call"),
        SCHEDULE_REMINDER("Schedule Reminder");

        public String text;

        Category(String text) {
            this.text = text;
        }
    }

    public enum DisplayType {

        MINIMUM("Minimum"),
        MAXIMUM("Maximum");

        public String text;

        DisplayType(String text) {
            this.text = text;
        }
    }

    private final int id;
    private final Category category;
    private DisplayType displayType;

    public Message(Category category, DisplayType displayType) {
        this(0, category, displayType);
    }

    public Message(int id, Category category, DisplayType displayType) {
        if (id == 0) id = randomGenerator.nextInt(Integer.MAX_VALUE);
        this.id = id;
        this.category = category;
        this.displayType = displayType;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != message.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        try {
            MessageDOMParser mp = new MessageDOMParser();
            MessageDOM dom = mp.createDocument();

            MessageElement root = dom.createRoot(mp.createXPath(), "Message");
            root.createChild("Category").setContent(getCategory().text);
            root.createChild("DisplayType").setContent(getDisplayType().text);
            appendMessageElements(root);

            String s = mp.print(dom, 0, false);

            // Cut the root element, what we send to the TV is not really XML, just
            // random element soup which I'm sure the Samsung guys think is XML...
            return s.replaceAll("<Message xmlns=\"urn:samsung-com:messagebox-1-0\">", "")
                    .replaceAll("</Message>", "");

            
        } catch (ParserException ex) {
            throw new RuntimeException(ex);
        }
    }
}
