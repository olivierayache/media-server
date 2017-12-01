/*
 * Copyright (C) 2014 TeleStax, Inc..
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

package org.mobicents.media.ha.cache;

import java.io.Serializable;
import org.gridgain.grid.lang.GridClosure;

/**
 *
 * @author Ayache
 * @param <E>
 * @param <R>
 */
public class ClosureImpl<E,R> implements GridClosure<E, R>, Serializable{

    private final Closure<E,R> closure;

    public ClosureImpl(Closure<E, R> closure) {
        this.closure = closure;
    }
    
    public R apply(E e) {
        return closure.apply(e);
    }
    
}
