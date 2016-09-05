/*
 * Copyright 2016 Karlsruhe Institute of Technology (KIT)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.kit.scc.backend;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

public class HpssCdmi {

  private static final Logger log = LoggerFactory.getLogger(HpssCdmi.class);

  /**
   * Reads this HPSS back-end capabilities from a config file.
   * 
   * @return a {@link JSONObject} with the back-end's capabilities
   */
  public JSONObject readCapabilitiesFromConfig() {
    JSONObject json = new JSONObject();

    try {

      InputStream in = getClass().getResourceAsStream("/capabilities.json");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      StringBuffer stringBuffer = new StringBuffer();
      String inputLine;
      while ((inputLine = reader.readLine()) != null) {
        stringBuffer.append(inputLine);
      }

      json = new JSONObject(stringBuffer.toString());

      log.debug("Capabilities config {}", json.toString());

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return json;
  }

  /**
   * Gets the HPSS back-end xattrs for the given path.
   * 
   * @param path the path to the back-end file or directory
   * @return a {@link JSONObject} with the xattrs
   */
  public JSONObject getXattrsFromBackEnd(String path) {
    JSONObject configuration = new JSONObject();

    try {

      InputStream in = getClass().getResourceAsStream("/configuration.json");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      StringBuffer stringBuffer = new StringBuffer();
      String inputLine;
      while ((inputLine = reader.readLine()) != null) {
        stringBuffer.append(inputLine);
      }

      configuration = new JSONObject(stringBuffer.toString());

      log.debug("Use configuration {}", configuration.toString());

    } catch (IOException ex) {
      ex.printStackTrace();
    }


    String url = configuration.getString("hpssgetxattrs") + path;
    String str =
        configuration.getString("rest_user") + ":" + configuration.getString("rest_password");

    String authorization = "Basic " + Base64.getEncoder().encodeToString(str.getBytes());
    log.info("Authorization {}", authorization);

    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(url);
    Header authorizationHeader = new BasicHeader("Authorization", authorization);
    httpGet.addHeader(authorizationHeader);
    CloseableHttpResponse response = null;
    BufferedReader buffReader = null;

    JSONObject json = new JSONObject();

    try {
      response = httpclient.execute(httpGet);

      log.info(response.getStatusLine().toString());
      HttpEntity entity = response.getEntity();
      buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
      StringBuffer stringBuffer = new StringBuffer();
      String inputLine;
      while ((inputLine = buffReader.readLine()) != null) {
        stringBuffer.append(inputLine);
      }
      EntityUtils.consume(entity);

      json = new JSONObject(stringBuffer.toString());
      log.info(json.toString());

    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (buffReader != null) {
        try {
          buffReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return json;
  }
}
