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

package org.apache.hc.core5.http.nio;

import java.io.IOException;
import java.nio.Buffer;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

/**
 * Abstract data stream channel.
 * <p>
 * Implementations are expected to be thread-safe.
 * </p>
 *
 * @param <T> data container accepted by the channel.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.SAFE)
public interface StreamChannel<T extends Buffer> {

    /**
     * Writes data from the data container into the underlying data stream.
     *
     * @param src source of data
     * @return The number of elements written, possibly zero
     * @throws IOException in case of an I/O error.
     */
    int write(T src) throws IOException;

    /**
     * Terminates the underlying data stream and optionally writes
     * a closing sequence.
     *
     * @throws IOException in case of an I/O error.
     */
    void endStream() throws IOException;
}
