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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.junit.jupiter.api.Test;

import com.hazelcast.client.cache.HazelcastClientCachingProvider;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.MaxSizePolicy;

import fr.paris.lutece.portal.service.cache.CacheSettings;

/**
 * Tests the size given to the Hazelcast caches.
 */
public class HazelcastCacheConfigurationCustomizerTest
{
    private final HazelcastCacheConfigurationCustomizer _customizer = new HazelcastCacheConfigurationCustomizer( );

    private static CacheSettings settings( int nSize )
    {
        return new CacheSettings( nSize, 3600, 0, false, false, false );
    }

    /**
     * The customizer applies to the Hazelcast providers only.
     */
    @Test
    public void testSupports( )
    {
        assertTrue( _customizer.supports( new HazelcastClientCachingProvider( ) ) );
        assertFalse( _customizer.supports( null ) );
    }

    /**
     * The size of the settings becomes the eviction size; the JCache settings built by the core are kept.
     */
    @Test
    public void testSizeAndJCacheSettingsKept( )
    {
        MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>( ).setTypes( String.class, String.class )
                .setStoreByValue( false ).setExpiryPolicyFactory( CreatedExpiryPolicy.factoryOf( new Duration( TimeUnit.SECONDS, 3600 ) ) );

        Configuration<String, String> result = _customizer.customize( "formsCacheService", configuration, settings( 20000 ) );

        CacheConfig<String, String> hazelcastConfig = (CacheConfig<String, String>) result;
        assertEquals( 20000, hazelcastConfig.getEvictionConfig( ).getSize( ) );
        assertEquals( MaxSizePolicy.ENTRY_COUNT, hazelcastConfig.getEvictionConfig( ).getMaxSizePolicy( ) );
        assertEquals( String.class, hazelcastConfig.getValueType( ) );
        assertEquals( configuration.getExpiryPolicyFactory( ), hazelcastConfig.getExpiryPolicyFactory( ) );
    }

    /**
     * A cache without a size keeps its configuration.
     */
    @Test
    public void testNoSize( )
    {
        MutableConfiguration<String, String> configuration = new MutableConfiguration<>( );

        assertSame( configuration, _customizer.customize( "pluginCache", configuration, settings( 0 ) ) );
    }
}
