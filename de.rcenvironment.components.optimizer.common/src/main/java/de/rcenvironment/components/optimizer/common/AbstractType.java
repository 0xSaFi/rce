/*
 * Copyright 2006-2019 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.components.optimizer.common;

import java.io.Serializable;

/**
 * Abstract class for typed structure classes.
 * @author Christian Weiss
 */
public abstract class AbstractType implements Serializable {

    private static final long serialVersionUID = 8717802746103860082L;

    private String name;

    private String type;

    public AbstractType(final String name, final String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

