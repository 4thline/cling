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

package org.fourthline.cling.osgi.device.light.model;

public class Dimmer extends PoweredDevice implements PowerSource {
	private Integer level;

	public Dimmer(PowerSource source, Integer level) {
		super(source);
		setLevel(level);
	}
	
	public Dimmer() {
		super();
		setLevel(new Integer(0));
	}

	public void setLevel(Integer level) {
		if (this.level != level) {
			this.level = level;
			setChanged();
			notifyObservers(this);
		}
	}

	public Integer getLevel() {
		return level;
	}
	
	@Override
	public Power getPower() {
		Power input = getPowerSource().getPower();
		Power output = null;
		
		if (input != null && !level.equals(new Integer(0))) {
			output = new Power((int) (input.getLevel().intValue() * (Math.min(level.intValue(), 100) / 100.0)));
		}
		
		return output;
	}
}
