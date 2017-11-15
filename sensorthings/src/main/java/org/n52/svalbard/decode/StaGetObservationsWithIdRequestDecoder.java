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
import java.util.List;
import java.util.Map;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.decode.exception.DecodingException;


/**
 * SensorThings API GET request for {@link org.n52.shetland.ogc.om.OmObservation}s with ID.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationsWithIdRequestDecoder extends AbstractStaRequestDecoder<GetObservationByIdRequest> {
//    public class StaGetObservationsRequestDecoder extends AbstractStaRequestDecoder<StaGetObservationsRequest> {

    private StaConstants.PathSegment resource;
    private List<StaConstants.PathSegment> path;
    private Map<StaConstants.QueryOption, String> queryOptions;

    public StaGetObservationsWithIdRequestDecoder() {
        super(GetObservationByIdRequest.class, StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, StaConstants.Operation.GET_OBSERVATIONS_WITH_ID.name());
    }

    @Override
    public GetObservationByIdRequest decodeJSON(JsonNode node, boolean validate) throws DecodingException {
        // node should be empty for all get requests

//        if (validate) {
//            JSONValidator.getInstance().validateAndThrow(node, getSchemaURI());
//        }
        GetObservationByIdRequest r = decodeRequest(node);
//        t.setExtensions(parseExtensions(node.path(JSONConstants.EXTENSIONS)));
        return r;
    }

    @Override
    public GetObservationByIdRequest decodeRequest(JsonNode node) throws DecodingException {
        // node should be empty for all get requests

        if (path.isEmpty()) {
            throw new DecodingException("There is no resource in path.");

        } else if (resource.getId() == null || resource.getId().isEmpty()) {
            throw new DecodingException("There is no resource id.");

        } else {

            // create regular (SOS) database request
            GetObservationByIdRequest request = new GetObservationByIdRequest(Sos1Constants.SOS, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.GetObservationById.name());

            // set ID
            try {
                request.setObservationId(Long.parseLong(resource.getId()));

            } catch (NumberFormatException nfe) {
                throw new DecodingException("STA entity identifier (@iot.id) must be parsable to long: " + nfe.getMessage());
            }

            // iterate path to add request parameters

            // handle parameters
            for (int i = path.size() - 1; i >= 0; i--) {
                StaConstants.PathSegment segment = path.get(i);
                StaConstants.PathComponent component = segment.getComponent();
                String id = segment.getId();

                if (component instanceof StaConstants.EntityPathComponent) {

                    //try {
                        if (segment.equals(resource)) {
                            // ignore

                        } else if (id != null && !id.isEmpty()) {
                            // ignore path components without ID

                            if (component.equals(StaConstants.EntitySet.Observations)) {

                                throw new UnsupportedOperationException("Other STA REST path entities are not supported yet.");

                            } else if (component.equals(StaConstants.EntitySet.Datastreams)) {

                                throw new UnsupportedOperationException("Other STA REST path entities are not supported yet.");

                            } else if (component.equals(StaConstants.EntitySet.FeaturesOfInterest)) {

                                throw new UnsupportedOperationException("Other STA REST path entities are not supported yet.");

                            } else if (component.equals(StaConstants.EntitySet.ObservedProperties)) {

                                throw new UnsupportedOperationException("Other STA REST path entities are not supported yet.");

                            } else if (component.equals(StaConstants.EntitySet.Sensors)
                                    || component.equals(StaConstants.EntitySet.Things)) {

                                throw new UnsupportedOperationException("Other STA REST path entities are not supported yet.");

                            } else {
                                throw new UnsupportedOperationException("Resource type '" + component.toString() + "' is not supported yet.");
                            }
                        }
                        else {}

                    //} catch (IOException ioe) {
                    //    throw new DecodingException("Error while decoding request! Message: %s", ioe.getMessage());
                    //}

                } else if (component instanceof StaConstants.Parameter) {
                    throw new UnsupportedOperationException("STA REST path parameters are not supported yet.");

                    // TODO set request parameters (name, UoM, spatial/temporal filters)
                    /* switch ((StaConstants.Parameter) component) {
                        case name:
                            break;
                        case description:
                            break;
                        case observationType:
                            break;
                        case unitOfMeasurement:
                            break;
                        case observedArea:
                            break;
                        case phenomenonTime:
                            break;
                        case resultTime:
                            break;
                        default:
                            break;
                    } */

                } else if (component instanceof StaConstants.Option) {
                    throw new UnsupportedOperationException("STA REST path options are not supported yet.");

                } else {
                    throw new UnsupportedOperationException("STA REST path segment'" + segment.toString() + "' could not be parsed.");
                }
            }

            return request;
        }
    }

    @Override
    public void setResource(StaConstants.PathSegment resource) {
        this.resource = resource;
    }

    @Override
    public void setPath(List<StaConstants.PathSegment> path) {
        this.path = path;
    }

    @Override
    public void setQueryOptions(Map<StaConstants.QueryOption, String> queryOptions) {
        this.queryOptions = queryOptions;
    }

}
