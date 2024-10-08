/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.core5.http.impl.nio;

import java.io.IOException;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.message.LineFormatter;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.util.CharArrayBuffer;

/**
 * Default {@link org.apache.hc.core5.http.nio.NHttpMessageWriter} implementation for {@link HttpRequest}s.
 *
 * @param <T> The type of {@link HttpRequest}.
 * @since 4.1
 */
public class DefaultHttpRequestWriter<T extends HttpRequest> extends AbstractMessageWriter<T> {

    private final Http1Config http1Config;

    /**
     * @since 5.3
     */
    public DefaultHttpRequestWriter(final Http1Config http1Config, final LineFormatter formatter) {
        super(formatter);
        this.http1Config = http1Config != null ? http1Config : Http1Config.DEFAULT;
    }

    /**
     * Creates an instance of DefaultHttpRequestWriter.
     *
     * @param formatter the line formatter If {@code null}
     *   {@link org.apache.hc.core5.http.message.BasicLineFormatter#INSTANCE} will be used.
     *
     * @since 4.3
     */
    public DefaultHttpRequestWriter(final LineFormatter formatter) {
        this(null, formatter);
    }

    /**
     * @since 4.3
     */
    public DefaultHttpRequestWriter() {
        this(null, null);
    }

    /**
     * Determines the HTTP protocol version to be communicated to the opposite
     * endpoint in the message header.
     *
     * @since 5.3
     */
    protected HttpVersion protocolVersion(final T request) {
        return http1Config.getVersion();
    }

    @Override
    protected void writeHeadLine(final T message, final CharArrayBuffer lineBuf) throws IOException {
        lineBuf.clear();
        getLineFormatter().formatRequestLine(lineBuf, new RequestLine(
                message.getMethod(),
                message.getRequestUri(),
                protocolVersion(message)));
    }

}
