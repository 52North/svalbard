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
import org.n52.shetland.ogc.sta.StaDatastream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.response.GetDatastreamsResponse;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * Request encoder for the SensorThings API Datastreams resource
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamResponseEncoder extends JSONEncoder<GetObservationResponse> {

//    inherited:
//    private final Set<EncoderKey> encoderKeys;
//    private EncoderRepository encoderRepository;
//    private final StaObservationEncoder observationEncoder;

    public StaGetDatastreamResponseEncoder() {
        //super(GetDatastreamsResponse.class, StaConstants.STA_HTTP_GET_PARAMETERNAME_DATASTREAMS);
        //super(GetDatastreamsResponse.class, Sos2Constants.EN_GET_OBSERVATION);

        super(GetObservationResponse.class,
                new OperationResponseEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        Sos2Constants.EN_GET_OBSERVATION, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode jsonNode, GetObservationResponse t) throws EncodingException {

        List<OmObservation> observationCollection = t.getObservationCollection();
        List<StaDatastream> datastreams = new ArrayList<>();

        // create datastreams from observations, group observations by their OmObservationConstellation

        Iterator<StaDatastream> dsIterator;
        Boolean found;

        for (Iterator<OmObservation> oIterator = observationCollection.iterator(); oIterator.hasNext();) {

            OmObservation o = oIterator.next();

            if (datastreams.size() == 0) {
                datastreams.add(new StaDatastream(o));

            } else {

                // find matching datastream for this observation
                dsIterator = datastreams.iterator();
                found = false;

                while (!found && dsIterator.hasNext()) {
                    StaDatastream ds = dsIterator.next();

                    if (o.getObservationConstellation().equals(ds.getObservationConstellation())) {
                        ds.addObservation(o);
                        found = true;
                    }
                }

                // if no match was found, create new Datastream from this observation
                if (!found) {
                    datastreams.add(new StaDatastream(o));
                }
            }
        }
        
        for (Iterator<StaDatastream> dsIterator2 = datastreams.iterator(); dsIterator2.hasNext();) {
            
            StaDatastream ds = dsIterator2.next();

//            jsonNode.put(StaConstants.STA_HTTP_GET_PARAMETERNAME_DATASTREAMS, ds);
        }

        // encode observatios for every Datastream
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonString = mapper.writeValueAsString(obj);

        
    }

    @Override
    public JsonNode encodeJSON(GetObservationResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        n.put(JSONConstants.REQUEST, t.getOperationName());
        n.put(JSONConstants.VERSION, t.getVersion());
        n.put(JSONConstants.SERVICE, t.getService());
        encodeResponse(n, t);
        return n;
    }
}
