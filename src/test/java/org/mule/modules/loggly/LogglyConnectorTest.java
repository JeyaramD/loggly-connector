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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.junit4.FunctionalTestCase;

public class LogglyConnectorTest extends FunctionalTestCase
{
    @Override
    protected Properties getStartUpProperties() {
        Properties properties = new Properties();
       String [] requiredProperties = {"inputsurl","inputkey", "subdomain", "username", "password"};

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
        	
            fail("Parameters not specified. Build using -Dinputsurl=your_url -Dinputkey=your_api_key -Dusername=user -Dpassword=password -Dsubdomain=subdomain." + missingProperties.toString() );
        }

        return properties;
    }

    @Override
    protected String getConfigFile()
    {
        return "mule-config.xml";
    }

    @Test
    public void testFlow() throws Exception
    {
    	runFlow("testFlow");
    }

    @Test
    public void testRetrieveFlow() throws Exception
    {
        runFlow("testRetrieveFlow","Test");
    }

    @Test
    public void testFacetFlow() throws Exception
    {
        runFlow("testFacetFlow");

    }

    @Test
    public void testInputFlow() throws Exception
    {
        runFlow("testInputFlow");
    }

    @Test
    public void testInputByNameFlow() throws Exception
    {
        runFlow("testInputByNameFlow");
    }

    @Test
    public void testInputsFlow() throws Exception
    {
        runFlow("testInputsFlow");
    }

    @Test
    public void testCreateInputFlow() throws Exception
    {
        runFlow("testCreateInputFlow");
    }

    @Test
    public void testAddCurrentDeviceFlow() throws Exception
    {
        runFlow("testAddCurrentDeviceFlow");
    }

    @Test
    public void testAddCurrentDeviceSyslog514Flow() throws Exception
    {
        runFlow("testAddCurrentDeviceSyslog514Flow");
    }

    @Test
    public void testRemoveDeviceFlow() throws Exception
    {
        runFlow("testRemoveDeviceFlow");
    }

    @Test
    public void testEnableDiscoveryFlow() throws Exception
    {
        runFlow("testEnableDiscoveryFlow");
    }

    @Test
    public void testDisableDiscoveryFlow() throws Exception
    {
        runFlow("testDisableDiscoveryFlow");
    }

    @Test
    public void testDevicesFlow() throws Exception
    {
        runFlow("testDevicesFlow");
    }

    @Test
    public void testDeviceFlow() throws Exception
    {
        runFlow("testDeviceFlow");
    }

    @Test
    public void testAddDeviceFlow() throws Exception
    {
        runFlow("testAddDeviceFlow");
    }

    @Test
    public void testDeleteDeviceFlow() throws Exception
    {
        runFlow("testDeleteDeviceFlow");
    }

    @Test
    public void testDeleteDeviceByIpFlow() throws Exception
    {
        runFlow("testDeleteDeviceByIpFlow");
    }

    protected <T, U> Object runFlow2(String flowName, U payload) throws Exception
    {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleContextTestCase.getTestEvent(payload);
        MuleEvent responseEvent = flow.process(event);

        return responseEvent.getMessage().getPayload();
    }

}