/*
 * Bosch SI Example Code License Version 1.0, January 2016
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
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE
 * QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL
 * NECESSARY SERVICING, REPAIR OR CORRECTION. THIS SHALL NOT APPLY TO MATERIAL DEFECTS AND DEFECTS OF TITLE WHICH BOSCH
 * SI HAS FRAUDULENTLY CONCEALED. APART FROM THE CASES STIPULATED ABOVE, BOSCH SI SHALL BE LIABLE WITHOUT LIMITATION FOR
 * INTENT OR GROSS NEGLIGENCE, FOR INJURIES TO LIFE, BODY OR HEALTH AND ACCORDING TO THE PROVISIONS OF THE GERMAN
 * PRODUCT LIABILITY ACT (PRODUKTHAFTUNGSGESETZ). THE SCOPE OF A GUARANTEE GRANTED BY BOSCH SI SHALL REMAIN UNAFFECTED
 * BY LIMITATIONS OF LIABILITY. IN ALL OTHER CASES, LIABILITY OF BOSCH SI IS EXCLUDED. THESE LIMITATIONS OF LIABILITY
 * ALSO APPLY IN REGARD TO THE FAULT OF VICARIOUS AGENTS OF BOSCH SI AND THE PERSONAL LIABILITY OF BOSCH SI'S EMPLOYEES,
 * REPRESENTATIVES AND ORGANS.
 */
package com.bosch.iot.hub;

import static com.bosch.iot.hub.HubConstants.ALIAS;
import static com.bosch.iot.hub.HubConstants.ALIAS_PASSWORD;
import static com.bosch.iot.hub.HubConstants.HUB_CLOUD_ENDPOINT;
import static com.bosch.iot.hub.HubConstants.KEY_STORE_LOCATION;
import static com.bosch.iot.hub.HubConstants.KEY_STORE_PASSWORD;
import static com.bosch.iot.hub.HubConstants.SOLUTION_CLIENT_ID;
import static com.bosch.iot.hub.HubConstants.TRUST_STORE_LOCATION;
import static com.bosch.iot.hub.HubConstants.TRUST_STORE_PASSWORD;

import com.bosch.iot.hub.client.DefaultIotHubClient;
import com.bosch.iot.hub.client.IotHubClient;

public class HubExample
{
   private static final String SOLUTION_TOPIC = "my_solution";

   public static void main(String[] args)
   {
      IotHubClient solutionClient = initSolutionClient();
      solutionClient.connect();
      solutionClient.createTopic(SOLUTION_TOPIC).thenAccept(topic -> {

      });
   }

   private static IotHubClient initSolutionClient()
   {
      return DefaultIotHubClient.newBuilder() //
         .endPoint(HUB_CLOUD_ENDPOINT) //
         .keyStore(KEY_STORE_LOCATION, KEY_STORE_PASSWORD) //
         .alias(ALIAS, ALIAS_PASSWORD) //
         .clientId(SOLUTION_CLIENT_ID) //
         .sslTrustStore(TRUST_STORE_LOCATION, TRUST_STORE_PASSWORD) //
         .build();
   }

}
