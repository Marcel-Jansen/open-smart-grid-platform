Feature: ConfigurationManagement SetConfiguration
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Set configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns a set configuration status over OSLP
      | Status | OK |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001   |
      | LightType            | <LightType>         |
      | DcLights             | <DcLights>          |
      | DcMap                | <DcMap>             |
      | RcType               | <RcType>            |
      | RcMap                | <RcMap>             |
      | PreferredLinkType    | <PreferredLinkType> |
      | MeterType            | <MeterType>         |
      | ShortInterval        | <ShortInterval>     |
      | LongInterval         | <LongInterval>      |
      | IntervalType         | <IntervalType>      |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set configuration response message for device "TEST1024000000001"
      | Result            | OK                  |
      | Description       |                     |
      | LightType         | <LightType>         |
      | DcLights          | <DcLights>          |
      | DcMap             | <DcMap>             |
      | RcType            | <RcType>            |
      | RcMap             | <RcMap>             |
      | PreferredLinkType | <PreferredLinkType> |
      | MeterType         | <MeterType>         |
      | ShortInterval     | <ShortInterval>     |
      | LongInterval      | <LongInterval>      |
      | IntervalType      | <IntervalType>      |

    Examples: 
      | LightType               | DcLights | DcMap   | RcType | RcMap | PreferredLinkType | MeterType | ShortInterval | LongInterval | IntervalType |
      | RELAY                   |          |         |        |       |                   | AUX       |               |              |              |
      | RELAY                   |          |         | TARIFF |   1,1 |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT         |          |         |        |       |                   |           |               |              |              |
      | ONE_TO_TEN_VOLT_REVERSE |          |         |        |       |                   |           |               |              |              |
      | DALI                    |        2 | 1,2;2,1 |        |       |                   |           |               |              |              |
      |                         |          |         |        |       |                   |           |            30 |              |              |
      |                         |          |         |        |       | GPRS              |           |               |              |              |
      | DALI                    |          |         |        |       |                   |           |               |              |              |
      | RELAY                   |          |         | LIGHT  |   1,1 |                   |           |               |              |              |
      |                         |          |         |        |       |                   |           |               |              |              |
      |                         |          |         |        |       |                   | P1        |               |              |              |
      |                         |          |         |        |       |                   |           |               |           10 | DAYS         |
      |                         |          |         |        |       |                   |           |               |           10 | MONTHS       |
      | RELAY                   |          |         | LIGHT  |   1,1 | CDMA              | PULSE     |            15 |           30 | DAYS         |
      | RELAY                   |          |         | LIGHT  |   1,1 | ETHERNET          | P1        |            15 |            1 | DAYS         |

  #	Scenario: Set configuration data with invalid data
  @OslpMockServer
  Scenario: Failed set configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns a set configuration status over OSLP
      | Status            | FAILURE  |
      | LightType         | RELAY    |
      | DcLights          |          |
      | DcMap             |          |
      | RcType            | LIGHT    |
      | RcMap             |      1,1 |
      | PreferredLinkType | ETHERNET |
      | MeterType         | P1       |
      | ShortInterval     |       15 |
      | LongInterval      |       30 |
      | IntervalType      | DAYS     |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    # Note: The exception returns always the string "Exception occurred while getting device configuration"
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports failure |

  @OslpMockServer
  Scenario: Rejected set configuration of a device
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns a set configuration status over OSLP
      | Status            | REJECTED |
      | LightType         | RELAY    |
      | DcLights          |          |
      | DcMap             |          |
      | RcType            | LIGHT    |
      | RcMap             |      1,1 |
      | PreferredLinkType | ETHERNET |
      | MeterType         | P1       |
      | ShortInterval     |       15 |
      | LongInterval      |       30 |
      | IntervalType      | DAYS     |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the set configuration async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set configuration OSLP message is sent to device "TEST1024000000001"
    # Note: The exception returns always the string "Exception occurred while getting device configuration"
    And the platform buffers a set configuration response message for device "TEST1024000000001" contains soap fault
      | Message | Device reports rejected |

Scenario Outline: Set configuration data with invalid data
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    And the device returns a set configuration status over OSLP
      | Status | OK |
    When receiving a set configuration request
      | DeviceIdentification | TEST1024000000001   |
      | LightType            | <LightType>         |
      | DcLights             | <DcLights>          |
      | DcMap                | <DcMap>             |
      | RcType               | <RcType>            |
      | RcMap                | <RcMap>             |
      | PreferredLinkType    | <PreferredLinkType> |
      | MeterType            | <MeterType>         |
      | ShortInterval        | <ShortInterval>     |
      | LongInterval         | <LongInterval>      |
      | IntervalType         | <IntervalType>      |
    Then the set configuration async response contains soap fault
      | FaultCode        | <FaultCode>        |
      | FaultString      | <FaultString>      |
      | ValidationErrors | <ValidationErrors> |

    Examples: 
      | LightType | DcLights | DcMap               | RcType          | RcMap                       | ShortInterval | PreferredLinkType | MeterType | LongInterval | IntervalType | FaultCode       | FaultString      | ValidationErrors                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | RELAY     |          |                     |                 |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | RELAY     |          |                     | TARIFF          |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | RELAY     |          |                     | TARIFF_REVERSED |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      |           |          |                     |                 |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-complex-type.2.4.b: The content of element 'ns2:RelayMap' is not complete. One of '{"http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10":RelayType}' is expected.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      |           |          |                     | TARIFF          |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      |           |          |                     | TARIFF_REVERSED |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Index' is not valid.;cvc-minInclusive-valid: Value '0' is not facet-valid with respect to minInclusive '1' for type '#AnonType_AddressRelayMap'.;cvc-type.3.1.3: The value '0' of element 'ns2:Address' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | DALI      |        5 | 1,1;2,2;3,3;4,4;5,5 |                 |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_NumberOfLightsDaliConfiguration'.;cvc-type.3.1.3: The value '5' of element 'ns2:NumberOfLights' is not valid.;cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:IndexAddressMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '5' is not facet-valid with respect to maxInclusive '4' for type '#AnonType_IndexIndexAddressMap'.;cvc-type.3.1.3: The value '5' of element 'ns2:Index' is not valid.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | RELAY     |          |                     | LIGHT           | 1,1;2,2;3,3;4,4;5,5;6,6;7,7 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid.;cvc-complex-type.2.4.d: Invalid content was found starting with element 'ns2:RelayMap'. No child element is expected at this point.;cvc-maxInclusive-valid: Value '7' is not facet-valid with respect to maxInclusive '6' for type '#AnonType_IndexRelayMap'.;cvc-type.3.1.3: The value '7' of element 'ns2:Index' is not valid. |
      #
      # Note: The items below are for other another test case
      #| RELAY           |        1 |                 1,1 |                 |                             |               |                   |           |              |              | SOAP-ENV:Server | VALIDATION_ERROR | |
      #| DALI            |          |                     | LIGHT           |                         1,1 |               |                   |           |              |              | SOAP-ENV:Server | Validation error | |
      #| DALI            |        2 |                 1,1 |                 |                             |               |                   |           |              |              | SOAP-ENV:Client | VALIDATION_ERROR | |
      #| DALI            |        1 | 1,1;2,2             |                 |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | |
      #|                 |        1 |                     | LIGHT           |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | |
      #| ONE_TO_TEN_VOLT |        1 |                     |                 |                             |               |                   |           |              |              | SOAP-ENV:Client | Validation error | |
      #| ONE_TO_TEN_VOLT |          |                     | TARIFF          |                         1,1 |               |                   |           |              |              | SOAP-ENV:Client | Validation error | |
      #| ONE_TO_TEN_VOLT |          |                     | TARIFF_REVERSED |                         1,1 |               |                   |           |              |              | SOAP-ENV:Server | Validation error | |
      #|                 |          |                     |                 |                             |               |                   |           |           10 |              | SOAP-ENV:Client | Validation error | |
      #|                 |          |                     |                 |                             |            12 |                   |           |              |              | SOAP-ENV:Client | Validation error | |
      #|                 |          |                     |                 |                             |               |                   |           |           31 | DAYS         | SOAP-ENV:Client | Validation error | |
      #|                 |          |                     |                 |                             |               |                   |           |           13 | MONTHS       | SOAP-ENV:Client | Validation error | |