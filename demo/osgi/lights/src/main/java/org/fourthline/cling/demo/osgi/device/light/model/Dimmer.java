/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.demo.osgi.device.light.model;

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
