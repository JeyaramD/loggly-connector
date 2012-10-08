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

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Transformer;
import org.mule.modules.loggly.model.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Loggly submodule containing transformers
 *
 * @author MuleSoft, Inc.
 */
@Module(name="loggly", schemaVersion="1.0", friendlyName = "Loggly Transformers", configElementName = "config-transformer")
public class LogglyTransformers {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("Loggly", new Version(1, 0, 0, null))
                .addDeserializer(Calendar.class, new CalendarConverter());
        simpleModule.addKeyDeserializer(Calendar.class,
                new CalendarConverter.CalendarKeyDeserializer());

        objectMapper.registerModule(simpleModule);
    }

    /**
     * Transform JSON to Result Set
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-result-set}
     *
     * @param json JSON string to transform
     * @return A {@link org.mule.modules.loggly.model.LogglyResultSet} object
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static LogglyResultSet transformJsonToResultSet(String json) throws IOException {
        return objectMapper.readValue(json, LogglyResultSet.class);
    }

    /**
     * Transform JSON to a Facet Search result
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-facet-result}
     *
     *
     * @param json JSON string to transform
     * @return A {@link org.mule.modules.loggly.model.LogglyFacetResult}
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static LogglyFacetResult transformJsonToFacetResult(String json) throws IOException {
        return objectMapper.readValue(json, LogglyFacetResult.class);
    }

    /**
     * Transform JSON to Input
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-input}
     *
     *
     * @param json JSON string to transform
     * @return A {@link LogglyFacetResult}
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static Input transformJsonToInput(String json) throws IOException {
        return objectMapper.readValue(json, Input.class);
    }

    /**
     * Transform JSON to Device
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-device}
     *
     * @param json JSON string to transform
     * @return A {@link LogglyFacetResult}
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static Device transformJsonToDevice(String json) throws IOException {
        return objectMapper.readValue(json, Device.class);
    }

    /**
     * Transform JSON to collection of Inputs
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-inputs}
     *
     *
     * @param json JSON string to transform
     * @return A {@link java.util.Collection}
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static Collection<Input> transformJsonToInputs(String json) throws IOException {
        return genericJsonToListTransformer(json);
    }

    /**
     * Transform JSON to collection of Devices
     * <p/>
     * {@sample.xml ../../../doc/Loggly-connector.xml.sample loggly:transform-json-to-devices}
     *
     * @param json JSON string to transform
     * @return A {@link Collection}
     * @throws java.io.IOException if the conversion fails
     */
    @Transformer(sourceTypes = {String.class})
    public static Collection<Device> transformJsonToDevices(String json) throws IOException {
        return genericJsonToListTransformer(json);
    }

    protected static <T> Collection<T> genericJsonToListTransformer(String json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<List<T>>() {});
    }

}
