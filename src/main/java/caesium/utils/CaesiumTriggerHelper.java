package caesium.utils;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class CaesiumTriggerHelper {
	public static String getTriggerInfo(Trigger trigger) {

		String result = "unknown";
		String triggerClassNameString = trigger.getClass().getName();
		switch (triggerClassNameString) {
		case "org.quartz.impl.triggers.CronTriggerImpl":
			CronTrigger cronTrigger = (CronTrigger) trigger;
			String cronExpr = cronTrigger.getCronExpression();
			result = cronExpr;
			break;
		case "org.quartz.impl.triggers.SimpleTriggerImpl":
			SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
			String simpleTriggerExpr = String.format("Interval: %s, RepeatCount: %s", simpleTrigger.getRepeatInterval(),
					simpleTrigger.getRepeatCount());
			result = simpleTriggerExpr;
			break;
		default:

			break;
		}
		return result;

	}
}
