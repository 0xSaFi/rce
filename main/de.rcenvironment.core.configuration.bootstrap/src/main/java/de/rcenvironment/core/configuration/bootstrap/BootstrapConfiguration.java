/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.configuration.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.rcenvironment.core.configuration.bootstrap.profile.BaseProfile;
import de.rcenvironment.core.configuration.bootstrap.profile.Profile;
import de.rcenvironment.core.configuration.bootstrap.profile.ProfileException;
import de.rcenvironment.core.configuration.bootstrap.profile.ProfileUtils;
import de.rcenvironment.core.configuration.bootstrap.ui.ProfileSelectionUI;
import de.rcenvironment.core.utils.common.StringUtils;

/**
 * Helper class that chooses the profile directory and related paths to use, based on command-line or .ini file parameters.
 * 
 * @author Robert Mischke
 * @author Oliver Seebach
 * @author Tobias Brieden
 */
public final class BootstrapConfiguration {

    /**
     * System property for exit code 1 on locked profile.
     */
    public static final String DRCE_LAUNCH_EXIT_ON_LOCKED_PROFILE = "rce.launch.exitOnLockedProfile";

    /**
     * Standard OSGi "osgi.install.area" property.
     * 
     * Note that (contrary to what the name suggests) this is *not* the actual installation directory which, for example, the /plugin and
     * /feature directories are located in. Instead, this property points to the /configuration directory inside that installation
     * directory.
     */
    public static final String SYSTEM_PROPERTY_OSGI_INSTALL_AREA = "osgi.install.area";

    private static final String USING_SHUTDOWN_PROFILE = "Using shutdown profile.";

    private static final String USING_FALLBACK_PROFILE = "Using fallback profile.";

    private static final String FAILED_TO_LOCK_PROFILE_TEMPLATE =
        "Failed to lock profile directory %s - most likely, another instance is already using it.";

    private static final String NO_LOCK_ON_FALLBACK_TEMPLATE =
        "Could not acquire a lock on the fallback profile directory %s either - giving up";

    private static final String FALLBACK_PROFILE_IS_DISABLED_SHUTTING_DOWN = "Fallback profile is disabled, shutting down.";

    private static final String PROFILE_OPTION_HINT = " (use -p/--profile <id or path> to override)";

    private static final String NEWER_PROFILE_VERSION_TEMPLATE =
        "The required version of the profile directory is %d"
            + " but the profile directory's current version is newer. Most likely, this is the case "
            + " because it has been used with a newer RCE version before. As downgrading of profiles is not supported,"
            + " the configured profile directory cannot be used with this RCE version."
            + " Choose another profile directory. (See the user guide for more information about the profile directory.)";

    private static final String PROFILE_OPTION_LONG_KEY = "--profile";

    private static final String PROFILE_OPTION_SHORT_KEY = "-p";

    // note: not using the singleton pattern so it can be reset by unit tests - misc_ro
    private static volatile BootstrapConfiguration instance;

    private final Profile originalProfile;

    private final Profile finalProfile;

    private final boolean shutdownRequested;

    private String profileOptionHintToPrint;

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Performs the bootstrap profile initialization.
     * 
     * @throws ParameterException
     * @throws ProfileException
     * @throws SystemExitException
     * @throws BootstrapException
     * 
     * @throws IOException on bootstrap profile path errors
     */
    private BootstrapConfiguration() throws ProfileException, ParameterException, SystemExitException {

        // TODO which of these calls should be replaced by log calls?
        // circumvent CheckStyle rule to generate basic output before the log system is initialized
        PrintStream stderr = System.err;
        PrintStream stdout = System.out;

        LaunchParameters launchParameters = LaunchParameters.getInstance();

        originalProfile = determineOriginalProfileDir(launchParameters);

        Profile preliminaryProfile = originalProfile;

        // For headless mode, fallback profile is automatically disabled.
        final boolean fallbackProfileDisabled =
            System.getProperties().containsKey(DRCE_LAUNCH_EXIT_ON_LOCKED_PROFILE) || launchParameters.containsToken("--headless")
                || launchParameters.containsToken("--batch");

        // In case of error either start in fallback profile or don't start
        if (!originalProfile.hasValidVersion()) {
            if (fallbackProfileDisabled) {
                // fail if the fallback profile is disabled
                log.error(StringUtils.format(NEWER_PROFILE_VERSION_TEMPLATE, Profile.PROFILE_VERSION_NUMBER));
                stderr.println(StringUtils.format(NEWER_PROFILE_VERSION_TEMPLATE, Profile.PROFILE_VERSION_NUMBER));
                throw new SystemExitException(0);
            } else {
                // else go on in the process with the fallback profile; instance validator will inform the user and force shutdown
                stderr.println(USING_FALLBACK_PROFILE);
                preliminaryProfile = ProfileUtils.getFallbackProfile();
            }
        }

        shutdownRequested = launchParameters.containsToken("--shutdown");
        if (shutdownRequested) {
            // the stub profile location for the process sending the shutdown signal is located in the data sub-directory
            preliminaryProfile = new Profile(new File(originalProfile.getInternalDirectory(), "shutdown"), true, true);
            stdout.println(USING_SHUTDOWN_PROFILE);
        }

        if (preliminaryProfile.attemptToLockProfileDirectory()) {
            finalProfile = preliminaryProfile;
        } else {
            stderr.println(StringUtils.format(FAILED_TO_LOCK_PROFILE_TEMPLATE, preliminaryProfile.getProfileDirectory()));
            if (fallbackProfileDisabled) {
                // If the fallback profile is disabled, shut down ...
                log.error(FALLBACK_PROFILE_IS_DISABLED_SHUTTING_DOWN);
                stderr.println(FALLBACK_PROFILE_IS_DISABLED_SHUTTING_DOWN);
                throw new SystemExitException(0);
            } else {
                // ... else try to create a fallback profile directory
                preliminaryProfile = ProfileUtils.getFallbackProfile();
                stderr.println(USING_FALLBACK_PROFILE);

                if (preliminaryProfile.attemptToLockProfileDirectory()) {
                    finalProfile = preliminaryProfile;
                } else {
                    throw new ProfileException(StringUtils.format(NO_LOCK_ON_FALLBACK_TEMPLATE, preliminaryProfile.getProfileDirectory()));
                }
            }
        }

        // if the user specified profile directory is used, print a modified profile option hint
        if (finalProfile.equals(originalProfile)) {
            stdout.println(StringUtils.format("Using profile directory %s %s", finalProfile.getProfileDirectory().getAbsolutePath(),
                profileOptionHintToPrint));
        }

        // mark the selected profile as recently used, but only if it neither a shutdown profile nor a fallback profile
        if (finalProfile.equals(originalProfile)) {
            try {
                originalProfile.markAsRecentlyUsed();
            } catch (ProfileException e) {
                // catch this exception. otherwise we could not start anymore only because the profile could not be marked correctly.
                log.warn("Unable to mark the profile as recently used.", e);
            }
        }

        loggingSetup();
    }

    /**
     * Initializes the singleton instance from system properties and launch parameters.
     * 
     * @throws ParameterException re-thrown
     * @throws ProfileException re-thrown
     * @throws SystemExitException re-thrown
     */
    public static void initialize() throws ProfileException, ParameterException, SystemExitException {
        instance = new BootstrapConfiguration();
    }

    /**
     * @return the singleton instance
     */
    public static BootstrapConfiguration getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No " + BootstrapConfiguration.class.getSimpleName()
                + " instance available - most likely, its containing bundle has not been properly initialized");
        }
        return instance;
    }

    /**
     * Determines which profile should be used as the original profile:
     * 
     * 1. Absolute or relative profile folder specified using the -p command line option
     * 
     * 2. Profile selected using the Profile Selection Dialog
     * 
     * 3. Fall back to the default profile
     * 
     * @throws SystemExitException Thrown if the Profile Selection Dialog was exited without a selection.
     */
    private Profile determineOriginalProfileDir(LaunchParameters launchParams)
        throws ProfileException, ParameterException, SystemExitException {

        String profilePath = null;

        // 1.
        // can be null if none of the options is used
        profilePath = launchParams.getNamedParameter(PROFILE_OPTION_SHORT_KEY, PROFILE_OPTION_LONG_KEY);

        if (profilePath != null) {
            this.profileOptionHintToPrint = "(as specified by the -p/--profile option)";
        } else {
            // 2.
            // if no argument was provided, but the option was still present, we start the profile selection dialog
            if (launchParams.containsToken(PROFILE_OPTION_SHORT_KEY, PROFILE_OPTION_LONG_KEY)) {
                Profile selectedProfile = new ProfileSelectionUI().run();

                // if no profile was selected we should exit completely
                if (selectedProfile == null) {
                    throw new SystemExitException(0);
                } else {
                    profilePath = selectedProfile.getProfileDirectory().getAbsolutePath();
                    this.profileOptionHintToPrint = "(as specified by the profile selection dialog)";
                }
            }
        }

        // 3.
        if (profilePath == null) {
            this.profileOptionHintToPrint = PROFILE_OPTION_HINT;
            // TODO it would be nice if different hints could be displayed depending on how the default was selected
            profilePath = ProfileUtils.getDefaultProfilePath().getAbsolutePath();
        }

        File configuredPath = new File(profilePath);
        File profileDir;
        if (configuredPath.isAbsolute()) {
            profileDir = configuredPath;
        } else {
            File profilesRootDirectory = ProfileUtils.getProfilesParentDirectory();
            profileDir = new File(profilesRootDirectory, profilePath).getAbsoluteFile();
        }

        return new Profile(profileDir, true, true);
    }

    /**
     * Before the final profile is known, all log messages are written to a startup log file in the common profile directory. As soon as the
     * final profile is known, the logging will be reconfigured to write new log messages into log files within the profile directory.
     * Furthermore, all logged messages from the old log file will be copied to the start of the new log file.
     */
    private void loggingSetup() {

        // deletes the old previous log and renames the existing old log to the new previous log
        LogArchiver.run(finalProfile.getProfileDirectory());
        String logfilesPrefix = "";
        if (shutdownRequested) {
            logfilesPrefix = "shutdown-";
        }
        LoggingReconfigurationHelper.reconfigure(finalProfile.getProfileDirectory(), logfilesPrefix);
    }

    public File getProfileDirectory() {
        return finalProfile.getProfileDirectory();
    }

    public Profile getProfile() {
        return finalProfile;
    }

    /**
     * Low-level access to the storage path for internal data. This method is intended for classes that need to remain independent of the
     * configuration service. Other classes should fetch the path from there.
     * 
     * @return the location for internal data files; default: "<profile dir>/internal"
     */
    public File getInternalDataDirectory() {
        return finalProfile.getInternalDirectory();
    }

    /**
     * Deletes the internal data directory if it is empty.
     * 
     * @return true if and only if the file or directory is successfully deleted
     */
    public boolean deleteInternalDataDirectoryIfEmpty() {

        // TODO the existence of this profile version file is an implementation detail of BaseProfile and therefore its deletion shouln't be
        // handled here.

        // delete the profile.version file within the internal data directory, this should be the only file in there
        new File(getInternalDataDirectory(), BaseProfile.PROFILE_VERSION_FILE_NAME).delete();

        return this.getInternalDataDirectory().delete();
    }

    public Profile getOriginalProfile() {
        return originalProfile;
    }

    public boolean isShutdownRequested() {
        return shutdownRequested;
    }

    /**
     * @return the shutdown.dat location for the process sending the shutdown signal is within its own profile directory
     */
    public File getOwnShutdownDataDirectory() {
        return finalProfile.getInternalDirectory();
    }

    /**
     * @return The location of the shutdown.dat of the process which should be terminated.
     */
    public File getTargetShutdownDataDirectory() {
        return originalProfile.getInternalDirectory();
    }

    // TODO move this method out of this class?
    /**
     * @return The path to the installation directory as defined through the osgi.install.area property.
     */
    public static File getInstallationDir() {
        String osgiInstallArea = System.getProperty(SYSTEM_PROPERTY_OSGI_INSTALL_AREA);
        if (osgiInstallArea != null) {
            String installationLocationPath = osgiInstallArea.replace("file:", "");

            File installationLocation = new File(installationLocationPath);
            if (installationLocation.isDirectory()) {
                // success
                return installationLocation.getAbsoluteFile();
            } else {
                throw new IllegalStateException("Property '" + SYSTEM_PROPERTY_OSGI_INSTALL_AREA
                    + "' is defined but does not point to a directory");
            }
        } else {
            throw new IllegalStateException("Property '" + SYSTEM_PROPERTY_OSGI_INSTALL_AREA
                + "' is null when it is required to determine the installation data directory");
        }
    }
}
