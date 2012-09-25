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

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AsyncWorkManager implements WorkManager {
    private static final Logger LOGGER = Logger.getLogger(AsyncWorkManager.class);
    public static final long PATIENCE = 300;

    private Buffer buffer;

    private Thread thread;

    /**
     * Connection timeout
     */
    public static final String HTTPS_LOGS_LOGGLY_INPUTS = "https://logs.loggly.com/inputs/";
    private String inputKey;
    private boolean end = false;

    public AsyncWorkManager(String inputKey) {
        this.buffer = new CircularFifoBuffer();
        this.inputKey = inputKey;

        thread = new Thread(new MessageConsumer());
        thread.start();
    }

    @Override
    public void send(String message) {
        synchronized (this.buffer) {
            this.buffer.add(message);
            this.buffer.notify();
        }
    }

    @Override
    public void stop() {
        end = true;

        LOGGER.debug("Waiting for MessageLoop thread to finish");
        while (thread.isAlive()) {

            try {
                thread.join(PATIENCE);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }

            LOGGER.debug("Still waiting...");
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
        LOGGER.debug("Finally!");
    }

    private class MessageConsumer implements Runnable {
        /*
        * We are using a different HttpClient in order to avoid
        * concurrency issues with the rest of the Loggly RestCall API.
        */
        private HttpClient httpClient = new HttpClient();

        @Override
        public void run() {
            synchronized (buffer) {
                while(!end) {
                    int i = -1;
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        LOGGER.error(e);
                    }
                    while ( ! buffer.isEmpty() ) {
                        String message = (String) buffer.remove();
                        RequestEntity entity;
                        try {
                            entity = new StringRequestEntity(message,"application/text", "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            LOGGER.error(e);
                            return;
                        }
                        /* Tried to recycle the PostMethod but that kind of usage
                         * is deprecated.
                         */
                        PostMethod postMethod = new PostMethod(HTTPS_LOGS_LOGGLY_INPUTS + inputKey);
                        postMethod.setRequestEntity(entity);

                        try {
                            i = httpClient.executeMethod(postMethod);
                        } catch (HttpException e) {
                            LOGGER.error(e);
                        } catch (IOException e) {
                            LOGGER.error(e);
                        } finally {
                            postMethod.releaseConnection();
                        }

                        LOGGER.info("Message [" + message + "] logged with HTTP Status code " + i + ".");
                    }
                }
            }
        }
    }
}
