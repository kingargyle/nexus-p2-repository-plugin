/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.its.nxcm1898;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.sonatype.jettytestsuite.ServletServer;
import org.sonatype.nexus.plugins.p2.repository.its.AbstractNexusProxyP2IntegrationIT;
import org.sonatype.nexus.rest.model.GlobalConfigurationResource;
import org.sonatype.nexus.test.utils.SettingsMessageUtil;

public abstract class AbstractProxyTimeout
    extends AbstractNexusProxyP2IntegrationIT
{

    public AbstractProxyTimeout()
    {
        super( "nxcm1898" );
        // System.setProperty( "org.eclipse.ecf.provider.filetransfer.retrieve.readTimeout", "30000" );
    }

    @Override
    public void startProxy()
        throws Exception
    {
        if ( server == null )
        {
            server = (ServletServer) lookup( ServletServer.ROLE, "timeout" );
            server.start();
        }
    }

    @Override
    @After
    public void stopProxy()
        throws Exception
    {
        if ( server != null )
        {
            server.stop();
            server = null;
        }
    }

    protected void doTest( final int timeout )
        throws IOException, Exception
    {
        final String nexusTestRepoUrl = getNexusTestRepoUrl();

        final File installDir = new File( "target/eclipse/nxcm1898" );

        // give it a good amount of time
        final GlobalConfigurationResource settings = SettingsMessageUtil.getCurrentSettings();
        settings.getGlobalConnectionSettings().setConnectionTimeout( timeout );
        SettingsMessageUtil.save( settings );

        installUsingP2( nexusTestRepoUrl, "com.sonatype.nexus.p2.its.feature.feature.group",
            installDir.getCanonicalPath() );

        final File feature = new File( installDir, "features/com.sonatype.nexus.p2.its.feature_1.0.0" );
        Assert.assertTrue( feature.exists() && feature.isDirectory() );

        final File bundle = new File( installDir, "plugins/com.sonatype.nexus.p2.its.bundle_1.0.0.jar" );
        Assert.assertTrue( bundle.canRead() );

    }

}