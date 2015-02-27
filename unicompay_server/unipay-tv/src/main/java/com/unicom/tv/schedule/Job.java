package com.unicom.tv.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

//@Component("job")
public class Job {
	private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

	@Scheduled(cron="0 0/30 * * * ?")
	public void refreshSettings() {
		
	}
}
