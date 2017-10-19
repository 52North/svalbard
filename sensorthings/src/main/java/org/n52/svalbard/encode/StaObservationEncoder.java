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
import java.util.Map;
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

    private static final Logger LOG = LoggerFactory.getLogger(StaObservationEncoder.class);

    public StaObservationEncoder() {
        super(StaObservation.class);
    }

    @Override
    public JsonNode encodeJSON(StaObservation observation) throws EncodingException {

        ObjectNode json = nodeFactory().objectNode();
        // TODO replace fix encoder (from SOS) with encodeObjectToJson(observation.getPhenomenonTime())
        //TimeJSONEncoder timeEncoder = new TimeJSONEncoder();

        json.put(StaConstants.ANNOTATION_ID, observation.getId())
            .put(StaConstants.ANNOTATION_SELF_LINK, observation.getSelfLink());

        // entities
        json.put(StaConstants.Entity.Datastream.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                observation.getChildrensBaseURL() + StaConstants.Entity.Datastream.name());

        if (observation.isSetFeatureOfInterest()) {
            try {
                json.set(StaConstants.Entity.FeatureOfInterest.name(), encodeObjectToJson(observation.getFeatureOfInterest()));
            } catch (EncodingException ex) {
                LOG.error("Error when encoding SensorThings FeatureOfInterest.", ex);
            }
        } else {
            json.put(StaConstants.Entity.FeatureOfInterest.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                observation.getChildrensBaseURL() + StaConstants.Entity.FeatureOfInterest.name());
        }

        // parameters, mandatory
        json.set(StaConstants.Parameter.phenomenonTime.name(), encodeObjectToJson(observation.getPhenomenonTime()));
        json.put(StaConstants.Parameter.result.name(), observation.getResult());
        json.set(StaConstants.Parameter.resultTime.name(), encodeObjectToJson(observation.getResultTime()));

        // parameters, optional
        // resultQuality
        if (observation.isSetResultQuality()) {
            ArrayNode rqArray = json.putArray(StaConstants.Parameter.resultQuality.name());
            observation.getResultQuality().forEach((String s) -> rqArray.add(s));
        }
        if (observation.isSetValidTime()) {
            json.set(StaConstants.Parameter.validTime.name(), encodeObjectToJson(observation.getValidTime()));
        }
        if (observation.isSetParameters()) {
            json.set(StaConstants.Parameter.parameters.name(), encodeParameters(observation.getParameters()));
        }

        return json;
    }

    private ObjectNode encodeParameters(Map<String, String> parameters) {

        ObjectNode json = nodeFactory().objectNode();

        if (parameters != null) {
            parameters.forEach((key, value) -> json.put(key, value));
        }
        return json;
    }
}
