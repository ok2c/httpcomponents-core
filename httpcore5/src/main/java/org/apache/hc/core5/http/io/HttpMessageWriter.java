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

package org.apache.hc.core5.http.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.MessageHeaders;

/**
 * Message writer intended to serialize HTTP message head to an output stream.
 *
 * @param <T> The type of {@link MessageHeaders}.
 * @since 4.0
 */
public interface HttpMessageWriter<T extends MessageHeaders> {

    /**
     * Serializes an instance of {@link MessageHeaders} to the given output stream.
     *
     * @param message HTTP message head
     * @param buffer session output buffer
     * @throws IOException in case of an I/O error
     * @throws HttpException in case of HTTP protocol violation
     */
    void write(T message, SessionOutputBuffer buffer, OutputStream outputStream) throws IOException, HttpException;

}
