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

package org.apache.hc.core5.http.impl.io;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.io.HttpMessageWriter;
import org.apache.hc.core5.http.io.HttpMessageWriterFactory;
import org.apache.hc.core5.http.message.BasicLineFormatter;
import org.apache.hc.core5.http.message.LineFormatter;

/**
 * Default factory for response message writers.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory<ClassicHttpResponse> {

    public static final DefaultHttpResponseWriterFactory INSTANCE = new DefaultHttpResponseWriterFactory();

    private final Http1Config http1Config;
    private final LineFormatter lineFormatter;

    /**
     * @since 5.3
     */
    public DefaultHttpResponseWriterFactory(final Http1Config http1Config, final LineFormatter lineFormatter) {
        super();
        this.http1Config = http1Config != null ? http1Config : Http1Config.DEFAULT;
        this.lineFormatter = lineFormatter != null ? lineFormatter : BasicLineFormatter.INSTANCE;
    }

    /**
     * @since 5.3
     */
    public DefaultHttpResponseWriterFactory(final Http1Config http1Config) {
        this(http1Config, null);
    }

    public DefaultHttpResponseWriterFactory(final LineFormatter lineFormatter) {
        this(null, lineFormatter);
    }

    public DefaultHttpResponseWriterFactory() {
        this(null, null);
    }

    @Override
    public HttpMessageWriter<ClassicHttpResponse> create() {
        return new DefaultHttpResponseWriter(http1Config, lineFormatter);
    }

}
