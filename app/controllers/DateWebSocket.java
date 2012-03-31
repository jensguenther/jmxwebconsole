package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Action;
import play.libs.F.Promise;
import play.mvc.*;
import play.mvc.Http.Inbound;
import play.mvc.Http.Outbound;

public class DateWebSocket extends WebSocketController {

	/**
	 * Live searches.
	 */
	public static void stream() {
		TimerJob tj = new TimerJob();
		while (inbound.isOpen() && outbound.isOpen()) {
			try {
				String date = await(tj.now());
				outbound.send(date);

			} catch (Throwable t) {
				Logger.error(ExceptionUtils.getStackTrace(t));
			}
		}
	}
	
	private static class TimerJob extends Job<String> {

		@Override
		public String doJobWithResult() throws InterruptedException {
			Thread.sleep(1000);
			return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date());
		}
	}
}
