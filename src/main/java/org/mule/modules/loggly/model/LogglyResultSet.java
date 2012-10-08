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

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class LogglyResultSet {
    List<Data> data;
    private int numFound;
    private Map<String,String> context;

    public void setData(List<Data> data) {
        this.data = data;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public List<Data> getData() {
        return data;
    }

    public int getNumFound() {
        return numFound;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public static class Data {
        @JsonProperty("inputid")
        private String inputId;

        @JsonProperty("inputname")
        private String inputName;
        private String ip;
        private String text;
        private boolean isjson;
        private Calendar timestamp;

        public void setTimestamp(Calendar calendar) {
            this.timestamp = calendar;
        }

        public void setInputId(String inputId) {
            this.inputId = inputId;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Calendar getTimestamp() {
            return timestamp;
        }

        public String getInputId() {
            return inputId;
        }

        public String getIp() {
            return ip;
        }

        public String getText() {
            return text;
        }

        public boolean isIsjson() {
            return isjson;
        }

        public void setIsjson(boolean isjson) {
            this.isjson = isjson;
        }

        public String getInputName() {
            return inputName;
        }

        public void setInputName(String inputName) {
            this.inputName = inputName;
        }
    }
}
