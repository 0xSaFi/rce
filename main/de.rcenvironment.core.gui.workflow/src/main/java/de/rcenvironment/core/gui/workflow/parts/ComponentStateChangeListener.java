/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.workflow.parts;

import java.io.Serializable;

import de.rcenvironment.core.component.api.ComponentConstants;
import de.rcenvironment.core.notification.DefaultNotificationSubscriber;
import de.rcenvironment.core.notification.Notification;
import de.rcenvironment.core.notification.NotificationSubscriber;


/**
 * {@link NotificationSubscriber} for {@link ComponentState} changes. 
 *
 * @author Christian Weiss
 */
public class ComponentStateChangeListener extends DefaultNotificationSubscriber {

    private static final long serialVersionUID = -5025502558454267143L;

    private transient WorkflowRunNodePart part;
    
    public ComponentStateChangeListener(WorkflowRunNodePart newPart) {
        part = newPart;
    }
    
    @Override
    public Class<? extends Serializable> getInterface() {
        return NotificationSubscriber.class;
    }

    @Override
    public void processNotification(Notification notification) {
        if (notification.getHeader().getNotificationIdentifier().contains(ComponentConstants.STATE_NOTIFICATION_ID_PREFIX)) {
            part.handleStateNotification(notification);            
        } else if (notification.getHeader().getNotificationIdentifier().contains(ComponentConstants
            .ITERATION_COUNT_NOTIFICATION_ID_PREFIX)) {
            part.handleExecutionCountNotification(notification);
        }
    }
}
