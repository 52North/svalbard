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

import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response encoder for the SensorThings API Observations resource, used by GetObservation operations.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationResponseEncoder extends StaAbstractGetObservationResponseEncoder<GetObservationResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetObservationResponseEncoder.class);

    public StaGetObservationResponseEncoder() {

        super(GetObservationResponse.class,
            new OperationResponseEncoderKey(Sos1Constants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.EN_GET_OBSERVATION, MediaTypes.APPLICATION_STA));
    }
}
