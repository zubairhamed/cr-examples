package com.bosch.cr.integration.examples;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.cr.integration.IntegrationClientConfiguration;
import com.bosch.cr.integration.IntegrationClient;
import com.bosch.cr.integration.IntegrationClientImpl;
import com.bosch.cr.integration.ThingIntegration;
import com.bosch.cr.integration.messaging.ProviderConfiguration;
import com.bosch.cr.integration.messaging.stomp.StompProviderConfiguration;

public class Examples
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Examples.class);
   public static final String BOSCH_IOT_CENTRAL_REGISTRY_ENDPOINT_URI =
      "wss://events-stomper.apps.bosch-iot-cloud.com:443/";

   public static void main(final String[] args) throws InterruptedException
   {
      /* Create a new integration client */
      final ProviderConfiguration providerConfiguration = StompProviderConfiguration.newBuilder()
         .proxyHost("cache.innovations.de")    // Configure proxy (if needed)
         .proxyPort(3128)                      // Configure proxy (if needed)
         .sslKeyStoreLocation(Examples.class.getResource("/bosch-iot-cloud.jks"))
         .sslKeyStorePassword("jks")
         .build();

      final IntegrationClientConfiguration integrationClientConfiguration = IntegrationClientConfiguration.newBuilder()
         .clientId("examples_client")
         .centralRegistryEndpointUri(BOSCH_IOT_CENTRAL_REGISTRY_ENDPOINT_URI)
         .providerConfiguration(providerConfiguration)
         .build();

      final IntegrationClient integrationClient = IntegrationClientImpl.newInstance(integrationClientConfiguration);

      /* Register for *all* lifecycle events of *all* things */
      final String allThings_lifecycleRegistration = "allThings_lifecycleRegistration";
      integrationClient.registerForThingLifecycleEvent(allThings_lifecycleRegistration,
         lifecycle -> LOGGER.info("lifecycle received: {}", lifecycle));

      /* Register for *all* attribute changes of *all* things */
      final String allThings_attributeChangeRegistration = "allThings_attributeChangeRegistration";
      integrationClient.registerForThingAttributeChange(allThings_attributeChangeRegistration,
         change -> LOGGER.info("attributeChange received: {}", change));

      /* Register for *specific* attribute changes of all things */
      final String allThings_specificAttributeChangeRegistration = "allThings_specificAttributeChangeRegistration";
      integrationClient.registerForThingAttributeChange(allThings_specificAttributeChangeRegistration, "address/city",
         change -> LOGGER.info("attributeChange received: {}", change));

      /* Create a new thing and define handlers for success and failure */
      integrationClient.createThing("myThing")
         .onSuccess( thing -> LOGGER.info("Thing created: {}", thing))
         .onFailure(throwable -> LOGGER.error("Create Thing Failed: {}", throwable))
         .apply();

      /* Terminate a registration using the client */
      integrationClient.deregister(allThings_lifecycleRegistration);

      /*--------------------------------------------------------------------------------------------------------------*/

      /* Create a handle for an existing thing */
      final ThingIntegration myThing = integrationClient.forThing("myThing");

      /* Register for *all* lifecycle events of a *specific* thing */
      final String myThing_lifecycleRegistration = "myThing_lifecycleRegistration";
      myThing.registerForThingLifecycleEvent(myThing_lifecycleRegistration,
         lifecycleEvent -> LOGGER.info("lifecycle received: {}", lifecycleEvent));

      /* Register for *all* attribute changes of a *specific* thing */
      final String myThing_attributeChangeRegistration = "myThing_attributeChangeRegistration";
      myThing.registerForThingAttributeChange(myThing_attributeChangeRegistration,
         change -> LOGGER.info("attributeChange received: {}", change));

      /* Register for *specific* attribute changes of a *specific* thing */
      final String myThing_specificAttributeChangeRegistration = "myThing_specificAttributeChangeRegistration";
      myThing.registerForThingAttributeChange(myThing_specificAttributeChangeRegistration, "address/city",
         change -> LOGGER.info("attributeChange received: {}", change));

      /* Create new attribute for a thing and define handlers for success and failure */
      myThing.changeAttribute("address/city", "Berlin")
         .onSuccess( _void -> LOGGER.info("New attribute created successfully."))
         .onFailure( throwable -> LOGGER.error("Failed to create new attribute: {}", throwable))
         .apply();

      /* Terminate a registration using a thingHandle */
      myThing.deregister(myThing_lifecycleRegistration);

      /* Delete a thing */
      myThing.deleteThing();

      /* Destroy the client and wait 30 seconds for its graceful shutdown */
      integrationClient.destroy(30, TimeUnit.SECONDS);
   }
}