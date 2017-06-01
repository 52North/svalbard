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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings Observation to JSON
 *
 * @author Martin Kiesow
 */
public class StaObservationEncoder extends JSONEncoder<StaObservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaObservationEncoder.class);

    public StaObservationEncoder() {
        super(StaObservation.class);
    }

    @Override
    public JsonNode encodeJSON(StaObservation observation) throws EncodingException {

        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.STA_ANNOTATION_ID, observation.getId());
        return json;
    }

}
