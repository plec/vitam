/**
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2019)
 * <p>
 * contact.vitam@culture.gouv.fr
 * <p>
 * This software is a computer program whose purpose is to implement a digital archiving back-office system managing
 * high volumetry securely and efficiently.
 * <p>
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 * <p>
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 * <p>
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */

package fr.gouv.vitam.storage.engine.server.rest;

import static java.lang.String.format;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import fr.gouv.vitam.common.exception.VitamApplicationServerException;
import fr.gouv.vitam.common.exception.VitamException;
import fr.gouv.vitam.common.logging.VitamLogger;
import fr.gouv.vitam.common.logging.VitamLoggerFactory;
import fr.gouv.vitam.common.security.waf.WafFilter;
import fr.gouv.vitam.common.server.VitamServer;
import fr.gouv.vitam.common.server.VitamServerFactory;
import fr.gouv.vitam.common.server.application.AbstractVitamApplication;

/**
 * Storage web application TODO refacto config
 */
public final class StorageApplication extends AbstractVitamApplication<StorageApplication, StorageConfiguration> {
    private static final String STORAGE_APPLICATION_STARTS_ON_DEFAULT_PORT =
        "StorageApplication Starts on default port";
    private static final VitamLogger LOGGER = VitamLoggerFactory.getInstance(StorageApplication.class);
    private static final String STORAGE_CONF_FILE_NAME = "storage-engine.conf";
    private static final String MODULE_NAME = "Storage-Engine";
    // TODO vitamServer must be instance variable to run test suites in parallel.
    private static VitamServer vitamServer;

    /**
     * StorageApplication constructor
     */
    protected StorageApplication() {
        super(StorageApplication.class, StorageConfiguration.class);
    }

    /**
     * Main method to run the application (doing start and join)
     *
     * @param args command line parameters
     * @throws IllegalStateException if the Vitam server cannot be launched
     */
    public static void main(String[] args) {
        try {
            if (args == null || args.length == 0) {
                LOGGER.error(format(VitamServer.CONFIG_FILE_IS_A_MANDATORY_ARGUMENT, STORAGE_CONF_FILE_NAME));
                throw new IllegalArgumentException(format(VitamServer.CONFIG_FILE_IS_A_MANDATORY_ARGUMENT,
                    STORAGE_CONF_FILE_NAME));
            }
            startApplication(args[0]);
            if (vitamServer != null && vitamServer.isStarted()) {
                vitamServer.join();
            }
        } catch (final Exception e) {
            LOGGER.error(format(VitamServer.SERVER_CAN_NOT_START, MODULE_NAME) + e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Prepare the application to be run or started.
     *
     * @param args the list of arguments as an array of strings
     * @return the VitamServer
     * @throws IllegalStateException if the server cannot be configured, meaning there are problem with the
     *         configuration
     */
    public static void startApplication(String configFile) throws VitamException {
        try {
            final StorageApplication application = new StorageApplication();
            application.configure(application.computeConfigurationPathFromInputArguments(configFile));
            run(application.getConfiguration());

        } catch (final VitamApplicationServerException e) {
            LOGGER.error(format(VitamServer.SERVER_CAN_NOT_START, MODULE_NAME) + e.getMessage(), e);
            throw new VitamException(format(VitamServer.SERVER_CAN_NOT_START, MODULE_NAME) + e.getMessage(), e);
        }
    }

    /**
     * run a server instance with the configuration only
     *
     * @param configuration as StorageConfiguration {@link StorageConfiguration}
     * @throws VitamApplicationServerException when server does'nt launched
     */

    public static void run(StorageConfiguration configuration) throws VitamApplicationServerException {
        final ServletContextHandler context = getStorageServletContext(configuration);
        String jettyConfig = configuration.getJettyConfig();
        vitamServer = VitamServerFactory.newVitamServerByJettyConf(jettyConfig);
        vitamServer.configure(context);

        try {
            // 1- create new serverConnector
            // ServerConnector http = new ServerConnector(vitamServer.getServer(), new
            // HttpConnectionFactory(httpConfig));
            // http.setHost(System.getProperty("jetty.host"));
            // http.setPort(Integer.valueOf(System.getProperty("jetty.port", "8080")));
            // http.setIdleTimeout(Integer.valueOf(System.getProperty("http.timeout", "30000")));
            // vitamServer.getServer().addConnector(http);
            // or
            // 2- update the connector
            // Connector[] https = vitamServer.getServer().getConnectors();
            // ((ServerConnector) https[0]).setPort(Integer.valueOf(System.getProperty("jetty.port", "8080")));

            vitamServer.getServer().start();
        } catch (Exception e) {
            LOGGER.error(format(VitamServer.SERVER_CAN_NOT_START, MODULE_NAME) + e.getMessage(), e);
            throw new VitamApplicationServerException(
                format(VitamServer.SERVER_CAN_NOT_START, MODULE_NAME) + e.getMessage(), e);
        }
    }

    private static ServletContextHandler getStorageServletContext(StorageConfiguration configuration) {
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);
        resourceConfig.register(new StorageResource(configuration));
        final ServletContainer servletContainer = new ServletContainer(resourceConfig);
        final ServletHolder sh = new ServletHolder(servletContainer);
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");

        context.addFilter(WafFilter.class, "/*", EnumSet.of(
            DispatcherType.INCLUDE, DispatcherType.REQUEST,
            DispatcherType.FORWARD, DispatcherType.ERROR));
        return context;
    }

    @Override
    protected Handler buildApplicationHandler() {
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);
        resourceConfig.register(new StorageResource(getConfiguration()));

        final ServletContainer servletContainer = new ServletContainer(resourceConfig);
        final ServletHolder sh = new ServletHolder(servletContainer);
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
        return context;
    }

    @Override
    protected String getConfigFilename() {
        return STORAGE_CONF_FILE_NAME;
    }


    /**
     * Stops the vitam server
     * 
     * @throws Exception
     */
    public static void stop() throws Exception {
        if (vitamServer != null && vitamServer.isStarted()) {
            vitamServer.stop();
        }
    }
}