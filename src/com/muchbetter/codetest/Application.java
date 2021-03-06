package com.muchbetter.codetest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.handlers.AuthHandler;
import com.muchbetter.codetest.handlers.BalanceHandler;
import com.muchbetter.codetest.handlers.LoginHandler;
import com.muchbetter.codetest.handlers.SpendHandler;
import com.muchbetter.codetest.handlers.TransactionHandler;

import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Handler;
import ratpack.handling.RequestLogger;
import ratpack.http.MutableHeaders;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerSpec;
import ratpack.server.ServerConfig;

/**
 * This is the main application class that bootstraps the services.
 * 
 * @author blues
 *
 */
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	private Handler loginHandler;
	private Handler balanceHandler;
	private Handler transactoinsHandler;
	private Handler spendHandler;
	private Handler authHandler;

	public Application() throws Exception {
		initHandlers();
		initServer();
	}

	public Application(Handler loginHandler, Handler balanceHandler, Handler transactionsHandler, Handler spendHandler,
			Handler authHandler) throws Exception {
		initHandlers(loginHandler, balanceHandler, transactionsHandler, spendHandler, authHandler);
		initServer();
	}

	private void initHandlers() {
		System.out.println("#### initHandlers called###");
		this.loginHandler = new LoginHandler();
		this.balanceHandler = new BalanceHandler();
		this.transactoinsHandler = new TransactionHandler();
		this.spendHandler = new SpendHandler();
		this.authHandler = new AuthHandler();
	}

	private void initHandlers(Handler loginHandler, Handler balanceHandler, Handler transactionsHandler,
			Handler spendHandler, Handler authHandler) {
		this.loginHandler = loginHandler;
		this.balanceHandler = balanceHandler;
		this.transactoinsHandler = transactionsHandler;
		this.spendHandler = spendHandler;
		this.authHandler = authHandler;
	}

	private void initServer() throws Exception {
		RatpackServer server = null;
		try {
			server = RatpackServer.of(this::configureServerSpec);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			server.stop();
		}
	}

	private void configureServerSpec(RatpackServerSpec spec) throws IOException {
		Path serverConfigProps = Files.createTempFile("serverConfig", ".json");
		Path dbConfigProps = Files.createTempFile("dbConfig", ".properties");
		ServerConfig serverConfig = ServerConfig.embedded()
				.onError(Action.throwException())
				.json(serverConfigProps)
				.onError(Action.throwException())
				.props(dbConfigProps)
				.build();
		spec.serverConfig(serverConfig).handlers(this::configureApplicationReSTEndpoints);
	}

	private void configureApplicationReSTEndpoints(Chain chain) {
		chain.all(ctx -> {
			RequestLogger.ncsa();
			ctx.next();
		}).all(ctx -> {
			MutableHeaders headers = ctx.getResponse().getHeaders();
			headers.set("Access-Control-Allow-Origin", "*");
			headers.set("Accept-Language", "en-us");
			headers.set("Accept-Charset", "UTF-8");
			headers.set("Content-Type", "application/json");
			headers.set("Cache-Control", "no-cache");
			ctx.next();
		}).post("login", this.loginHandler::handle).post("spend", ctx -> {
			LOGGER.info("####Inside spend initial handler#####");
			ctx.insert(this.authHandler, this.spendHandler);
		}).get("balance", ctx -> {
			LOGGER.info("####Inside balance initial handler####");
			ctx.insert(this.authHandler, this.balanceHandler);
		}).get("transactions", ctx -> {
			LOGGER.info("####Inside transactions initial handler####");
			ctx.insert(this.authHandler, this.transactoinsHandler);
		});
	}

}
