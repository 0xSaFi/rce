/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.gui.workflow.editor.commands.endpoint;

import java.util.HashMap;
import java.util.Map;

import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.gui.workflow.editor.properties.Refreshable;

/**
 * Adds one single input, another input with given suffix and one outputs with the same name as the
 * input.
 * 
 * @author Sascha Zur
 */
public class AddDynamicInputWithAnotherInputAndOutputCommand extends AddDynamicInputWithOutputCommand {

    private final String inputNameSuffix;

    private final String groupForOtherInput;

    private Map<String, String> metaDataInputWithSuffix;

    public AddDynamicInputWithAnotherInputAndOutputCommand(String dynamicEndpointId, String name, DataType type,
        Map<String, String> metaData, String inputNameSuffix, String groupForOtherInput,
        Refreshable... panes) {
        super(dynamicEndpointId, name, type, metaData, panes);
        this.inputNameSuffix = inputNameSuffix;
        this.groupForOtherInput = groupForOtherInput;
        this.metaDataInputWithSuffix = new HashMap<>();
        metaDataInputWithSuffix.putAll(metaData);
    }

    @Override
    public void execute() {
        InputWithOutputsCommandUtils.addInputWithSuffix(getProperties(), dynEndpointId, name, type, inputNameSuffix,
            groupForOtherInput, metaDataInputWithSuffix);
        super.execute();
    }

    @Override
    public void undo() {
        InputWithOutputsCommandUtils.removeInputWithSuffix(getProperties(), name, inputNameSuffix);
        super.undo();
    }

    /**
     * Adds the given meta data to the current ones.
     * 
     * @param additionalMetaDataInputWithSuffix to add.
     */
    public void addMetaDataToInputWithSuffix(Map<String, String> additionalMetaDataInputWithSuffix) {
        this.metaDataInputWithSuffix.putAll(additionalMetaDataInputWithSuffix);
    }

}
