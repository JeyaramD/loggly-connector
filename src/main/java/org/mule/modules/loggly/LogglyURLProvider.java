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

public class LogglyURLProvider {
    public static final String HTTPS_LOGS_LOGGLY_INPUTS = "https://logs.loggly.com/inputs/";
//    public static final String HTTPS_LOGS_LOGGLY_INPUTS = "http://localhost:8000/logs.loggly.com/inputs/";
    public static final String BASE_URL = "https://{subdomain}.loggly.com/api/";
//    public static final String BASE_URL = "http://localhost:8000/{subdomain}.loggly.com/api/";
}
