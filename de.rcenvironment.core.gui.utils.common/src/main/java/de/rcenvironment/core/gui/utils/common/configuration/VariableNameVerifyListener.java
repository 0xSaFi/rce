/*
 * Copyright 2006-2019 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.utils.common.configuration;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Verifies that endpoint names only contain characters a..z, A..Z, 0..9, _ or blank.
 * 
 * @author Sascha Zur
 */
public class VariableNameVerifyListener implements Listener {

    /**
     * Option if input should not begin with a number.
     */
    public static final int NO_LEADING_NUMBERS = 1;

    /**
     * Option if input should not have underscores.
     */
    public static final int NO_UNDERSCORE = 2;

    /**
     * Option for no restriction.
     */
    public static final int NONE = 0;

    private int function = NONE;

    private Text text;

    private boolean allowBlank;

    public VariableNameVerifyListener(boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    public VariableNameVerifyListener(int function, Text text) {
        this.function = function;
        this.text = text;
    }

    @Override
    public void handleEvent(Event arg0) {

        String string = arg0.text;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') && !(c >= '0' && c <= '9')
                && !(c == '_') && (!allowBlank && c == ' ')) {
                arg0.doit = false;
                return;
            }

            if ((function & NO_LEADING_NUMBERS) > 0) {
                if (text.getCaretPosition() == 0 && Character.isDigit(string.charAt(0))) {
                    arg0.doit = false;
                    return;
                }
            }
            if ((function & NO_UNDERSCORE) > 0) {
                if (c == '_') {
                    arg0.doit = false;
                    return;
                }
            }
        }
    }
}
