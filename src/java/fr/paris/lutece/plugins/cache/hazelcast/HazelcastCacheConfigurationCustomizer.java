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
import javax.cache.spi.CachingProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.paris.lutece.portal.service.cache.CacheSettings;
import fr.paris.lutece.portal.service.cache.ICacheConfigurationCustomizer;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Gives the caches created on Hazelcast the size of their Lutece settings (maxElementsInMemory). JCache has no size:
 * without this customizer every Hazelcast cache keeps the default eviction of 10000 entries.
 * <p>
 * Hazelcast applies the configuration only when the cache is created in the cluster: a cache that already exists, or
 * that the member configuration declares by name, keeps its own settings. The size is counted per member.
 * <p>
 * This bean does not use the Hazelcast API itself (see {@link HazelcastCacheConfigurations}): deployed by mistake on a
 * site without Hazelcast, it is discovered and does nothing.
 */
@ApplicationScoped
public class HazelcastCacheConfigurationCustomizer implements ICacheConfigurationCustomizer
{
    private static final String HAZELCAST_PACKAGE = "com.hazelcast.";
    private static final Logger _logger = LogManager.getLogger( "lutece.cache" );

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports( CachingProvider provider )
    {
        return provider != null && provider.getClass( ).getName( ).startsWith( HAZELCAST_PACKAGE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V> Configuration<K, V> customize( String strCacheName, Configuration<K, V> configuration, CacheSettings settings )
    {
        if ( settings.maxElementsInMemory( ) <= 0 || !( configuration instanceof CompleteConfiguration ) )
        {
            return configuration;
        }
        Configuration<K, V> hazelcastConfig = HazelcastCacheConfigurations.withMaxEntries( (CompleteConfiguration<K, V>) configuration,
                settings.maxElementsInMemory( ) );
        _logger.debug( "Hazelcast cache {}: {} entries per member", strCacheName, settings.maxElementsInMemory( ) );
        return hazelcastConfig;
    }
}
