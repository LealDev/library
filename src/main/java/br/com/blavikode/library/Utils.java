package br.com.blavikode.library;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static br.com.blavikode.library.ApplicationConstants.*;
import static com.google.api.client.googleapis.auth.oauth2.GoogleCredential.fromStream;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.currentThread;
import static java.time.LocalDate.now;
import static java.time.LocalDateTime.of;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public final class Utils {
  private static final Logger LOGGER = getLogger(Utils.class);

  public enum Action {
    SAVE, DELETE, NONE, IMPORT
  }

  public static String getLogin() {
    return allNotNull(getContext(), getContext().getAuthentication()) ? getContext().getAuthentication().getName() : EMPTY;
  }

//  public static Boolean isRoot() {
//    return allNotNull(getContext(), getContext().getAuthentication()) ? ((Usuario) getContext().getAuthentication().getDetails()).getRoot() : false;
//  }
//
//  public static Boolean isApp() {
//    return allNotNull(getContext(), getContext().getAuthentication()) ? ((Usuario) getContext().getAuthentication().getDetails()).getApp() : false;
//  }
//
//  public static Authentication getAuthentication(final Usuario usuario) {
//    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, EMPTY, newArrayList());
//    authentication.setDetails(authentication.getPrincipal());
//    return authentication;
//  }

  public static Date midnightMinusDays(final int days) {
    return from(of(now(), MIDNIGHT).minusDays(days).atZone(systemDefault()).toInstant());
  }

  public static Date midnightPlusDays(final int days) {
    return from(of(now(), MIDNIGHT).plusDays(days).atZone(systemDefault()).toInstant());
  }

  public static Date noonMinusDays(final int days) {
    return from(of(now(), NOON).minusDays(days).atZone(systemDefault()).toInstant());
  }

  public static Date noonPlusDays(final int days) {
    return from(of(now(), NOON).plusDays(days).atZone(systemDefault()).toInstant());
  }

  public static String getAccessToken() throws IOException {
    final GoogleCredential googleCredential = fromStream(currentThread().getContextClassLoader().getResourceAsStream(SERVICE_ACCOUNT_JSON)).createScoped(newArrayList(MESSAGING_SCOPE));
    googleCredential.refreshToken();
    return googleCredential.getAccessToken();
  }

  public static String returnDateFormat(final Date date, final String format) {
    if (!allNotNull(date)) {
      return null;
    } else {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      final DateFormat dateFormat = new SimpleDateFormat(format);
      return dateFormat.format(calendar.getTime());
    }
  }

  public static long getDiffInMinutes(final Date dataOnline) {
    final LocalDateTime dataAtual = new Date().toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    final LocalDateTime localDateOnline = dataOnline.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    return ChronoUnit.MINUTES.between(localDateOnline, dataAtual);
  }

  public static long getDiffBetweenDatesInMinutes(final Date startDate, final Date endDate) {
    final LocalDateTime localStartDate = startDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    final LocalDateTime localEndDate = endDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    return ChronoUnit.MINUTES.between(localStartDate, localEndDate);
  }

  public static long getDiffBetweenDatesInSeconds(final Date startDate, final Date endDate) {
    final LocalDateTime localStartDate = startDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    final LocalDateTime localEndDate = endDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    return ChronoUnit.SECONDS.between(localStartDate, localEndDate);
  }

  public static long getDiffBetweenDatesInDays(final Date startDate, final Date endDate) {
    final LocalDateTime localStartDate = startDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    final LocalDateTime localEndDate = endDate.toInstant().atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO)).toLocalDateTime();
    return ChronoUnit.DAYS.between(localStartDate, localEndDate);
  }

  public static Date converteStringDate(final String dateString) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    formatter.setTimeZone(TimeZone.getTimeZone(TZ_AMERICA_SAO_PAULO));
    return formatter.parse(dateString);
  }

  public static Date addDaysToDate(final Date startDate, final int days) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DAY_OF_MONTH, days);
    return c.getTime();
  }

//  public static String createThumbnail(String conteudoFoto) {
//    try {
//      ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
//      write(resize(read(new ByteArrayInputStream(decodeBase64(conteudoFoto))), Method.AUTOMATIC, Mode.AUTOMATIC, 150, OP_ANTIALIAS), "jpg", thumbOutput);
//      return new String(encodeBase64(thumbOutput.toByteArray()));
//    } catch (IOException e) {
//      LOGGER.warn("Couldn't create a thumbnail", e);
//    }
//    return conteudoFoto;
//  }

  public static String[] propertyNames(Object source, String... ignoreProperties) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = src.getPropertyDescriptors();
    Set<String> emptyNames = newHashSet(ignoreProperties);
    for (PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      if (!allNotNull(srcValue)) {
        emptyNames.add(pd.getName());
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  public static String getProperty(final Object object, final String name) {
    try {
      return PropertyUtils.getProperty(object, name).toString();
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean isCNPJ(String CNPJ) {
    if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") || CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") || CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") || CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") || CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999") || (CNPJ.length() != 14)) {
      return (false);
    }

    char dig13, dig14;
    int sm, i, r, num, peso;

    try {
      sm = 0;
      peso = 2;
      for (i = 11; i >= 0; i--) {
        num = (int) (CNPJ.charAt(i) - 48);
        sm = sm + (num * peso);
        peso = peso + 1;
        if (peso == 10) {
          peso = 2;
        }
      }

      r = sm % 11;
      if ((r == 0) || (r == 1)) {
        dig13 = '0';
      } else {
        dig13 = (char) ((11 - r) + 48);
      }

      sm = 0;
      peso = 2;
      for (i = 12; i >= 0; i--) {
        num = (int) (CNPJ.charAt(i) - 48);
        sm = sm + (num * peso);
        peso = peso + 1;
        if (peso == 10) {
          peso = 2;
        }
      }
      r = sm % 11;
      if ((r == 0) || (r == 1)) {
        dig14 = '0';
      } else {
        dig14 = (char) ((11 - r) + 48);
      }

      if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13))) {
        return (true);
      } else {
        return (false);
      }

    } catch (InputMismatchException erro) {
      return (false);
    }
  }

  public static boolean isSEXO(String SEXO) {
    if (SEXO.equals("MASCULINO") || SEXO.equals("FEMININO")) {
      return true;
    }
    return false;
  }

  public static boolean isCPF(String CPF) {
    if (CPF.equals("00000000000") || CPF.equals("11111111111") || CPF.equals("22222222222") || CPF.equals("33333333333") || CPF.equals("44444444444") || CPF.equals("55555555555") || CPF.equals("66666666666") || CPF.equals("77777777777") || CPF.equals("88888888888") || CPF.equals("99999999999") || (CPF.length() != 11)) {
      return false;
    }

    char dig10, dig11;
    int sm, i, r, num, peso;

    try {
      sm = 0;
      peso = 10;
      for (i = 0; i < 9; i++) {
        num = (int) (CPF.charAt(i) - 48);
        sm = sm + (num * peso);
        peso = peso - 1;
      }

      r = 11 - (sm % 11);
      if ((r == 10) || (r == 11)) {
        dig10 = '0';
      } else {
        dig10 = (char) (r + 48);
      }

      sm = 0;
      peso = 11;
      for (i = 0; i < 10; i++) {
        num = (int) (CPF.charAt(i) - 48);
        sm = sm + (num * peso);
        peso = peso - 1;
      }
      r = 11 - (sm % 11);
      if ((r == 10) || (r == 11)) {
        dig11 = '0';
      } else {
        dig11 = (char) (r + 48);
      }
      if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) {
        return true;
      } else {
        return false;
      }
    } catch (InputMismatchException erro) {
      return (false);
    }
  }

  public static String printCPF(String CPF) {
    return (CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." + CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
  }

  public static String printCNPJ(String CNPJ) {
    return (CNPJ.substring(0, 2) + "." + CNPJ.substring(2, 5) + "." + CNPJ.substring(5, 8) + "/" + CNPJ.substring(8, 12) + "-" + CNPJ.substring(12, 14));
  }

  public static String identificaException(Exception exception, Action action) {
    if (exception.getCause() instanceof ConstraintViolationException) {
      ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception.getCause();
      switch (constraintViolationException.getConstraintName()) {
        default:
          return exception.getMessage();
      }
    } else {
      return exception.getMessage();
    }
  }

  private Utils() {
    super();
  }

}
