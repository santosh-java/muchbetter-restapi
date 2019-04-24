package com.muchbetter.codetest.datamodel.db;

import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muchbetter.codetest.utils.StackTraceUtil;

public class DataSourceFactory {
	public static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

	public static enum DataSourceType {
		REDIS;
	}

	public static IDataSource getDataSource(DataSourceType dataSourceType) {
		try {
			switch (dataSourceType) {
			case REDIS:
				Config config = getRedisConfig();
				return new RedisDataSource(config);
			default:
				config = getRedisConfig();
				return new RedisDataSource(config);
			}
		} catch (Exception e) {
			LOGGER.error(StackTraceUtil.getStackTraceAsString(e));
			throw e;
		}
	}

	private static Config getRedisConfig() {
		// TODO Method where we can read the properties required for creating Config
		// object based on the datasourceType
		// As of now, I am using single redis server and setting default configuration
		// settings.
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		return config;
	}
}
