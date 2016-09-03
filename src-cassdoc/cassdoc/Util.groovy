package cassdoc

import groovy.transform.CompileStatic

import java.util.regex.Pattern

import org.apache.commons.lang3.StringUtils


@CompileStatic
class IDUtil {
  public static String sampleUUID = "d7f06b43-38ae-11e4-90a1-0e7d72389a6a";
  public static final java.util.UUID ZERO_UUID = new java.util.UUID(0, 0);
  public static final Pattern UUID_REGEX = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
  public static final Pattern DOCUUID_REGEX = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}-[A-Za-z0-9]+");

  // compress UUID with base64 or other base?
  // use timeuuid/uuid + type code compound key rather than a space-inefficient string?
  public static String timeUUID() { return new com.eaio.uuid.UUID().toString(); }
  public static String randomUUID() { return java.util.UUID.randomUUID().toString(); }
  public static java.util.UUID timeuuid()
  {
    return timeuuidCASS();
  }

  public static java.util.UUID timeuuidEAIO()
  {
    return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
  }

  public static java.util.UUID timeuuidCASS()
  {
    UUIDGen.getTimeUUID()
  }

  public static java.util.UUID randomuuid()
  {
    return java.util.UUID.randomUUID();
  }
  public static String idSuffix(String docUUID) { StringUtils.substring(docUUID, sampleUUID.length()+1) }
  public static String uuidPrefix(String docUUID) { StringUtils.substring(docUUID, 0, sampleUUID.length()) }
  public static long extractUnixTimeFromEaioTimeUUID(String cupcakeIDIN)
  {
    // magic sauce: http://stackoverflow.com/questions/13070674/get-the-unix-timestamp-from-type-1-uuid
    String cupcakeID = cupcakeIDIN;

    // in case -PROD or other enttype suffixes are dangling
    if (cupcakeID.length() > sampleUUID.length())
    {
      cupcakeID = cupcakeID.substring(0, sampleUUID.length());
    }

    // time uuid components are 100ths of seconds since adoption of the Gregorian Calendar...
    // so we need to convert to 1000ths of seconds since UNIX Epoch
    java.util.UUID uuid = java.util.UUID.fromString(cupcakeID);
    long juuTime = uuid.timestamp();
    long time = (juuTime.intdiv(10000L)) + gregorianEpoch;

    return time;
  }

  // ---- private

  private static final long gregorianEpoch = getGregorianEpoch();
  private static long getGregorianEpoch()
  {
    Calendar uuidEpoch = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    uuidEpoch.clear();
    uuidEpoch.set(1582, 9, 15, 0, 0, 0); // 9 = October
    long epochMillis = uuidEpoch.getTime().getTime();
    return epochMillis;
  }

}


@CompileStatic
class ListMap {
  static List get(Map map, Object key) {
    List l = (List)map.get(key)
    if (l == null) {
      l = new ArrayList()
      map.put(key, l)
    }
    return l
  }

  static put(Map map, Object key, Object val) {
    List l = (List)map.get(key)
    if (l == null) {
      l = new ArrayList()
      map.put(key, l)
    }
    l.add(val)
  }
}


@CompileStatic
class SetMap {
  static Set get(Map map, Object key) {
    Set l = (Set)map.get(key)
    if (l == null) {
      l = new HashSet()
      map.put(key, l)
    }
    return l
  }

  static put(Map map, Object key, Object val) {
    Set l = (Set)map.get(key)
    if (l == null) {
      l = new HashSet()
      map.put(key, l)
    }
    l.add(val)
  }
}

