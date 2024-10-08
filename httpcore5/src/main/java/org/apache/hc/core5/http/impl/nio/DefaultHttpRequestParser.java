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

import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestFactory;
import org.apache.hc.core5.http.MessageConstraintException;
import org.apache.hc.core5.http.RequestHeaderFieldsTooLargeException;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.message.LineParser;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.nio.SessionInputBuffer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.CharArrayBuffer;

/**
 * Default {@link org.apache.hc.core5.http.nio.NHttpMessageParser} implementation for {@link HttpRequest}s.
 *
 * @param <T> The type of {@link HttpRequest}.
 * @since 4.1
 */
public class DefaultHttpRequestParser<T extends HttpRequest> extends AbstractMessageParser<T> {

    private final HttpRequestFactory<T> requestFactory;

    /**
     * @since 5.3
     */
    public DefaultHttpRequestParser(
            final Http1Config http1Config,
            final LineParser parser,
            final HttpRequestFactory<T> requestFactory) {
        super(http1Config, parser);
        this.requestFactory = Args.notNull(requestFactory, "Request factory");
    }

    /**
     * @since 5.3
     */
    public DefaultHttpRequestParser(final Http1Config http1Config, final HttpRequestFactory<T> requestFactory) {
        this(http1Config, null, requestFactory);
    }

    /**
     * @deprecated Use {@link #DefaultHttpRequestParser(Http1Config, HttpRequestFactory)}  }
     */
    @Deprecated
    public DefaultHttpRequestParser(final HttpRequestFactory<T> requestFactory, final Http1Config http1Config) {
        this(requestFactory, null, http1Config);
    }

    /**
     * @deprecated Use {@link #DefaultHttpRequestParser(Http1Config, LineParser, HttpRequestFactory)}  }
     */
    @Deprecated
    public DefaultHttpRequestParser(
            final HttpRequestFactory<T> requestFactory,
            final LineParser parser,
            final Http1Config http1Config) {
        this(http1Config, parser, requestFactory);
    }

    /**
     * @since 4.3
     */
    public DefaultHttpRequestParser(final HttpRequestFactory<T> requestFactory) {
        this(null, null, requestFactory);
    }

    @Override
    public T parse(final SessionInputBuffer sessionBuffer, final boolean endOfStream) throws IOException, HttpException {
        try {
            return super.parse(sessionBuffer, endOfStream);
        } catch (final MessageConstraintException ex) {
            throw new RequestHeaderFieldsTooLargeException(ex.getMessage(), ex);
        }
    }

    @Override
    protected T createMessage(final CharArrayBuffer buffer) throws HttpException {
        final RequestLine requestLine = getLineParser().parseRequestLine(buffer);
        final T request = this.requestFactory.newHttpRequest(requestLine.getMethod(), requestLine.getUri());
        request.setVersion(requestLine.getProtocolVersion());
        return request;
    }

}
