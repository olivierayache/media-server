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
package org.mobicents.media.controls.rest.core.exceptions;

/**
 *
 * @author Ayache
 */
public class InitFailedException extends RuntimeException {

    /**
     * Creates a new instance of <code>InitFailedException</code> without detail
     * message.
     */
    public InitFailedException() {
    }

    /**
     * Constructs an instance of <code>InitFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InitFailedException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>InitFailedException</code> with the
     * specified cause.
     *
     * @param cause the cause of exception.
     */
    public InitFailedException(Throwable cause) {
        super(cause);
    }

}
