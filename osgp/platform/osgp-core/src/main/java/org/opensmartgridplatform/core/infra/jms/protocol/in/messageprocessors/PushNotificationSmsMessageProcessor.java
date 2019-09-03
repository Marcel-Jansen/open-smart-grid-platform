/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dlmsPushNotificationSmsMessageProcessor")
@Transactional(value = "transactionManager")
public class PushNotificationSmsMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationSmsMessageProcessor.class);

    @Autowired
    private EventNotificationMessageService eventNotificationMessageService;

    @Autowired
    private DeviceRepository deviceRepository;

    protected PushNotificationSmsMessageProcessor() {
        super(MessageType.PUSH_NOTIFICATION_SMS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {

        final MessageMetadata metadata = MessageMetadata.fromMessage(message);

        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, metadata.getOrganisationIdentification(), metadata.getDeviceIdentification());

        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        final Object dataObject = requestMessage.getRequest();

        try {
            final PushNotificationSmsDto pushNotificationSms = (PushNotificationSmsDto) dataObject;

            this.storeSmsAsEvent(pushNotificationSms);

            if (pushNotificationSms.getIpAddress() != null && !"".equals(pushNotificationSms.getIpAddress())) {

                LOGGER.info("Updating device {} IP address from {} to {}", metadata.getDeviceIdentification(),
                        requestMessage.getIpAddress(), pushNotificationSms.getIpAddress());

                // Convert the IP address from String to InetAddress.
                final InetAddress address = InetAddress.getByName(pushNotificationSms.getIpAddress());

                // Lookup device
                final Device device = this.deviceRepository.findByDeviceIdentification(
                        metadata.getDeviceIdentification());
                if (device != null) {
                    device.updateRegistrationData(address, device.getDeviceType());
                    device.updateConnectionDetailsToSuccess();
                    this.deviceRepository.save(device);
                } else {
                    LOGGER.warn(
                            "Device with ID = {} not found. Discard Sms notification request from ip address = {} of "
                                    + "device", metadata.getDeviceIdentification(), address);
                }
            } else {
                LOGGER.warn("Sms notification request for device = {} has no new IP address. Discard request.",
                        metadata.getDeviceIdentification());
            }

        } catch (final UnknownHostException e) {
            LOGGER.error("UnknownHostException", e);
            JMSException jmsException = new JMSException(e.getMessage());
            jmsException.setLinkedException(e);
            throw jmsException;
        }
    }

    private void storeSmsAsEvent(final PushNotificationSmsDto pushNotificationSms) {
        try {
            /*
             * Push notifications for SMS don't contain date/time info, use new
             * Date() as time with the notification.
             */
            this.eventNotificationMessageService.handleEvent(pushNotificationSms.getDeviceIdentification(), new Date(),
                    org.opensmartgridplatform.domain.core.valueobjects.EventType.SMS_NOTIFICATION,
                    pushNotificationSms.getIpAddress(), 0);
        } catch (final UnknownEntityException uee) {
            LOGGER.warn("Unable to store event for Push Notification Sms from unknown device: {}", pushNotificationSms,
                    uee);
        } catch (final Exception e) {
            LOGGER.error("Error storing event for Push Notification Sms: {}", pushNotificationSms, e);
        }
    }
}
