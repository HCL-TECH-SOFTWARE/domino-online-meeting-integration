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
import java.util.TreeSet;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
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

  // private static final List<String> validStateParams = new ArrayList<>();

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

    // validStateParams.add(DOMIConstants.GTM_PATH);
    // validStateParams.add(DOMIConstants.ZOOM_PATH);
    // validStateParams.add(DOMIConstants.WEBEX_PATH);
    // validStateParams.add(DOMIConstants.TEAMS_PATH);

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
    this.router.route().handler(this::keepTheBadBoysOut);
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

  private void keepTheBadBoysOut(final RoutingContext ctx) {
    System.out.println("Bad Boys what you gonna do?");

    final String state = ctx.request().getParam("state");
    if (null == state) {
      ctx.next();
      return;
    }

    // TODO Spanky Use a List of all valid paths, then stream the list and filter,
    // if fails then error, if not fail then good.

    // if (validStateParams.stream().filter(s -> state.startsWith(s)).count() > 0) {
    // System.out.println("Invalid route");
    // ctx.fail(400);
    // return;
    // }

    if (DOMIProvider.getPaths().stream().filter(s -> state.startsWith(s)).count() > 0) {
      System.out.println("Invalid route");
      ctx.fail(400);
      return;
    }

    ctx.next();

  }

  /**
   * Load routes for all meeting provider
   * 
   * @param hostName for this server
   * @throws Exception what went wrong
   */
  private void createMeetingProviderRoutes(final String hostName) throws DOMIException {
    // Initialize key variables from config
    final String zoomClientId = this.config().getString(DOMIConstants.ZOOM_CLIENT_ID, "");
    final String zoomClientSecret = this.config().getString(DOMIConstants.ZOOM_CLIENT_SECRET, "");
    final String gtmClientId = this.config().getString(DOMIConstants.GTM_CLIENT_ID, "");
    final String gtmClientSecret = this.config().getString(DOMIConstants.GTM_CLIENT_SECRET, "");
    final String teamsClientId = this.config().getString(DOMIConstants.TEAMS_CLIENT_ID, "");
    final String teamsClientSecret = this.config().getString(DOMIConstants.TEAMS_CLIENT_SECRET, "");
    final String webexClientId = this.config().getString(DOMIConstants.WEBEX_CLIENT_ID, "");
    final String webexClientSecret = this.config().getString(DOMIConstants.WEBEX_CLIENT_SECRET, "");

    final OnlineMeetingProviderFactory zoomFactory = new OnlineMeetingProviderFactoryHolder(
        zoomClientId, zoomClientSecret, DOMIConstants.ZOOM_LABEL, hostName);
    final OnlineMeetingProviderParameterBuilder zoomBuilder = new OnlineMeetingProviderParameterBuilder()
        .vertx(this.vertx)
        .router(this.router)
        .authUrl(DOMIConstants.ZOOM_AUTHORIZE_URL)
        .tokenUrl(DOMIConstants.ZOOM_TOKEN_URL)
        .revocationUrl(DOMIConstants.ZOOM_REVOCATION_URL)
        .callbackRoute(DOMIConstants.ZOOM_CALLBACK_ROUTE)
        .refreshRoute(DOMIConstants.ZOOM_REFRESH_ROUTE)
        .revokeRoute(DOMIConstants.ZOOM_REVOKE_ROUTE)
        .scopes(DOMIConstants.ZOOM_SCOPES)
        .path(DOMIConstants.ZOOM_PATH)
        .extraParams(new JsonObject());
    zoomFactory.createAndEnableRoutes(zoomBuilder.build());

    final OnlineMeetingProviderFactory teamsFactory = new OnlineMeetingProviderFactoryHolder(
        teamsClientId, teamsClientSecret, DOMIConstants.TEAMS_LABEL, hostName);
    final OnlineMeetingProviderParameterBuilder teamsBuilder = new OnlineMeetingProviderParameterBuilder()
        .vertx(this.vertx)
        .router(this.router)
        .authUrl(DOMIConstants.TEAMS_AUTHORIZE_URL)
        .tokenUrl(DOMIConstants.TEAMS_TOKEN_URL)
        .revocationUrl(DOMIConstants.TEAMS_REVOCATION_URL)
        .callbackRoute(DOMIConstants.TEAMS_CALLBACK_ROUTE)
        .refreshRoute(DOMIConstants.TEAMS_REFRESH_ROUTE)
        .revokeRoute(DOMIConstants.TEAMS_REVOKE_ROUTE)
        .scopes(DOMIConstants.TEAMS_SCOPES)
        .path(DOMIConstants.TEAMS_PATH)
        .extraParams(new JsonObject());
    teamsFactory.createAndEnableRoutes(teamsBuilder.build());

    final OnlineMeetingProviderFactory webexFactory = new OnlineMeetingProviderFactoryHolder(
        webexClientId, webexClientSecret, DOMIConstants.WEBEX_LABEL, hostName);

    final JsonObject extraParams = new JsonObject()
        .put("client_id", webexClientId)
        .put("client_secret", webexClientSecret);
    final OnlineMeetingProviderParameterBuilder webexBuilder = new OnlineMeetingProviderParameterBuilder()
        .vertx(this.vertx)
        .router(this.router)
        .authUrl(DOMIConstants.WEBEX_AUTHORIZE_URL)
        .tokenUrl(DOMIConstants.WEBEX_TOKEN_URL)
        .revocationUrl(DOMIConstants.WEBEX_REVOCATION_URL)
        .callbackRoute(DOMIConstants.WEBEX_CALLBACK_ROUTE)
        .refreshRoute(DOMIConstants.WEBEX_REFRESH_ROUTE)
        .revokeRoute(DOMIConstants.WEBEX_REVOKE_ROUTE)
        .scopes(DOMIConstants.WEBEX_SCOPES)
        .path(DOMIConstants.WEBEX_PATH)
        .extraParams(extraParams);
    webexFactory.createAndEnableRoutes(webexBuilder.build());

    final OnlineMeetingProviderFactory gtmFactory = new OnlineMeetingProviderFactoryHolder(
        gtmClientId, gtmClientSecret, DOMIConstants.GTM_LABEL, hostName);
    final OnlineMeetingProviderParameterBuilder gtmBuilder = new OnlineMeetingProviderParameterBuilder()
        .vertx(this.vertx)
        .router(this.router)
        .authUrl(DOMIConstants.GTM_AUTHORIZE_URL)
        .tokenUrl(DOMIConstants.GTM_TOKEN_URL)
        .revocationUrl(DOMIConstants.GTM_REVOCATION_URL)
        .callbackRoute(DOMIConstants.GTM_CALLBACK_ROUTE)
        .refreshRoute(DOMIConstants.GTM_REFRESH_ROUTE)
        .revokeRoute(DOMIConstants.GTM_REVOKE_ROUTE)
        .scopes(DOMIConstants.GTM_SCOPES)
        .path(DOMIConstants.GTM_PATH)
        .extraParams(new JsonObject());
    gtmFactory.createAndEnableRoutes(gtmBuilder.build());
  }
}
