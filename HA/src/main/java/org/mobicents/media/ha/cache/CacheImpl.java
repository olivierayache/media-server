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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gridgain.grid.GridException;
import org.gridgain.grid.cache.GridCache;

public class CacheImpl<K, V> implements Cache<K, V> {

    private final GridCache<K, V> cache;

    public CacheImpl(GridCache<K, V> cache) {
        this.cache = cache;
    }
    
    public boolean addEntry(K key, V value) {
        try {
            return cache.putx(key, value);
        } catch (GridException ex) {
            Logger.getLogger(CacheImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeEntry(K key) {
        try {
            return cache.removex(key);
        } catch (GridException ex) {
            Logger.getLogger(CacheImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public V entry(K key) {
        try {
            return cache.get(key);
        } catch (GridException ex) {
            Logger.getLogger(CacheImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean editEntry(final K key, final Closure<V, V> transform) {
        try {
            cache.entry(key).transform(new ClosureImpl(transform));
            return true;
        } catch (GridException ex) {
            Logger.getLogger(CacheImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Map<K, V> getData() {
        return cache.toMap();
    }
    
    public Iterable<V> getValues(){
        return cache.values();
    }

}
