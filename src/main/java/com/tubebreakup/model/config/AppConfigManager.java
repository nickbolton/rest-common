package com.tubebreakup.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AppConfigManager<T extends AppConfig> {

	@Autowired
	private AppConfigValueRepository appConfigValueRepository;

	private transient Long lastFetchTime = 0L;

	private T appConfig = null;

	public void initialize(Class<T> clazz) {
		fetchAppConfig(clazz);
	}

	public T getAppConfig(Class<T> clazz) {
		Long elapsedTime = new Date().getTime() - lastFetchTime;
		Boolean needsFetch = (appConfig == null) || (elapsedTime > appConfig.getConfigTtl());
		if (needsFetch) {
			fetchAppConfig(clazz);
		}
		return appConfig;
	}

	public void saveAppConfig(T updatedAppConfig) {
		List<AppConfigValue> values = updatedAppConfig.toValues();
		appConfigValueRepository.saveAll(values);
		appConfig = updatedAppConfig;
	}

	private void fetchAppConfig(Class<T> clazz) {
		appConfig = getInstanceOfT(clazz);
		List<AppConfigValue> values = appConfigValueRepository.findAll();
		if (values.size() <= 0) {
			values = appConfig.toValues();
			values = appConfigValueRepository.saveAll(values);
		}
		for (AppConfigValue value: values) {
			appConfig.setValue(value);
		}
	}

	private T getInstanceOfT(Class<T> aClass) {
		try {
			return aClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
