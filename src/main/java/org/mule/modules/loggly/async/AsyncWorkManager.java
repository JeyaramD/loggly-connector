/**
 * Mule Loggly Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.loggly.async;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.mule.modules.loggly.LogglyURLProvider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AsyncWorkManager implements WorkManager {
    private static final Logger LOGGER = Logger.getLogger(AsyncWorkManager.class);
    public static final long PATIENCE = 300;

    private final Buffer buffer;

    private Thread thread;

    private String inputKey;

    private ReentrantReadWriteLock lock;

    private volatile boolean end = false;

    public AsyncWorkManager(String inputKey) {
        this.buffer = new CircularFifoBuffer();
        this.inputKey = inputKey;

        lock = new ReentrantReadWriteLock();

        thread = new Thread(new MessageConsumer());
        thread.start();
    }

    @Override
    public void send(String message) throws InterruptedException {
        if (lock.writeLock().tryLock()) {
            try
            {
                this.buffer.add(message);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            if( LOGGER.isDebugEnabled() ) {
                LOGGER.debug("Unable to get a hold of a write lock");
            }
        }
    }

    @Override
    public void stop() {
        end = true;

        LOGGER.debug("Waiting for MessageLoop thread to finish");
        if (thread.isAlive()) {

            try {
                thread.join(PATIENCE);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }

            LOGGER.debug("Still waiting...");
            if (thread.isAlive()) {
                thread.interrupt();
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
            while (!end) {
                int i = -1;
                try {
                        String message = null;
                        if (lock.readLock().tryLock()) {
                            try
                            {
                                message = (String) buffer.get();
                            } finally {
                                lock.readLock().unlock();
                            }
                        } else {
                            Thread.sleep(100);
                        }

                        if( message != null ) {
                            RequestEntity entity;
                            entity = new StringRequestEntity(message, "application/text", "UTF-8");

                                /* Tried to recycle the PostMethod but that kind of usage
                                 * is deprecated.
                                 */
                            PostMethod postMethod = new PostMethod(LogglyURLProvider.HTTPS_LOGS_LOGGLY_INPUTS + inputKey);
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

                                /* Log only when no 2xx HTTP status codes was returned */
                            if (i - 200 > 100 || i - 200 < 0) {
                                LOGGER.error("Message [" + message + "] logged with HTTP Status code " + i + ".");
                            } else {
                                if (lock.writeLock().tryLock()) {
                                    try
                                    {
                                        buffer.remove();
                                    } finally {
                                        lock.writeLock().unlock();
                                    }
                                } else {
                                    Thread.sleep(100);
                                }
                            }
                        }
                } catch (BufferUnderflowException bue) {
                    //LOGGER.error(bue);
                    // do nothing
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e);
                } catch (InterruptedException e) {
                    return;
                }

            }

        }
    }
}
