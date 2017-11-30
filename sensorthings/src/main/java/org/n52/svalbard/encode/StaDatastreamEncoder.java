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
import java.util.logging.Level;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaDatastream;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings Datastream to JSON
 *
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

        // entities (expanded entity/entities of navigation link)
        if (datastream.getThing() != null) {
            json.set(StaConstants.Entity.Thing.name(), encodeObjectToJson(datastream.getThing()));

        } else {
            json.put(StaConstants.Entity.Thing.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.Thing.name());
        }

        if (datastream.getSensor() != null) {
            json.set(StaConstants.Entity.Sensor.name(), encodeObjectToJson(datastream.getSensor()));

        } else {
            json.put(StaConstants.Entity.Sensor.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.Sensor.name());
        }

        if (datastream.getObservedProperty() != null) {
            json.set(StaConstants.Entity.ObservedProperty.name(), encodeObjectToJson(datastream.getObservedProperty()));

        } else {
            json.put(StaConstants.Entity.ObservedProperty.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.Entity.ObservedProperty.name());
        }

        if (datastream.getObservations() != null && !datastream.getObservations().isEmpty()) {

            int count = 0;
            ArrayNode oArray = json.putArray(StaConstants.EntitySet.Observations.name());

            Iterator<StaObservation> iterator = datastream.getObservations().iterator();
            while(iterator.hasNext()) {
                try {
                    StaObservation o = iterator.next();

                    oArray.add(encodeObjectToJson(o));
                    count++;
                } catch (EncodingException ex) {
                    LOG.error("Error when encoding SensorThings Observation.", ex);
                }
            }
            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_COUNT, count);

        } else if (datastream.getObservationStream() != null) {

            Encoder converter = new StaObservationConverter();

            ArrayNode oArray = json.putArray(StaConstants.EntitySet.Observations.name());

            try {
                datastream.getObservationStream().forEachRemaining((OmObservation o) -> {
                    try {
                        oArray.add(encodeObjectToJson(converter.encode(o)));
                    } catch (EncodingException ex) {
                        LOG.error("Error when encoding SensorThings Observation.", ex);
                    }
                });
            } catch (OwsExceptionReport ex) {
                LOG.error("Error when encoding SensorThings Observation.", ex);
                throw new EncodingException("Error when encoding SensorThings Observation.");
            }

            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_COUNT, oArray.size());
        } else {
            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                datastream.getChildrensBaseURL() + StaConstants.EntitySet.Observations.name());
        }

        // parameters, mandatory
        json.put(StaConstants.Parameter.name.name(), datastream.getName())
            .put(StaConstants.Parameter.description.name(), datastream.getDescription());
        json.set(StaConstants.Parameter.unitOfMeasurement.name(), encodeUoM(datastream.getUnitOfMeasurement()));
        json.put(StaConstants.Parameter.observationType.name(), datastream.getObservationType());

        // parameters, optional and derived
        if (datastream.getObservedArea() != null) {
            json.set(StaConstants.Parameter.observedArea.name(), encodeObjectToJson(datastream.getObservedArea()));
        }
        if (datastream.getPhenomenonTime() != null) {
            json.set(StaConstants.Parameter.phenomenonTime.name(), encodeObjectToJson(datastream.getPhenomenonTime()));
        }
        if (datastream.getResultTime() != null) {
            json.set(StaConstants.Parameter.resultTime.name(), encodeObjectToJson(datastream.getResultTime()));
        }

        return json;
    }

    private JsonNode encodeUoM(UoM unit) {
        if (unit == null || unit.isEmpty()) {
            return null;
        }
        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.UomParameter.name.name(), unit.getName());
        json.put(StaConstants.UomParameter.symbol.name(), unit.getUom());
        json.put(StaConstants.UomParameter.definition.name(), unit.getLink());

        return json;
    }
}
