package edu.kit.scc.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.CdmiObjectStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import edu.kit.scc.backend.HpssBackend;

public class HpssBackendTest {

  private static final Logger log = LoggerFactory.getLogger(HpssBackendTest.class);

  private static final String diskAndTapeUri = "/cdmi_capabilities/dataobject/DiskAndTape";
  private static final String tapeUri = "/cdmi_capabilities/dataobject/TapeOnly";

  @Test
  public void createHpssBackendTest() {
    HpssBackend backend = new HpssBackend(new HashMap<String, String>());

    assertNotNull(backend);
  }

  @Test
  public void getCurrentStatusRootDirectoryTest() {
    HpssBackend backend = new HpssBackend(new HashMap<String, String>());

    assertNotNull(backend);

    try {
      CdmiObjectStatus status = backend.getCurrentStatus("/");
      assertNotNull(status);
      log.debug(status.toString());
    } catch (BackEndException e) {
      fail();
    }
  }

  @Test
  public void getCurrentStatusTestFileTest() {
    HpssBackend backend = new HpssBackend(new HashMap<String, String>());

    assertNotNull(backend);

    try {
      CdmiObjectStatus status = backend.getCurrentStatus("/test.txt");
      assertNotNull(status);

      assertNotNull(status.getCurrentCapabilitiesUri());
      assertNotNull(status.getMonitoredAttributes());

      log.debug(status.toString());
    } catch (BackEndException e) {
      fail();
    }
  }

  @Test
  public void changeQoSRootDirectoryFailTest() {
    HpssBackend backend = new HpssBackend(new HashMap<String, String>());

    assertNotNull(backend);

    try {
      CdmiObjectStatus status = backend.getCurrentStatus("/");
      assertNotNull(status);

      try {
        backend.updateCdmiObject("/test.txt", tapeUri);
        fail();
      } catch (BackEndException e) {
        log.debug(e.getMessage());
      }
    } catch (BackEndException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void changeQoSTestFileTest() {
    HpssBackend backend = new HpssBackend(new HashMap<String, String>());

    assertNotNull(backend);

    try {
      CdmiObjectStatus status = backend.getCurrentStatus("/test.txt");
      assertNotNull(status);

      assertNotNull(status.getCurrentCapabilitiesUri());
      assertNotNull(status.getMonitoredAttributes());

      log.debug(status.toString());

      if (status.getCurrentCapabilitiesUri().equals(diskAndTapeUri)) {
        try {
          backend.updateCdmiObject("/test.txt", tapeUri);
        } catch (BackEndException e) {
          log.debug(e.getMessage());
        }
      } else if (status.getCurrentCapabilitiesUri().equals(tapeUri)) {
        try {
          backend.updateCdmiObject("/test.txt", diskAndTapeUri);
        } catch (BackEndException e) {
          log.debug(e.getMessage());
        }
      } else {
        fail();
      }
    } catch (BackEndException e) {
      e.printStackTrace();
      fail();
    }
  }
}
