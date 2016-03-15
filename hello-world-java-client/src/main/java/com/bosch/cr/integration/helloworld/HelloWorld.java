package com.bosch.cr.integration.helloworld;
/*
 *                                            Bosch SI Example Code License
 *                                              Version 1.0, January 2016
 *
 * Copyright 2016 Bosch Software Innovations GmbH ("Bosch SI"). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * BOSCH SI PROVIDES THE PROGRAM "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF
 * ALL NECESSARY SERVICING, REPAIR OR CORRECTION. THIS SHALL NOT APPLY TO MATERIAL DEFECTS AND DEFECTS OF TITLE WHICH
 * BOSCH SI HAS FRAUDULENTLY CONCEALED. APART FROM THE CASES STIPULATED ABOVE, BOSCH SI SHALL BE LIABLE WITHOUT
 * LIMITATION FOR INTENT OR GROSS NEGLIGENCE, FOR INJURIES TO LIFE, BODY OR HEALTH AND ACCORDING TO THE PROVISIONS OF
 * THE GERMAN PRODUCT LIABILITY ACT (PRODUKTHAFTUNGSGESETZ). THE SCOPE OF A GUARANTEE GRANTED BY BOSCH SI SHALL REMAIN
 * UNAFFECTED BY LIMITATIONS OF LIABILITY. IN ALL OTHER CASES, LIABILITY OF BOSCH SI IS EXCLUDED. THESE LIMITATIONS OF
 * LIABILITY ALSO APPLY IN REGARD TO THE FAULT OF VICARIOUS AGENTS OF BOSCH SI AND THE PERSONAL LIABILITY OF BOSCH SI'S
 * EMPLOYEES, REPRESENTATIVES AND ORGANS.
 */

import com.bosch.cr.integration.IntegrationClient;
import com.bosch.cr.integration.client.IntegrationClientImpl;
import com.bosch.cr.integration.client.configuration.AuthenticationConfiguration;
import com.bosch.cr.integration.client.configuration.IntegrationClientConfiguration;
import com.bosch.cr.integration.client.configuration.PublicKeyAuthenticationConfiguration;
import com.bosch.cr.integration.client.configuration.TrustStoreConfiguration;
import com.bosch.cr.integration.things.ThingHandle;
import com.bosch.cr.integration.things.ThingIntegration;
import com.bosch.cr.json.JsonFactory;
import com.bosch.cr.json.JsonObject;
import com.bosch.cr.model.acl.AccessControlList;
import com.bosch.cr.model.acl.AclEntry;
import com.bosch.cr.model.acl.Permission;
import com.bosch.cr.model.attributes.Attributes;
import com.bosch.cr.model.attributes.AttributesBuilder;
import com.bosch.cr.model.attributes.AttributesModelFactory;
import com.bosch.cr.model.authorization.AuthorizationModelFactory;
import com.bosch.cr.model.things.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HelloWorld {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorld.class);

    // Things Service in Cloud
    public static final String BOSCH_IOT_CENTRAL_REGISTRY_WS_ENDPOINT_URL = "wss://events.apps.bosch-iot-cloud.com";

    // Insert your Solution ID here
    public static final String SOLUTION_ID = "<your-solution-id>";
    public static final String CLIENT_ID = SOLUTION_ID;

    // Insert your User ID here
    public static  final String USER_ID = "<your-user-id>";

    public static final URL KEYSTORE_LOCATION = HelloWorld.class.getResource("/CRClient.jks");
    public static final String KEYSTORE_PASSWORD = "solutionPass";
    public static final URL TRUSTSTORE_LOCATION = HelloWorld.class.getResource("/bosch-iot-cloud.jks");
    public static final String TRUSTSTORE_PASSWORD = "jks";
    public static final String ALIAS = "CR";
    public static final String ALIAS_PASSWORD = "crPass";
    public static int i = 0;

    final IntegrationClient integrationClient;
    final ThingIntegration thingIntegration;

    /**
     * Client instantiation
     */
    public HelloWorld()
    {
        /* build an authentication configuration */
        final AuthenticationConfiguration authenticationConfiguration =
                PublicKeyAuthenticationConfiguration.newBuilder().clientId(CLIENT_ID) //
                        .keyStoreLocation(KEYSTORE_LOCATION) //
                        .keyStorePassword(KEYSTORE_PASSWORD) //
                        .alias(ALIAS) //
                        .aliasPassword(ALIAS_PASSWORD) //
                        .build();

        /* configure a truststore that contains trusted certificates */
        final TrustStoreConfiguration trustStore =
                TrustStoreConfiguration.newBuilder().location(TRUSTSTORE_LOCATION).password(TRUSTSTORE_PASSWORD).build();

         /* provide required configuration (authentication configuration and CR URI),
         optional configuration (proxy, truststore etc.) can be added when needed */
        final IntegrationClientConfiguration integrationClientConfiguration =
                IntegrationClientConfiguration.newBuilder().authenticationConfiguration(authenticationConfiguration)
                        .centralRegistryEndpointUrl(BOSCH_IOT_CENTRAL_REGISTRY_WS_ENDPOINT_URL)
                        // .proxyConfiguration(proxy)
                        .trustStoreConfiguration(trustStore).build();

        LOGGER.info("Creating CR Integration Client for ClientID: {}", CLIENT_ID);

        /* Create a new integration client object to start interacting with the Central Registry */
        integrationClient = IntegrationClientImpl.newInstance(integrationClientConfiguration);

        /* Create a new thing integration object to start interacting with the Central Registry */
        thingIntegration = integrationClient.things();
    }

    /**
     * Create an empty Thing
     */
    public void createEmptyThing()
    {
        try {
            thingIntegration.create().thenAccept(thing -> LOGGER.info("Thing with following ID created: {}", thing)).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a Thing with given ThingId
     */
    public void createThing(String thingID)
    {
        try {
            thingIntegration.create(thingID).thenAccept(thing -> LOGGER.info("Thing created: {}", thing)).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find a Thing with given ThingId
     */
    public void getThingByID(String thingID)
    {
        Thing thing;
        ThingHandle thingHandle = thingIntegration.forId(thingID);
        try {

            thing = thingHandle.retrieve().get(2, TimeUnit.SECONDS);

            LOGGER.info("Thing with ID found: {}", thingHandle.getThingId() );
            LOGGER.info("Thing Attributes: {}", thing.getAttributes());

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update Attributes of a specified Thing
     */
    public void updateThing(String thingID)
    {
        Thing thing;
        ThingHandle thingHandle = thingIntegration.forId(thingID);
        try {

            thing = thingHandle.retrieve().get(2, TimeUnit.SECONDS);

            Attributes attributes = AttributesModelFactory.newAttributesBuilder().set("Counter", i++).build();
            thing = thing.setAttributes(attributes);

            thingIntegration.update(thing).get(2, TimeUnit.SECONDS);

            LOGGER.info("Thing with ID: {} updated!", thingHandle.getThingId());

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the ACL of a specified Thing
     */
    public void updateACL(String thingID)
    {
        Thing thing;
        final AclEntry acl;
        ThingHandle thingHandle = thingIntegration.forId(thingID);
        try {

            thing = thingHandle.retrieve().get(2, TimeUnit.SECONDS);
            acl = AclEntry.newInstance(AuthorizationModelFactory.newAuthSubject(USER_ID), Permission.READ, Permission.WRITE, Permission.ADMINISTRATE );
            thing = thing.setAclEntry(acl);

            thingIntegration.update(thing).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a specified Thing
     */
    public void deleteThing(String thingID)
    {
        try {
            thingIntegration.delete(thingID).get(2, TimeUnit.SECONDS);

            LOGGER.info("Thing with ID deleted: {}", thingID );

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    /**
     * Destroys the client and waits for its graceful shutdown.
     */
    public void terminate()
    {
        /* Gracefully shutdown the integrationClient */
        integrationClient.destroy();
    }
}
