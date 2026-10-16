/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.cache.hazelcast;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;

import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MaxSizePolicy;

/**
 * Builds the Hazelcast configuration of a cache. Only this class uses the Hazelcast API: it is loaded on its first call,
 * which happens only when Hazelcast is the caching provider, so a site without Hazelcast never loads it.
 */
final class HazelcastCacheConfigurations
{
    private HazelcastCacheConfigurations( )
    {
    }

    /**
     * Returns the Hazelcast configuration of a cache bounded to a number of entries per member (LRU eviction)
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     * @param configuration
     *            the JCache configuration built by the core
     * @param nMaxEntries
     *            the maximum number of entries per member
     * @return a CacheConfig keeping the settings of the given configuration
     */
    static <K, V> Configuration<K, V> withMaxEntries( CompleteConfiguration<K, V> configuration, int nMaxEntries )
    {
        CacheConfig<K, V> hazelcastConfig = ( configuration instanceof CacheConfig ) ? (CacheConfig<K, V>) configuration
                : new CacheConfig<>( configuration );
        hazelcastConfig.getEvictionConfig( ).setSize( nMaxEntries ).setMaxSizePolicy( MaxSizePolicy.ENTRY_COUNT ).setEvictionPolicy( EvictionPolicy.LRU );
        return hazelcastConfig;
    }
}
