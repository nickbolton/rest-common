package com.tubebreakup.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AppConfigManager {

	@Autowired
	private AppConfigValueRepository appConfigValueRepository;

	private transient Long lastFetchTime = 0L;

	private AppConfig appConfig = null;

	public void initialize() {
		fetchAppConfig();
	}

	public AppConfig getAppConfig() {
		Long elapsedTime = new Date().getTime() - lastFetchTime;
		Boolean needsFetch = (appConfig == null) || (elapsedTime > appConfig.getConfigTtl());
		if (needsFetch) {
			fetchAppConfig();
		}
		return appConfig;
	}

	public void saveAppConfig(AppConfig updatedAppConfig) {
		List<AppConfigValue> values = updatedAppConfig.toValues();
		appConfigValueRepository.saveAll(values);
		appConfig = updatedAppConfig;
	}

	private void fetchAppConfig() {
		appConfig = new AppConfig();
		List<AppConfigValue> values = appConfigValueRepository.findAll();
		if (values.size() <= 0) {
			values = appConfig.toValues();
			values = appConfigValueRepository.saveAll(values);
		}
		for (AppConfigValue value: values) {
			appConfig.setValue(value);
		}
	}
}
