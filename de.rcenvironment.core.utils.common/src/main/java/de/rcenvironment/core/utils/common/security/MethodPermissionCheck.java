/*
 * Copyright 2006-2019 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.utils.common.security;

import java.lang.reflect.Method;

/**
 * A callback for custom permission checks before invoking a method.
 * 
 * @author Robert Mischke
 */
public interface MethodPermissionCheck {

    /**
     * A callback that checks whether access to a method should be granted.
     * 
     * @param method the requested method
     * @return true if access should be granted, false if not
     */
    boolean checkPermission(Method method);
}
