package org.opensmartgridplatform.shared.application.config.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class JmsPropertyReaderTest {

    private static String PROPERTY_PREFIX = "jms.test";

    private static String DEFAULT_QUEUE = "default-queue";
    private static boolean DEFAULT_USE_BACKOFF = true;

    private static String CUSTOM_QUEUE = "custom-queue";
    private static boolean CUSTOM_USE_BACKOFF = false;

    @Mock
    Environment environment;

    @Mock
    JmsConfiguration defaultJmsConfiguration;

    private JmsPropertyReader jmsPropertyReader;

    @Before
    public void setup() {
        this.jmsPropertyReader = new JmsPropertyReader(this.environment, PROPERTY_PREFIX, this.defaultJmsConfiguration);
        when(this.defaultJmsConfiguration.getQueue()).thenReturn(DEFAULT_QUEUE);
        when(this.defaultJmsConfiguration.isUseExponentialBackOff()).thenReturn(DEFAULT_USE_BACKOFF);
    }

    @Test
    public void testGetCustomQueueValueWhenCustomPropertyExists() {
        // Arrange
        final String property = "queue";
        final String propertyName = PROPERTY_PREFIX + "." + property;
        when(this.environment.getProperty(propertyName, String.class)).thenReturn(CUSTOM_QUEUE);

        // Act
        final String actual = this.jmsPropertyReader.get(property, String.class);

        // Assert
        verify(this.environment).getProperty(propertyName, String.class);
        verify(this.defaultJmsConfiguration, never()).getQueue();
        assertThat(actual).isEqualTo(CUSTOM_QUEUE);
    }

    @Test
    public void testGetDefaultQueueValueWhenCustomPropertyDoesNotExist() {
        // Arrange
        final String property = "queue";
        final String propertyName = PROPERTY_PREFIX + "." + property;
        when(this.environment.getProperty(propertyName, String.class)).thenReturn(null);

        // Act
        final String actual = this.jmsPropertyReader.get(property, String.class);

        // Assert
        verify(this.environment).getProperty(propertyName, String.class);
        verify(this.defaultJmsConfiguration).getQueue();
        assertThat(actual).isEqualTo(DEFAULT_QUEUE);
    }

    @Test
    public void testGetCustomBooleanValueWhenCustomPropertyExists() {
        // Arrange
        final String property = "use.exponential.back.off";
        final String propertyName = PROPERTY_PREFIX + "." + property;
        when(this.environment.getProperty(propertyName, boolean.class)).thenReturn(CUSTOM_USE_BACKOFF);

        // Act
        final boolean actual = this.jmsPropertyReader.get(property, boolean.class);

        // Assert
        verify(this.environment).getProperty(propertyName, boolean.class);
        verify(this.defaultJmsConfiguration, never()).isUseExponentialBackOff();
        assertThat(actual).isEqualTo(CUSTOM_USE_BACKOFF);
    }

    @Test
    public void testGetDefaultBooleanValueWhenCustomPropertyDoesNotExist() {
        // Arrange
        final String property = "use.exponential.back.off";
        final String propertyName = PROPERTY_PREFIX + "." + property;
        when(this.environment.getProperty(propertyName, boolean.class)).thenReturn(null);

        // Act
        final boolean actual = this.jmsPropertyReader.get(property, boolean.class);

        // Assert
        verify(this.environment).getProperty(propertyName, boolean.class);
        verify(this.defaultJmsConfiguration).isUseExponentialBackOff();
        assertThat(actual).isEqualTo(DEFAULT_USE_BACKOFF);
    }

}
