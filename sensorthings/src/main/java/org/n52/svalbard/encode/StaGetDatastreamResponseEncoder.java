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
import org.n52.shetland.ogc.sta.StaDatastream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.GmlAbstractGeometry;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API Datastreams resource;
 * transforms SOS Observations into SensorThings Datastreams and Observations.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamResponseEncoder extends JSONEncoder<GetObservationResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetDatastreamResponseEncoder.class);

    public StaGetDatastreamResponseEncoder() {

        super(GetObservationResponse.class,
                new OperationResponseEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        Sos2Constants.EN_GET_OBSERVATION, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, GetObservationResponse t) throws EncodingException {

        List<OmObservation> observationCollection = t.getObservationCollection();
        List<StaDatastream> datastreams = new ArrayList<>();

        // create datastreams from observations, group observations by their OmObservationConstellation
        Iterator<StaDatastream> dsIterator;
        Boolean found;

        for (OmObservation o : observationCollection) {
            if (datastreams.isEmpty()) {

                String id = createDatastreamId(o.getObservationConstellation());
                String ot = getObservationType(o);

                if (ot.equals(StaConstants.OBSERVATION_TYPE_TRUTH_OBSERVATION)) {
                    datastreams.add(new StaDatastream(id, o.getObservationConstellation(), ot, transformObservation(o, id)));
                } else {
                    UoM uom = o.getValue().getValue().getUnitObject();
                    datastreams.add(new StaDatastream(id, o.getObservationConstellation(), ot, uom, transformObservation(o, id)));
                }

            } else {

                // find matching datastream for this observation
                dsIterator = datastreams.iterator();
                found = false;

                while (!found && dsIterator.hasNext()) {
                    StaDatastream ds = dsIterator.next();

                    if (o.getObservationConstellation().equals(ds.getObservationConstellation())) {
                        ds.addObservation(transformObservation(o, ds.getId()));
                        found = true;
                    }
                }

                // if no match was found, create new Datastream from this observation
                if (!found) {
                    String id = createDatastreamId(o.getObservationConstellation());
                    String ot = getObservationType(o);

                    if (ot.equals(StaConstants.OBSERVATION_TYPE_TRUTH_OBSERVATION)) {
                        datastreams.add(new StaDatastream(id, o.getObservationConstellation(), ot, transformObservation(o, id)));
                    } else {
                        UoM uom = o.getValue().getValue().getUnitObject();
                        datastreams.add(new StaDatastream(id, o.getObservationConstellation(), ot, uom, transformObservation(o, id)));
                    }
                }
            }
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, datastreams.size());
        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode datastreams
        for (StaDatastream ds : datastreams) {
            dsArray.add(encodeObjectToJson(ds));
        }
    }

    @Override
    public JsonNode encodeJSON(GetObservationResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }

    /**
     * create a new ID for a datastream from procedure, observable properties, offering and feature of interest
     * @param oc observation constellation
     * @return an id
     */
    private String createDatastreamId(OmObservationConstellation oc) {

        String[] pr = oc.getProcedureIdentifier().split("/");
        String[] op = oc.getObservablePropertyIdentifier().split("/");
        String[] fi = oc.getFeatureOfInterestIdentifier().split("/");

        StringBuilder sb = new StringBuilder();
        oc.getOfferings().forEach((String s) -> {

                String[] of = s.split("/");
                sb.append(of[of.length - 1]).append("_");
            });

        return "pr" + pr[pr.length - 1] + "_op" + op[op.length - 1] + "_of" + sb.toString() + "fi" + fi[fi.length - 1];
    }

    private String getObservationType(OmObservation o) {

        if (o.getValue().getValue().getClass() == CategoryValue.class) {
            return StaConstants.OBSERVATION_TYPE_CATEGORY_OBSERVATION;

        } else if (o.getValue().getValue().getClass() == CountValue.class) {
            return StaConstants.OBSERVATION_TYPE_COUNT_OBSERVATION;

        } else if (o.getValue().getValue().getClass() == QuantityValue.class) {
            return StaConstants.OBSERVATION_TYPE_MEASUREMENT;

        } else if (o.getValue().getValue().getClass() == BooleanValue.class) {
            return StaConstants.OBSERVATION_TYPE_TRUTH_OBSERVATION;

        } else {
            return StaConstants.OBSERVATION_TYPE_OBSERVATION;
        }
    }

    /**
     * transform a SOS Observation into a STA Observation
     * @param o SOS Observation
     * @return SensorThings Observation
     */
    private StaObservation transformObservation(OmObservation o, String datastreamID) {

        StaObservation staObservation = new StaObservation(o.getObservationID());

        staObservation.setPhenomenonTime(o.getPhenomenonTime());
        staObservation.setResult(o.getValue().getValue().getValue().toString());

        staObservation.setResultTime(o.getResultTime());
//        private String resultQuality; // [0..n]
        staObservation.setValidTime(o.getValidTime());
//        private String parameters; // [0..1]
//
        staObservation.setDatastream(datastreamID);

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
