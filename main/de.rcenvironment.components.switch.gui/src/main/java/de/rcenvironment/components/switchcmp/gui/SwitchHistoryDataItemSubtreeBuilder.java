/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.switchcmp.gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import de.rcenvironment.components.switchcmp.common.SwitchComponentConstants;
import de.rcenvironment.components.switchcmp.common.SwitchComponentHistoryDataItem;
import de.rcenvironment.core.datamodel.api.TypedDatumSerializer;
import de.rcenvironment.core.datamodel.api.TypedDatumService;
import de.rcenvironment.core.gui.datamanagement.browser.spi.CommonHistoryDataItemSubtreeBuilderUtils;
import de.rcenvironment.core.gui.datamanagement.browser.spi.ComponentHistoryDataItemSubtreeBuilder;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNode;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNodeType;
import de.rcenvironment.core.gui.resources.api.ImageManager;
import de.rcenvironment.core.gui.resources.api.StandardImages;
import de.rcenvironment.core.utils.incubator.ServiceRegistry;
import de.rcenvironment.core.utils.incubator.ServiceRegistryAccess;

/**
 * 
 * Implementation of {@link ComponentHistoryDataItemSubtreeBuilder} for this component.
 *
 * @author David Scholz
 * @author Doreen Seider
 */
public class SwitchHistoryDataItemSubtreeBuilder implements ComponentHistoryDataItemSubtreeBuilder {

    private static final Image COMPONENT_ICON;

    static {
        String iconPath = "platform:/plugin/de.rcenvironment.components.switch.common/resources/switch_16.png";
        URL url = null;
        try {
            url = new URL(iconPath);
        } catch (MalformedURLException e) {
            LogFactory.getLog(SwitchHistoryDataItemSubtreeBuilder.class).error("Component icon not found: " + iconPath);
        }
        if (url != null) {
            COMPONENT_ICON = ImageDescriptor.createFromURL(url).createImage();
        } else {
            COMPONENT_ICON = null;
        }
    }
    
    @Override
    public String[] getSupportedHistoryDataItemIdentifier() {
        return new String[] { SwitchComponentConstants.COMPONENT_ID };
    }

    @Override
    public Serializable deserializeHistoryDataItem(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (Serializable) ois.readObject();
    }

    @Override
    public void buildInitialHistoryDataItemSubtree(Serializable historyDataItem, DMBrowserNode parentNode) {
        ServiceRegistryAccess registryAccess = ServiceRegistry.createAccessFor(this);
        TypedDatumSerializer serializer = registryAccess.getService(TypedDatumService.class).getSerializer();

        if (historyDataItem instanceof String) {
            SwitchComponentHistoryDataItem historyData;
            try {
                historyData = SwitchComponentHistoryDataItem.fromString((String) historyDataItem,
                    serializer, SwitchComponentConstants.COMPONENT_ID);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            CommonHistoryDataItemSubtreeBuilderUtils.buildDefaultHistoryDataItemSubtrees(historyData, parentNode);
            DMBrowserNode conditionNode = DMBrowserNode.addNewChildNode("Condition", DMBrowserNodeType.Custom, parentNode);
            conditionNode.setIcon(ImageManager.getInstance().getSharedImage(StandardImages.QUESTION_MARK_16));
            DMBrowserNode actualConditionnode = DMBrowserNode.addNewLeafNode(
                "Actual: " + StringUtils.abbreviate(historyData.getActualCondition(),
                    CommonHistoryDataItemSubtreeBuilderUtils.MAX_LABEL_LENGTH), DMBrowserNodeType.CommonText, conditionNode);
            actualConditionnode.setFileContentAndName(historyData.getActualCondition(), "Actual condition");
            DMBrowserNode templateConditionnode = DMBrowserNode.addNewLeafNode(
                "Pattern: " + StringUtils.abbreviate(historyData.getConditionPattern(),
                    CommonHistoryDataItemSubtreeBuilderUtils.MAX_LABEL_LENGTH), DMBrowserNodeType.CommonText, conditionNode);
            templateConditionnode.setFileContentAndName(historyData.getConditionPattern(), "Condition pattern");
        } else {
            String exceptionInformationText = "";
            if (historyDataItem != null) {
                exceptionInformationText = String.format("Parsing history data point failed: Expected type %s, but was of type %s",
                    String.class.getCanonicalName(),
                    historyDataItem.getClass().getCanonicalName());
            } else {
                exceptionInformationText = String.format("Parsing history data point failed: Expected type %s, actual type not available.",
                    String.class.getCanonicalName());
            }
            throw new IllegalArgumentException(exceptionInformationText);
        }

    }

    @Override
    public Image getComponentIcon(String historyDataItemIdentifier) {
        return COMPONENT_ICON;
    }

}
