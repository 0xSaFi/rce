/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.integration.cpacs;

import de.rcenvironment.core.datamodel.api.EndpointType;
import de.rcenvironment.core.gui.workflow.editor.properties.EndpointPropertySection;
import de.rcenvironment.core.gui.workflow.editor.properties.Messages;
import de.rcenvironment.core.gui.xpathchooser.XPathChooserPropertyViewPane;

/**
 * {@link EndpointPropertySection} for all custom integrated CPACS tools.
 * 
 * @author Doreen Seider
 */
public class CpacsIntegrationEndpointPropertySection extends EndpointPropertySection {

    public CpacsIntegrationEndpointPropertySection() {
        super();
        XPathChooserPropertyViewPane inputPane = new XPathChooserPropertyViewPane(
            Messages.inputs, EndpointType.INPUT, "default", new String[] {}, null, this);
        XPathChooserPropertyViewPane outputPane = new XPathChooserPropertyViewPane(
            Messages.outputs, EndpointType.OUTPUT, "default", new String[] {}, null, this);
        setColumns(2);
        setPanes(inputPane, outputPane);
    }
}
