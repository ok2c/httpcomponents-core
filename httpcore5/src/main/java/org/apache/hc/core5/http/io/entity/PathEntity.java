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

package org.apache.hc.core5.http.io.entity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Args;

/**
 * A self contained, repeatable entity that obtains its content from a path.
 * <p>
 * This class contains {@link ThreadingBehavior#IMMUTABLE_CONDITIONAL immutable attributes} but subclasses may contain
 * additional immutable or mutable attributes.
 * </p>
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class PathEntity extends AbstractHttpEntity {

    private final Path path;

    /**
     * Constructs a new instance with the given attributes kept as immutable.
     * <p>
     * The new instance:
     * </p>
     * <ul>
     * <li>is not chunked.</li>
     * </ul>
     *
     * @param path            The message body contents will be set from this path.
     * @param contentType     The content-type, may be null.
     * @param contentEncoding The content encoding string, may be null.
     */
    public PathEntity(final Path path, final ContentType contentType, final String contentEncoding) {
        super(contentType, contentEncoding);
        this.path = Args.notNull(path, "Path");
    }

    /**
     * Constructs a new instance with the given attributes kept as immutable.
     * <p>
     * The new instance:
     * </p>
     * <ul>
     * <li>is not chunked.</li>
     * <li>does not define a content encoding.</li>
     * </ul>
     *
     * @param path            The message body contents will be set from this path.
     * @param contentType     The content-type, may be null.
     */
    public PathEntity(final Path path, final ContentType contentType) {
        super(contentType, null);
        this.path = Args.notNull(path, "Path");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation always returns {@code false}.
     * </p>
     */
    @Override
    public final boolean isRepeatable() {
        return true;
    }

    @Override
    public final long getContentLength() {
        try {
            return Files.size(this.path);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public final InputStream getContent() throws IOException {
        return Files.newInputStream(path);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation always returns {@code false}.
     * </p>
     */
    @Override
    public final boolean isStreaming() {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is a no-op.
     * </p>
     */
    @Override
    public final void close() throws IOException {
        // do nothing
    }

}
