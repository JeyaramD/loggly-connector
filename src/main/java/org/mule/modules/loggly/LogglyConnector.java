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
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.loggly.async.AsyncWorkManager;
import org.mule.modules.loggly.async.WorkManager;

import javax.annotation.PostConstruct;

/**
 * Loggly Async Logger </p> This Module handles logging of messages. The policy
 * that is used is asynchronous, this means that each message to log is stored
 * in a Circular Queue of a fixed size. Once the maximum size of the queue is
 * reached the first element will be discarded and replaced with the new logged
 * message.
 * 
 * @author MuleSoft, Inc.
 */
@Module(name = "loggly", schemaVersion = "1.4", friendlyName = "Loggly", configElementName = "config-logger")
public class LogglyConnector {
	private static final Logger LOGGER = Logger
			.getLogger(LogglyConnector.class);

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
	 * Inputs URL to access the log-sending API.
	 *  eg. https://logs-01.loggly.com/inputs/
	 */
	@Configurable
	@Default("https://logs-01.loggly.com/inputs/")
	private String inputsURL;

	/**
	 * Input Key to access the log-sending API.
	 */
	@Configurable
	private String inputKey;

	/**
	 * Comma seperated tags to tag published logs. 
	 * eg. tag/mule,esb
	 */
	@Configurable
	@Default("tag/mule,esb")
	private String tags;

	@PostConstruct
	public void init() {
		this.workManager = new AsyncWorkManager(inputsURL, inputKey, tags);
	}

	/**
	 * Logs a message into a previously configured Loggly account.
	 * <p/>
	 * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:logger}
	 * 
	 * @param message
	 *            Message to log
	 * @throws Exception
	 *             error while doing the request and receiving the response
	 */
	@Processor
	public void logger(@Default("#[payload]")String message) throws Exception {
		LOGGER.info(message);
		this.workManager.send(message);
	}

	public String getInputsURL() {
		return inputsURL;
	}

	public void setInputsURL(String inputsURL) {
		this.inputsURL = checkAndAddFowardSlash(inputsURL);
	}

	public String getInputKey() {
		return inputKey;
	}

	public void setInputKey(String inputKey) {
		this.inputKey = inputKey;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	
	private String checkAndAddFowardSlash(String str)
	{
		if(str.charAt(str.length() - 1) == '/')
			return str + "/";
		return str;
	}
}