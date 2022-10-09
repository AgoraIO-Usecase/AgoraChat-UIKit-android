package io.agora.chat.uikit.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.agora.util.EMLog;
import io.agora.util.TimeInfo;

public class EaseDateUtils {

	private static final long INTERVAL_IN_MILLISECONDS = 30 * 1000;
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	public static String getTimestampString(Context context, Date messageDate) {
	    String format = null;
        String language = Locale.getDefault().getLanguage();
        boolean isZh = language.startsWith("zh");
        long messageTime = messageDate.getTime();
        if (isSameDay(messageTime)) {
        	if(is24HourFormat(context)) {
				format = "HH:mm";
        	}else {
				if(isZh) {
					format = "aa hh:mm";
				} else {
					format = "hh:mm aa";
				}
			}
        } else if (isYesterday(messageTime)) {
            if(isZh){
            	if(is24HourFormat(context)) {
					format = "昨天 HH:mm";
            	}else {
					format = "昨天aa hh:mm";
				}
            }else{
            	if(is24HourFormat(context)) {
					return "Yesterday " + new SimpleDateFormat("HH:mm",Locale.ENGLISH).format(messageDate);
            	}else {
					return "Yesterday " + new SimpleDateFormat("hh:mm aa",Locale.ENGLISH).format(messageDate);
				}
            }
        }
        else if (isWithinThisWeek(messageDate)){
			return printDayOfWeek(context,isZh,messageDate);
		}
        else {
            if(isZh){
            	if(is24HourFormat(context)) {
					format = "M月d日 HH:mm";
            	}else {
					format = "M月d日aa hh:mm";
				}
            }
            else{
            	if(is24HourFormat(context)) {
					format = "MMM dd HH:mm";
            	}else {
					format = "MMM dd hh:mm aa";
				}
            }
        }
        if(isZh){
            return new SimpleDateFormat(format,Locale.CHINESE).format(messageDate);
        }else{
            return new SimpleDateFormat(format,Locale.ENGLISH).format(messageDate);
        }
	}

	private static boolean isWithinThisWeek(Date date){
		Calendar todayCal = Calendar.getInstance();
		Calendar dateCal = Calendar.getInstance();

		todayCal.setTime(new Date());
		dateCal.setTime(date);

		//Compare whether the current date has the same number of weeks in the year
		if (todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}

	private static String printDayOfWeek(Context context ,boolean isZh,Date messageDate) {
		String format = "";
		String week = "";
		long messageTime = messageDate.getTime();
		Date date = new Date(messageTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		format = check24Hour(context,isZh);
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {

			case Calendar.SUNDAY:
				EMLog.d("EaseDateUtils", "Today is Sunday");
				if(isZh){
					week = "星期日 ";
				}else {
					week = "Sun ";
				}
				break;
			case Calendar.MONDAY:
				EMLog.d("EaseDateUtils", "Today is Monday");
				if(isZh){
					week = "星期一 ";
				}else {
					week = "Mon ";
				}
				break;
			case Calendar.TUESDAY:
				EMLog.d("EaseDateUtils", "Today is Tuesday");
				if(isZh){
					week = "星期二 ";
				}else {
					week = "Tue ";
				}
				break;
			case Calendar.WEDNESDAY:
				EMLog.d("EaseDateUtils", "Today is Wednesday");
				if(isZh){
					week = "星期三 ";
				}else {
					week = "Wed ";
				}
				break;
			case Calendar.THURSDAY:
				EMLog.d("EaseDateUtils", "Today is Thursday");
				if(isZh){
					week = "星期四";
				}else {
					week = "Thu ";
				}
				break;
			case Calendar.FRIDAY:
				EMLog.d("EaseDateUtils", "Today is Friday");
				if(isZh){
					week = "星期五 ";
				}else {
					week = "Fri ";
				}
				break;
			case Calendar.SATURDAY:
				EMLog.d("EaseDateUtils", "Today is Saturday");
				if(isZh){
					week = "星期六 ";
				}else {
					week = "Sat";
				}
				break;
			default:

				break;
		}
		return week + new SimpleDateFormat(format,isZh? Locale.CHINESE : Locale.ENGLISH).format(messageDate);
	}

	private static String check24Hour(Context context,boolean isZh){
		String format = "";
		if(is24HourFormat(context)) {
			format = "HH:mm";
		}else {
			if(isZh) {
				format = "aa hh:mm";
			} else {
				format = "hh:mm aa";
			}
		}
		return format;
	}

	public static String getPresenceTimestampString(long time){
		String language = Locale.getDefault().getLanguage();
		boolean isZh = language.startsWith("zh");
		Date date1 = new Date(time*1000);
		Date date2 = new Date(System.currentTimeMillis());

		long diff = date2.getTime() - date1.getTime();
		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
		long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
		long second = (diff/1000-days * 24* 60 * 60 -hours * 60 * 60-minutes * 60);

		if (days > 0 ){
			return isZh? days + "天前" : days + "day ago";
		}
		if (hours > 0 ){
			return isZh? hours + "小时前" : hours + "h ago";
		}
		if (minutes > 0){
			return isZh? minutes + "分前" : minutes + "m ago";
		}
		if (second > 0){
			return isZh? second + "秒前" : second + "s ago";
		}
		return "";
	}


	public static boolean isCloseEnough(long time1, long time2) {
		// long time1 = date1.getTime();
		// long time2 = date2.getTime();
		long delta = time1 - time2;
		if (delta < 0) {
			delta = -delta;
		}
		return delta < INTERVAL_IN_MILLISECONDS;
	}

	private static boolean isSameDay(long inputTime) {
		
		TimeInfo tStartAndEndTime = getTodayStartAndEndTime();
		if(inputTime>tStartAndEndTime.getStartTime()&&inputTime<tStartAndEndTime.getEndTime())
			return true;
		return false;
	}

	private static boolean isYesterday(long inputTime) {
		TimeInfo yStartAndEndTime = getYesterdayStartAndEndTime();
		if(inputTime>yStartAndEndTime.getStartTime()&&inputTime<yStartAndEndTime.getEndTime())
			return true;
		return false;
	}

    @SuppressLint("SimpleDateFormat")
	public static Date StringToDate(String dateStr, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 
	 * @param timeLength Millisecond
	 * @return
	 */
	@SuppressWarnings("UnusedAssignment")
	@SuppressLint("DefaultLocale")
	public static String toTime(int timeLength) {
		timeLength /= 1000;
		int minute = timeLength / 60;
		int hour = 0;
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		int second = timeLength % 60;
		// return String.format("%02d:%02d:%02d", hour, minute, second);
		return String.format("%02d:%02d", minute, second);
	}
	/**
	 * 
	 * @param timeLength second
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String toTimeBySecond(int timeLength) {
//		timeLength /= 1000;
		int minute = timeLength / 60;
		int hour = 0;
		if (minute >= 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		int second = timeLength % 60;
		// return String.format("%02d:%02d:%02d", hour, minute, second);
		return String.format("%02d:%02d", minute, second);
	}
	 
	

	public static TimeInfo getYesterdayStartAndEndTime() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.DATE, -1);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);

		Date startDate = calendar1.getTime();
		long startTime = startDate.getTime();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, -1);
		calendar2.set(Calendar.HOUR_OF_DAY, 23);
		calendar2.set(Calendar.MINUTE, 59);
		calendar2.set(Calendar.SECOND, 59);
		calendar2.set(Calendar.MILLISECOND, 999);
		Date endDate = calendar2.getTime();
		long endTime = endDate.getTime();
		TimeInfo info = new TimeInfo();
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		return info;
	}

	public static TimeInfo getTodayStartAndEndTime() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar1.getTime();
		long startTime = startDate.getTime();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.HOUR_OF_DAY, 23);
		calendar2.set(Calendar.MINUTE, 59);
		calendar2.set(Calendar.SECOND, 59);
		calendar2.set(Calendar.MILLISECOND, 999);
		Date endDate = calendar2.getTime();
		long endTime = endDate.getTime();
		TimeInfo info = new TimeInfo();
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		return info;
	}

	public static TimeInfo getBeforeYesterdayStartAndEndTime() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.DATE, -2);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar1.getTime();
		long startTime = startDate.getTime();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, -2);
		calendar2.set(Calendar.HOUR_OF_DAY, 23);
		calendar2.set(Calendar.MINUTE, 59);
		calendar2.set(Calendar.SECOND, 59);
		calendar2.set(Calendar.MILLISECOND, 999);
		Date endDate = calendar2.getTime();
		long endTime = endDate.getTime();
		TimeInfo info = new TimeInfo();
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		return info;
	}
	
	/**
	 * endtime is today
	 * @return
	 */
	public static TimeInfo getCurrentMonthStartAndEndTime(){
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.DATE, 1);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar1.getTime();
		long startTime = startDate.getTime();

		Calendar calendar2 = Calendar.getInstance();
//		calendar2.set(Calendar.HOUR_OF_DAY, 23);
//		calendar2.set(Calendar.MINUTE, 59);
//		calendar2.set(Calendar.SECOND, 59);
//		calendar2.set(Calendar.MILLISECOND, 999);
		Date endDate = calendar2.getTime();
		long endTime = endDate.getTime();
		TimeInfo info = new TimeInfo();
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		return info;
	}
	
	public static TimeInfo getLastMonthStartAndEndTime(){
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.MONTH, -1);
		calendar1.set(Calendar.DATE, 1);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE, 0);
		calendar1.set(Calendar.SECOND, 0);
		calendar1.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar1.getTime();
		long startTime = startDate.getTime();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.MONTH, -1);
		calendar2.set(Calendar.DATE, 1);
		calendar2.set(Calendar.HOUR_OF_DAY, 23);
		calendar2.set(Calendar.MINUTE, 59);
		calendar2.set(Calendar.SECOND, 59);
		calendar2.set(Calendar.MILLISECOND, 999);
		calendar2.roll(Calendar.DATE,  - 1 );
		Date endDate = calendar2.getTime();
		long endTime = endDate.getTime();
		TimeInfo info = new TimeInfo();
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		return info;
	}
	
	public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());        
    }

	/**
	 * Determine whether it is a 24-hour clock
	 * @param context
	 * @return
	 */
	public static boolean is24HourFormat(Context context) {
		return android.text.format.DateFormat.is24HourFormat(context);
	}

	public static String getTimestampSimpleString(Context context, long msgTimestamp) {
		String language = Locale.getDefault().getLanguage();
		boolean isZh = language.startsWith("zh");
		StringBuilder builder = new StringBuilder();
		if(isWithinOneMinute(msgTimestamp)) {
			if(isZh){
				return "刚刚";
			}else {
				return "just";
			}
		}
		if(isWithinOneHour(msgTimestamp)) {
			int minute = getMinuteDifWithinSameHour(msgTimestamp);
			if(isZh){
				return builder.append(minute).append(" 分钟前").toString();
			}else {
				return builder.append(minute).append("m ago").toString();
			}
		}
		if(isWithin24Hour(msgTimestamp)) {
			int hour = getHourDifWithin24Hour(msgTimestamp);
			if(isZh) {
				return builder.append(hour).append(" 小时前").toString();
			}else {
				return builder.append(hour).append("h ago").toString();
			}
		}
		if(isSameWeek(msgTimestamp)) {
			int day = getDayDifWithinSameWeek(msgTimestamp);
			if(isZh) {
				return builder.append(day).append(" 天前").toString();
			}else {
				return builder.append(day).append("d ago").toString();
			}
		}
		if(isSameMonth(msgTimestamp)) {
			int week = getWeekDifWithinSameMonth(msgTimestamp);
			if(isZh) {
				return builder.append(week).append(" 周前").toString();
			}else {
				return builder.append(week).append("wk ago").toString();
			}
		}
		if(isSameYear(msgTimestamp)) {
			int month = getMonthDifWithinSameYear(msgTimestamp);
			if(isZh) {
				return builder.append(month).append(" 月前").toString();
			}else {
				return builder.append(month).append("mo ago").toString();
			}
		}
		int yearDif = getYearDif(msgTimestamp);
		if(isZh) {
			return builder.append(yearDif).append(" 年前").toString();
		}else {
			return builder.append(yearDif).append("yr ago").toString();
		}
	}

	private static boolean isWithinOneMinute(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		long current = calendar.getTime().getTime();
		return current > msgTimestamp && current - msgTimestamp < 60 * 1000;
	}

	private static boolean isWithinOneHour(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		long current = calendar.getTime().getTime();
		return current > msgTimestamp && current - msgTimestamp < 60 * 60 * 1000;
	}

	private static boolean isWithin24Hour(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		long current = calendar.getTime().getTime();
		return current > msgTimestamp && current - msgTimestamp < 24 * 60 * 60 * 1000;
	}

	private static boolean isSameWeek(long msgTimestamp) {
		return getWeekDifWithinSameMonth(msgTimestamp) == 0;
	}

	private static boolean isSameMonth(long msgTimestamp) {
		return getMonthDifWithinSameYear(msgTimestamp) == 0;
	}

	private static boolean isSameYear(long msgTimestamp) {
		return getYearDif(msgTimestamp) == 0;
	}

	/**
	 * The result may be negative number, you should consider this situation。
	 * @param msgTimestamp
	 * @return
	 */
	private static int getMinuteDifWithinSameHour(long msgTimestamp) {
		if(!isWithinOneHour(msgTimestamp)) {
		    return -1;
		}
		Calendar calendar = Calendar.getInstance(UTC);
		return (int) (Math.ceil((calendar.getTime().getTime() - msgTimestamp)*1.0f/(60 * 1000)));
	}

	/**
	 * If not within 24 hour, return -1.
	 * @param msgTimestamp
	 * @return
	 */
	private static int getHourDifWithin24Hour(long msgTimestamp) {
		if(!isWithin24Hour(msgTimestamp)) {
		    return -1;
		}
		Calendar calendar = Calendar.getInstance(UTC);
		return (int) (Math.ceil((calendar.getTime().getTime() - msgTimestamp)*1.0f/(60 * 60 * 1000)));
	}

	/**
	 * If not in the same week of year, return -1.
	 * @param msgTimestamp
	 * @return
	 */
	private static int getDayDifWithinSameWeek(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		int curYear = calendar.get(Calendar.YEAR);
		int curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		int curDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		calendar.setTime(new Date(msgTimestamp));
		int year = calendar.get(Calendar.YEAR);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if(curYear != year || curWeek != week) {
			return -1;
		}
		return curDayOfWeek - dayOfWeek;
	}

	/**
	 * If not in the same month, return -1.
	 * @param msgTimestamp
	 * @return
	 */
	private static int getWeekDifWithinSameMonth(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH);
		int curWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

		calendar.setTime(new Date(msgTimestamp));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

		if(curYear != year || curMonth != month) {
			return -1;
		}
		return curWeekOfMonth - weekOfMonth;
	}

	/**
	 * If not in the same year, return -1.
	 * @param msgTimestamp
	 * @return
	 */
	private static int getMonthDifWithinSameYear(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH);

		calendar.setTime(new Date(msgTimestamp));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);

		if(curYear != year) {
			return -1;
		}
		return curMonth - month;
	}

	private static int getYearDif(long msgTimestamp) {
		Calendar calendar = Calendar.getInstance(UTC);
		int curYear = calendar.get(Calendar.YEAR);

		calendar.setTime(new Date(msgTimestamp));
		int year = calendar.get(Calendar.YEAR);
		return curYear - year;
	}
}
