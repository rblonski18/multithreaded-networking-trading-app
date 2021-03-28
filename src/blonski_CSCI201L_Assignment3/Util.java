package blonski_CSCI201L_Assignment3;

import java.time.Duration;
import java.time.Instant;

public class Util {

		public static String printMessage(String message, Instant first) {
			Instant second = Instant.now();
			Duration duration = Duration.between(first, second);
			long mill = duration.toMillis();
			long min = duration.toMinutes();
			min %= 60;
			mill %= 1000;
			long seconds = duration.getSeconds();
			String minS = String.format("%02d", min);
			String millS = String.format("%03d", mill);
			String secondsS = String.format("%02d", seconds);
			return "["+minS+":"+secondsS+":"+millS+"]" + " " + message;
		}

}