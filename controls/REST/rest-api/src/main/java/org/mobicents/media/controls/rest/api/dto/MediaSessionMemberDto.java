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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Ayache
 */
public class MediaSessionMemberDto {

    public static enum MemberType {
        EMITER, RECEIVER, BOTH;
    }

    private final String id;
    private final boolean announcementNeeded; 
    private final boolean dtmfDetectorNeeded; 
    private final boolean recorderNeeded;
    private String localSdp;
    private byte[] remoteSdp;
    private final MemberType type;
    private final List<String> peers = new LinkedList<String>();

    public MediaSessionMemberDto(String id, boolean announcementNeeded, boolean dtmfDetectorNeeded, boolean recorderNeeded, byte[] sdp, MemberType type) {
        this.id = id;
        this.announcementNeeded = announcementNeeded;
        this.dtmfDetectorNeeded = dtmfDetectorNeeded;
        this.recorderNeeded = recorderNeeded;
        this.remoteSdp = sdp;
        this.type = type;
    }

    public List<String> getPeers() {
        return peers;
    }

    public MemberType getType() {
        return type;
    }

    public void setLocalSdp(String localSdp) {
        this.localSdp = localSdp;
    }

    public String getLocalSdp() {
        return localSdp;
    }

    public byte[] getRemoteSdp() {
        return remoteSdp;
    }

    public boolean isAnnouncementNeeded() {
        return announcementNeeded;
    }

    public boolean isRecorderNeeded() {
        return recorderNeeded;
    }

    public boolean isDtmfDetectorNeeded() {
        return dtmfDetectorNeeded;
    }
    
}
