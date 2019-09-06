/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetDeviceLifecycleStatusResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = 2422800403820207739L;

    private final String deviceIdentification;
    private final DeviceLifecycleStatusDto deviceLifecycleStatus;

    public SetDeviceLifecycleStatusResponseDto(final String deviceIdentification, final DeviceLifecycleStatusDto deviceLifecycleStatus) {
        this.deviceIdentification = deviceIdentification;
        this.deviceLifecycleStatus = deviceLifecycleStatus;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public DeviceLifecycleStatusDto getDeviceLifecycleStatus() {
        return this.deviceLifecycleStatus;
    }

}
