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
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaDatastream;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings Datastream to JSON
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaDatastreamEncoder extends JSONEncoder<StaDatastream> {

    private static final Logger LOG = LoggerFactory.getLogger(StaDatastreamEncoder.class);

    public StaDatastreamEncoder() {
        super(StaDatastream.class);
    }

    @Override
    public JsonNode encodeJSON(StaDatastream datastream) throws EncodingException {
        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.ANNOTATION_ID, datastream.getId())
            .put(StaConstants.ANNOTATION_SELF_LINK, datastream.getSelfLink());

        // entities
        json.put(StaConstants.Entity.Thing.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.Thing.name())
            .put(StaConstants.Entity.Sensor.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.Sensor.name())
            .put(StaConstants.Entity.ObservedProperty.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.ObservedProperty.name());

        // Observations
        if (datastream.isSetObservationList()) {
            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_COUNT, datastream.getObservationList().size());
            ArrayNode oArray = json.putArray(StaConstants.EntitySet.Observations.name());

            datastream.getObservationList().forEach((StaObservation o) -> {
                try {
                    oArray.add(encodeObjectToJson(o));
                } catch (EncodingException ex) {
                    LOG.error("Error when encoding SensorThings Observation.", ex);
                }
            });
        } else {
            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.EntitySet.Observations.name());
        }

        // parameters, mandatory
        json.put(StaConstants.Parameter.name.name(), datastream.getName())
            .put(StaConstants.Parameter.description.name(), datastream.getDescription())
            .set(StaConstants.Parameter.unitOfMeasurement.name(), encodeUoM(datastream.getUnitOfMeasurement()));
        json.put(StaConstants.Parameter.observationType.name(), datastream.getObservationType());

        // parameters, optional and derived
//        observedArea; // GM_Envelope[0..1]
//        phenomenonTime; // TM_Period[0..1]
//        resultTime; // TM_Period[0..1]

        return json;
    }

    private JsonNode encodeUoM(UoM unit) {
        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.UomParameter.name.name(), unit.getName());
        json.put(StaConstants.UomParameter.symbol.name(), unit.getUom());
        json.put(StaConstants.UomParameter.definition.name(), unit.getLink());

        return json;
    }
}
