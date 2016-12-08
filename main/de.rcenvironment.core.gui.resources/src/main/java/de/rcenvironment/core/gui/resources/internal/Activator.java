/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.gui.resources.internal;

import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.gui.resources.api.ColorManager;
import de.rcenvironment.core.gui.resources.api.ComponentImageManager;
import de.rcenvironment.core.gui.resources.api.FontManager;
import de.rcenvironment.core.gui.resources.api.ImageManager;

/**
 * Standard bundle activator.
 * 
 * @author Robert Mischke
 */
public class Activator implements BundleActivator {

    /**
     * Creates an {@link ImageManager} on startup.
     * 
     * @param bundleContext (not used)
     */
    @Override
    public void start(BundleContext bundleContext) {
        synchronized (ImageManager.class) {
            ImageManager instance = ImageManager.getInstance();
            if (instance != null) {
                throw new IllegalStateException("Image manager already present");
            }
            ImageManager.setInstance(new ImageManagerImpl());
        }
        synchronized (FontManager.class) {
            FontManager instance = FontManager.getInstance();
            if (instance != null) {
                throw new IllegalStateException("Font manager already present");
            }
            FontManager.setInstance(new FontManagerImpl());
        }
        synchronized (ColorManager.class) {
            ColorManager instance = ColorManager.getInstance();
            if (instance != null) {
                throw new IllegalStateException("Color manager already present");
            }
            ColorManager.setInstance(new ColorManagerImpl());
        }
        
        synchronized (ComponentImageManager.class) {
            //TODO add check if component image manger is already present, see above ^
            ComponentImageManager.getInstance();
        }
    }

    /**
     * Disposes the previously created {@link ImageManager} on shutdown.
     * 
     * @param bundleContext (not used)
     */
    @Override
    public void stop(BundleContext bundleContext) {
        synchronized (ImageManager.class) {
            ImageManager oldInstance = ImageManager.getInstance();
            if (oldInstance == null) {
                // this can happen if initialization failed, so don't throw another exception
                LogFactory.getLog(getClass()).warn("No image manager present on shutdown");
                return;
            }
            ImageManager.setInstance(null);
            ((ImageManagerImpl) oldInstance).dispose();
        }
        synchronized (FontManager.class) {
            FontManager oldInstance = FontManager.getInstance();
            if (oldInstance == null) {
                // this can happen if initialization failed, so don't throw another exception
                LogFactory.getLog(getClass()).warn("No font manager present on shutdown");
                return;
            }
            ImageManager.setInstance(null);
            ((FontManagerImpl) oldInstance).dispose();
        }
        synchronized (ColorManager.class) {
            ColorManager oldInstance = ColorManager.getInstance();
            if (oldInstance == null) {
                // this can happen if initialization failed, so don't throw another exception
                LogFactory.getLog(getClass()).warn("No color manager present on shutdown");
                return;
            }
            ColorManager.setInstance(null);
            ((ColorManagerImpl) oldInstance).dispose();
        }
    }

}
