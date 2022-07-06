/* ========================================================================== *
 * Copyright (c) 2021 HCL America, Inc.                       *
 *                            All rights reserved.                            *
 * ========================================================================== *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 * ========================================================================== */

package com.hcl.labs.domi;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.hcl.labs.domi.providers.OnlineMeetingProviderFactory;
import com.hcl.labs.domi.providers.OnlineMeetingProviderFactoryHolder;
import com.hcl.labs.domi.providers.OnlineMeetingProviderParameterBuilder;
import com.hcl.labs.domi.tools.DOMIConstants;
import com.hcl.labs.domi.tools.DOMIException;
import com.hcl.labs.domi.tools.DOMIProvider;
import com.hcl.labs.domi.tools.DOMIUtils;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

/**
 * @author Paul Withers
 *         Entry point and Vert.x Verticle
 */
public class MainVerticle extends AbstractVerticle {

  /**
   * The system configuration at System start
   */
  private static JsonObject config;
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  private static final String DEFAULT_CSP_VALUE = "default-src 'self';";
  private static final String HEADER_CSP = "Content-Security-Policy";
  private static final String ORIGIN = "Origin";
  private static final String HEADER_STRICT_TLS = "Strict-Transport-Security";
  private static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
  private static final String HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
  private static final String HEADER_REFERRER_POLICY = "Referrer-Policy";
  private static final String HEADER_PERMISSIONS_POLICY = "Permissions-Policy";
  public static final String X_HEADER = "X-Clacks-Overhead";
  public static final String HEADER_GNU_TP = "GNU Terry Pratchett";

  /**
   * Flag to determine if we use TLS only, might get overwritten
   * in listenerStart()
   */
  private boolean useTLS = true;

  private final Set<String> corsHosts = new HashSet<>();

  /**
   * Configure the use of Java JDK logging to use
   * Log4J. In Keep we use slf4j, but some external
   * libraries, most notably com.sun.mail from
   * JakartaEE uses JDK logging. To ensure that
   * everything ends up in the same logger this
   * code runs once on start
   */
  static {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    System.setProperty("log4j.configurationFile", "/log4j2.json");
  }

  /**
   * Retrieves Prometheus metrics settings from the config
   *
   * @param config
   * @return
   */
  private static MicrometerMetricsOptions getMetricsOptions() {
    // Performance metrics using Micrometer

    final JsonObject promMetricsConfig = MainVerticle.config.getJsonObject("prometheusMetrics", new JsonObject());
    final Integer port = MainVerticle.config.getInteger(DOMIConstants.CONFIG_METRICSPORT, 8890);
    final HttpServerOptions serverOptions = DOMIUtils.getServerOptions(DOMIConstants.CONFIG_METRICSPORT, port, MainVerticle.config);
    MainVerticle.LOGGER.info(
        "Metrics is configured for port {} {}", port,
        serverOptions.isSsl() ? "with HTTPS" : "HTTP only");
    final VertxPrometheusOptions prometheusOptions = new VertxPrometheusOptions(promMetricsConfig)
        .setEmbeddedServerOptions(serverOptions);

    final JsonObject metricsConfig = MainVerticle.config.getJsonObject("metrics", new JsonObject());
    return new MicrometerMetricsOptions(metricsConfig)
        .setPrometheusOptions(prometheusOptions)
        .setEnabled(true);
  }

  /**
   * Check if the application gets started in debug mode this will extend
   * the loop timeout to avoid warnings of debugger blocking
   * the event loop
   *
   * @return true if debug mode
   */
  public static boolean isDebug() {
    return Boolean.parseBoolean(System.getenv("DEBUG"));
  }

  /**
   * Loads the configuration from the internal configuration file or JSON file
   * that is in the
   * configuration directory
   *
   * @param vertx         - the vertx instance
   * @param configDirName - the configuration directory
   * @return Future with a configuration
   */
  public static Future<JsonObject> loadConfig(final Vertx vertx, final String configDirName) {
    final ConfigRetrieverOptions options = new ConfigRetrieverOptions();
    options.addStore(new ConfigStoreOptions()
        .setType("json")
        .setConfig(DOMIUtils.getJsonFromResource("/config.json")));

    // Now check all JSON files in the config directory
    final File cfgDir = new File(configDirName);
    if (!cfgDir.exists()) {
      MainVerticle.LOGGER.error("Configuration directory is missing: {}", cfgDir.getAbsolutePath());
      try {
        Files.createDirectories(cfgDir.toPath());
      } catch (final IOException e) {
        return Future.failedFuture(e);
      }
    }

    // We process by file name
    if (cfgDir.isDirectory()) {
      final FilenameFilter filter = (dir, name) -> name.endsWith(".json");
      final String[] files = cfgDir.list(filter);
      if (null != files) {
        final TreeSet<String> filesByName = new TreeSet<>();
        filesByName.addAll(Arrays.asList(files));
        filesByName.forEach(fn -> options.addStore(new ConfigStoreOptions()
            .setType("file")
            .setConfig(new JsonObject()
                .put("path", Paths.get(configDirName, fn).toString()))));
      }
    }

    // Finally environment parameters
    final ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env")
        .setConfig(new JsonObject().put("keys", DOMIUtils.getEnvironmentParamsToRead()));

    options.addStore(envStore);
    final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    return Future.future(retriever::getConfig);
  }

  /**
   * Entrypoint, initialising and running an instance of this class
   *
   * @param args configuration directory
   */
  public static void main(final String[] args) {

    final String configDirectory = args.length < 1 ? DOMIConstants.CONFIG_DIR : args[0];
    MainVerticle.runVerticle(new MainVerticle(configDirectory));

  }

  /**
   * Loads config and runs an instance of this class using current Vert.x instance
   *
   * @param mainVerticle to run
   */
  public static void runVerticle(final MainVerticle mainVerticle) {
    final Vertx vertx = Vertx.vertx();

    MainVerticle.loadConfig(vertx, mainVerticle.configDir).compose(newConfig -> {
      MainVerticle.config = newConfig;
      return vertx.close();
    })
        .onFailure(failure -> {
          MainVerticle.LOGGER.error(failure.getMessage());
          System.exit(1);
        })
        .onSuccess(v -> MainVerticle.runVerticleWithConfig(mainVerticle));
  }

  /**
   * Runs an instance of this class with the config previously loaded
   *
   * @param mainVerticle to run
   */
  private static void runVerticleWithConfig(final MainVerticle mainVerticle) {
    final Consumer<Vertx> runner = vertx -> {
      final DeploymentOptions depOpt = new DeploymentOptions().setConfig(MainVerticle.config);

      vertx.deployVerticle(mainVerticle, depOpt)
          .onSuccess(
              result -> MainVerticle.sendStartupMessage(mainVerticle.getClass().getName(), result))
          .onFailure(err -> {
            MainVerticle.LOGGER.error("\n\n\n *********** FATAL ********** \n\n\n", err);
            System.exit(1);
          });
    };

    // Get Vertx and Prometheus configuration settings
    // from the "vertx" section in the config
    final JsonObject vertxConfig = MainVerticle.config.getJsonObject("vertx", new JsonObject());
    final VertxOptions options = new VertxOptions(vertxConfig);
    final MicrometerMetricsOptions metricsOptions = MainVerticle.getMetricsOptions();

    // Print debug message
    if (MainVerticle.isDebug()) {
      System.setProperty("jnx.debuginit", "true");
      options.setBlockedThreadCheckInterval(1000 * 3600);
      MainVerticle.LOGGER.info(
          "*****************************\n*** Running in debug mode ***\n*****************************");
    }

    // Add metrics
    metricsOptions.setJvmMetricsEnabled(true);
    options.setMetricsOptions(metricsOptions);
    final Vertx vertx = Vertx.vertx(options);
    runner.accept(vertx);
  }

  /**
   * Sends a startup complete message out as information
   *
   * @param verticleName - ClassName of main verticle
   * @param verticleId   - ID of the verticle being launched
   */
  static void sendStartupMessage(final String verticleName, final String verticleId) {
    final StringBuilder builder = new StringBuilder();
    builder.append("\n\n***** DOMI Properties\n\n");
    final String prop = DOMIUtils.getStringFromResource(DOMIConstants.CONFIG_PROPERTY_RESOURCENAME);
    builder.append(prop)
        .append("\n\n*** Main verticle ")
        .append(verticleName)
        .append("\n    deployed as ")
        .append(verticleId)
        .append(" ***\n*** Domino Online Meeting Integrations open for business ***\n\n");
    MainVerticle.LOGGER.info("{}", builder);
  }

  private final String configDir;

  private Router router;

  /**
   * Constructor
   *
   * @param configDir where JSON configuration files can be found
   */
  public MainVerticle(final String configDir) {
    this.configDir = configDir;
  }

  /*
   * (non-Javadoc)
   * @see io.vertx.core.AbstractVerticle#start(io.vertx.core.Promise)
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final String hostName = this.config().getString(DOMIConstants.CONFIG_HOSTNAME);
    if (null == hostName) {
      throw new DOMIException("Cannot continue without a valid hostname in config");
    }
    MainVerticle.LOGGER.info("Starting up with hostname " + hostName);

    final Integer port = MainVerticle.config.getInteger(DOMIConstants.CONFIG_PORT, 8878);
    final HttpServerOptions serverOptions = DOMIUtils.getServerOptions(DOMIConstants.CONFIG_PORT, port, MainVerticle.config);

    // Get router
    this.router = Router.router(this.getVertx());
    this.router.route().handler(this::validateState);

    this.setupCorsHostLookup();
    final Handler<RoutingContext> strictCSP = MainVerticle.createContentSecurityHeaderHandler(MainVerticle.DEFAULT_CSP_VALUE);

    this.router.route().handler(strictCSP);

    this.router.route().handler(this::handlerCheckCorsHeaders);
    this.router.route().handler(this::handlerStrictTLS);
    this.router.route().handler(this::handlerPratchett);
    this.router.route().handler(BodyHandler.create(false));

    // Add static and error routes
    this.router
        .get("/*")
        .handler(StaticHandler.create().setIndexPage("index.html"));
    this.router.errorHandler(404, ErrorHandler.create(this.vertx, "notfound.html", false));
    this.router.errorHandler(500, ErrorHandler.create(this.vertx, "error.html", true));

    try {
      createMeetingProviderRoutes(hostName);
    } catch (DOMIException e) {
      startPromise.fail(e.getCause());
    }

    // Start server
    this.vertx.createHttpServer(serverOptions).requestHandler(this.router).listen(
        serverOptions.getPort(),
        http -> {
          if (http.succeeded()) {
            startPromise.complete();
            MainVerticle.LOGGER.info("HTTP server started on {} {}", serverOptions.getPort(),
                serverOptions.isSsl() ? "with HTTPS" : "HTTP only");
          } else {
            startPromise.fail(http.cause());
          }
        });
  }

  private void validateState(final RoutingContext ctx) {
    // state needs to be absent or, if present, start with a known DOMIProvider path
    final String state = ctx.request().getParam("state");

    try {
      if (null == state || DOMIProvider.getPaths().stream().filter(state::startsWith).count() > 0) {
        ctx.next();
        return;
      }

      throw new IllegalArgumentException("Disallowed QueryString State parameter: " + state);

    } catch (Exception e) {
      LoggerFactory.getLogger(OnlineMeetingProviderFactoryHolder.class).error("State Not Allowed {} {}", state, e);
      ctx.fail(400);
    }
  }

  /**
   * Load routes for all meeting provider
   * 
   * @param hostName for this server
   * @throws Exception what went wrong
   */
  private void createMeetingProviderRoutes(final String hostName) throws DOMIException {
    for (DOMIProvider dp : DOMIProvider.values()) {
      final String clientId = this.config().getString(dp.CLIENT_ID_ENV, "");
      final String clientSecret = this.config().getString(dp.CLIENT_SECRET_ENV, "");

      // Webex requires extra parameters additional content for the body for the call
      // to the
      // TOKEN_URL, so needs building here
      JsonObject extraParams = new JsonObject();
      if ("WEBEX".equals(dp.name())) {
        extraParams.put("client_id", clientId)
            .put("client_secret", clientSecret);
      }

      final OnlineMeetingProviderFactory mpFactory = new OnlineMeetingProviderFactoryHolder(
          clientId, clientSecret, dp.LABEL, hostName);
      final OnlineMeetingProviderParameterBuilder mpBuilder = new OnlineMeetingProviderParameterBuilder()
          .vertx(this.vertx)
          .router(this.router)
          .authUrl(dp.AUTHORIZE_URL)
          .tokenUrl(dp.TOKEN_URL)
          .revocationUrl(dp.REVOCATION_URL)
          .callbackRoute(dp.CALLBACK_ROUTE)
          .refreshRoute(dp.REFRESH_ROUTE)
          .revokeRoute(dp.REVOKE_ROUTE)
          .scopes(dp.SCOPES)
          .path(dp.PATH)
          .extraParams(extraParams);
      mpFactory.createAndEnableRoutes(mpBuilder.build());
    }
  }

  /**
   * Creates a handler for a Content security header injection
   *
   * @param cspValue The content security header to set
   * @return A Routing context handler setting the content-security-policy
   */
  private static Handler<RoutingContext> createContentSecurityHeaderHandler(final String cspValue) {
    return ctx -> {
      final String realValue = Strings.isNullOrEmpty(cspValue) ? MainVerticle.DEFAULT_CSP_VALUE : cspValue;
      final HttpServerResponse response = ctx.response();
      if (response.headers().contains(MainVerticle.HEADER_CSP)) {
        response.headers().remove(MainVerticle.HEADER_CSP);
      }
      response.putHeader(MainVerticle.HEADER_CSP, realValue);
      ctx.next();
    };
  }

  private void handlerCheckCorsHeaders(final RoutingContext ctx) {
    final HttpServerResponse response = ctx.response();
    final String reqOrigin = ctx.request().getHeader(MainVerticle.ORIGIN);
    if (reqOrigin != null && this.isAllowCors(reqOrigin)) {
      response.putHeader("Access-Control-Allow-Origin", reqOrigin);
      // Tell browser that response might change with origin
      response.putHeader("Vary", MainVerticle.ORIGIN);
      MainVerticle.LOGGER.trace("CORS added for {}", reqOrigin);
    }
    ctx.next();
  }

  /**
   * Adds a TLS Strict Header
   *
   * @param ctx Routing context
   */
  private void handlerStrictTLS(final RoutingContext ctx) {
    final HttpServerResponse response = ctx.response();
    if (this.useTLS && !response.headers().contains(MainVerticle.HEADER_STRICT_TLS)) {
      // One month enforcement of TLS
      response.putHeader(MainVerticle.HEADER_STRICT_TLS, "max-age=2592000");
    }

    ctx.next();
  }

  private void handlerPratchett(final RoutingContext ctx) {
    // See http://www.gnuterrypratchett.com/
    ctx.response().putHeader(MainVerticle.X_HEADER, MainVerticle.HEADER_GNU_TP);
    ctx.next();
  }

  private void handlerXframeOptions(final RoutingContext ctx) {
    // See
    // https://scotthelme.co.uk/hardening-your-http-response-headers/#x-frame-options
    ctx.response().putHeader(MainVerticle.HEADER_X_FRAME_OPTIONS, "SAMEORIGIN");
    ctx.next();
  }

  private void handlerXcontentTypeOptions(final RoutingContext ctx) {
    // See
    // https://scotthelme.co.uk/hardening-your-http-response-headers/#x-content-type-options
    ctx.response().putHeader(MainVerticle.HEADER_X_CONTENT_TYPE_OPTIONS, "nosniff");
    ctx.next();
  }

  private void handlerReferrerPolicy(final RoutingContext ctx) {
    // See https://scotthelme.co.uk/a-new-security-header-referrer-policy/
    ctx.response().putHeader(MainVerticle.HEADER_REFERRER_POLICY, "no-referrer");
    ctx.next();
  }

  private void handlerPermissionsPolicy(final RoutingContext ctx) {
    // See http://www.gnuterrypratchett.com/
    ctx.response().putHeader(MainVerticle.HEADER_PERMISSIONS_POLICY, "sync-xhr()");
    ctx.next();
  }

  /**
   * @param originIncoming - the Host where the request came from
   * @return true if we serve it
   */
  private boolean isAllowCors(final String originIncoming) {
    // Checking the ENDING of the origin, so
    // acme.com will enable a.acme.com, b.acme.com etc
    // Stripping out a port
    final String[] parts = originIncoming.split(":");
    final String origin = parts.length > 1 ? parts[1] : parts[0];
    for (final String allowedDomain : this.corsHosts) {
      if (origin.endsWith(allowedDomain)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Reads the cors lookup values into a lookup map
   */
  private void setupCorsHostLookup() {
    final JsonObject input = this.config().getJsonObject("CORS", new JsonObject());
    input.stream()
        .filter(entry -> entry.getValue() instanceof Boolean)
        .filter(entry -> (Boolean) entry.getValue())
        .forEach(entry -> this.corsHosts.add(entry.getKey()));
  }

}
