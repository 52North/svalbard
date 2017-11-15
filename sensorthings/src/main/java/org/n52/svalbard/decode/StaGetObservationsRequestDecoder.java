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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.decode.exception.DecodingException;


/**
 * SensorThings API GET request for {@link org.n52.shetland.ogc.om.OmObservation}s
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationsRequestDecoder extends AbstractStaRequestDecoder<GetObservationRequest> {
//    public class StaGetObservationsRequestDecoder extends AbstractStaRequestDecoder<StaGetObservationsRequest> {

    private StaConstants.PathSegment resource;
    private List<StaConstants.PathSegment> path;
    private Map<StaConstants.QueryOption, String> queryOptions;

    public StaGetObservationsRequestDecoder() {
        super(GetObservationRequest.class, StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, StaConstants.Operation.GET_OBSERVATIONS.name());
    }

    @Override
    public GetObservationRequest decodeJSON(JsonNode node, boolean validate) throws DecodingException {
        // node should be empty for all get requests

//        if (validate) {
//            JSONValidator.getInstance().validateAndThrow(node, getSchemaURI());
//        }
        GetObservationRequest r = decodeRequest(node);
//        t.setExtensions(parseExtensions(node.path(JSONConstants.EXTENSIONS)));
        return r;
    }

    @Override
    public GetObservationRequest decodeRequest(JsonNode node) throws DecodingException {
        // node should be empty for all get requests

        if (path.isEmpty()) {
            throw new DecodingException("There is no resource in path.");
        } else {

            // create regular (SOS) database request
            GetObservationRequest request = new GetObservationRequest(Sos1Constants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation.name());

            // iterate path to add request parameters

            // handle parameters
            StaConstants.PathComponent queryComponent = null;
            for (int i = path.size() - 1; i >= 0; i--) {
                StaConstants.PathSegment segment = path.get(i);
                StaConstants.PathComponent component = segment.getComponent();
                String id = segment.getId();

                if (component instanceof StaConstants.EntityPathComponent) {

                    try {
                        // set the main resource (once)
                        if (queryComponent == null) {
                            queryComponent = component;

                            // ignore ID, as this is no GetObservationById request

                        } else if (id != null && !id.isEmpty()) {
                            // ignore path components without ID

                            if (component.equals(StaConstants.EntitySet.Datastreams)) {

                                // set procedure, offering, observed property and feature of interest from datastream

                                // procedure
                                String procedure = id.replaceFirst("_op.*$", "");
                                if (procedure.startsWith("pr") && procedure.length() > 2
                                        && request.getProcedures().isEmpty()) {
                                    request.setProcedures(Arrays.asList(procedure.substring(2)));
                                }

                                // observed property
                                String observedProperty = id.replaceFirst("^pr.*_", "").replaceFirst("_of.*$", "");
                                if (observedProperty.startsWith("op") && observedProperty.length() > 2
                                        && request.getObservedProperties().isEmpty()) {
                                    request.setObservedProperties(Arrays.asList(observedProperty.substring(2)));
                                }

                                // offering
                                String offerings = id.replaceFirst("^pr.*_op.*_", "").replaceFirst("_fi.*$", "");
                                if (offerings.startsWith("of") && offerings.length() > 2
                                        && request.getOfferings().isEmpty()) {

                                    for (String of : Arrays.asList(offerings.substring(2).split("_"))) {

                                        if (!of.isEmpty()) {
                                            request.addOffering(of);
                                        }
                                    }
                                }

                                // feature of interest
                                String[] split = id.split("_");
                                String featureOfInterest = split[split.length - 1];
                                if (featureOfInterest.startsWith("fi") && featureOfInterest.length() > 2
                                        && request.getFeatureIdentifiers().isEmpty()) {
                                    request.setFeatureIdentifiers(Arrays.asList(featureOfInterest.substring(2)));
                                }

                            } else if (component.equals(StaConstants.EntitySet.FeaturesOfInterest)) {

                                if (request.getFeatureIdentifiers() != null || !request.getFeatureIdentifiers().isEmpty()) {
                                    throw new IOException("The resource path contains contradicting information in " + segment.toString());
                                } else {
                                    request.setFeatureIdentifiers(Arrays.asList(id));
                                }
                            } else if (component.equals(StaConstants.EntitySet.ObservedProperties)) {

                                if (request.getObservedProperties() != null || !request.getObservedProperties().isEmpty()) {
                                    throw new IOException("The resource path contains contradicting information in " + segment.toString());
                                } else {
                                    request.setObservedProperties(Arrays.asList(id));
                                }

                            } else if (component.equals(StaConstants.EntitySet.Sensors)
                                    || component.equals(StaConstants.EntitySet.Things)) {

                                if (request.getProcedures() != null || !request.getProcedures().isEmpty()) {
                                    throw new IOException("The resource path contains contradicting information in " + segment.toString());
                                } else {
                                    request.setProcedures(Arrays.asList(id));
                                }
                            } else {
                                throw new UnsupportedOperationException("Resource type '" + component.toString() + "' is not supported yet.");
                            }
                        }
                        else {}

                    } catch (IOException ioe) {
                        throw new DecodingException("Error while decoding request! Message: %s", ioe.getMessage());
                    }

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
