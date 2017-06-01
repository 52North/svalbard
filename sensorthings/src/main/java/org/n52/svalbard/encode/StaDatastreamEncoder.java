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
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaDatastream;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * SensorThings Datastream to JSON
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaDatastreamEncoder extends JSONEncoder<StaDatastream> {

    public StaDatastreamEncoder() {
        super(StaDatastream.class);
    }

    @Override
    public JsonNode encodeJSON(StaDatastream datastream) throws EncodingException {
        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.STA_ANNOTATION_ID, datastream.getId());

        // TODO
//        iotSelfLink; // http://SERVICEURL/VERSION/Datastreams(ID)
//        thing; // http://SERVICEURL/VERSION/Things(ID)
//        sensor; // http://SERVICEURL/VERSION/Sensors(ID)
//        observedProperty; // http://SERVICEURL/VERSION/ObservedProperties(ID)
//        name;
//        description;
//        observationType; // ValueCode
//        unitOfMeasurement; //JSON_Object
//        observedArea; // GM_Envelope[0..1]
//        phenomenonTime; // TM_Period[0..1]
//        resultTime; // TM_Period[0..1]
//        observationCollection //List<OmObservation>

        ArrayNode oArray = json.putArray(StaConstants.STA_RESOURCE_RELATION_OBSERVATIONS);



//        for (Iterator<OmObservation> oIterator = t.getObservationCollection().iterator(); oIterator.hasNext();) {
//
//            OmObservation o = oIterator.next();
//            json.set(StaConstants.STA_RESOURCE_RELATION_OBSERVATIONS, encodeObjectToJson(o));
//        }

        for (StaObservation o : datastream.getObservationCollection()) {

            oArray.add(encodeObjectToJson(o));
        }

//        datastream.getObservationCollection().forEach((OmObservation observation) -> {
//            oArray.add(encodeObjectToJson(observation));
//        });

        return json;
    }

}
