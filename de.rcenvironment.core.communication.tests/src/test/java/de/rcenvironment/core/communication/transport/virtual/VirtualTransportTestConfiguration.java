/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.communication.transport.virtual;

import de.rcenvironment.core.communication.channel.MessageChannelIdFactory;
import de.rcenvironment.core.communication.testutils.TestConfigurationImpl;
import de.rcenvironment.core.communication.transport.spi.DefaultMessageChannelIdFactoryImpl;
import de.rcenvironment.core.communication.transport.virtual.testutils.VirtualNetworkContactPointGenerator;

/**
 * Test configuration for {@link VirtualNetworkTransportProvider} tests.
 * 
 * @author Robert Mischke
 */
public class VirtualTransportTestConfiguration extends TestConfigurationImpl {

    private static final int DEFAULT_TRAFFIC_WAIT_TIMEOUT_MSEC = 1000;

    private static final int DEFAULT_NETWORK_SILENCE_WAIT_TIME_MSEC = 1000;

    private static final int DEFAULT_NETWORK_SILENCE_WAIT_TIMEOUT_MSEC = 25000;

    public VirtualTransportTestConfiguration(boolean useDuplexTransport) {
        setContactPointGenerator(new VirtualNetworkContactPointGenerator("virtualhost"));
        MessageChannelIdFactory connectionIdFactory = new DefaultMessageChannelIdFactoryImpl();
        setTransportProvider(new VirtualNetworkTransportProvider(useDuplexTransport, connectionIdFactory));
        setDefaultTrafficWaitTimeout(DEFAULT_TRAFFIC_WAIT_TIMEOUT_MSEC);
        setDefaultNetworkSilenceWait(DEFAULT_NETWORK_SILENCE_WAIT_TIME_MSEC);
        setDefaultNetworkSilenceWaitTimeout(DEFAULT_NETWORK_SILENCE_WAIT_TIMEOUT_MSEC);
    }

}
