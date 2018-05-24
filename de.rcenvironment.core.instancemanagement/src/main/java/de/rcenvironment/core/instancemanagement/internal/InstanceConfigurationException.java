/*
 * Copyright (C) 2006-2017 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.instancemanagement.internal;

import de.rcenvironment.core.configuration.ConfigurationException;

/**
 * 
 * {@link Exception} type for {@link InstanceConfigurationImpl} specific failures.
 *
 * @author David Scholz
 */
public class InstanceConfigurationException extends ConfigurationException {

    /**
     * 
     */
    private static final long serialVersionUID = -7526032139846516126L;
    
    public InstanceConfigurationException(String msg) {
        super(msg);
    }
    
    public InstanceConfigurationException(String msg, Exception e) {
        super(msg, e);
    }

}
