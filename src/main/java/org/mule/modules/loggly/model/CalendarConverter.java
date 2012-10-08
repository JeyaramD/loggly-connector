/**
 * Mule Loggly Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.loggly.model;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.KeyDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarConverter extends JsonDeserializer<Calendar> {

    @Override
    public Calendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String timestamp = jp.getText();
        return parseDate(timestamp);
    }

    private static Calendar parseDate(String timestamp) {
        final DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(isoFormat.parse(timestamp));
        } catch (ParseException e) {
            calendar = Calendar.getInstance();
            try {
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp));
            } catch (ParseException e1) {
                throw new RuntimeException(e1);
            }
        }
        return calendar;
    }

    public static class CalendarKeyDeserializer extends KeyDeserializer {
        @Override
        public Calendar deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return parseDate(key);
        }
    }
}
