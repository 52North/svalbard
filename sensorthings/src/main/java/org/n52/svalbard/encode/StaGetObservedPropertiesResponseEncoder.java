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
import java.util.Iterator;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaObservedProperty;
import org.n52.shetland.ogc.sta.response.StaGetObservedPropertiesResponse;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API ObservedProperty resource.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservedPropertiesResponseEncoder extends JSONEncoder<StaGetObservedPropertiesResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetObservedPropertiesResponseEncoder.class);

    public StaGetObservedPropertiesResponseEncoder() {

        super(StaGetObservedPropertiesResponse.class,
            new OperationResponseEncoderKey(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                StaConstants.Operation.GET_OBSERVED_PROPERTIES, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, StaGetObservedPropertiesResponse t) throws EncodingException {

        // add nodes
        ArrayNode array = json.putArray(StaConstants.VALUES);

        // encode entities
        Iterator<StaObservedProperty> iterator = t.getObservedProperties().iterator();

        while(iterator.hasNext()) {
            array.add(encodeObjectToJson(iterator.next()));
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, t.getObservedProperties().size());
    }

    @Override
    public JsonNode encodeJSON(StaGetObservedPropertiesResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }
}
