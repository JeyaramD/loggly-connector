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

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.rest.RestHttpClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Loggly Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Module(name="loggly", schemaVersion="1.0", friendlyName = "Loggly")
public class LogglyConnector
{
    private static final Logger LOGGER = Logger.getLogger(LogglyConnector.class);

    private WorkManager workManager;

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    @PreDestroy
    public void end() {
        this.workManager.stop();
    }

    /**
     * Input Key
     */
    @Configurable
    @Optional
    private String inputKey;

    /**
     * REST Http Client
     */
    @RestHttpClient
    private HttpClient httpClient = new HttpClient();

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
     * @return Status code
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    public void logger(String message) throws Exception {
        this.workManager.send(message);
    }
}