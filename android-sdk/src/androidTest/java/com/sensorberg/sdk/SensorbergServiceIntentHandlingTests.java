package com.sensorberg.sdk;

import com.sensorberg.sdk.di.TestComponent;
import com.sensorberg.sdk.internal.interfaces.BluetoothPlatform;
import com.sensorberg.sdk.internal.interfaces.Platform;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import util.TestConstants;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class SensorbergServiceIntentHandlingTests {

    private SensorbergService tested;

    @Before
    public void setUp() throws Exception {
        ((TestComponent) SensorbergTestApplication.getComponent()).inject(this);

        tested = spy(new SensorbergService());
        tested.onCreate();
        tested.bluetoothPlatform = Mockito.mock(BluetoothPlatform.class);
        tested.platform = Mockito.mock(Platform.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void should_handle_empty_intent() throws Exception {
        Intent sensorbergServiceStartIntent = new Intent(InstrumentationRegistry.getContext(), SensorbergService.class);

        int handleIntentResult = tested.handleIntent(sensorbergServiceStartIntent);

        Assertions.assertThat(handleIntentResult).isEqualTo(SensorbergService.START_STICKY);
        Assertions.assertThat(tested.bootstrapper).isNull();

        Mockito.verify(tested, Mockito.times(1)).handleDebuggingIntent(sensorbergServiceStartIntent, tested);
        Mockito.verify(tested, Mockito.times(1)).handleIntentEvenIfNoBootstrapperPresent(sensorbergServiceStartIntent);
        Mockito.verify(tested, Mockito.times(1)).updateDiskConfiguration(sensorbergServiceStartIntent);

        Mockito.verify(tested, Mockito.times(0)).startSensorbergService(anyString());
        Mockito.verify(tested, Mockito.times(0)).stopSensorbergService();
        Mockito.verify(tested, Mockito.times(0)).handleIntentMessage(sensorbergServiceStartIntent);
    }

    @Test
    public void should_handle_intent_with_shutdown_message() throws Exception {
        Intent sensorbergServiceShutdownIntent = new Intent(InstrumentationRegistry.getContext(), SensorbergService.class);
        sensorbergServiceShutdownIntent.putExtra(SensorbergServiceMessage.EXTRA_GENERIC_TYPE, SensorbergServiceMessage.MSG_SHUTDOWN);

        int handleIntentResult = tested.handleIntent(sensorbergServiceShutdownIntent);

        Assertions.assertThat(handleIntentResult).isEqualTo(SensorbergService.START_NOT_STICKY);
        Mockito.verify(tested, Mockito.times(1)).handleDebuggingIntent(sensorbergServiceShutdownIntent, tested);
        Mockito.verify(tested, Mockito.times(1)).handleIntentEvenIfNoBootstrapperPresent(sensorbergServiceShutdownIntent);
        Mockito.verify(tested, Mockito.times(1)).stopSensorbergService();

        Mockito.verify(tested, Mockito.times(0)).updateDiskConfiguration(sensorbergServiceShutdownIntent);
        Mockito.verify(tested, Mockito.times(0)).startSensorbergService(anyString());
        Mockito.verify(tested, Mockito.times(0)).handleIntentMessage(sensorbergServiceShutdownIntent);
    }

    @Test
    public void should_handle_intent_with_start_message_and_api_key() throws Exception {
        Intent sensorbergServiceStartIntent = new Intent(InstrumentationRegistry.getContext(), SensorbergService.class);
        sensorbergServiceStartIntent.putExtra(SensorbergServiceMessage.EXTRA_START_SERVICE, 1);
        sensorbergServiceStartIntent.putExtra(SensorbergServiceMessage.EXTRA_API_KEY, TestConstants.API_TOKEN_DEFAULT);

        int handleIntentResult = tested.handleIntent(sensorbergServiceStartIntent);

        Assertions.assertThat(handleIntentResult).isEqualTo(SensorbergService.START_STICKY);
        Mockito.verify(tested, Mockito.times(1)).handleDebuggingIntent(sensorbergServiceStartIntent, tested);
        Mockito.verify(tested, Mockito.times(1)).handleIntentEvenIfNoBootstrapperPresent(sensorbergServiceStartIntent);
        Mockito.verify(tested, Mockito.times(1)).updateDiskConfiguration(sensorbergServiceStartIntent);
        Mockito.verify(tested, Mockito.times(1)).startSensorbergService(TestConstants.API_TOKEN_DEFAULT);

        Mockito.verify(tested, Mockito.times(0)).stopSensorbergService();
        Mockito.verify(tested, Mockito.times(0)).handleIntentMessage(sensorbergServiceStartIntent);
    }

    @Test
    public void should_handle_intent_with_generic_noop_message() throws Exception {
        Intent sensorbergServiceStartIntent = new Intent(InstrumentationRegistry.getContext(), SensorbergService.class);
        sensorbergServiceStartIntent.putExtra(SensorbergServiceMessage.EXTRA_GENERIC_TYPE, -1);

        int handleIntentResult = tested.handleIntent(sensorbergServiceStartIntent);

        Assertions.assertThat(handleIntentResult).isEqualTo(SensorbergService.START_STICKY);
        Assertions.assertThat(tested.bootstrapper).isNotNull();

        Mockito.verify(tested, Mockito.times(1)).handleDebuggingIntent(sensorbergServiceStartIntent, tested);
        Mockito.verify(tested, Mockito.times(1)).handleIntentEvenIfNoBootstrapperPresent(sensorbergServiceStartIntent);
        Mockito.verify(tested, Mockito.times(1)).updateDiskConfiguration(sensorbergServiceStartIntent);
        Mockito.verify(tested, Mockito.times(1)).handleIntentMessage(sensorbergServiceStartIntent);

        Mockito.verify(tested, Mockito.times(0)).startSensorbergService(anyString());
        Mockito.verify(tested, Mockito.times(0)).stopSensorbergService();
    }

    //TODO handleIntentMessage
}
