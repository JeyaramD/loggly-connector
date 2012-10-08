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
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.rest.*;
import org.mule.modules.loggly.exceptions.LogglyRuntimeException;
import org.mule.modules.loggly.exceptions.TimeoutException;
import org.mule.modules.loggly.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * Loggly Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Module(name="loggly", schemaVersion="1.0", friendlyName = "Loggly Retrieve", configElementName = "config-retrieve")
public abstract class LogglyRetrieveConnector
{
    private static final int TIMEOUT = 7200;

    public static final String BASE_URL = LogglyURLProvider.BASE_URL;


    private int getPort() {
        String  url = BASE_URL;
        url = url.replace("{subdomain}", String.valueOf(getSubdomain()));
        try {
            return new URL(url).getPort();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHost() {
        String  url = BASE_URL;
        url = url.replace("{subdomain}", String.valueOf(getSubdomain()));
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logging Subdomain
     */
    @Configurable
    @RestUriParam("subdomain")
    private String subdomain;

    /**
     * Loggly Username
     */
    @Configurable
    private String username;

    /**
     * Loggly Password
     */
    @Configurable
    private String password;

    /**
     * REST Http Client
     */
    @RestHttpClient
    private HttpClient httpClient = new HttpClient();

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Start
    public void before() {
        httpClient.getState().setCredentials(new AuthScope(getHost(), getPort()), new UsernamePasswordCredentials(username,password));
    }

    /**
     * Searches for a log message.
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:search}
     *
     * @param query String to search in logs.

     * @param rows Number of rows returned by search. Defaults to 10, maximum of 2000.
     * @param start Offset for starting row. Defaults to 0.
     * @param from Start time for the search. Defaults to NOW-24HOURS.
     * @param until End time for the search. Defaults to NOW.
     * @param order Direction of results returned, either 'asc' or 'desc'. Defaults to 'desc'.
     *
     * @return Search results
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "search/", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[message.inboundProperties['http.status'] != 200]", exception = LogglyRuntimeException.class)
            })
    public abstract LogglyResultSet search(@RestQueryParam("q") String query,
                                           @RestQueryParam("rows") @Default("10") @Optional int rows,
                                           @RestQueryParam("start") @Default("0") @Optional int start,
                                           @RestQueryParam("from") @Default("NOW-24HOURS") @Optional String from,
                                           @RestQueryParam("until") @Default("NOW") @Optional String until,
                                           @RestQueryParam("order") @Default("ASC") @Optional Order order) throws Exception;

    /**
     * Provides faceted results from an account on either date, ip, or input fields. Facets return counts of events over a time range.
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:facet}
     *
     * @param query String to search in logs.
     * @param facetType Facet type to use.
     * @param from Start time for the search. Defaults to NOW-1HOUR.
     * @param until End time for the search. Defaults to NOW.
     * @param buckets Number of buckets the results are split into for a given time range. Defaults to 50.
     * @param gap Set the gap time between buckets. Defaults to +1HOUR
     *
     * @return Facet results
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "facets/{facet_type}", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[message.inboundProperties['http.status'] != 200]", exception = LogglyRuntimeException.class)
            })
    public abstract LogglyFacetResult facet(@RestQueryParam("q") String query,
                                            @RestUriParam("facet_type") FacetType facetType,
                                            @RestQueryParam("buckets") @Default("50") @Optional int buckets,
                                            @RestQueryParam("gap") @Default("+1HOUR") @Optional String gap,
                                            @RestQueryParam("from") @Default("NOW-1HOUR") @Optional String from,
                                            @RestQueryParam("until") @Default("NOW") @Optional String until) throws Exception;

    /**
     * Fetch an input for an account using its id. Without id
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:input}
     *
     * @param id Id of the input
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[message.inboundProperties['http.status'] != 200]", exception = LogglyRuntimeException.class)
            })
    public abstract Input input(@RestUriParam("id") int id) throws Exception;

    /**
     * Fetch an input for an account using its name.
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:input-by-name}
     *
     * @param inputName Name of the input
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[message.inboundProperties['http.status'] != 200]", exception = LogglyRuntimeException.class)
            })
    public abstract Input inputByName(@RestQueryParam("name") String inputName) throws Exception;

    /**
     * Fetch all the inputs associated with the account.
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:inputs}
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[message.inboundProperties['http.status'] != 200]", exception = LogglyRuntimeException.class)
            })
    public abstract Collection<Input> inputs() throws Exception;

    /**
     * Create an input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:create-input}
     *
     * @param inputName Name
     * @param description Description
     * @param service Service to use
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void createInput(@RestPostParam("name") String inputName,
                                     @RestPostParam("description") String description,
                                     @RestPostParam("service") Service service)throws Exception;

    /**
     * Add the current machine as a device to an existing input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:add-current-device}
     *
     * @param id Input ID
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}/adddevice/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void addCurrentDevice(@RestUriParam("id") int id) throws Exception;

    /**
     * Remove Device from Input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:remove-device}
     *
     * @param id Device ID
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}/removedevice/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void removeDevice(@RestUriParam("id") int id) throws Exception;

    /**
     * Add the current machine with Syslog 514 UDP as a device to an existing input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:add-current-device-to-syslog514}
     *
     * @param id Device ID
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}/add514/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void addCurrentDeviceToSyslog514(@RestUriParam("id") int id) throws Exception;

    /**
     * Puts the Input in discovery mode
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:enable-input-discovery}
     *
     * @param id Device ID
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}/discover/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void enableInputDiscovery(@RestUriParam("id") int id) throws Exception;

    /**
     * Disables discovery mode on the device
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:disable-input-discovery}
     *
     * @param id Device ID
     *
     * @return Input input
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "inputs/{id}/discover/", method = HttpMethod.DELETE,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void disableInputDiscovery(@RestUriParam("id") int id) throws Exception;

    /**
     * Get all the Devices
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:devices}
     *
     * @return devices
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "devices/", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract Collection<Device> devices() throws Exception;

    /**
     * Get a device by id
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:device}
     *
     * @param id device to retrieve
     *
     * @return devices
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "devices/{id}", method = HttpMethod.GET,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract Device device(@RestUriParam("id") int id) throws Exception;

    /**
     * Adds a device to an input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:add-device}
     *
     * @param id Input ID where to attach the device
     * @param ip IP Address of the device
     * @param deviceName Name of the device
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "devices/", method = HttpMethod.POST,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void addDevice(@RestPostParam("input_id") Integer id,
                                   @RestPostParam("ip") String ip,
                                   @RestPostParam("name") String deviceName) throws Exception;

    /**
     * Deletes a Device from an Input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:delete-device}
     *
     * @param id Device ID to remove
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "devices/{id}", method = HttpMethod.DELETE,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void deleteDevice(@RestUriParam("id") int id) throws Exception;

    /**
     * Deletes a Device (identified by an IP address) from an Input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:delete-device-by-ip}
     *
     * @param ip IP Address of the Device to remove
     *
     * @throws Exception error while doing the request and receiving the response
     */
    @Processor
    @RestTimeout(timeout = TIMEOUT, exception = TimeoutException.class)
    @RestCall(uri = BASE_URL + "devices/{ip}", method = HttpMethod.DELETE,
            exceptions = {
                    @RestExceptionOn(expression = "#[!message.inboundProperties['http.status'].startsWith('20')]", exception = LogglyRuntimeException.class)
            })
    public abstract void deleteDeviceByIp(@RestUriParam("ip") String ip) throws Exception;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}