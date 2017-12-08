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
package org.mobicents.media.controls.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.mobicents.media.controls.rest.api.dto.MediaSessionDto;
import org.mobicents.media.controls.rest.api.dto.MediaSessionMemberDto;

/**
 *
 * @author Ayache
 */
@Path("/media-controller")
public interface IMediaServerControllerService {

    @PUT
    @Path("/conference/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    MediaSessionDto createConference(@PathParam("id") String id);

    @PUT
    @Path("/listener")
    @Consumes(MediaType.APPLICATION_JSON)
    String createMember(@QueryParam("cid") String conferenceId, MediaSessionMemberDto memberDto);

    @POST
    @Path("/listeners")
    @Consumes(MediaType.APPLICATION_JSON)
    void addMemberToSession(@QueryParam("cid") String conferenceId, MediaSessionMemberDto memberDto);

    @DELETE
    @Path("/listener/{id}")
    void removeListenerFromConference(@QueryParam("cid") String conferenceId, @PathParam("id") String listenerId);

    @DELETE
    @Path("/conference/{id}")
    void deleteConference(String id);
}
