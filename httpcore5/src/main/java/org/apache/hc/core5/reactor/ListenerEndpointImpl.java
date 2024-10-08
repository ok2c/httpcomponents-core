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

package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.Closer;

class ListenerEndpointImpl implements ListenerEndpoint {

    private final SelectionKey key;
    final SocketAddress address;
    final Object attachment;
    private final AtomicBoolean closed;

    public ListenerEndpointImpl(final SelectionKey key, final Object attachment, final SocketAddress address) {
        super();
        this.key = key;
        this.address = address;
        this.attachment = attachment;
        this.closed = new AtomicBoolean();
    }

    @Override
    public SocketAddress getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        return "endpoint: " + address;
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public void close() throws IOException {
        if (closed.compareAndSet(false, true)) {
            key.cancel();
            key.channel().close();
        }
    }

    @Override
    public void close(final CloseMode closeMode) {
        Closer.closeQuietly(this);
    }

}
