package com.netconnect.sitienergy.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class NCUtils {

	public static String trimMobile(String mobile) {

		mobile = mobile.replaceAll(" ", "").replaceAll(",", "")
				.replaceAll("-", "");
		if (mobile.length() > 10)
			return mobile.substring(mobile.length() - 10, mobile.length());
		else
			return "";
	}

	public static String getValue(HashMap<Integer, String> hashMap, int key) {

		if (key == 0 || hashMap.get(key) == null)
			return "";
		else
			return hashMap.get(key);
	}

	public static String getValue(HashMap<Integer, String> hashMap, String key) {

		return getValue(hashMap, key, " or ");
	}

	public static Integer getKey(Map<Integer, String> entryMap, String value) {

		if (entryMap != null) {
			for (Entry<Integer, String> entry : entryMap.entrySet()) {
				if (entry.getValue().equalsIgnoreCase(value)) {
					return entry.getKey();
				}
			}
		}
		return 0;
	}

	public static Bitmap StringToBitMap(String encodedString){
		try{
			byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
			Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		}catch(Exception e){
			e.getMessage();
			return null;
		}
	}

	public static String getValue(HashMap<Integer, String> hashMap, String key,
			String secondLast) {

		if (isAny(key) || hashMap == null)
			return "";
		String value = "";
		int keys[] = stringToIntArray(key);
		for (int i = 0; i < keys.length - 1; i++) {
			String temp = hashMap.get(keys[i]);
			if (temp != null)
				value += ", " + temp;
		}

		if (value.equals("")) {
			String temp = hashMap.get(keys[keys.length - 1]);
			if (temp != null)
				value = temp;
		} else {
			String temp = hashMap.get(keys[keys.length - 1]);
			if (temp != null)
				value = value + secondLast + temp;
		}

		return value.replaceFirst(", ", "");
	}

	public static String getString(Map<String, String> hashMap, String keys) {

		String value = "";
		for (String key : keys.split("[,++]")) {
			String temp = hashMap.get(key);
			if (temp != null)
				value += "," + temp;
		}
		if (value.startsWith(","))
			return value.substring(1);
		else
			return value;
	}

	public static LinkedHashMap<Integer, String> getHashMapList(
			LinkedHashMap<Integer, LinkedHashMap<Integer, String>> hashMap,
			Object key) {

		if (key == null)
			key = "";
		return getHashMapList(hashMap, key);
	}

	public static LinkedHashMap<Integer, String> getHashMapList(
			LinkedHashMap<Integer, LinkedHashMap<Integer, String>> hashMap,
			String key) {

		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		int keys[] = stringToIntArray(key);
		for (int i = 0; i < keys.length; i++) {
			Map<Integer, String> tmpMap = hashMap.get(toInteger(keys[i]));
			if (tmpMap != null)
				map.putAll(tmpMap);
		}
		return map;
	}

	public static LinkedHashMap<Integer, String> getHashMapList(
			Map<Integer, String> hashMap, Object key) {

		if (key == null)
			key = "";
		return getHashMapList(hashMap, key.toString());
	}

	public static LinkedHashMap<Integer, String> getHashMapList(
			Map<Integer, String> hashMap, int key) {

		return getHashMapList(hashMap, String.valueOf(key));
	}

	public static LinkedHashMap<Integer, String> getHashMapList(
			Map<Integer, String> hashMap, String key) {

		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		int keys[] = stringToIntArray(key);
		for (int i = 0; i < keys.length; i++) {
			String value = hashMap.get(toInteger(keys[i]));
			if (value != null)
				map.put(keys[i], value);
		}
		return map;
	}

	public static String formatText(String text) {

		String format = "";
		if (text == null)
			return "";

		if (text.length() <= 5) {
			boolean cap = true;
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) < 97) {
					cap = false;
					break;
				}
			}
			if (cap)
				return text;
		}

		text = text.trim();
		String token[] = text.split("[ /\\+_]");

		for (int i = 0; i < token.length; i++) {
			if (token[i].length() <= 4
					&& token[i].toUpperCase(Locale.ENGLISH).equals(token[i]))
				format += token[i] + " ";
			else
				format += formatWord(token[i]) + " ";
		}
		return format.trim();
	}

	public static String keyValueHTML(String key, int value) {

		return keyValueHTML(key, String.valueOf(value));
	}

	public static String keyValueHTML(String key, double value) {

		return keyValueHTML(key, String.valueOf(value));
	}

	public static String keyValueHTML(String title, Object value) {

		if (value != null)
			return keyValueHTML(title, value.toString(), 1);
		else
			return keyValueHTML(title, "", 1);
	}

	public static String keyValueHTML(String title, String value, int colspan) {

		String html = "";
		if (title != null) {
			html += "<td class='title'>" + title + "</td>";
			html += "<td style='width:5px;'>:</td>";
			html += "<td class='value' colspan = " + colspan + ">"
					+ (isAny(value) ? "N/A" : value) + "&nbsp;</td>";
		} else if (!isAny(value))
			html += "<td colspan = 6 class='value'>" + value + "&nbsp;</td>";

		return html;
	}

	public static String keyValueHTML(HashMap<String, Object> map, String key,
			int colspan) {

		return keyValueHTML(map, key, key, colspan);
	}

	public static String keyValueHTML(HashMap<String, Object> map, String key) {

		return keyValueHTML(map, key, key, 1);
	}

	public static String keyValueHTML(HashMap<String, Object> map, String key,
			String title) {

		return keyValueHTML(map, key, title, 1);
	}

	public static String keyValueHTML(HashMap<String, Object> map, String key,
			String title, int colspan) {

		String html = "";
		String value = "";
		if (key != null)
			value = map.get(key) != null ? map.get(key).toString() : "";
		else
			value = "";

		if (title != null) {
			html += "<td class='title'>" + title + "</td>";
			html += "<td style='width:5px;'>:</td>";
			html += "<td class='value' colspan = " + colspan + ">"
					+ (isAny(value) ? "N/A" : value) + "&nbsp;</td>";
		} else if (!isAny(value))
			html += "<td colspan = 6 class='value'>" + value + "&nbsp;</td>";

		return html;
	}

	public static String getAvailableFor(int key) {

		if (key == 1)
			return "Sale";
		else if (key == 2)
			return "Rent / Lease";
		else
			return "N/A";
	}

	public static int getAvailableFor(String key) {

		key = key.toUpperCase(Locale.ENGLISH);
		if (key.indexOf("SALE") != -1)
			return 1;
		else if (key.indexOf("RENT") != -1 || key.indexOf("LEASE") != -1)
			return 2;
		else
			return 0;
	}

	public static String getRequiredFor(int key) {

		if (key == 1)
			return "purchase";
		else if (key == 2)
			return "rent/lease";
		else
			return "N/A";
	}

	public static int getRequiredFor(String key) {

		key = key.toUpperCase(Locale.ENGLISH);
		if (key.indexOf("PURCHASE") != -1)
			return 1;
		else if (key.indexOf("RENT") != -1 || key.indexOf("LEASE") != -1)
			return 2;
		else
			return 0;
	}

	public static String getGender(int key) {

		if (key == 1)
			return "Male";
		else if (key == 2)
			return "Female";
		else
			return "N/A";
	}

	public static int getGender(String key) {

		key = key.toUpperCase(Locale.ENGLISH);
		if (key.indexOf("FEMALE") != -1)
			return 2;
		else if (key.indexOf("MALE") != -1)
			return 1;
		else
			return 0;
	}

	public static String getMaritalStatus(int key) {

		if (key == 1)
			return "Single";
		else if (key == 2)
			return "Married";
		else if (key == 3)
			return "Divorced";
		else if (key == 4)
			return "Widowed/Widower";
		else
			return "N/A";
	}

	public static int getMaritalStatus(String key) {

		key = key.toUpperCase(Locale.ENGLISH);
		if (key.indexOf("SINGLE") != -1)
			return 1;
		else if (key.indexOf("MARRIED") != -1)
			return 2;
		else if (key.indexOf("DIVORCED") != -1)
			return 3;
		else if (key.indexOf("WIDOWED") != -1 || key.indexOf("WIDOWEDER") != -1)
			return 4;
		else
			return 0;
	}

	public static String getTaskType(int key) {

		if (key == 1)
			return "Phone Call";
		else if (key == 2)
			return "Meeting";
		else if (key == 3)
			return "General";
		else
			return "N/A";
	}

	public static String getContactType(int key) {

		if (key == 1)
			return "Client";
		else if (key == 2)
			return "Associate";
		else if (key == 3)
			return "Developer";
		else
			return "Contact";
	}

	public static String getContactType(Object oKey) {

		int key = toInteger(oKey);
		if (key == 1)
			return "Client";
		else if (key == 2)
			return "Associate";
		else if (key == 3)
			return "Developer";
		else
			return "Contact";
	}

	public static int getContactTypeID(String key) {

		key = key.toUpperCase(Locale.ENGLISH);
		if (key.indexOf("CLIENT") != -1)
			return 1;
		else if (key.indexOf("ASSOCIATE") != -1 || key.indexOf("DEALER") != -1)
			return 2;
		else if (key.indexOf("DEVELOPER") != -1 || key.indexOf("BUILDER") != -1)
			return 3;
		else
			return 0;
	}

	public static boolean isEquals(String textA, String textB) {

		if (isAny(textA))
			textA = "";
		if (isAny(textB))
			textB = "";
		return textA.trim().equals(textB.trim());
	}

	public static boolean isEquals(Object oA, Object oB) {

		if (oA == null && oB == null)
			return true;
		else if (oA != null && oB == null)
			return false;
		else if (oA == null && oB != null)
			return false;
		else
			return oA.equals(oB);
	}

	public static boolean toBoolean(Object o) {

		if (o == null)
			return false;
		else
			return o.equals(true);
	}

	public static int toInteger(Object o) {

		if (o == null)
			return 0;
		else
			return toInteger(o.toString());
	}

	public static int toInteger(Object o, int defaultValue) {

		if (o == null)
			return defaultValue;
		else
			return toInteger(o.toString());
	}

	public static int toInteger(String text) {

		return toInteger(text, 0);
	}

	public static int toInteger(String text, int defaultValue) {

		return (int) (toDouble(text, defaultValue));
	}

	public static double toDouble(Object o) {

		if (o == null)
			return 0;
		else
			return toDouble(o.toString());
	}

	public static double toDouble(String text) {

		return toDouble(text, 0);
	}

	public static double toDouble(String text, double defaultValue) {

		double value = defaultValue;
		try {
			text = text.toLowerCase(Locale.ENGLISH).replaceAll(",", "")
					.replaceAll("%", "").replaceAll("/", "");
			if (text.indexOf("cr") != -1) {
				text = text.replaceAll("crores", "").replaceAll("crore", "")
						.replaceAll("cr", "");
				value = Double.parseDouble(text.trim()) * 10000000;
			} else if (text.indexOf("mn") != -1 || text.indexOf("m") != -1) {
				text = text.replaceAll("mn", "").replaceAll("m", "");
				value = Double.parseDouble(text.trim()) * 1000000;
			} else if (text.indexOf("l") != -1) {

				text = text.replaceAll("lakhs", "").replaceAll("lacs", "")
						.replaceAll("lakh", "").replaceAll("lac", "")
						.replaceAll("lc", "").replaceAll("l", "");
				value = Double.parseDouble(text.trim()) * 100000;
			} else if (text.indexOf("k") != -1) {
				text = text.replaceAll("k", "");
				value = Double.parseDouble(text.trim()) * 1000;
			} else
				value = Double.parseDouble(text.trim());
		} catch (Exception e) {
		}

		return value;
	}

	public static String toString(Object o) {

		if (o == null)
			return "";
		else
			return o.toString();
	}

	public static String toString(Object o, String format) {

		if (o == null)
			return "";
		else if (o instanceof java.util.Date) {
			SimpleDateFormat formatter = new SimpleDateFormat(format,
					Locale.ENGLISH);
			return formatter.format(o);
		}
		return "";
	}

	public static String toString(int value) {

		return toString(value, "");
	}

	public static String toString(int value, String defaultValue) {

		if (value <= 0)
			return defaultValue;
		else
			return String.valueOf(value);
	}

	public static String toString(double value) {

		return toString(value, "");
	}

	public static String toString(double value, String defaultValue) {

		NumberFormat nf = null;
		if (value < 100)
			nf = new DecimalFormat("###.00");
		else
			nf = new DecimalFormat("###.##");

		if (value <= 0)
			return defaultValue;
		else
			return nf.format(value);
	}

	public static String roundTo2Decimal(double number) {

		NumberFormat nf = new DecimalFormat("###.00");
		return nf.format(number);
	}

	public static String formatTo2Digits(int value) {

		if (value < 10)
			return "0" + String.valueOf(value);
		else
			return String.valueOf(value);
	}

	public static String formatNumber(int number, String defaultValue) {

		return formatNumber((double) number, defaultValue);
	}

	public static String formatNumber(int number) {

		return formatNumber((double) number);
	}

	public static String formatNumber(double number) {

		return formatNumber(number, "");
	}

	public static String formatNumber(double number, String defaultValue) {

		if (number <= 0 && defaultValue != null)
			return defaultValue;

		DecimalFormat formatter = new DecimalFormat("###,###.###");
		return formatter.format(number);
	}

	public static String numberToString(double number) {

		return numberToString(number, "");
	}

	public static String numberToString(double number, String defaultValue) {

		DecimalFormat formatter = new DecimalFormat("###,###.###");
		if (number / 10000000 >= 1)
			return formatter.format(number / 10000000) + " Cr";
		else if (number / 100000 >= 1)
			return formatter.format(number / 100000) + " Lac";
		else
			return formatter.format(number);
	}

	public static int getRandomNumber(int n) {

		int startingNumber = (int) Math.pow(10, n - 1);
		int offset = (int) Math.pow(10, n) - startingNumber - 1;
		Random randomGenerator = new Random();
		int randomInt = startingNumber + randomGenerator.nextInt(offset);
		return randomInt;
	}

	public static String currentTime() {

		return toString(new Timestamp(System.currentTimeMillis()));
	}

	public static String currentTime(String format) {

		return toString(new Timestamp(System.currentTimeMillis()), "dd-MM-yyyy");
	}

	public static String toString(Date sqlDate) {

		if (isAny(sqlDate))
			return "";
		try {
			if (sqlDate.getTime() > System.currentTimeMillis() * 2)
				return "-";
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",
					Locale.ENGLISH);
			return formatter.format(sqlDate);
		} catch (Exception e) {
		}
		return "";
	}

	public static java.util.Date toUtilDate(Date date) {

		if (date == null)
			return null;
		return new java.util.Date(date.getTime());
	}

	public static String toString(Timestamp timestamp) {

		return toString(timestamp, "dd-MM-yyyy hh:mm aaa");
	}

	public static String toString(Timestamp timestamp, String format,
			String timeZone) {

		if (isAny(timestamp))
			return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format,
					Locale.ENGLISH);
			formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
			return formatter.format(timestamp);
		} catch (Exception e) {
		}
		return null;
	}

	public static String toString(Date timestamp, String format) {

		if (timestamp == null || timestamp.getTime() == 0)
			return "-";

		String date = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format,
					Locale.ENGLISH);
			date = formatter.format(timestamp);
		} catch (Exception e) {
		}
		return date;
	}

	public static Date toDate(Object o) {

		if (o instanceof java.util.Date)
			return new Date(((java.util.Date) o).getTime());
		else
			return new Date(System.currentTimeMillis());
	}

	public static Date toDate(String date) {

		return toDate(date, "MM-dd-yyyy");
	}

	public static Date toDate(String date, String format) {

		return toDate(date, format, "GMT");
	}

	public static Date toDate(String date, String format, String timeZone) {

		if (isAny(date))
			return null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format,
					Locale.ENGLISH);
			formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
			return new Date(formatter.parse(date).getTime());
		} catch (Exception e) {
		}
		return null;
	}

	public static Timestamp toTimestamp(Object o) {

		if (o instanceof java.util.Date)
			return new Timestamp(((java.util.Date) o).getTime());
		else
			return new Timestamp(System.currentTimeMillis());
	}

	public static Timestamp toTimestamp(String timestamp) {

		return toTimestamp(timestamp, "yyyy-MM-dd hh:mm:ss");
	}

	public static Timestamp toTimestamp(String timestamp, String format) {

		if (isAny(timestamp))
			return null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format,
					Locale.ENGLISH);
			return new Timestamp(formatter.parse(timestamp).getTime());
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean validateURL(String URL) {

		Pattern p = Pattern
				.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		return p.matcher(URL).matches();
	}

	public static boolean validateEmail(String email) {

		if (isAny(email))
			return false;

		String emailArray[] = email.split(",");
		for (int i = 0; i < emailArray.length; i++) {
			String emailID = emailArray[i].trim();
			Pattern p = Pattern
					.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
			if (!p.matcher(emailID.toLowerCase(Locale.ENGLISH)).matches())
				return false;
		}
		return true;
	}

	public static boolean validateMobile(String mobile) {

		if (isAny(mobile))
			return false;

		String mobileArray[] = mobile.split(",");
		for (int i = 0; i < mobileArray.length; i++) {
			String mobileNo = mobileArray[i].trim();
			Pattern p = Pattern.compile("[0-9]*");
			if (!p.matcher(mobileNo.toLowerCase(Locale.ENGLISH)).matches())
				return false;
		}
		return true;
	}

	public static double areaConvertor(int previousUnit, int newUnit,
			double value) {

		double[] multiplyFactor = new double[11];
		multiplyFactor[0] = 1;
		multiplyFactor[1] = 0.0930000000000000000; // sq.mts
		multiplyFactor[2] = 0.1111111111111000000; // sq.yards
		multiplyFactor[3] = 0.0000229566300000000; // acres
		multiplyFactor[4] = 0.0000092904166000000; // hectares
		multiplyFactor[5] = 0.0000573921030000000; // bigha1
		multiplyFactor[6] = 0.000002869605100000; // biswa1
		multiplyFactor[7] = 0.00138888890000000; // kottah
		multiplyFactor[8] = 0.00018521194000000; // kanal
		multiplyFactor[9] = 0.00000926059690000; // marla
		multiplyFactor[10] = 0.00041666667000000;// ground
		double areaValueInSqFt = value / multiplyFactor[previousUnit];
		return areaValueInSqFt * multiplyFactor[newUnit];
	}

	public static String arrayToString(int array[]) {

		if (array == null || array.length == 0)
			return "";

		if (array.length == 1)
			return String.valueOf(array[0]);

		String string = String.valueOf(array[0]);
		for (int i = 1; i < array.length; i++)
			string += "," + array[i];

		return string;
	}

	public static int[] stringToIntArray(String string) {

		if (isAny(string))
			return new int[0];

		String tmp[] = string.split(",");
		int itmp[] = new int[tmp.length];
		for (int i = 0; i < itmp.length; i++) {
			itmp[i] = toInteger(tmp[i].trim(), -1);
		}
		return itmp;
	}

	public static ArrayList<Integer> toIntArrayList(String string) {

		ArrayList<Integer> list = new ArrayList<Integer>();
		string = string.replace("[", "").replace("]", "");
		if (!isAny(string)) {
			String tmp[] = string.split(",");
			for (String s : tmp)
				list.add(toInteger(s));
		}
		return list;
	}

	public static String formatSentence(String sentence) {

		if (sentence == null)
			return "";

		sentence = sentence.trim().toLowerCase(Locale.ENGLISH);
		String token[] = sentence.split("[ +_]");

		sentence = "";

		sentence += formatWord(token[0]) + " ";
		for (int i = 1; i < token.length; i++)
			sentence += token[i] + " ";

		return sentence.trim();
	}

	public static String format(String string) {

		return format(string, 2);
	}

	public static String format(String string, int ignoreLength) {

		if (string == null)
			return "";

		if (string.endsWith("'"))
			return string;

		string = string.trim();
		string = string.replaceAll("___", " / ");
		string = string.replaceAll("__", " : ");
		string = string.replaceAll("_", " ");
		String token[] = string.split("[ /\\+_]");

		String sentence = "";

		for (int i = 0; i < token.length; i++) {
			if (string.toUpperCase(Locale.ENGLISH).indexOf(
					token[i].toUpperCase(Locale.ENGLISH) + "/") != -1)
				sentence += formatWord(token[i], ignoreLength) + "/";
			else if (string.toUpperCase(Locale.ENGLISH).indexOf(
					token[i].toUpperCase(Locale.ENGLISH) + "\\") != -1)
				sentence += formatWord(token[i], ignoreLength) + "/";
			else
				sentence += formatWord(token[i], ignoreLength) + " ";
		}
		return sentence.trim();
	}

	public static String formatWord(String word) {

		return formatWord(word, 2);
	}

	public static String formatWord(String word, int ignoreLength) {

		if (word == null)
			return "";

		if (word.length() <= ignoreLength) {
			boolean cap = true;
			for (int i = 0; i < word.length(); i++) {
				if (word.charAt(i) >= 97) {
					cap = false;
					break;
				}
			}
			if (cap)
				return word;
		}

		word = word.trim();

		if (word.length() == 0)
			return "";
		else if (word.length() <= ignoreLength)
			return word;
		else
			return word.substring(0, 1).toUpperCase(Locale.ENGLISH)
					+ word.substring(1).toLowerCase(Locale.ENGLISH);
	}

	public static String[] removeFromArray(String array[], String item) {

		try {
			String tmpArray[] = new String[array.length - 1];
			int j = 0;
			for (int i = 0; i < array.length; i++) {
				if (!array[i].equalsIgnoreCase(item))
					tmpArray[j++] = array[i];
			}

			return tmpArray;
		} catch (Exception e) {
			return array;
		}
	}

	public static String[] addToArray(String array[], String item) {

		if (array == null)
			return new String[] { item };

		String tmpArray[] = new String[array.length + 1];

		System.arraycopy(array, 0, tmpArray, 0, array.length);
		tmpArray[array.length] = item;

		return tmpArray;
	}

	public static String arrayToString(String array[]) {

		if (array == null || array.length == 0)
			return "";
		if (array.length == 1)
			return array[0];

		String string = array[0];
		for (int i = 1; i < array.length; i++)
			string += "," + array[i];

		return string;
	}

	public static boolean isCommonExists(ArrayList<String> listA,
			String arrayB[]) {

		if (listA == null || arrayB == null)
			return true;
		for (int i = 0; i < arrayB.length; i++) {
			if (listA.contains(arrayB[i].trim()))
				return true;
		}

		return false;
	}

	public static boolean isCommonExists(ArrayList<String> listA, String arrayB) {

		return isCommonExists(listA, arrayB.split(","));
	}

	public static boolean isCommonExists(String arrayA[], String arrayB[]) {

		if (arrayA == null || arrayB == null)
			return true;

		for (int i = 0; i < arrayA.length; i++) {
			for (int j = 0; j < arrayB.length; j++) {
				if (arrayA[i].trim().equalsIgnoreCase(arrayB[j].trim()))
					return true;
			}
		}

		return false;
	}

	public static boolean isCommonExists(String arrayA, String arrayB) {

		if (arrayA == null || arrayB == null)
			return true;
		return isCommonExists(arrayA.split(","), arrayB.split(","));
	}

	public static boolean isCommonExists(String arrayA, int value) {

		if (arrayA == null)
			return true;
		return isCommonExists(arrayA, String.valueOf(value));
	}

	public static boolean isCommonExists(String arrayA, byte value) {

		if (arrayA == null)
			return true;
		return isCommonExists(arrayA, String.valueOf(value));
	}

	public static boolean isCommonExists(String arrayA, short value) {

		if (arrayA == null)
			return true;
		return isCommonExists(arrayA, String.valueOf(value));
	}

	public static boolean isExists(ArrayList<String> listA, String arrayB[]) {

		if (listA == null || arrayB == null)
			return false;

		for (int i = 0; i < arrayB.length; i++) {
			if (!listA.contains(arrayB[i].trim()))
				return false;
		}

		return true;
	}

	public static boolean isExists(ArrayList<String> listA, String arrayB) {

		if (listA == null || arrayB == null)
			return true;
		return isExists(listA, arrayB.split(","));
	}

	public static boolean isExists(String arrayA[], String arrayB[]) {

		if (arrayA == null || arrayB == null)
			return false;

		for (int i = 0; i < arrayA.length; i++) {
			for (int j = 0; j < arrayB.length; j++) {
				if (!arrayA[i].trim().equalsIgnoreCase(arrayB[j].trim()))
					return false;
			}
		}

		return true;
	}

	public static boolean isExists(String arrayA, String arrayB) {

		if (arrayA == null || arrayB == null)
			return false;
		return isExists(arrayA.split(","), arrayB.split(","));
	}

	public static boolean isExists(String arrayA, int value) {

		if (arrayA == null)
			return false;
		return isExists(arrayA, String.valueOf(value));
	}

	public static boolean isExists(String arrayA, byte value) {

		if (arrayA == null)
			return false;
		return isExists(arrayA, String.valueOf(value));
	}

	public static boolean isExists(String arrayA, short value) {

		if (arrayA == null)
			return false;
		return isExists(arrayA, String.valueOf(value));
	}

	public static boolean inBetween(double max, double min, double value) {

		return (value >= min && value <= max);
	}

	public static boolean inBetween(int max, int min, int value) {

		return (value >= min && value <= max);
	}

	public static boolean inBetween(byte max, byte min, byte value) {

		return (value >= min && value <= max);
	}

	public static boolean inBetween(int max, int min, int value[]) {

		boolean flag = false;
		for (int i = 0; i < value.length; i++)
			if (value[i] >= min && value[i] <= max)
				flag = true;
		return flag;
	}

	public static boolean inBetween(Timestamp max, Timestamp min,
			Timestamp value) {

		if (max == null || min == null || value == null)
			return false;
		return (value.after(min) && value.before(max)) || max.equals(value)
				|| min.equals(value);
	}

	public static boolean inBetween(Date max, Date min, Date value) {

		if (max == null || min == null || value == null)
			return false;
		Timestamp tmax = new Timestamp(max.getTime() + 24 * 60 * 60 * 1000);
		Timestamp tmin = new Timestamp(min.getTime());
		Timestamp tvalue = new Timestamp(value.getTime());

		return inBetween(tmax, tmin, tvalue);
	}

	public static int getIndex(String array[], int value) {

		int index = 0;
		for (int i = 0; i < value; i++)
			if (!array[i].equals(""))
				index++;

		return index;
	}

	public static int[] getIndeces(String array1[], String array2[]) {

		if (array2.length == 1 && array2[0].equals("-"))
			return new int[] {};

		int indices[] = new int[array2.length];
		for (int i = 0; i < array2.length; i++) {
			for (int j = 0; j < array1.length; j++) {
				if (array1[j].equalsIgnoreCase(array2[i])) {
					indices[i] = j;
					break;
				}
			}
		}
		return indices;
	}

	public static int getIndex(String array[], String value) {

		for (int i = 0; i < array.length; i++)
			if (array[i].equalsIgnoreCase(value))
				return i;

		return -1;
	}

	public static int getIndex(int array[], int value) {

		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;

		return -1;
	}

	public static String[] getArray(String array[], int indices[]) {

		String tmpArray[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++)
			if (array.length > indices[i])
				tmpArray[i] = array[indices[i]];
		return tmpArray;
	}

	public static String[] getCommonArray(String arrayA[], String arrayB[]) {

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < arrayA.length; i++) {
			for (int j = 0; j < arrayB.length; j++) {
				if (arrayA[i].equalsIgnoreCase(arrayB[j]))
					list.add(arrayA[i]);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public static int getFileSize(String filePath) {

		return getFileSize(new File(filePath));
	}

	public static int getFileSize(File file) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return fis.available();
		} catch (Exception e) {
			return 0;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	public static String toBinarySize(long size) {

		long kb = 1024;
		long mb = kb * kb;

		if (size < kb)
			return size + " B";
		else if (size < mb)
			return roundTo2Decimal((double) size / kb) + " KB";
		else
			return roundTo2Decimal((double) size / mb) + " MB";
	}

	public static boolean isAny(Object o) {

		if (o == null)
			return true;
		else
			return isCommonExists(o.toString().split(","), new String[] { "-1",
					"null", "Any", "Min", "Max", "All", "Select", "None", "",
					"()", "[]", "0", "0.0", "[All]" });
	}

	public static String toTime(long seconds) {

		int t = (int) (seconds / 1000);
		return toTime(t) + "." + (seconds - t * 1000);
	}

	public static String toTime(int seconds) {

		int hrs = seconds / (3600);
		int min = (seconds / 60) % 60;
		int sec = seconds % 60;
		return formatTo2Digits(hrs) + ":" + formatTo2Digits(min) + ":"
				+ formatTo2Digits(sec);
	}

	public static void deleteFile(String filePath) {

		deleteFile(new File(filePath));
	}

	public static void deleteFile(File file) {

		file.delete();
	}

	public static void createFile(String filePath) {

		createFile(new File(filePath));
	}

	public static void createFile(File file) {

		try {
			new File(file.getParent()).mkdirs();
			new FileWriter(file).close();
		} catch (Exception e) {
		}
	}

	public static int getMonthDays(int year, int month) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static String formatTime(long miliseconds) {

		int seconds = (int) miliseconds / 1000;
		int hr = seconds / 3600;
		int min = (seconds - hr * 3600) / 60;
		if (hr == 12)
			return formatTo2Digits(hr) + ":" + formatTo2Digits(min) + " PM";
		else if (hr == 0)
			return formatTo2Digits(12) + ":" + formatTo2Digits(min) + " AM";
		else if (hr > 12)
			return formatTo2Digits(hr - 12) + ":" + formatTo2Digits(min)
					+ " PM";
		else
			return formatTo2Digits(hr) + ":" + formatTo2Digits(min) + " AM";
	}

	public static String formatTime(int seconds) {

		int hr = seconds / 3600;
		int min = (seconds - hr * 3600) / 60;
		int sec = seconds - hr * 3600 - min * 60;
		return formatTo2Digits(hr) + ":" + formatTo2Digits(min) + ":"
				+ formatTo2Digits(sec);
	}

	public static int toSeconds(String time) {

		String token[] = time.split(":");
		return toInteger(token[0]) * 3600 + toInteger(token[1]) * 60
				+ toInteger(token[2]);
	}

	public static ArrayList<String> intersection(ArrayList<String> listA,
			ArrayList<String> listB) {

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < listA.size(); i++)
			if (listB.contains(listA.get(i)))
				list.add(list.get(i));
		return list;
	}

	public static void sleep(final long ms) {

		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	public static void closeConnection(Connection conn) {

		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}

	public static void close(Connection conn) {

		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}

	public static void close(PreparedStatement ps) {

		try {
			if (ps != null)
				ps.close();
		} catch (Exception e) {
		}
	}

	public static void close(CallableStatement cs) {

		try {
			if (cs != null)
				cs.close();
		} catch (Exception e) {
		}
	}

	public static void close(ResultSet rs) {

		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
		}
	}

	public static void close(InputStream is) {

		try {
			if (is != null)
				is.close();
		} catch (Exception e) {
		}
	}

	public static void close(OutputStream os) {

		try {
			if (os != null)
				os.close();
		} catch (Exception e) {
		}
	}

	public static void close(FileInputStream fis) {

		try {
			if (fis != null)
				fis.close();
		} catch (Exception e) {
		}
	}

	public static void close(FileOutputStream fos) {

		try {
			if (fos != null)
				fos.close();
		} catch (Exception e) {
		}
	}

	public static void close(ObjectOutputStream oos) {

		try {
			if (oos != null)
				oos.close();
		} catch (Exception e) {
		}
	}

	public static void close(ObjectInputStream ois) {

		try {
			if (ois != null)
				ois.close();
		} catch (Exception e) {
		}
	}

	public static void close(Socket socket) {

		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
	}

	public static int hex2decimal(String s) {

		String digits = "0123456789ABCDEF";
		s = s.toUpperCase(Locale.ENGLISH);
		int val = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}

	public static String decimal2hex(int d) {

		String digits = "0123456789ABCDEF";
		if (d == 0)
			return "00";
		String hex = "";
		while (d > 0) {
			int digit = d % 16;
			hex = digits.charAt(digit) + hex;
			d = d / 16;
		}
		if (hex.length() == 1)
			return "0" + hex;
		else
			return hex;
	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {

		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	public static HashMap<String, String> getJSONList(String json) {

		return getJSONList(json, ";,");
	}

	public static HashMap<String, String> getJSONList(String json, String delim) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {
			if (json != null) {
				json = json.replaceAll("[}{']", "");
				String jsonToken[] = json.split("[" + delim + "]");
				for (int i = 0; i < jsonToken.length; i++) {
					String token[] = jsonToken[i].split(":");
					if (token.length > 1)
						hashMap.put(token[0], token[1]);
				}
			}
		} catch (Exception e) {
		}
		return hashMap;
	}

	public static HashMap<String, String> getTagHashMap(String text) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		int i = 0;
		while ((i = text.indexOf("{", i)) != -1) {
			int j = text.indexOf("}", i);
			hashMap.put(text.substring(i, j + 1), "");
			i++;
		}
		return hashMap;
	}

	public static String formatExpression(String expression) {

		while (expression.indexOf("%") != -1) {
			int index = expression.indexOf("%");
			String tmp = "";
			int j = index - 1;
			while (expression.charAt(j) != ' ' && expression.charAt(j) != '*') {
				tmp = expression.charAt(j) + tmp;
				j--;
			}
			double fraction = toDouble(tmp) / 100;
			expression = expression.replaceAll(tmp + "%",
					String.valueOf(fraction));
		}
		return expression;
	}

	public static String getRangeFormat(int min, int max) {

		String range = "";
		if (min == max && min <= 0)
			return "";

		else if (min == max)
			return formatNumber(min);

		if (min <= 0)
			range = "up";
		else
			range = formatNumber(min);

		if (max <= 0)
			range += " to any";
		else
			range += " to " + formatNumber(max);

		return range;
	}

	public static String getYearRangeFormat(int min, int max) {

		String range = "";

		if (min == max && (min == 0 || min == -1))
			return "";
		else if (min == max)
			return String.valueOf(min);

		if (min <= 0)
			range = "up";
		else
			range = String.valueOf(min);

		if (max <= 0)
			range += "+";
		else
			range += " to " + String.valueOf(max);

		return range;
	}

	public static String getRangeFormat(double min, double max) {

		String range = "";

		if (min == max && min <= 0)
			return "";

		else if (min == max)
			return NCUtils.formatNumber(min);

		if (min <= 0)
			range = "up";
		else
			range = formatNumber(min);

		if (max <= 0)
			range += "+";
		else
			range += " to " + formatNumber(max);

		return range;
	}

	public static String getRangeFormat_(double min, double max) {

		String range = "";

		if (min == max && min <= 0)
			return "";

		else if (min == max)
			return NCUtils.numberToString(min);

		if (min <= 0)
			range = "up";
		else
			range = numberToString(min);

		if (max <= 0)
			range += "+";
		else
			range += " to " + numberToString(max);

		return range;
	}

	@SuppressWarnings("unchecked")
	public static boolean isDifferent(HashMap<String, Object> map1,
			HashMap<String, Object> map2) {

		Set<String> keySet = map1.keySet();
		for (Object key : keySet) {
			if (key.equals("metaText"))
				continue;

			Object o1 = map1.get(key);
			Object o2 = map2.get(key);
			if (o1 != null && o2 != null) {
				if (map1.get(key) instanceof HashMap) {
					if (isDifferent((HashMap<String, Object>) o1,
							(HashMap<String, Object>) o2))
						return true;
				} else {
					String value1 = map1.get(key).toString();
					String value2 = map2.get(key).toString();
					if (!isEquals(value1, value2)) {
						return true;
					}
				}
			} else if (isAny(o1) && isAny(o2))
				continue;
			else {
				return true;
			}
		}

		return false;
	}

	public static String getDifference(HashMap<String, Object> map1,
			HashMap<String, Object> map2) {

		Set<String> keySet = map1.keySet();
		String trs = "";
		for (String key : keySet) {
			if (map1.get(key) instanceof String
					&& map2.get(key) instanceof String) {
				String value1 = map1.get(key).toString();
				String value2 = map2.get(key).toString();
				if (!isEquals(value1, value2)) {
					trs += "<tr>\n";
					trs += "<td class='ft'>" + key + "</td>\n";
					trs += "<td class='ov'>" + value1 + "</td>\n";
					trs += "<td class='nv'>" + value2 + "</td>\n";
					trs += "<tr>\n";
				}
			}
		}

		if (!trs.equals("")) {
			String table = "";
			table += "<tr>\n";
			table += "<th>Field Type</th>\n";
			table += "<th>Before</th>\n";
			table += "<th>After</th>\n";
			table += "<tr>\n";
			table = "<table class='ct'>\n" + table + trs + "</table>";

			return table;
		}

		return "";
	}

	public static String searchMap(Map<Integer, String> map, String keywords) {

		ArrayList<String> list = new ArrayList<String>();
		Set<Integer> keySet = map.keySet();
		keywords = keywords.toUpperCase(Locale.ENGLISH);
		for (Integer key : keySet) {
			String value = map.get(key);
			if (!NCUtils.isAny(value)
					&& (keywords.equals(".") || keywords.equals(" ") || value
							.toUpperCase(Locale.ENGLISH).indexOf(keywords) != -1))
				list.add("{\"id\":\"" + key + "\",\"name\":\"" + value + "\"}");
		}
		return list.toString();
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> toHashMap(
			HashMap<String, Object> object) {

		HashMap<String, String> map = new HashMap<String, String>();
		Set<String> keySet = object.keySet();
		for (String key : keySet) {
			Object o = object.get(key);
			if (o instanceof HashMap)
				map.putAll(toHashMap((HashMap<String, Object>) o));
			if (o instanceof java.util.Date) {
				Timestamp time = new Timestamp(((java.util.Date) o).getTime());
				map.put(key, NCUtils.toString(time));
			} else {
				if (object.get(key) != null)
					map.put(key, object.get(key).toString());
				else
					map.put(key, "");
			}
		}
		return map;
	}

	public static int[] toIntArray(Collection<Integer> array) {

		int iArray[] = new int[array.size()];
		int i = 0;
		for (Integer I : array)
			iArray[i++] = I.intValue();
		return iArray;
	}

	public static String formatWebsite(String website) {

		website = website.toLowerCase(Locale.ENGLISH);
		if (website.startsWith("www.") || website.indexOf("://www.") != -1)
			website = website.replaceAll("www.", "");
		if (website.endsWith("/"))
			website = website.substring(0, website.length() - 1);
		return website;
	}

	public static String toWebLink(String website) {

		website = website.toLowerCase(Locale.ENGLISH);
		if (website.startsWith("www.") || website.indexOf("://www.") != -1)
			website = website.replaceAll("www.", "");
		if (website.endsWith("/"))
			website = website.substring(0, website.length() - 1);
		if (website.startsWith("http"))
			return "<a href='" + website + "' target = '_blank'>" + website
					+ "</a>";
		else
			return "<a href='http://" + website + "' target = '_blank'>http://"
					+ website + "</a>";
	}

	public static Calendar toCalendar(String date) {

		return toCalendar(date, "dd-MM-yyyy");
	}

	public static Calendar toCalendar(String date, String format) {

		try {
			Calendar cal = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
			cal.setTime(df.parse(date));
			return cal;
		} catch (Exception e) {
		}
		return Calendar.getInstance();
	}

	public static HashMap<String, String> jsonToHashMap(String json) {

		return jsonToHashMap(json, ";,");
	}

	public static HashMap<String, String> jsonToHashMap(String json,
			String delim) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {
			if (json != null) {
				json = json.replaceAll("[}{']", "");
				String jsonToken[] = json.split("[" + delim + "]");
				for (int i = 0; i < jsonToken.length; i++) {
					String token[] = jsonToken[i].split(":");
					if (token.length > 1)
						hashMap.put(token[0], token[1]);
				}
			}
		} catch (Exception e) {
		}
		return hashMap;
	}

	public static Set<Integer> toIntSet(String ids) {

		Set<Integer> set = new HashSet<Integer>();
		if (!isAny(ids)) {
			String[] tokens = ids.split(",");
			for (String token : tokens)
				set.add(NCUtils.toInteger(token));
		}
		return set;
	}

	public static Set<String> toSet(String ids) {

		Set<String> set = new HashSet<String>();
		if (!isAny(ids)) {
			String[] tokens = ids.split(",");
			for (String token : tokens) {
				set.add(token.trim());
			}
		}
		return set;
	}

	public static String toString(Collection<?> c) {

		if (c == null)
			return "";
		else
			return c.toString().replace("[", "").replace("]", "")
					.replace(", ", ",");
	}
}
