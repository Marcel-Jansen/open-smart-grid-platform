/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;

public class SetDeviceLifecycleStatusResponseData extends ActionResponse {

    private static final long serialVersionUID = 3636769765482239443L;

    private final String gatewayDeviceIdentification;
    private final DeviceLifecycleStatus deviceLifecycleStatus;

    public SetDeviceLifecycleStatusResponseData(final String gatewayDeviceIdentification, final DeviceLifecycleStatus deviceLifecycleStatus) {
        this.gatewayDeviceIdentification = gatewayDeviceIdentification;
        this.deviceLifecycleStatus = deviceLifecycleStatus;
    }

    public String getGatewayDeviceIdentification() {
        return this.gatewayDeviceIdentification;
    }

    public DeviceLifecycleStatus getDeviceLifecycleStatus() {
        return this.deviceLifecycleStatus;
    }

}
