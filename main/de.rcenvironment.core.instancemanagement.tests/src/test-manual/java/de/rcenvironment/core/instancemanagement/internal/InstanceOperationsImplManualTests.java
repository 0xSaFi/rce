/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.instancemanagement.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.rcenvironment.core.utils.testing.ParameterizedTestUtils;
import de.rcenvironment.core.utils.testing.TestParametersProvider;

/**
 * Integration tests for {@link InstanceOperationsImpl} that are not intended to be run within the automated test battery, usually because
 * it requires certain external resources.
 * 
 * @author Robert Mischke
 */
public class InstanceOperationsImplManualTests {

    private TestParametersProvider testParameters;

    /**
     * Common setup.
     * 
     * @throws Exception on uncaught exceptions
     */
    @Before
    public void setUp() throws Exception {
        testParameters = new ParameterizedTestUtils().readDefaultPropertiesFile(getClass());
    }

    /**
     * Common teardown.
     * 
     * @throws Exception on uncaught exceptions
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Tests the basic start/stop cycle with a provided external installation.
     * 
     * @throws IOException on uncaught exceptions
     */
    @Test
    public void startStopRoundTrip() throws IOException {

        // must exist
        final File installationDir = testParameters.getExistingDir("startStopRoundTrip.installationDir");
        // may exist
        final File profileDir = testParameters.getDefinedFileOrDir("startStopRoundTrip.profileDir");
        List<File> profileDirList = new ArrayList<>();
        profileDirList.add(profileDir);
        // optional repetitions, e.g. for stability testing
        final int repetitions = testParameters.getOptionalInteger("startStopRoundTrip.repetitions", 1);

        assertTrue(profileDir.getPath(), profileDir.isAbsolute()); // doesn't have to exist, but should be absolute for reliable starting
        assertTrue(installationDir.getPath(), installationDir.isAbsolute());
        assertTrue(installationDir.getPath(), installationDir.isDirectory());

        InstanceOperationsImpl instanceOperations = new InstanceOperationsImpl();

        assertFalse(instanceOperations.isProfileLocked(profileDir));

        for (int i = 0; i < repetitions; i++) {

            instanceOperations.startInstanceUsingInstallation(profileDirList, installationDir, 0, null, false);
            assertTrue("profile not locked after start", instanceOperations.isProfileLocked(profileDir));

            instanceOperations.shutdownInstance(profileDirList, 0, null);
            assertFalse("profile still locked after shutdown", instanceOperations.isProfileLocked(profileDir));
        }
    }
}
