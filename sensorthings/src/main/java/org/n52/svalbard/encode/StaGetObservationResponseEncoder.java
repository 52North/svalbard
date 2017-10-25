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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API Observations resource;
 * transforms SOS Observations into SensorThings Observations.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationResponseEncoder extends JSONEncoder<GetObservationResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetObservationResponseEncoder.class);

    public StaGetObservationResponseEncoder() {

        super(GetObservationResponse.class,
            new OperationResponseEncoderKey(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                Sos2Constants.EN_GET_OBSERVATION, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, GetObservationResponse t) throws EncodingException {

        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode observations
        int observationCount = 0;
        try {
            while (t.getObservationCollection().hasNext()) {
                OmObservation o = t.getObservationCollection().next();
                if (o.getValue() instanceof ObservationStream) {
                    ObservationStream value = (ObservationStream) o.getValue();
                        while (value.hasNext()) {
                            dsArray.add(encodeObjectToJson(transformObservation(value.next())));
                            observationCount++;
                    }
                } else {
                    dsArray.add(encodeObjectToJson(transformObservation(o)));
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
    public JsonNode encodeJSON(GetObservationResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }

    /**
     * transform a SOS Observation into a STA Observation
     * @param o SOS Observation
     * @return SensorThings Observation
     */
    private StaObservation transformObservation(OmObservation o) {

        StaObservation staObservation = new StaObservation(o.getObservationID());

        staObservation.setPhenomenonTime(o.getPhenomenonTime());
        staObservation.setResult(o.getValue().getValue().getValue().toString());

        staObservation.setResultTime(o.getResultTime());
//        private String resultQuality; // [0..n]
        staObservation.setValidTime(o.getValidTime());
//        private String parameters; // [0..1]

        // TODO create Datastream id from procedure, observedProperty, offering and featureOfInterest
        // not necessary for GET Observations
        //staObservation.setDatastream(datastreamID);

        staObservation.setFeatureOfInterest(
                transformFeatureOfInterest(o.getObservationConstellation().getFeatureOfInterest()));

        // test data
        if (o.getObservationID().equals("1")) {
            List<String> al = new ArrayList<>(2);
            al.add("ResultQualityTestString1");
            al.add("ResultQualityTestString2");
            staObservation.setResultQuality(al);

            Map<String, String> pa = new HashMap<>(3);
            pa.put("key1", "value1");
            pa.put("key2", "value2");
            pa.put("key3", "value3");
            staObservation.setParameters(pa);
        }
        return staObservation;
    }

    private StaFeatureOfInterest transformFeatureOfInterest(AbstractFeature f) {

        StringBuilder name = new StringBuilder();
        f.getName().forEach((CodeType ct) -> name.append(ct.getValue()));

        StaFeatureOfInterest staFOI = new StaFeatureOfInterest(f.getIdentifier(), name.toString(),
                f.getDescription(), StaConstants.SPATIAL_ENCODING_TYPE_GEOJSON);

        if (f instanceof SamplingFeature) {
            staFOI.setFeature(((SamplingFeature) f).getGeometry());
        }

        return staFOI;
    }
}
