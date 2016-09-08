/*
 * Copyright 2016 Karlsruhe Institute of Technology (KIT)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.kit.scc.backend;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HpssBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(HpssBackend.class);

  private ArrayList<BackendCapability> backendCapabilities = new ArrayList<>();

  HpssCdmi hpssCdmi = new HpssCdmi();

  /**
   * Construct the HPSS back-end module.
   * 
   * @param properties back-end properties
   */
  public HpssBackend(Map<String, String> properties) {

    JSONObject capabilities = hpssCdmi.readCapabilitiesFromConfig();

    Map<String, Object> containerCapabilities = new HashMap<>();
    JSONObject jsonCapabilities = capabilities.getJSONObject("container_capabilities");
    for (String key : jsonCapabilities.keySet()) {
      containerCapabilities.put(key, jsonCapabilities.get(key));
    }

    JSONObject containerClasses = capabilities.getJSONObject("container_classes");
    for (String key : containerClasses.keySet()) {
      JSONObject containerClass = containerClasses.getJSONObject(key);
      log.debug("found {} capabilities class {}: {}", CapabilityType.CONTAINER, key,
          containerClass.toString());

      BackendCapability backendCapability = new BackendCapability(key, CapabilityType.CONTAINER);

      Map<String, Object> metadata = new HashMap<>();
      for (String capability : containerClass.keySet()) {
        metadata.put(capability, containerClass.get(capability));
      }

      backendCapability.setMetadata(metadata);
      backendCapability.setCapabilities(containerCapabilities);

      backendCapabilities.add(backendCapability);
    }

    Map<String, Object> dataObjectCapabilities = new HashMap<>();
    jsonCapabilities = capabilities.getJSONObject("dataobject_capabilities");
    for (String key : jsonCapabilities.keySet()) {
      dataObjectCapabilities.put(key, jsonCapabilities.get(key));
    }

    JSONObject dataObjectClasses = capabilities.getJSONObject("container_classes");
    for (String key : dataObjectClasses.keySet()) {
      JSONObject dataObjectClass = dataObjectClasses.getJSONObject(key);
      log.debug("found {} capabilities class {}: {}", CapabilityType.DATAOBJECT, key,
          dataObjectClass.toString());

      BackendCapability backendCapability = new BackendCapability(key, CapabilityType.DATAOBJECT);

      Map<String, Object> metadata = new HashMap<>();
      for (String capability : dataObjectClass.keySet()) {
        metadata.put(capability, dataObjectClass.get(capability));
      }

      backendCapability.setMetadata(metadata);
      backendCapability.setCapabilities(dataObjectCapabilities);

      backendCapabilities.add(backendCapability);
    }
  }

  @Override
  public List<BackendCapability> getCapabilities() throws BackEndException {
    return backendCapabilities;
  }

  @Override
  public CdmiObjectStatus getCurrentStatus(String path) throws BackEndException {

    JSONObject json = hpssCdmi.getXattrsFromBackEnd(path);

    String currentCapabilitiesUri = "/cdmi_capabilities/dataobject/CosSmallFilesE2EDP";
    if (json != null) {
      String type = json.getString("Type");
      if (type.equals("Directory")) {
        currentCapabilitiesUri = "/cdmi_capabilities/container/CosSmallFilesE2EDP";
      }
    }

    Map<String, Object> metadata = new HashMap<>();
    for (String key : json.keySet()) {
      metadata.put(key, json.get(key));
    }

    return new CdmiObjectStatus(metadata, currentCapabilitiesUri, null);
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {
    throw new BackEndException("not supported");
  }

}
