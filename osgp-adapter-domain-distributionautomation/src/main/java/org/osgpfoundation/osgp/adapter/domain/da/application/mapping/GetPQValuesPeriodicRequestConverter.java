/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.domain.da.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.osgpfoundation.osgp.dto.da.GetPQValuesPeriodicRequestDto;

public class GetPQValuesPeriodicRequestConverter extends BidirectionalConverter<GetPQValuesPeriodicRequest, GetPQValuesPeriodicRequestDto> {

    @Override
    public GetPQValuesPeriodicRequestDto convertTo(final GetPQValuesPeriodicRequest source, final Type<GetPQValuesPeriodicRequestDto> destinationType) {
        return new GetPQValuesPeriodicRequestDto("", source.getFrom(), source.getTo());
    }

    @Override
    public GetPQValuesPeriodicRequest convertFrom(final GetPQValuesPeriodicRequestDto source, final Type<GetPQValuesPeriodicRequest> destinationType) {
        return new GetPQValuesPeriodicRequest(source.getFrom(), source.getTo());
    }
}
