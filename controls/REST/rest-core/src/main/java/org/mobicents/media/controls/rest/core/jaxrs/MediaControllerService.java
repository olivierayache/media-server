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
package org.mobicents.media.controls.rest.core.jaxrs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mobicents.media.ComponentType;
import org.mobicents.media.controls.rest.api.IMediaServerControllerService;
import org.mobicents.media.controls.rest.api.dto.MediaSessionDto;
import org.mobicents.media.controls.rest.api.dto.MediaSessionMemberDto;
import org.mobicents.media.controls.rest.core.endpoints.MediaSessionFactory;
import org.mobicents.media.controls.rest.core.endpoints.MediaSessionMember;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.dtmf.DtmfDetectorListener;
import org.mobicents.media.server.spi.dtmf.DtmfEvent;
import org.mobicents.media.server.spi.listener.TooManyListenersException;

/**
 *
 * @author Ayache
 */
public class MediaControllerService implements IMediaServerControllerService {

    public MediaSessionDto createConference(String id) {
        MediaSessionDto mediaSessionDto = new MediaSessionDto(id);
        MediaSessionFactory.createSession(mediaSessionDto);
        return mediaSessionDto;
    }

    public String createMember(String conferenceId, MediaSessionMemberDto memberDto) {
        MediaSessionMember mediaSessionMember = new MediaSessionMember();
        mediaSessionMember.init(memberDto);
        MediaSessionFactory.getSession(conferenceId).addMember(mediaSessionMember);
        return memberDto.getLocalSdp();
    }

    public void addMemberToSession(final String conferenceId, MediaSessionMemberDto memberDto) {
        final MediaSessionMember mediaSessionMember = new MediaSessionMember();
        mediaSessionMember.init(memberDto);
        try {
            mediaSessionMember.connectToRemote(memberDto.getRemoteSdp());
        } catch (IOException ex) {
            Logger.getLogger(MediaControllerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (memberDto.isAnnouncementNeeded()) {
            mediaSessionMember.getResources().get(ComponentType.PLAYER).activate();
        }
        if (memberDto.isDtmfDetectorNeeded()) {
            try {
                ((DtmfDetector) mediaSessionMember.getResources().get(ComponentType.DTMF_DETECTOR)).addListener(new DtmfDetectorListener() {
                    public void process(DtmfEvent event) {
                        try {
                            mediaSessionMember.connectToPeer();
                        } catch (IOException ex) {
                            Logger.getLogger(MediaControllerService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            } catch (TooManyListenersException ex) {
                Logger.getLogger(MediaControllerService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (memberDto.isRecorderNeeded()) {
            mediaSessionMember.getResources().get(ComponentType.RECORDER).activate();
        }
    }

    public void removeListenerFromConference(final String conferenceId, String listenerId) {
        MediaSessionFactory.getSession(conferenceId).removeMember(listenerId);
    }

    public void deleteConference(String id) {
        MediaSessionFactory.destroySession(id);
    }

}
