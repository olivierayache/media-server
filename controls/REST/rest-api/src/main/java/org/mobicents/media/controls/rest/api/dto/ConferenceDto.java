/*
 * Copyright (C) 2017 TeleStax, Inc..
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.mobicents.media.controls.rest.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ayache
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConferenceDto {

    private final String id;
    private final boolean conferenceRecorded;
    private final boolean announcementNeeded;
    private final String remoteSdp;

    /**
     * Constructor
     * 
     * @param id 
     * @param conferenceRecorded 
     * @param announcementNeeded 
     * @param remoteSdp 
     */
    public ConferenceDto(String id, boolean conferenceRecorded, boolean announcementNeeded, String remoteSdp) {
        this.id = id;
        this.conferenceRecorded = conferenceRecorded;
        this.announcementNeeded = announcementNeeded;
        this.remoteSdp = remoteSdp;
    }

    /**
     * Constructor for serialization/deserialization
     * 
     */
    public ConferenceDto() {
        this.id = null;
        this.conferenceRecorded = false;
        this.announcementNeeded = false;
        this.remoteSdp = null;
    }

    public String getId() {
        return id;
    }

    public boolean isAnouncementNeeded() {
        return announcementNeeded;
    }

    public boolean isConferenceRecorded() {
        return conferenceRecorded;
    }

    public String getRemoteSdp() {
        return remoteSdp;
    }

}
