package com.hcl.labs.domi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Paul Withers
 *         Test class
 */
@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  public TestMainVerticle() {

  }

  /**
   * Code to return before each test
   *
   * @param vertx
   * @param testContext
   */
  @BeforeEach
  void deployVerticle(final Vertx vertx, final VertxTestContext testContext) {
    // vertx.deployVerticle(new MainVerticle(""),
    // testContext.succeeding(id -> testContext.completeNow()));
    testContext.completeNow();
  }

  /**
   * Test
   *
   * @param vertx
   * @param testContext
   * @throws Throwable
   */
  @Test
  void verticleDeployed(final Vertx vertx, final VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
