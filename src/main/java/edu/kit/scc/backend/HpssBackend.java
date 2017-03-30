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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

public class HpssBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(HpssBackend.class);

  private ArrayList<BackendCapability> backendCapabilities = new ArrayList<>();

  private JSONObject exports;

  HpssCdmi hpssCdmi = new HpssCdmi();

  /**
   * Construct the HPSS back-end module.
   * 
   * @param properties back-end properties
   */
  public HpssBackend(Map<String, String> properties) {

    JSONObject capabilities = hpssCdmi.readCapabilitiesFromConfig();

    exports = capabilities.getJSONObject("container_exports");

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

    JSONObject dataObjectClasses = capabilities.getJSONObject("dataobject_classes");
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

    String currentCapabilitiesUri = null;
    String targetCapabilitiesUri = null;
    Map<String, Object> metadata = new HashMap<>();
    BackendCapability capability = null;
    List<String> children = new ArrayList<>();
    String associationTime = "";
    HashMap<String, Object> exportAttributes = null;

    if (json != null) {
      if (json.has("TimeModified")) {
        String modificationTime = json.getString("TimeModified");
        DateFormat inFormat = new SimpleDateFormat("EEEMMMddHH:mm:ssyyyy");
        try {
          Date date = inFormat.parse(modificationTime);
          DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // UTC
          TimeZone tz = TimeZone.getTimeZone("UTC");
          outFormat.setTimeZone(tz);
          associationTime = outFormat.format(date);
        } catch (ParseException e) {
          log.warn("Could not read modification time {}", modificationTime);
        }
      }

      if (json.has("Type")) {
        String type = json.getString("Type");
        if (type.equals("Directory")) {

          currentCapabilitiesUri = "/cdmi_capabilities/container/CosSmallFilesE2EDP";
          capability =
              backendCapabilities.stream().filter(b -> b.getType().equals(CapabilityType.CONTAINER)
                  && b.getName().equals("CosSmallFilesE2EDP")).findFirst().get();

          JSONObject childrenJson = hpssCdmi.listDirectoryFromBackEnd(path);
          log.debug(childrenJson.toString());

          JSONArray childrenArray = childrenJson.getJSONArray("children");
          childrenArray.forEach(c -> children.add(String.valueOf(c)));

          exportAttributes = new HashMap<>();
          for (String key : exports.keySet()) {
            exportAttributes.put(key, exports.get(key));
          }

        } else if (type.equals("File")) {

          String bytesOnDisk = json.getString("BytesAtLevel[0]");
          String bytesOnTape1 = json.getString("BytesAtLevel[1]");
          // String bytesOnTape2 = json.getString("BytesAtLevel[2]");

          if (bytesOnTape1 != null && !bytesOnTape1.equals("0bytes")) {
            if (bytesOnDisk != null && !bytesOnDisk.equals("0bytes")) {
              currentCapabilitiesUri = "/cdmi_capabilities/dataobject/DiskAndTape";
              capability = backendCapabilities.stream()
                  .filter(b -> b.getType().equals(CapabilityType.DATAOBJECT)
                      && b.getName().equals("DiskAndTape"))
                  .findFirst().get();
            } else {
              currentCapabilitiesUri = "/cdmi_capabilities/dataobject/TapeOnly";
              capability = backendCapabilities.stream()
                  .filter(b -> b.getType().equals(CapabilityType.DATAOBJECT)
                      && b.getName().equals("TapeOnly"))
                  .findFirst().get();
            }

          } else {
            currentCapabilitiesUri = "/cdmi_capabilities/dataobject/DiskOnly";
            capability = backendCapabilities.stream()
                .filter(b -> b.getType().equals(CapabilityType.DATAOBJECT)
                    && b.getName().equals("DiskOnly"))
                .findFirst().get();
          }
        }
      } else if (json.has("hpssgetxattrs")) {
        String msg = json.getString("hpssgetxattrs");
        log.debug(msg);
        if (msg.contains("staging")) {
          currentCapabilitiesUri = "/cdmi_capabilities/dataobject/TapeOnly";
          capability = backendCapabilities.stream().filter(
              b -> b.getType().equals(CapabilityType.DATAOBJECT) && b.getName().equals("TapeOnly"))
              .findFirst().get();

          targetCapabilitiesUri = "/cdmi_capabilities/dataobject/DiskAndTape";
          metadata.put("cdmi_recommended_polling_interval", "50000");
        }
      } else {
        throw new BackEndException();
      }
    } else {
      throw new BackEndException();
    }

    // for (String key : json.keySet()) {
    // metadata.put(key, json.get(key));
    // }

    if (capability != null) {
      for (Entry<String, Object> e : capability.getMetadata().entrySet()) {
        metadata.put(e.getKey() + "_provided", e.getValue());
      }
    }

    metadata.put("cdmi_capability_association_time", associationTime);

    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(metadata, currentCapabilitiesUri, targetCapabilitiesUri);

    if (exportAttributes != null && !exportAttributes.isEmpty()) {
      currentStatus.setExportAttributes(exportAttributes);
    }

    if (!children.isEmpty()) {
      currentStatus.setChildren(children);
    }

    return currentStatus;
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {
    CdmiObjectStatus currentStatus = getCurrentStatus(path);

    if (currentStatus.getTargetCapabilitiesUri() != null) {
      // object already in transition
      String message = "object already in transition";
      log.debug(message);
      throw new BackEndException(message);
    }

    if (currentStatus.getCurrentCapabilitiesUri()
        .equals("/cdmi_capabilities/container/CosSmallFilesE2EDP")) {
      // QoS transition for containers not allowed
      String message = "can not change QoS for containers";
      log.debug(message);
      throw new BackEndException(message);
    }

    if (currentStatus.getCurrentCapabilitiesUri()
        .equals("/cdmi_capabilities/dataobject/DiskAndTape")
        && targetCapabilitiesUri.equals("/cdmi_capabilities/dataobject/TapeOnly")) {
      // purge
      String message = "transition from " + currentStatus.getCurrentCapabilitiesUri() + " to "
          + targetCapabilitiesUri;
      log.debug(message);
      log.debug("make purge on HPSS...");
      JSONObject result = hpssCdmi.purgeFromBackEnd(path);
      log.debug(result.toString());
    } else if (currentStatus.getCurrentCapabilitiesUri()
        .equals("/cdmi_capabilities/dataobject/TapeOnly")
        && targetCapabilitiesUri.equals("/cdmi_capabilities/dataobject/DiskAndTape")) {
      // stage
      String message = "transition from " + currentStatus.getCurrentCapabilitiesUri() + " to "
          + targetCapabilitiesUri;
      log.debug(message);
      log.debug("make stage on HPSS...");
      JSONObject result = hpssCdmi.stageFromBackEnd(path);
      log.debug(result.toString());
    } else {
      String message = "transition from " + currentStatus.getCurrentCapabilitiesUri() + " to "
          + targetCapabilitiesUri + " not supported";
      log.debug(message);
      throw new BackEndException(message);
    }
  }

}
