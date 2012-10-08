/**
 * Mule Loggly Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.loggly;

import org.apache.log4j.Logger;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.modules.loggly.async.AsyncWorkManager;
import org.mule.modules.loggly.async.WorkManager;

import javax.annotation.PostConstruct;

/**
 * Loggly Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Module(name="loggly", schemaVersion="1.0", friendlyName = "Loggly")
public class LogglyConnector
{
    private static final Logger LOGGER = Logger.getLogger(LogglyConnector.class);

    /**
     * The work manager that handles the messages to log.
     */
    private WorkManager workManager;

    @Stop
    public void end() {
        /* Shutdown. Close the work manager. */
        this.workManager.stop();
    }

    /**
     * Input Key to access the log-sending API.
     */
    @Configurable
    private String inputKey;

    @PostConstruct
    public void init() {
        this.workManager = new AsyncWorkManager(inputKey);
    }

    /**
     * Logs a message into a previously configured Loggly account.
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:logger}
     *
     * @param message Message to log
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    public void logger(String message) throws Exception {
        this.workManager.send(message);
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }


}