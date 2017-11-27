/*
 * Copyright 2016-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.svalbard.encode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.n52.janmayen.Json;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract response encoder for the SensorThings API Observations resource.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public abstract class StaAbstractGetObservationResponseEncoder<T extends AbstractObservationResponse> extends JSONEncoder<T> {

    private static final Logger LOG = LoggerFactory.getLogger(StaAbstractGetObservationResponseEncoder.class);

    public StaAbstractGetObservationResponseEncoder(Class<? super T> type, EncoderKey... additionalKeys) {

        super(type, additionalKeys);
    }

    protected void encodeResponse(ObjectNode json, T t) throws EncodingException {

        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode observations
        int observationCount = 0;
        Encoder converter = new StaObservationConverter();

        try {
            while (t.getObservationCollection().hasNext()) {
                OmObservation o = t.getObservationCollection().next();
                if (o.getValue() instanceof ObservationStream) {
                    ObservationStream value = (ObservationStream) o.getValue();
                        while (value.hasNext()) {
                            dsArray.add(encodeObjectToJson(converter.encode(value.next())));
                            observationCount++;
                    }
                } else {
                    dsArray.add(encodeObjectToJson(converter.encode(o)));
                    observationCount++;
                }
            }
        } catch (OwsExceptionReport ex) {
            throw new EncodingException(ex);
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, observationCount);
    }

    @Override
    public JsonNode encodeJSON(T t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }
}
