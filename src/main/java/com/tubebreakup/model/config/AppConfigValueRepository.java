package com.tubebreakup.model.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigValueRepository extends JpaRepository<AppConfigValue, String> {

}
