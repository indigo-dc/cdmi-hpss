/*
 * Copyright 2016 Karlsruhe Institute of Technology (KIT)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.kit.scc.backend;

import org.indigo.cdmi.SubjectBasedStorageBackend;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;

import java.util.Map;

public class HpssBackendFactory implements StorageBackendFactory {

  private final String type = "hpss_backend";
  private final String description = "HPSS storage back-end connector module";

  @Override
  public StorageBackend createStorageBackend(Map<String, String> properties)
      throws IllegalArgumentException {
    return new SubjectBasedStorageBackend(new HpssBackend(properties));
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getType() {
    return this.type;
  }

}
