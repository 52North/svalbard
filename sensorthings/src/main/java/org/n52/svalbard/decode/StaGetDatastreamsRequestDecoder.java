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
package org.n52.svalbard.decode;

import com.fasterxml.jackson.databind.JsonNode;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.decode.json.AbstractSosRequestDecoder;

import org.n52.svalbard.decode.exception.DecodingException;

/**
 * SensorThings API decoder for Datastream requests.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamsRequestDecoder extends AbstractSosRequestDecoder<GetObservationRequest> {

    public StaGetDatastreamsRequestDecoder() {
        super(GetObservationRequest.class, StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                SosConstants.Operations.GetObservation);
    }
    @Override
    protected String getSchemaURI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected GetObservationRequest decodeRequest(JsonNode node) throws DecodingException {
        GetObservationRequest r = new GetObservationRequest();
//        r.setFeatureIdentifiers(parseStringOrStringList(node.path(FEATURE_OF_INTEREST)));
//        r.setObservedProperties(parseStringOrStringList(node.path(OBSERVED_PROPERTY)));
//        r.setOfferings(parseStringOrStringList(node.path(OFFERING)));
//        r.setProcedures(parseStringOrStringList(node.path(PROCEDURE)));
//        r.setResponseFormat(node.path(RESPONSE_FORMAT).textValue());
//        r.setResponseMode(node.path(RESPONSE_MODE).textValue());
//        r.setResultModel(node.path(RESULT_MODEL).textValue());
//        r.setResultFilter(parseComparisonFilter(node.path(RESULT_FILTER)));
//        r.setSpatialFilter(parseSpatialFilter(node.path(SPATIAL_FILTER)));
//        r.setTemporalFilters(parseTemporalFilters(node.path(TEMPORAL_FILTER)));
//        // TODO whats that for?
//        r.setRequestString(Json.print(node));


        return r;
    }

}
