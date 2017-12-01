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

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;

/**
 *
 * @author Ayache
 */
public class CacheManager {

    private static final CacheManager INSTANCE = new CacheManager();
    private String cacheConf;
    private String cacheName;
    private Grid grid;
    private Cache cache;

    private CacheManager(){    
    }
    
    public static CacheManager getInstance(){
        return INSTANCE;
    }
    
    public void setCacheConf(String cacheConf) {
        this.cacheConf = cacheConf;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void start() throws GridException {
        grid = GridGain.start(cacheConf);
        cache = new CacheImpl(grid.cache(cacheName));
    }
    
    public <K,V> Cache<K,V> getCache(){
        return cache;
    }
}
