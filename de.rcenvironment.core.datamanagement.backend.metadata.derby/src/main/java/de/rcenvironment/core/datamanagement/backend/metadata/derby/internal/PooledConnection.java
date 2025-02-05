/*
 * Copyright 2006-2019 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.datamanagement.backend.metadata.derby.internal;

import java.sql.Connection;

/**
 * A pooled Connection.
 * 
 * @author Christian Weiss
 */
interface PooledConnection extends Connection {

    void increment();

    void decrement();
}
