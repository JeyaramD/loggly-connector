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

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LogglyConnectorTest extends FunctionalTestCase
{
    @Override
    protected Properties getStartUpProperties() {
        Properties properties = new Properties();
        String [] requiredProperties = {"inputKey", "subdomain", "username", "password"};
        List<String> missingProperties = new ArrayList<String>();

        for (String requiredProperty : requiredProperties) {
            String requiredPropertyValue = System.getProperty(requiredProperty);
            if (requiredPropertyValue == "" || requiredPropertyValue == null) {
                missingProperties.add(requiredPropertyValue);
            } else {
                properties.put(requiredProperty, requiredPropertyValue);
            }
        }

        if (!missingProperties.isEmpty()) {
            fail("Parameters not specified. Build using -Dloggly.input.key=your_api_key -Dusername=user -Dpassword=password -Dsubdomain=subdomain.");
        }

        return properties;
    }

    @Override
    protected String getConfigResources()
    {
        return "mule-config.xml";
    }

    @Test
    public void testFlow() throws Exception
    {
        runFlow("testFlow", null);
    }

    @Test
    public void testRetrieveFlow() throws Exception
    {
        runFlow("testRetrieveFlow", null);
    }

    @Test
    public void testFacetFlow() throws Exception
    {
        runFlow("testFacetFlow", null);
    }

    @Test
    public void testInputFlow() throws Exception
    {
        runFlow("testInputFlow", null);
    }

    @Test
    public void testInputByNameFlow() throws Exception
    {
        runFlow("testInputByNameFlow", null);
    }

    @Test
    public void testInputsFlow() throws Exception
    {
        runFlow("testInputsFlow", null);
    }

    @Test
    public void testCreateInputFlow() throws Exception
    {
        runFlow("testCreateInputFlow", null);
    }

    @Test
    public void testAddCurrentDeviceFlow() throws Exception
    {
        runFlow("testAddCurrentDeviceFlow", null);
    }

    @Test
    public void testAddCurrentDeviceSyslog514Flow() throws Exception
    {
        runFlow("testAddCurrentDeviceSyslog514Flow", null);
    }

    @Test
    public void testRemoveDeviceFlow() throws Exception
    {
        runFlow("testRemoveDeviceFlow", null);
    }

    @Test
    public void testEnableDiscoveryFlow() throws Exception
    {
        runFlow("testEnableDiscoveryFlow", null);
    }

    @Test
    public void testDisableDiscoveryFlow() throws Exception
    {
        runFlow("testDisableDiscoveryFlow", null);
    }

    @Test
    public void testDevicesFlow() throws Exception
    {
        runFlow("testDevicesFlow", null);
    }

    @Test
    public void testDeviceFlow() throws Exception
    {
        runFlow("testDeviceFlow", null);
    }

    @Test
    public void testAddDeviceFlow() throws Exception
    {
        runFlow("testAddDeviceFlow", null);
    }

    @Test
    public void testDeleteDeviceFlow() throws Exception
    {
        runFlow("testDeleteDeviceFlow", null);
    }

    @Test
    public void testDeleteDeviceByIpFlow() throws Exception
    {
        runFlow("testDeleteDeviceByIpFlow", null);
    }

    protected <T, U> Object runFlow(String flowName, U payload) throws Exception
    {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleContextTestCase.getTestEvent(payload);
        MuleEvent responseEvent = flow.process(event);

        return responseEvent.getMessage().getPayload();
    }


    /**
    * Run the flow specified by name and assert equality on the expected output
    *
    * @param flowName The name of the flow to run
    * @param expect The expected output
    */
    protected <T> void runFlowAndExpect(String flowName, T expect) throws Exception
    {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleContextTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
    * Run the flow specified by name using the specified payload and assert
    * equality on the expected output
    *
    * @param flowName The name of the flow to run
    * @param expect The expected output
    * @param payload The payload of the input event
    */
    protected <T, U> void runFlowWithPayloadAndExpect(String flowName, T expect, U payload) throws Exception
    {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleContextTestCase.getTestEvent(payload);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
     * Retrieve a flow by name from the registry
     *
     * @param name Name of the flow to retrieve
     */
    protected Flow lookupFlowConstruct(String name)
    {
        return (Flow) muleContext.getRegistry().lookupFlowConstruct(name);
    }

}