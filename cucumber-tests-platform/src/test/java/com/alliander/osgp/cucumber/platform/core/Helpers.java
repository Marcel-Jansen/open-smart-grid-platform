/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

public class Helpers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Helpers.class);

    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";

    /**
     * This shortcut makes a new Map from the given map with the given key value
     * pair
     *
     * @param settings
     *            the input map
     * @param key
     * @param value
     * @return an updated Map with the new key value pair
     */
    public static Map<String, String> addSetting(final Map<String, String> settings, final String key,
            final String value) {
        final Map<String, String> result = new HashMap<>();
        result.putAll(settings);
        result.put(key, value);
        return result;
    }

    /**
     * When running automatic tests, it might be that not each project is
     * started in tomcat. When the repo's are cleared at the beginning of a test
     * run, you get some exceptions when the database wasn't found. Therefore
     * this method is created to ignore that.
     *
     * @param repo
     *            The repository to remove.
     */
    public static <T extends AbstractEntity> void cleanRepoAbstractEntity(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAllInBatch();
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * When running automatic tests, in a beforestep all tables (except for some
     * 'stamdata') are cleared, you get some exceptions when the database wasn't
     * found. Therefore this method is created to ignore that
     *
     * @param repo
     */
    public static <T extends Serializable> void cleanRepoSerializable(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAllInBatch();
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * @param settings
     * @param key
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key) {
        return Boolean.parseBoolean(settings.get(key));
    }

    /**
     * Get the boolean value of the given key in the settings. If it didn't
     * exist return the defaultValue.
     *
     * @param settings
     *            The settings to get the value from.
     * @param key
     *            The key to get the boolean from.
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key, final Boolean defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getBoolean(settings, key);
    }

    /**
     * Get a date time object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the date time.
     * @return The date time.
     */
    public static DateTime getDate(final Map<String, String> settings, final String key) {
        return getDate(settings, key, DateTime.now());
    }

    /**
     * Get a date time object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the date time.
     * @param defaultDate
     *            The default date to return.
     * @return The date time.
     */
    public static DateTime getDate(final Map<String, String> settings, final String key, final DateTime defaultDate) {
        if (!settings.containsKey(key)) {
            return defaultDate;
        }

        return DateTime.parse(settings.get(key));
    }

    /**
     * This is a generic method which will translate the given string to a
     * datetime. Supported: 
     * 
     * now + 3 months 
     * tomorrow - 1 year 
     * yesterday + 2 weeks
     * today at midday
     * yesterday at midnight
     * now at midday + 1 week
     *
     * @param dateString
     * @return
     * @throws Exception
     */
    public static DateTime getDateTime(final String dateString) throws Exception {

        DateTime retval;

        if (dateString.isEmpty()) {
            return null;
        }

        final String pattern = "([a-z ]*)[ ]*([+-]?)[ ]*([0-9]*)[ ]*([a-z]*)";
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(dateString);

        if (m.groupCount() != 4) {
            throw new Exception("Incorrect dateString [" + dateString + "]");
        }

        m.find();

        final String when = m.group(1).toLowerCase();
        final String op = m.group(2);
        final String offset = m.group(3);
        final String what = m.group(4);

        Integer numberToAddOrSubstract = 0;
        if (!offset.isEmpty()) {
            numberToAddOrSubstract = Integer.parseInt(offset);
        }

        final String whenPattern = "([a-z]*)[ ]*([a-z]*)[ ]*([a-z]*)?";
        final Matcher whenMatcher = Pattern.compile(whenPattern).matcher(when);
        whenMatcher.find();
        switch (whenMatcher.group(1)) {
        case "tomorrow":
            retval = DateTime.now().plusDays(1);
            break;
        case "yesterday":
            retval = DateTime.now().minusDays(1);
            break;
        case "now":
        case "today":
            retval = DateTime.now();
            break;
        default:
            throw new Exception("Incorrect dateString [" + dateString + "], expected the string to begin with tomorrow, yesterday or now or today");
        }
        
        if (whenMatcher.groupCount() > 1 && whenMatcher.group(2).equals("at")) {
            
            switch (whenMatcher.group(3)) {
            case "midday": 
                retval = retval.withHourOfDay(12);
                break;
            case "midnight":
                retval = retval.withHourOfDay(0);
                break;
            default:
                throw new Exception("Incorrect dateString [" + dateString + "], expected \"midday\" or \"midnight\"");
            }
            retval = retval.withMinuteOfHour(0);
            retval = retval.withSecondOfMinute(0);
        }

        if (op.equals("+")) {
            switch (what) {
            case "days":
                retval = retval.plusDays(numberToAddOrSubstract);
            case "hours":
                retval = retval.plusHours(numberToAddOrSubstract);
            case "weeks":
                retval = retval.plusWeeks(numberToAddOrSubstract);
            case "months":
                retval = retval.plusMonths(numberToAddOrSubstract);
            case "years":
                retval = retval.plusYears(numberToAddOrSubstract);
            }
        } else {
            switch (what) {
            case "days":
                retval = retval.minusDays(numberToAddOrSubstract);
            case "hours":
                retval = retval.minusHours(numberToAddOrSubstract);
            case "weeks":
                retval = retval.minusWeeks(numberToAddOrSubstract);
            case "months":
                retval = retval.minusMonths(numberToAddOrSubstract);
            case "years":
                retval = retval.minusYears(numberToAddOrSubstract);
            }

        }

        return retval;
    }

    /**
     *
     * @param startDate
     * @param defaultStartDate
     * @return
     * @throws Exception
     */
    public static DateTime getDateTime2(final String startDate, final DateTime defaultStartDate) throws Exception {
        if (startDate == null) {
            return defaultStartDate;
        }
        final DateTime dateTime = getDateTime(startDate);
        if (dateTime == null) {
            return defaultStartDate;
        }
        return dateTime;
    }

    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType) {
        if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
            return null;
        }

        return Enum.valueOf(enumType, settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param enumType
     * @param defaultValue
     * @return
     */
    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType, final E defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getEnum(settings, key, enumType);
    }

    /**
     * Get a float object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the float object.
     * @param defaultValue
     *            The default value to return if the key wasn't found.
     * @return The float object.
     */
    public static Float getFloat(final Map<String, String> settings, final String key, final Float defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return Float.parseFloat(settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(final Map<String, String> settings, final String key) {

        return Integer.parseInt(settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(final Map<String, String> settings, final String key, final Integer defaultValue) {

        if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
            return defaultValue;
        }

        return getInteger(settings, key);
    }

    /**
     * Get a long value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @return The long
     */
    public static Long getLong(final Map<String, String> settings, final String key) {

        if (!settings.containsKey(key)) {
            return new java.util.Random().nextLong();
        }

        final Long value = Long.parseLong(settings.get(key));
        return value;
    }

    /**
     * Get a long value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return The long
     */
    public static Long getLong(final Map<String, String> settings, final String key, final Long defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Long value = Long.parseLong(settings.get(key));
        return value;
    }

    public static Short getShort(final Map<String, String> settings, final String key) {
        return Short.parseShort(settings.get(key));
    }

    /**
     * Get a Short value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return The long
     */
    public static Short getShort(final Map<String, String> settings, final String key, final Short defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Short value = Short.parseShort(settings.get(key));
        return value;
    }

    public static String getString(final Map<String, String> settings, final String key) {
        return settings.get(key);
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(final Map<String, String> settings, final String key, final String defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getString(settings, key);
    }

    /**
     * Check the correlationUid in the response and save it in the current
     * scenarioContext.
     *
     * @param response
     *            The response to find the correlationUid in.
     * @param organizationIdentification
     *            The organizationIdentifier used. Default test-org will be
     *            used.
     * @throws Throwable
     */
    public static void saveCorrelationUidInScenarioContext(final String correlationUid,
            String organizationIdentification) throws Throwable {
        if (organizationIdentification == null || organizationIdentification.isEmpty()) {
            organizationIdentification = Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
        }

        // Validate the correlation-id starts with correct organization
        Assert.isTrue(correlationUid.startsWith(organizationIdentification));
        ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, correlationUid);
    }

    /**
     * Store the correlationUid and deviceIdentification in the ScenarioContext,
     * given the AsyncResponse
     *
     * @param asyncResponse
     *            The AsyncResponse used to retrieve the values (mentioned
     *            above) to store in the ScenarioContext
     *
     * @throws Throwable
     */
    public static void saveAsyncResponse(final AsyncResponse asyncResponse) throws Throwable {
        ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, asyncResponse.getDeviceIdentification());
    }

}
