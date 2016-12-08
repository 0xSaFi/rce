/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.rpc.internal;

import de.rcenvironment.core.communication.api.ServiceCallContext;
import de.rcenvironment.core.communication.common.LogicalNodeSessionId;
import de.rcenvironment.core.utils.common.StringUtils;

/**
 * {@link ServiceCallContext} implementation.
 * 
 * @author Robert Mischke
 */
public final class ServiceCallContextImpl implements ServiceCallContext {

    private final LogicalNodeSessionId caller;

    private final LogicalNodeSessionId receiver;

    private final String serviceName;

    private final String methodName;

    public ServiceCallContextImpl(LogicalNodeSessionId caller, LogicalNodeSessionId receiver, String serviceName, String methodName) {
        this.caller = caller;
        this.receiver = receiver;
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    @Override
    public LogicalNodeSessionId getReceivingNode() {
        return receiver;
    }

    @Override
    public LogicalNodeSessionId getCallingNode() {
        return caller;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return StringUtils.format("Service call to %s.%s() from %s to local id %s", serviceName, methodName, caller, receiver);
    }
}
