/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.evaluationmemory.execution.internal;

import java.io.IOException;
import java.util.SortedMap;

import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.TypedDatum;

/**
 * Reads and writes tuples from/to the memory file.
 * 
 * @author Doreen Seider
 */
public interface EvaluationMemoryAccess {

    /**
     * Adds a evaluation values to the memory file.
     * 
     * @param inputValues values to evaluate
     * @param outputValues evaluation results
     * @throws IOException if writing values to the memory file failed or values don't match the ones in the memory file
     */
    void addEvaluationValues(SortedMap<String, TypedDatum> inputValues, 
        SortedMap<String, TypedDatum> outputValues) throws IOException;
    
    /**
     * Reads a tuple from the memory file by the given key.
     * 
     * @param inputValues values to evaluate
     * @param outputs outputs for evaluation results
     * @return evaluation results if, <code>null</code> if no one exists for the given values
     * @throws IOException if reading values from the memory file failed or values don't match the ones in the memory file
     */
    SortedMap<String, TypedDatum> getEvaluationResult(SortedMap<String, TypedDatum> inputValues,
        SortedMap<String, DataType> outputs) throws IOException;

    /**
     * 
     * @param inputs inputs (name, data type) 
     * @param outputs outputs (name, data type)
     * @throws IOException if setting definitions failed
     */
    void setInputsOutputsDefinition(SortedMap<String, DataType> inputs, SortedMap<String, DataType> outputs) throws IOException;

    /**
     * Validates a evaluation memory file: Have all of the key and tuples the same size. Do they match the inputs and outputs definition in
     * the file.
     * 
     * @param inputs inputs (name, data type) expected
     * @param outputs outputs (name, data type) expected
     * @throws IOException if validation fails
     */
    void validateEvaluationMemory(SortedMap<String, DataType> inputs, SortedMap<String, DataType> outputs) throws IOException;
}
