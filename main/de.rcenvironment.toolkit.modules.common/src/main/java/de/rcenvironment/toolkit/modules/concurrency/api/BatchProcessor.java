/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.toolkit.modules.concurrency.api;

import java.util.List;

/**
 * Receiver interface for generated batches of elements; typically used with {@link BatchAggregator}.
 * 
 * @param <T> the element type of the received batches; must match the associated {@link BatchAggregator}
 * 
 * @author Robert Mischke
 */
public interface BatchProcessor<T> {

    /**
     * Callback method for a single generated batch.
     * 
     * @param batch the generated batch following the FIFO principle
     */
    void processBatch(List<T> batch);
}
