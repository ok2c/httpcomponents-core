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
package org.apache.hc.core5.json.http;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.support.AbstractServerExchangeHandler;
import org.apache.hc.core5.http.nio.support.BasicRequestConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

/**
 * JSON {@link AsyncRequestConsumer} and {@link AsyncResponseConsumer} pipeline assembler.
 *
 * @since 5.5
 */
public final class AsyncJsonServerPipeline {

    private final ObjectMapper objectMapper;

    public AsyncJsonServerPipeline(final ObjectMapper objectMapper) {
        this.objectMapper = Args.notNull(objectMapper, "Object mapper");
    }

    public RequestStage receive() {
        return new RequestStage();
    }

    public class RequestStage {

        private RequestStage() {
        }

        /**
         * Creates {@link AsyncRequestConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and request content body.
         *
         * @param <T> the type of result objects produced by the consumer.
         */
        public <T> HandlerStage<T> consume(final Supplier<AsyncEntityConsumer<T>> contentConsumerSupplier) {
            return new HandlerStage(new BasicRequestConsumer<>(contentConsumerSupplier));
        }

        /**
         * Creates {@link AsyncRequestConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and the de-serialized JSON body.
         *
         * @param javaType the java type of the de-serialized object.
         * @param <T>      the type of result objects produced by the consumer.
         */
        public <T> HandlerStage<T> map(final JavaType javaType) {
            return new HandlerStage(JsonRequestConsumers.create(objectMapper, javaType));
        }

        /**
         * Creates {@link AsyncRequestConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and the de-serialized JSON body.
         *
         * @param objectClazz the class of the de-serialized object.
         * @param <T>         the type of result objects produced by the consumer.
         */
        public <T> HandlerStage<T> map(final Class<T> objectClazz) {
            return new HandlerStage(JsonRequestConsumers.create(objectMapper, objectClazz));
        }

        /**
         * Creates {@link AsyncRequestConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and the de-serialized JSON body.
         *
         * @param typeReference the type reference of the de-serialized object.
         * @param <T>           the type of result objects produced by the consumer.
         */
        public <T> HandlerStage<T> map(final TypeReference<T> typeReference) {
            return new HandlerStage(JsonRequestConsumers.create(objectMapper, typeReference));
        }

        /**
         * Creates {@link AsyncRequestConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and the de-serialized JSON body.
         */
        public HandlerStage<JsonNode> mapAsJsonNode() {
            return new HandlerStage<>(JsonRequestConsumers.create(objectMapper.getFactory()));
        }

    }

    public class HandlerStage<I> {

        private final AsyncRequestConsumer<Message<HttpRequest, I>> requestConsumer;

        private HandlerStage(AsyncRequestConsumer<Message<HttpRequest, I>> requestConsumer) {
            this.requestConsumer = requestConsumer;
        }

        public <O> ResponseStage<I, O> handle(final JsonRequestHandler<I, O> handler) {
            return new ResponseStage<>(requestConsumer, handler);
        }

    }

    public class ResponseStage<I, O> {

        private final AsyncRequestConsumer<Message<HttpRequest, I>> requestConsumer;
        private final JsonRequestHandler<I, O> handler;

        private ResponseStage(final AsyncRequestConsumer<Message<HttpRequest, I>> requestConsumer,
                              final JsonRequestHandler<I, O> handler) {
            this.requestConsumer = requestConsumer;
            this.handler = handler;
        }

        /**
         * Creates {@link AsyncRequestProducer} that generates an HTTP GET request.
         *
         * @return the request producer.
         */
        public AsyncServerExchangeHandler send() {
            return new AbstractServerExchangeHandler<Message<HttpRequest, I>>() {

                @Override
                protected AsyncRequestConsumer<Message<HttpRequest, I>> supplyConsumer(
                        final HttpRequest request,
                        final EntityDetails entityDetails,
                        final HttpContext context) throws HttpException {
                    return requestConsumer;
                }

                @Override
                protected void handle(
                        final Message<HttpRequest, I> requestMessage,
                        final AsyncServerRequestHandler.ResponseTrigger responseTrigger,
                        final HttpContext context) throws HttpException, IOException {
                    handler.handle(requestMessage, responseMessage -> {
                        final HttpResponse response = responseMessage.getHead();
                        final O body = responseMessage.getBody();
                        final AsyncResponseProducer responseProducer;
                        if (body instanceof JsonNode) {
                            responseProducer = JsonResponseProducers.create(
                                    response,
                                    (JsonNode) body,
                                    objectMapper);
                        } else {
                            responseProducer = JsonResponseProducers.create(
                                    response,
                                    body,
                                    objectMapper);
                        }
                        responseTrigger.submitResponse(responseProducer, context);
                    });

                }
            };
        }

    }

}
