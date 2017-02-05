/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getShort;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;

import cucumber.api.java.en.Given;

public class SmartMeterSteps extends BaseDeviceSteps {

    @Autowired
    private SmartMeterRepository smartMeterRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    /**
     * Given a smart meter exists.
     * .
     * @param settings
     */
    @Given("^a smart meter$")
    @Transactional("txMgrCore")
    public Device aSmartMeter(final Map<String, String> settings) {
        
        final String deviceIdentification = getString(settings, "DeviceIdentification", Defaults.DEVICE_IDENTIFICATION); 
    	SmartMeter smartMeter = new SmartMeter(
    	        deviceIdentification,
        		getString(settings, "Alias", Defaults.ALIAS),
        		getString(settings, "ContainerCity", Defaults.CONTAINER_CITY),
        		getString(settings, "ContainerPostalCode", Defaults.CONTAINER_POSTALCODE),
        		getString(settings, "ContainerStreet", Defaults.CONTAINER_STREET),
        		getString(settings, "ContainerNumber", Defaults.CONTAINER_NUMBER),
        		getString(settings, "ContainerMunicipality", Defaults.CONTAINER_MUNICIPALITY),
        		getFloat(settings, "GPSLatitude", Defaults.LATITUDE),
        		getFloat(settings, "GPSLongitude", Defaults.LONGITUDE)
        		);
    	
    	smartMeter.setSupplier(getString(settings, Keys.SUPPLIER, Defaults.SUPPLIER));
    	
        if (settings.containsKey(Keys.KEY_GATEWAY_DEVICE_ID)) {
            smartMeter.setChannel(getShort(settings, Keys.KEY_CHANNEL, Defaults.CHANNEL));
            final Device smartEMeter = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.KEY_GATEWAY_DEVICE_ID));
            smartMeter.updateGatewayDevice(smartEMeter);
        }
    	
    	smartMeterRepository.save(smartMeter);
    	
    	return this.updateDevice(deviceIdentification, settings);
    }
}
