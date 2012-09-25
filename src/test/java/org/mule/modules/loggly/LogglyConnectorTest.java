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
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.FunctionalTestCase;

import java.util.Properties;

public class LogglyConnectorTest extends FunctionalTestCase
{
    @Override
    protected Properties getStartUpProperties() {
        Properties properties = new Properties();
        String apiKey = System.getProperty("inputKey");
        if (apiKey == null || "".equals(apiKey) ) {
            fail("apiKey parameter not specified. Build using -DinputKey=your_api_key.");
        }

        properties.put("inputKey", apiKey);
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

    protected <T, U> Object runFlow(String flowName, U payload) throws Exception
    {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(payload);
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
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
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
        MuleEvent event = AbstractMuleTestCase.getTestEvent(payload);
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
        return (Flow) AbstractMuleTestCase.muleContext.getRegistry().lookupFlowConstruct(name);
    }
}
