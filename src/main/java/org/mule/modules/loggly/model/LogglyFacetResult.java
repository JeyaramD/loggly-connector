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
import java.util.Map;

public class LogglyFacetResult {
    private int numFound;
    private String gap;
    @JsonProperty("gmt_offset")
    private String gmtOffset;
    private int start;
    private Context context;

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Map<Calendar, Integer> getData() {
        return data;
    }

    public void setData(Map<Calendar, Integer> data) {
        this.data = data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setGmtOffset(String gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public String getGmtOffset() {
        return gmtOffset;
    }

    public static class Context {
        private Integer rows;
        private String from;
        private String until;
        private int start;
        private String query;
        private Order order;

        public Integer getRows() {
            return rows;
        }

        public void setRows(Integer rows) {
            this.rows = rows;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getUntil() {
            return until;
        }

        public void setUntil(String until) {
            this.until = until;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = Order.valueOf(order.toUpperCase());
        }
    }

    private Map<Calendar,Integer> data;
}
