package edu.kit.scc.test;

import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.backend.HpssCdmi;

public class HpssCdmiTest {

  private static final Logger log = LoggerFactory.getLogger(HpssCdmiTest.class);

  @Test
  public void readCapabilitiesFromConfigTest() {
    HpssCdmi hpssCdmi = new HpssCdmi();

    JSONObject json = hpssCdmi.readCapabilitiesFromConfig();

    assertNotNull(json);
  }

  @Test
  public void listDirectoryFromBackEndTest() {
    HpssCdmi hpssCdmi = new HpssCdmi();

    JSONObject json = hpssCdmi.listDirectoryFromBackEnd("/");

    assertNotNull(json);

    log.debug(json.toString());
  }

  @Test
  public void getXattrsFromBackEndTest() {
    HpssCdmi hpssCdmi = new HpssCdmi();

    JSONObject json = hpssCdmi.getXattrsFromBackEnd("/");

    assertNotNull(json);

    log.debug(json.toString());
  }
}
