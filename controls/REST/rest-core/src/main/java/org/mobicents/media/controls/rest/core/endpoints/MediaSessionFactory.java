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
package org.mobicents.media.controls.rest.core.endpoints;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mobicents.media.controls.rest.api.dto.MediaSessionDto;

/**
 *
 * @author Ayache
 */
public class MediaSessionFactory {

    private static final Map<String, MediaSession> MAP = new ConcurrentHashMap<String, MediaSession>();

    public static MediaSession getSession(String id) {
        return MAP.get(id);
    }

    public static void createSession(MediaSessionDto dto) {
        if (MAP.put(dto.getId(), new MediaSession(dto.getId())) != null) {
            throw new IllegalStateException("Session already exists");
        }
    }

    public static void destroySession(String id) {
        MediaSession remove = MAP.remove(id);
        if (remove != null) {
            remove.destroy();
        }
    }
}
