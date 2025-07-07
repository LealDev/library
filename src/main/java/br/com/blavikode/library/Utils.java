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
        case WOS_AGENDAMENTO_ID_TIPO_ASO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_AGENDAMENTO_ID_TIPO_ASO_FK;
          } else {
            return EXCECAO_DELETE_WOS_AGENDAMENTO_ID_TIPO_ASO_FK;
          }
        case WOS_ALARME_VELOCIDADE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ALARME_VELOCIDADE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ALARME_VELOCIDADE_ID_FUNCIONARIO_FK;
          }
        case WOS_ALARME_VELOCIDADE_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ALARME_VELOCIDADE_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ALARME_VELOCIDADE_ID_JORNADA_FK;
          }
        case WOS_ALARME_VELOCIDADE_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ALARME_VELOCIDADE_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ALARME_VELOCIDADE_ID_SEGMENTO_FK;
          }
        case WOS_AREA_ATUACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ASSOCIADO_CPF_UC:
          return EXCECAO_WOS_ASSOCIADO_CPF_UC;
        case WOS_ATIVIDADE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_AVISO_VELOCIDADE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_AVISO_VELOCIDADE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_AVISO_VELOCIDADE_ID_FUNCIONARIO_FK;
          }
        case WOS_AVISO_VELOCIDADE_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_AVISO_VELOCIDADE_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_AVISO_VELOCIDADE_ID_JORNADA_FK;
          }
        case WOS_AVISO_VELOCIDADE_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_AVISO_VELOCIDADE_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_AVISO_VELOCIDADE_ID_SEGMENTO_FK;
          }
        case WOS_BAIRRO_ID_CIDADE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_BAIRRO_ID_CIDADE_FK;
          } else {
            return EXCECAO_DELETE_WOS_BAIRRO_ID_CIDADE_FK;
          }
        case WOS_CATEGORIA_CHECKLIST_JORNADA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_COMBUSTIVEL_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOGRADOURO_ID_BAIRRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_LOGRADOURO_ID_BAIRRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_LOGRADOURO_ID_BAIRRO_FK;
          }
        case WOS_LOGRADOURO_ID_TIPO_LOGRADOURO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_LOGRADOURO_ID_TIPO_LOGRADOURO_FK;
          } else {
            return EXCECAO_DELETE_WOS_LOGRADOURO_ID_TIPO_LOGRADOURO_FK;
          }
        case WOS_BAIRRO_NOME_ID_CIDADE_UC:
          return EXCECAO_WOS_BAIRRO_NOME_CIDADE_UC;
        case WOS_BENEFICIO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CAMINHAO_ID_MATERIAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_MATERIAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_ID_MATERIAL_FK;
          }
        case WOS_CAMINHAO_ID_MODELO_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_MODELO_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_ID_MODELO_VEICULO_FK;
          }
        case WOS_CAMINHAO_ID_TIPO_CARGA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_TIPO_CARGA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_ID_TIPO_CARGA_FK;
          }
        case WOS_CAMINHAO_ID_TIPO_CARROCERIA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_TIPO_CARROCERIA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_ID_TIPO_CARROCERIA_FK;
          }
        case WOS_CAMINHAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CAMINHAO_TIPO_CONDENSADO_ID_CAMINHAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_TIPO_CONDENSADO_CAMINHAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_TIPO_CONDENSADO_ID_CAMINHAO_FK;
          }
        case WOS_CAMINHAO_TIPO_CONDENSADO_ID_TIPO_CONDENSADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAMINHAO_ID_TIPO_CONDENSADO_TIPO_CONDENSADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAMINHAO_TIPO_CONDENSADO_ID_TIPO_CONDENSADO_FK;
          }
        case WOS_CARGA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CATEGORIA_ITEM_PREPARACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CATEGORIA_PLANO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CATEGORIA_VEICULO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_CATEGORIA_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_CATEGORIA_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_CATEGORIA_VEICULO_FK;
          }
        case WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK;
          } else {
            return EXCECAO_DELETE_WOS_CATEGORIA_VEICULO_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK;
          }
        case WOS_CATEGORIA_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_CATEGORIA_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CATEGORIA_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_CATEGORIA_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CATEGORIA_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_CATEGORIA_VEICULO_FK;
          }
        case WOS_CAVALO_MECANICO_ID_MODELO_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CAVALO_MECANICO_ID_MODELO_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CAVALO_MECANICO_ID_MODELO_VEICULO_FK;
          }
        case WOS_CAVALO_MECANICO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CHECKLIST_ATUACAO_ID_ITEM_CHECKLIST_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CHECKLIST_ATUACAO_ID_ITEM_CHECKLIST_FK;
          } else {
            return EXCECAO_DELETE_WOS_CHECKLIST_ATUACAO_ID_ITEM_CHECKLIST_FK;
          }
        case WOS_CHECKLIST_ATUACAO_ID_TIPO_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CHECKLIST_ATUACAO_ID_TIPO_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CHECKLIST_ATUACAO_ID_TIPO_ATUACAO_FK;
          }
        case WOS_CHECKLIST_PREPARACAO_ID_ITEM_PREPARACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CHECKLIST_PREPARACAO_ID_ITEM_PREPARACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CHECKLIST_PREPARACAO_ID_ITEM_PREPARACAO_FK;
          }
        case WOS_CHECKLIST_PREPARACAO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CHECKLIST_PREPARACAO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CHECKLIST_PREPARACAO_ID_TIPO_SERVICO_FK;
          }
        case WOS_CHECKLIST_PREPARACAO_ID_ITEM_PREPARACAO_ID_TIPO_SERVIC_UC:
          return EXCECAO_WOS_CHECKLIST_PREPARACAO_ID_ITEM_PREPARACAO_ID_TIPO_SERVIC_UC;
        case WOS_CIDADE_ID_ESTADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CIDADE_ID_ESTADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CIDADE_ID_ESTADO_FK;
          }
        case WOS_CIDADE_NOME_ID_ESTADO_UC:
          return EXCECAO_WOS_CIDADE_NOME_ESTADO_UC;
        case WOS_CLASSE_RETORNO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CLIENTE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CLIENTE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CLIENTE_ID_FUNCIONARIO_FK;
          }
        case WOS_CLIENTE_CPF_UC:
          return EXCECAO_WOS_CLIENTE_CPF_UC;
        case WOS_CLIENTE_RG_UC:
          return EXCECAO_WOS_CLIENTE_RG_UC;
        case WOS_COBERTURA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CODIGO_DESCONTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CODIGO_INFORMACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CODIGO_LEITURA_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CODIGO_LEITURA_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CODIGO_LEITURA_ID_TIPO_SERVICO_FK;
          }
        case WOS_CODIGO_LEITURA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CODIGO_NEGOCIACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_COLETA_ID_CAMINHAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_ID_CAMINHAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_ID_CAMINHAO_FK;
          }
        case WOS_COLETA_ID_DESCARTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_ID_DESCARTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_ID_DESCARTE_FK;
          }
        case WOS_COLETA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_ID_FUNCIONARIO_FK;
          }
        case WOS_COLETA_ID_ORDEM_SERVICO_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_ID_ORDEM_SERVICO_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_ID_ORDEM_SERVICO_ROTA_FK;
          }
        case WOS_COLETA_ID_RESERVATORIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_ID_RESERVATORIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_ID_RESERVATORIO_FK;
          }
        case WOS_COLETA_ID_ORDEM_SERVICO_ROTA_ID_RESERVATORIO_UC:
          return EXCECAO_WOS_COLETA_ID_ORDEM_SERVICO_ROTA_ID_RESERVATORIO_UC;
        case WOS_COLETA_FOTO_ID_COLETA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_FOTO_ID_COLETA_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_FOTO_ID_COLETA_FK;
          }
        case WOS_COLUNA_CODIGO_TIPO_UC:
          return EXCECAO_WOS_COLUNA_CODIGO_TIPO_UC;
        case WOS_COLETA_FOTO_ID_COLETA_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_COLETA_FOTO_ID_COLETA_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_COLETA_FOTO_ID_COLETA_FOTO_BLOB_FK;
          }
        case WOS_COMPONENTE_NOME_UC:
          return EXCECAO_WOS_COLETA_ID_ORDEM_SERVICO_ROTA_ID_RESERVATORIO_UC;
        case WOS_CON_PROJETO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CON_PROJETO_FUNCIONARIO_ID_CON_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CON_PROJETO_FUNCIONARIO_ID_CON_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CON_PROJETO_FUNCIONARIO_ID_CON_PROJETO_FK;
          }
        case WOS_CON_PROJETO_FUNCIONARIO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CON_PROJETO_FUNCIONARIO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CON_PROJETO_FUNCIONARIO_ID_FUNCIONARIO_FK;
          }
        case WOS_CONDICAO_HIDROMETRO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CONDICAO_INSPECAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CONSUMO_ID_AREA_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONSUMO_ID_AREA_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONSUMO_ID_AREA_ATUACAO_FK;
          }
        case WOS_CONSUMO_ID_CODIGO_LEITURA_ANTERIOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONSUMO_ID_CODIGO_LEITURA_ANTERIOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONSUMO_ID_CODIGO_LEITURA_ANTERIOR_FK;
          }
        case WOS_CONSUMO_ID_CODIGO_LEITURA_ATUAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONSUMO_ID_CODIGO_LEITURA_ATUAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONSUMO_ID_CODIGO_LEITURA_ATUAL_FK;
          }
        case WOS_CONSUMO_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONSUMO_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONSUMO_ID_MATRICULA_FK;
          }
        case WOS_CONSUMO_ID_ORDEM_SERVICO_SANEAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONSUMO_ID_ORDEM_SERVICO_SANEAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONSUMO_ID_ORDEM_SERVICO_SANEAMENTO_FK;
          }
        case WOS_CONSUMO_DATA_LEITURA_ATUAL_ID_MATRICULA_UC:
          return EXCECAO_WOS_CONSUMO_DATA_LEITURA_ATUAL_MATRICULA_UC;
        case WOS_CONTRATO_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ID_EMPRESA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ID_EMPRESA_FK;
          }
        case WOS_CONTRATO_ID_EMPRESA_CONTRATADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ID_EMPRESA_CONTRATADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ID_EMPRESA_CONTRATADA_FK;
          }
        case WOS_CONTRATO_ASSOCIACAO_ID_ASSOCIADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ASSOCIACAO_ID_ASSOCIADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ASSOCIACAO_ID_ASSOCIADO_FK;
          }
        case WOS_CONTRATO_ASSOCIACAO_ID_PLANO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ASSOCIACAO_ID_PLANO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ASSOCIACAO_ID_PLANO_FK;
          }
        case WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_COBERTURA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_COBERTURA_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_COBERTURA_FK;
          }
        case WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_CONTRATO_ASSOCIACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_CONTRATO_ASSOCIACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_CONTRATO_ASSOCIACAO_COBERTURAO_ID_CONTRATO_ASSOCIACAO_FK;
          }
        case WOS_CRITICIDADE_INSPECAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CRITICIDADE_LOCAL_INSTALACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CRITICIDADE_TAREFA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_DADO_JORNADA_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DADO_JORNADA_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_DADO_JORNADA_ID_PONTO_FK;
          }
        case WOS_DESCARTE_ID_CAMINHAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DESCARTE_ID_CAMINHAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_DESCARTE_ID_CAMINHAO_FK;
          }
        case WOS_DESCARTE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DESCARTE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_DESCARTE_ID_FUNCIONARIO_FK;
          }
        case WOS_DESCARTE_ID_LOCAL_DESCARTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DESCARTE_ID_LOCAL_DESCARTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_DESCARTE_ID_LOCAL_DESCARTE_FK;
          }
        case WOS_DESCARTE_ID_TIPO_CONDENSADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DESCARTE_ID_TIPO_CONDENSADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_DESCARTE_ID_TIPO_CONDENSADO_FK;
          }
        case WOS_DIMENSIONAL_INSPECAO_ID_ITEM_RESULTADO_INSPECAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_DIMENSIONAL_INSPECAO_ID_ITEM_RESULTADO_INSPECAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_DIMENSIONAL_INSPECAO_ID_ITEM_RESULTADO_INSPECAO_FK;
          }
        case WOS_EMPRESA_NOME_TIPO_EMPRESA_UC:
          return EXCECAO_DUPLICATED_KEY_NOME_TIPO_EMPRESA;
        case WOS_EMPREGADO_CPF_UC:
          return EXCECAO_WOS_EMPREGADO_CPF_UC;
        case WOS_EMPREGADO_EMPRESA_ID_EMPRESA_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_EMPRESA_ID_EMPRESA_FK;
        case WOS_EMPREGADO_EXAME_ID_EXAME_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_EXAME_ID_EXAME_FK;
        case WOS_EXAME_TIPO_ASO_ID_EXAME_FK:
          return EXCECAO_DELETE_EXAME_TIPO_ASO_ID_EXAME_FK;
        case WOS_RISCO_EXAME_ID_EXAME_FK:
          return EXCECAO_DELETE_RISCO_EXAME_ID_RISCO_FK;
        case WOS_ESPECIALIDADE_EXAME_ID_EXAME_FK:
          return EXCECAO_DELETE_ESPECIALIDADE_EXAME_ID_ESPECIALIDADE_FK;
        case WOS_EMPREGADO_TREINAMENTO_ID_TREINAMENTO_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_TREINAMENTO_ID_TREINAMENTO_FK;
        case WOS_EMPREGADO_EPI_ID_EPI_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_EPI_ID_EPI_FK;
        case WOS_EMPREGADO_TIPO_ASO_ID_TIPO_ASO_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_TIPO_ASO_ID_TIPO_ASO_FK;
        case WOS_EXAME_ID_ESPECIALIDADE_FK:
          return EXCECAO_DELETE_WOS_EXAME_ID_ESPECIALIDADE_FK;
        case WOS_EQUIPAMENTO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_EQUIPAMENTO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_EQUIPAMENTO_ID_FUNCIONARIO_FK;
          }
        case WOS_EQUIPAMENTO_SANEAMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_EQUIPE_ID_LIDER_EQUIPE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_EQUIPE_ID_LIDER_EQUIPE_FK;
          } else {
            return EXCECAO_DELETE_WOS_EQUIPE_ID_LIDER_EQUIPE_FK;
          }
        case WOS_EQUIPE_FUNCIONARIOS_ID_EQUIPE_ID_FUNCIONARIO_UC:
          return EXCECAO_WOS_EQUIPE_FUNCIONARIOS_EQUIPE_FUNCIONARIO_UC;
        case WOS_ESTADO_NOME_UC:
          return EXCECAO_WOS_ESTADO_NOME_UC;
        case WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK;
          }
        case WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_EQUIPAMENTO_SANEAMENTO_ID_PROJETO_FK;
          }
        case WOS_ESTOQUE_ID_MATERIAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_ID_MATERIAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_ID_MATERIAL_FK;
          }
        case WOS_ESTOQUE_ID_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_ID_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_ID_PROJETO_FK;
          }
        case WOS_ESTOQUE_SERVICO_ID_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_SERVICO_ID_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_SERVICO_ID_PROJETO_FK;
          }
        case WOS_ESTOQUE_SERVICO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ESTOQUE_SERVICO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ESTOQUE_SERVICO_ID_TIPO_SERVICO_FK;
          }
        case WOS_EXAME_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_RISCO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ESPECIALIDADE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_FABRICANTE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_EVENTO_REGISTRO_JORNADA_ID_REGISTRO_JORNADA_TIPO_EVENTO_DAT:
          return EXCECAO_WOS_EVENTO_REGISTRO_JORNADA_ID_REGISTRO_JORNADA_TIPO_EVENTO_DAT;
        case WOS_FILTRO_CODIGO_TIPO_UC:
          return EXCECAO_WOS_FILTRO_CODIGO_TIPO_UC;
        case WOS_FIM_JORNADA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FIM_JORNADA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FIM_JORNADA_ID_FUNCIONARIO_FK;
          }
        case WOS_FIM_JORNADA_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FIM_JORNADA_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_FIM_JORNADA_ID_JORNADA_FK;
          }
        case WOS_FOTO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FOTO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FOTO_ID_CONTRATO_FK;
          }
        case WOS_FOTO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FOTO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FOTO_ID_FUNCIONARIO_FK;
          }
        case WOS_FUNCAO_EMPRESA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_FUNCAO_EMPRESA_EXAME_ID_EXAME_FK:
          return EXCECAO_DELETE_WOS_FUNCAO_EMPRESA_EXAME_ID_EXAME_FK;
        case WOS_EMPRESA_ID_FUNCAO_EMPRESA_FK:
          return EXCECAO_DELETE_WOS_EMPRESA_ID_FUNCAO_EMPRESA_FK;
        case WOS_EMPREGADO_EMPRESA_ID_FUNCAO_EMPRESA_FK:
          return EXCECAO_DELETE_WOS_EMPREGADO_EMPRESA_ID_FUNCAO_EMPRESA_FK;
        case WOS_FUNCAO_EMPRESA_ID_EMPRESA_FK:
          return EXCECAO_DELETE_WOS_FUNCAO_EMPRESA_ID_EMPRESA_FK;
        case WOS_FUNCAO_EMPRESA_TREINAMENTO_ID_TREINAMENTO_FK:
          return EXCECAO_DELETE_WOS_FUNCAO_EMPRESA_TREINAMENTO_ID_TREINAMENTO_FK;
        case WOS_FUNCAO_EMPRESA_EPI_ID_EPI_FK:
          return EXCECAO_DELETE_WOS_FUNCAO_EMPRESA_EPI_ID_EPI_FK;
        case WOS_FUNCAO_NOME_UC:
          return EXCECAO_WOS_FUNCAO_NOME_UC;
        case WOS_FUNCAO_FUNCAO_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCAO_FUNCAO_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCAO_FUNCAO_PERMISSAO_FK;
          }
        case WOS_FUNCAO_PERMISSAO_FUNCAO_ID_FUNCAO_ID_PERMISSAO_UC:
          return EXCECAO_WOS_FUNCAO_PERMISSAO_FUNCAO_ID_FUNCAO_ID_PERMISSAO_UC;
        case WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK;
          }
        case WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_FUNCAO_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_FUNCAO_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCAO_PERMISSAO_COMPONENTE_ID_FUNCAO_PERMISSAO_FK;
          }
        case WOS_FUNCAO_USUARIO_FUNCAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCAO_USUARIO_FUNCAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCAO_USUARIO_FUNCAO_FK;
          }
        case WOS_FUNCIONARIO_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCIONARIO_ID_EMPRESA_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCIONARIO_ID_EMPRESA_FK;
          }
        case WOS_FUNCIONARIO_LOGIN_UC:
          return EXCECAO_WOS_FUNCIONARIO_LOGIN_UC;
        case WOS_FUNCIONARIO_CONTRATO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCIONARIO_CONTRATO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCIONARIO_CONTRATO_ID_CONTRATO_FK;
          }
        case WOS_FUNCIONARIO_CONTRATO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCIONARIO_CONTRATO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCIONARIO_CONTRATO_ID_FUNCIONARIO_FK;
          }
        case WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_FUNCIONARIO_FK;
          }
        case WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_PARAMETRO_SISTEMA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_PARAMETRO_SISTEMA_FK;
          } else {
            return EXCECAO_DELETE_WOS_FUNCIONARIO_PARAMETRO_SISTEMA_ID_PARAMETRO_SISTEMA_FK;
          }
        case WOS_GPS_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_GPS_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_GPS_ID_FUNCIONARIO_FK;
          }
        case WOS_GPS_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_GPS_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_GPS_ID_JORNADA_FK;
          }
        case WOS_IMOVEL_TIPO_IMOVEL_ID_IMOVEL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_IMOVEL_TIPO_IMOVEL_ID_IMOVEL_FK;
          } else {
            return EXCECAO_DELETE_WOS_IMOVEL_TIPO_IMOVEL_ID_IMOVEL_FK;
          }
        case WOS_IMOVEL_TIPO_IMOVEL_ID_TIPO_IMOVEL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_IMOVEL_TIPO_IMOVEL_ID_TIPO_IMOVEL_FK;
          } else {
            return EXCECAO_DELETE_WOS_IMOVEL_TIPO_IMOVEL_ID_TIPO_IMOVEL_FK;
          }
        case WOS_INDICADOR_NECESSIDADE_IMPEDIMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_INICIO_JORNADA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_INICIO_JORNADA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_INICIO_JORNADA_ID_FUNCIONARIO_FK;
          }
        case WOS_INICIO_JORNADA_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_INICIO_JORNADA_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_INICIO_JORNADA_ID_JORNADA_FK;
          }
        case WOS_ITEM_CHECKLIST_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ITEM_CHECKLIST_JORNADA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ITEM_MANUTENCAO_PREVENTIVA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ITEM_PREPARACAO_ID_CATEGORIA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ITEM_PREPARACAO_ID_CATEGORIA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_PREPARACAO_ID_CATEGORIA_FK;
          }
        case WOS_ITEM_RESULTADO_INSPECAO_ID_CONDICAO_INSPECAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ITEM_RESULTADO_INSPECAO_ID_CONDICAO_INSPECAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_RESULTADO_INSPECAO_ID_CONDICAO_INSPECAO_FK;
          }
        case WOS_ITEM_RESULTADO_INSPECAO_ID_LAUDO_INSPECAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ITEM_RESULTADO_INSPECAO_ID_LAUDO_INSPECAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_RESULTADO_INSPECAO_ID_LAUDO_INSPECAO_FK;
          }
        case WOS_ITEM_RESULTADO_INSPECAO_ID_ORDEM_SERVICO_PERITAGEM_FK:
          if (action.equals(Action.SAVE)) {
            return ID_;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_RESULTADO_INSPECAO_ID_ORDEM_SERVICO_PERITAGEM_FK;
          }
        case WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FK;
          }
        case WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_ITEM_RESULTADO_INSPECAO_FOTO_ID_ITEM_RESULTADO_INSPECAO_FOTO_BLOB_FK;
          }
        case WOS_JORNADA_ID_CAMINHAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_CAMINHAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_CAMINHAO_FK;
          }
        case WOS_JORNADA_ID_CAVALO_MECANICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_CAVALO_MECANICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_CAVALO_MECANICO_FK;
          }
        case WOS_JORNADA_ID_DESTINATARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_DESTINATARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_DESTINATARIO_FK;
          }
        case WOS_JORNADA_ID_MOTIVO_CANCELAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_MOTIVO_CANCELAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_MOTIVO_CANCELAMENTO_FK;
          }
        case WOS_JORNADA_ID_PLANEJADOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_PLANEJADOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_PLANEJADOR_FK;
          }
        case WOS_JORNADA_ID_REMETENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_REMETENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_REMETENTE_FK;
          }
        case WOS_JORNADA_ID_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_ROTA_FK;
          }
        case WOS_JORNADA_ID_SEMIRREBOQUE_1_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_SEMIRREBOQUE_1_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_SEMIRREBOQUE_1_FK;
          }
        case WOS_JORNADA_ID_SEMIRREBOQUE_2_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_SEMIRREBOQUE_2_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_SEMIRREBOQUE_2_FK;
          }
        case WOS_JORNADA_ID_SEMIRREBOQUE_3_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_ID_SEMIRREBOQUE_3_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_ID_SEMIRREBOQUE_3_FK;
          }
        case WOS_JORNADA_CARGA_ID_CARGA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_CARGA_ID_CARGA_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_CARGA_ID_CARGA_FK;
          }
        case WOS_JORNADA_CARGAS_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_CARGAS_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_CARGAS_ID_JORNADA_FK;
          }
        case WOS_JORNADA_PONTO_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_PONTO_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_PONTO_ID_JORNADA_FK;
          }
        case WOS_JORNADA_PONTO_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_PONTO_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_PONTO_ID_PONTO_FK;
          }
        case WOS_JORNADA_PONTO_MENSAGEM_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_PONTO_MENSAGEM_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_PONTO_MENSAGEM_ID_JORNADA_FK;
          }
        case WOS_JORNADA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_JORNADA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK;
          } else {
            return EXCECAO_DELETE_WOS_JORNADA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK;
          }
        case WOS_LABEL_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LAUDO_INSPECAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOCAL_CONJUNTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FK;
          }
        case WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_LOCAL_CONJUNTO_FOTO_ID_LOCAL_CONJUNTO_FOTO_BLOB_FK;
          }
        case WOS_LOCAL_CONJUNTO_FOTO_ID_ORDEM_SERVICO_PERITAGEM_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_LOCAL_CONJUNTO_FOTO_ID_ORDEM_SERVICO_PERITAGEM_FK;
          } else {
            return EXCECAO_DELETE_WOS_LOCAL_CONJUNTO_FOTO_ID_ORDEM_SERVICO_PERITAGEM_FK;
          }
        case WOS_LOCAL_DESCARTE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOCAL_HIDROMETRO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOCAL_INSTALACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_LOCAL_SERVICO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MATERIAL_CODIGO_EMPRESA_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO_EMPRESA;
        case WOS_MATERIAL_CODIGO_CLIENTE_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO_CLIENTE;
        case WOS_MATERIAL_SERVICO_ID_MATERIAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATERIAL_SERVICO_ID_MATERIAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATERIAL_SERVICO_ID_MATERIAL_FK;
          }
        case WOS_MATERIAL_SERVICO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATERIAL_SERVICO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATERIAL_SERVICO_ID_TIPO_SERVICO_FK;
          }
        case WOS_MATERIAL_SERVICO_ID_MATERIAL_ID_SERVICO_UC:
          return EXCECAO_WOS_MATERIAL_SERVICO_ID_MATERIAL_ID_SERVICO_UC;
        case WOS_MATRICULA_ID_AREA_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_AREA_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_AREA_ATUACAO_FK;
          }
        case WOS_MATRICULA_ID_BENEFICIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_BENEFICIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_BENEFICIO_FK;
          }
        case WOS_MATRICULA_ID_CONDICAO_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_CONDICAO_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_CONDICAO_HIDROMETRO_FK;
          }
        case WOS_MATRICULA_ID_IMOVEL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_IMOVEL_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_IMOVEL_FK;
          }
        case WOS_MATRICULA_ID_PADRAO_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_PADRAO_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_PADRAO_HIDROMETRO_FK;
          }
        case WOS_MATRICULA_ID_STATUS_LIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_STATUS_LIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_STATUS_LIGACAO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_ABASTECIMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_ABASTECIMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_ABASTECIMENTO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_ENTIDADE_ATENDENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_ENTIDADE_ATENDENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_ENTIDADE_ATENDENTE_FK;
          }
        case WOS_MATRICULA_ID_TIPO_ENTIDADE_CONTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_ENTIDADE_CONTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_ENTIDADE_CONTA_FK;
          }
        case WOS_MATRICULA_ID_TIPO_ENTIDADE_MORADOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_ENTIDADE_MORADOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_ENTIDADE_MORADOR_FK;
          }
        case WOS_MATRICULA_ID_TIPO_ENTIDADE_SIGNATARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_ENTIDADE_SIGNATARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_ENTIDADE_SIGNATARIO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_LIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_LIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_LIGACAO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_LOGRADOURO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_LOGRADOURO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_LOGRADOURO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_LEITO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_LEITO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_LEITO_FK;
          }
        case WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_PASSEIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_PASSEIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_ID_TIPO_PAVIMENTACAO_PASSEIO_FK;
          }
        case WOS_MATRICULA_MATRICULA_UC:
          return EXCECAO_WOS_MATRICULA_MATRICULA_UC;
        case WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_DOCUMENTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_DELETE_WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_DOCUMENTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_DOCUMENTO_BLOB_FK;
          }
        case WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_DELETE_WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_DOCUMENTO_ID_MATRICULA_FK;
          }
        case WOS_MATRICULA_FOTO_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_FOTO_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_FOTO_ID_MATRICULA_FK;
          }
        case WOS_MATRICULA_FOTO_ID_MATRICULA_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MATRICULA_FOTO_ID_MATRICULA_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_MATRICULA_FOTO_ID_MATRICULA_FOTO_BLOB_FK;
          }
        case WOS_MENSAGEM_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MENSAGEM_ID_EMPRESA_FK;
          } else {
            return EXCECAO_DELETE_WOS_MENSAGEM_ID_EMPRESA_FK;
          }
        case WOS_MENSAGEM_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MENSAGEM_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_MENSAGEM_ID_FUNCIONARIO_FK;
          }
        case WOS_MODELO_FUNCAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MODELO_FUNCAO_EXAME_ID_EXAME_FK:
          return EXCECAO_DELETE_WOS_MODELO_FUNCAO_EXAME_ID_EXAME_FK;
        case WOS_MODELO_FUNCAO_TREINAMENTO_ID_TREINAMENTO_FK:
          return EXCECAO_DELETE_WOS_MODELO_FUNCAO_TREINAMENTO_ID_TREINAMENTO_FK;
        case WOS_MODELO_FUNCAO_EPI_ID_EPI_FK:
          return EXCECAO_DELETE_WOS_MODELO_FUNCAO_EPI_ID_EPI_FK;
        case WOS_MODELO_SEMIRREBOQUE_ID_FABRICANTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MODELO_SEMIRREBOQUE_ID_FABRICANTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_MODELO_SEMIRREBOQUE_ID_FABRICANTE_FK;
          }
        case WOS_MODELO_SEMIRREBOQUE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MODELO_VEICULO_ID_FABRICANTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_MODELO_VEICULO_ID_FABRICANTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_MODELO_VEICULO_ID_FABRICANTE_FK;
          }
        case WOS_MODELO_VEICULO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MOTIVO_CANCELAMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MOTIVO_IMPRODUTIVIDADE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MOTIVO_REJEICAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_MOTIVO_RELIGACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ONU_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ORDEM_SERVICO_ID_LIDER_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_LIDER_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_LIDER_FK;
          }
        case WOS_ORDEM_SERVICO_ID_MOTIVO_REJEICAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_MOTIVO_REJEICAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_MOTIVO_REJEICAO_FK;
          }
        case WOS_ORDEM_SERVICO_ID_PROGRAMADOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_PROGRAMADOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_PROGRAMADOR_FK;
          }
        case WOS_ORDEM_SERVICO_ID_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_PROJETO_FK;
          }
        case WOS_ORDEM_SERVICO_ID_STATUS_ANTERIOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_STATUS_ANTERIOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_STATUS_ANTERIOR_FK;
          }
        case WOS_ORDEM_SERVICO_ID_STATUS_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_STATUS_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_STATUS_FK;
          }
        case WOS_ORDEM_SERVICO_ID_SUPERVISOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_SUPERVISOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_SUPERVISOR_FK;
          }
        case WOS_ORDEM_SERVICO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ID_TIPO_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_NUMERO_UC:
          return EXCECAO_WOS_ORDEM_SERVICO_NUMERO_UC;
        case WOS_ORDEM_SERVICO_AGENDA_ID_CON_PROJETO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_AGENDA_ID_CON_PROJETO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_AGENDA_ID_CON_PROJETO_FK;
          }
        case WOS_ORDEM_SERVICO_AGENDA_ID_MOTIVO_CANCELAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_AGENDA_ID_MOTIVO_CANCELAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_AGENDA_ID_MOTIVO_CANCELAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_DOCUMENTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_DOCUMENTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_DOCUMENTO_BLOB_FK;
          }
        case WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_DOCUMENTO_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_FOTO_ID_ORDEM_SERVICO_FOTO_BLOB_FK;
          }
        case WOS_ORDEM_SERVICO_FUNCIONARIO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_FUNCIONARIO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_FUNCIONARIO_ID_FUNCIONARIO_FK;
          }
        case WOS_ORDEM_SERVICO_FUNCIONARIO_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_FUNCIONARIO_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_FUNCIONARIO_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_IMPRODUTIVIDA_ID_MOTIVO_IMPRODUTIVIDADE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_IMPRODUTIVIDA_ID_MOTIVO_IMPRODUTIVIDADE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_IMPRODUTIVIDA_ID_MOTIVO_IMPRODUTIVIDADE_FK;
          }
        case WOS_ORDEM_SERVICO_IMPRODUTIVIDADE_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_IMPRODUTIVIDADE_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_IMPRODUTIVIDADE_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_MANUTENCAO_ID_FASE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_MANUTENCAO_ID_FASE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_MANUTENCAO_ID_FASE_FK;
          }
        case WOS_ORDEM_SERVICO_MANUTENCAO_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_MANUTENCAO_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_MANUTENCAO_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_MATERIAL_APLICADO_MATERIAL_ID_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_MATERIAL_APLICADO_MATERIAL_ID_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_MATERIAL_APLICADO_MATERIAL_ID_FK;
          }
        case WOS_ORDEM_SERVICO_MATERIAL_APLICADO_ORDEM_SERVICO_ID_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_MATERIAL_APLICADO_ORDEM_SERVICO_ID_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_MATERIAL_APLICADO_ORDEM_SERVICO_ID_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_ID_AREA_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_PERITAGEM_ID_AREA_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_PERITAGEM_ID_AREA_ATUACAO_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_ID_CRITICIDADE_INSPECAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_PERITAGEM_ID_CRITICIDADE_INSPECAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_PERITAGEM_ID_CRITICIDADE_INSPECAO_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_ID_LOCAL_CONJUNTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_PERITAGEM_ID_LOCAL_CONJUNTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_PERITAGEM_ID_LOCAL_CONJUNTO_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_ID_MOTIVO_CANCELAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_PERITAGEM_ID_MOTIVO_CANCELAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_PERITAGEM_ID_MOTIVO_CANCELAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_ID_PLANEJADOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_PERITAGEM_ID_PLANEJADOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_PERITAGEM_ID_PLANEJADOR_FK;
          }
        case WOS_ORDEM_SERVICO_PERITAGEM_NUMERO_ORDEM_PECAS_MANUTENCAO_OPERACAO_UC:
          return EXCECAO_WOS_ORDEM_SERVICO_PERITAGEM_NUMERO_ORDEM_PECAS_MANUTENCAO_OPERACAO_UC;
        case WOS_ORDEM_SERVICO_ROTA_ID_CAMINHAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_ID_CAMINHAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_ID_CAMINHAO_FK;
          }
        case WOS_ORDEM_SERVICO_ROTA_ID_MOTIVO_CANCELAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_ID_MOTIVO_CANCELAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_ID_MOTIVO_CANCELAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_ROTA_ID_TIPO_CONDENSADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_ID_TIPO_CONDENSADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_ID_TIPO_CONDENSADO_FK;
          }
        case WOS_ORDEM_SERVICO_ROTA_ID_TURNO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_ID_TURNO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_ID_TURNO_FK;
          }
        case WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_ORDEM_SERVICO_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_ORDEM_SERVICO_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_ORDEM_SERVICO_ROTA_FK;
          }
        case WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_RESERVATORIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_RESERVATORIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_ROTA_RESERVATORIO_ID_RESERVATORIO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_AREA_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_AREA_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_AREA_ATUACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_INFORMACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_INFORMACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_INFORMACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_LEITURA_ATUAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_LEITURA_ATUAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CODIGO_LEITURA_ATUAL_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONDICAO_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONDICAO_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONDICAO_HIDROMETRO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_CONTRATO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_IMPEDIMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_IMPEDIMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_IMPEDIMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_INFORMATIVO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_INFORMATIVO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_INFORMATIVO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_HIDROMETRO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_LOCAL_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MATRICULA_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_CANCELAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_CANCELAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_CANCELAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_RELIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_RELIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_MOTIVO_RELIGACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_PADRAO_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_PADRAO_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_PADRAO_HIDROMETRO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_STATUS_LIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_STATUS_LIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_STATUS_LIGACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ABASTECIMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ABASTECIMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ABASTECIMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ATUACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_CORTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_CORTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_CORTE_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_ATENDENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_ATENDENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_ATENDENTE_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_MORADOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_MORADOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ENTIDADE_MORADOR_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ESCORAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ESCORAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_ESCORAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_HIDROMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_HIDROMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_HIDROMETRO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_IMOVEL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_IMOVEL_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_IMOVEL_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LACRE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LACRE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LACRE_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LIGACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LOGRADOURO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LOGRADOURO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_LOGRADOURO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_SERVICO_EXECUTADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_SERVICO_EXECUTADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_TIPO_SERVICO_EXECUTADO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_ID_ULTIMO_TIPO_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_ULTIMO_TIPO_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_ID_ULTIMO_TIPO_ATUACAO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_EQUIPAMENTO_SANEAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_ORDEM_SERVICO_SANEAMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_ORDEM_SERVICO_SANEAMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_SANEAMENTO_EQUIPAMENTO_SANEAMENTO_ID_ORDEM_SERVICO_SANEAMENTO_FK;
          }
        case WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_ORDEM_SERVICO_FK;
          }
        case WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_PAVIMENTACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_PAVIMENTACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_PAVIMENTACAO_FK;
          }
        case WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ORDEM_SERVICO_TIPO_SERVICO_COMPLEMENTAR_ID_TIPO_SERVICO_FK;
          }
        case WOS_PADRAO_HIDROMETRO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PARAMETRO_SISTEMA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PERFIL_RELATORIO_NOME_UC:
          return EXCECAO_DUPLICATED_KEY_NOME;
        case WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_FILTRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_FILTRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_FILTRO_FK;
          }
        case WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_PERFIL_RELATORIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_PERFIL_RELATORIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERFIL_RELATORIO_FILTRO_SELECIONADO_ID_PERFIL_RELATORIO_FK;
          }
        case WOS_PERMISSAO_COMPONENTE_ID_PERMISSAO_ID_COMPONENTE_UC:
          return EXCECAO_WOS_PERMISSAO_COMPONENTE_ID_PERMISSAO_ID_COMPONENTE_UC;
        case WOS_PERMISSAO_NOME_UC:
          return EXCECAO_WOS_PERMISSAO_NOME_UC;
        case WOS_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERMISSAO_COMPONENTE_ID_COMPONENTE_FK;
          }
        case WOS_PERMISSAO_COMPONENTE_ID_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERMISSAO_COMPONENTE_ID_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERMISSAO_COMPONENTE_ID_PERMISSAO_FK;
          }
        case WOS_PERMISSAO_FUNCAO_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERMISSAO_FUNCAO_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERMISSAO_FUNCAO_PERMISSAO_FK;
          }
        case WOS_PERMISSAO_USUARIO_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PERMISSAO_USUARIO_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PERMISSAO_USUARIO_PERMISSAO_FK;
          }
        case WOS_PLACA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PLACA_REGISTRADA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLACA_REGISTRADA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLACA_REGISTRADA_ID_FUNCIONARIO_FK;
          }
        case WOS_PLACA_REGISTRADA_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLACA_REGISTRADA_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLACA_REGISTRADA_ID_JORNADA_FK;
          }
        case WOS_PLACA_REGISTRADA_ID_PLACA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLACA_REGISTRADA_ID_PLACA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLACA_REGISTRADA_ID_PLACA_FK;
          }
        case WOS_PLANEJAMENTO_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANEJAMENTO_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANEJAMENTO_ID_MATRICULA_FK;
          }
        case WOS_PLANEJAMENTO_ID_STATUS_LIGACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANEJAMENTO_ID_STATUS_LIGACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANEJAMENTO_ID_STATUS_LIGACAO_FK;
          }
        case WOS_PLANEJAMENTO_ID_TIPO_LOGRADOURO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANEJAMENTO_ID_TIPO_LOGRADOURO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANEJAMENTO_ID_TIPO_LOGRADOURO_FK;
          }
        case WOS_PLANEJAMENTO_ID_MATRICULA_UC:
          return EXCECAO_WOS_PLANEJAMENTO_ID_MATRICULA_UC;
        case WOS_PLANO_ID_CATEGORIA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANO_ID_CATEGORIA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANO_ID_CATEGORIA_FK;
          }
        case WOS_PLANO_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANO_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANO_ID_SEGMENTO_FK;
          }
        case WOS_PLANO_COBERTURA_ID_COBERTURA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANO_COBERTURA_ID_COBERTURA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANO_COBERTURA_ID_COBERTURA_FK;
          }
        case WOS_PLANO_COBERTURA_ID_PLANO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANO_COBERTURA_ID_PLANO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANO_COBERTURA_ID_PLANO_FK;
          }
        case WOS_PLANO_FAIXA_ID_PLANO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PLANO_FAIXA_ID_PLANO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PLANO_FAIXA_ID_PLANO_FK;
          }
        case WOS_PONTO_ID_PLACA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_ID_PLACA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_ID_PLACA_FK;
          }
        case WOS_PONTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PONTO_APOIO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_APOIO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_APOIO_ID_FUNCIONARIO_FK;
          }
        case WOS_PONTO_APOIO_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_APOIO_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_APOIO_ID_JORNADA_FK;
          }
        case WOS_PONTO_APOIO_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_APOIO_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_APOIO_ID_PONTO_FK;
          }
        case WOS_PONTO_CONTROLE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_CONTROLE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_CONTROLE_ID_FUNCIONARIO_FK;
          }
        case WOS_PONTO_CONTROLE_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_CONTROLE_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_CONTROLE_ID_JORNADA_FK;
          }
        case WOS_PONTO_CONTROLE_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_CONTROLE_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_CONTROLE_ID_PONTO_FK;
          }
        case WOS_PONTO_MENSAGEM_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PONTO_PREVISTO_JORNADA_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_PREVISTO_JORNADA_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_PREVISTO_JORNADA_ID_JORNADA_FK;
          }
        case WOS_PONTO_PREVISTO_JORNADA_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_PREVISTO_JORNADA_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_PREVISTO_JORNADA_ID_PONTO_FK;
          }
        case WOS_PONTO_REFERENCIA_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_REFERENCIA_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_REFERENCIA_ID_FUNCIONARIO_FK;
          }
        case WOS_PONTO_REFERENCIA_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_REFERENCIA_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_REFERENCIA_ID_JORNADA_FK;
          }
        case WOS_PONTO_REFERENCIA_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_REFERENCIA_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_REFERENCIA_ID_PONTO_FK;
          }
        case WOS_POSTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_POSTO_ID_RESPONSAVEL_UC:
          return EXCECAO_DUPLICATED_WOS_POSTO_ID_RESPONSAVEL_UC;
        case WOS_PRIORIDADE_ORDEM_SERVICO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_PROJETO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PROJETO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_PROJETO_ID_CONTRATO_FK;
          }
        case WOS_RECEBIVEL_ID_CODIGO_NEGOCIACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RECEBIVEL_ID_CODIGO_NEGOCIACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RECEBIVEL_ID_CODIGO_NEGOCIACAO_FK;
          }
        case WOS_RECEBIVEL_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RECEBIVEL_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RECEBIVEL_ID_CONTRATO_FK;
          }
        case WOS_RECEBIVEL_ID_LIDER_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RECEBIVEL_ID_LIDER_FK;
          } else {
            return EXCECAO_DELETE_WOS_RECEBIVEL_ID_LIDER_FK;
          }
        case WOS_RECEBIVEL_ID_TIPO_ABASTECIMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RECEBIVEL_ID_TIPO_ABASTECIMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RECEBIVEL_ID_TIPO_ABASTECIMENTO_FK;
          }
        case WOS_RECEBIVEL_ID_MATRICULA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RECEBIVEL_ID_MATRICULA_FK;
          } else {
            return EXCECAO_DELETE_WOS_RECEBIVEL_ID_MATRICULA_FK;
          }
        case WOS_RECEBIVEL_NUMERO_ORDEM_SERVICO_UC:
          return EXCECAO_WOS_RECEBIVEL_NUMERO_ORDEM_SERVICO_UC;
        case WOS_REPRESENTANTE_CPF_UC:
          return EXCECAO_WOS_REPRESENTANTE_CPF_UC;
        case WOS_RELATORIO_PERSONALIZADO_NOME_UC:
          return EXCECAO_WOS_RECEBIVEL_NUMERO_ORDEM_SERVICO_UC;
        case WOS_RELATORIO_PERSONALIZADO_COLUNA_SELECIONADA_ID_RELATORIO_PERSONALIZADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RELATORIO_PERSONALIZADO_COLUNA_SELECIONADA_ID_RELATORIO_PERSONALIZADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RELATORIO_PERSONALIZADO_COLUNA_SELECIONADA_ID_RELATORIO_PERSONALIZADO_FK;
          }
        case WOS_REGISTRO_JORNADA_CODIGO_UC:
          return EXCECAO_WOS_REGISTRO_JORNADA_CODIGO_UC;
        case WOS_REGISTRO_DIALOGO_DIARIO_SEGURANCA_ID_DIALOGO_DIARIO_SEGURANCA_FK:
          return EXCECAO_DELETE_WOS_REGISTRO_DIALOGO_DIARIO_SEGURANCA_ID_DIALOGO_DIARIO_SEGURANCA_FK;
        case WOS_RESERVATORIO_ID_AREA_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_ID_AREA_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_ID_AREA_ATUACAO_FK;
          }
        case WOS_RESERVATORIO_ID_MATERIAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_ID_MATERIAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_ID_MATERIAL_FK;
          }
        case WOS_RESERVATORIO_ID_TIPO_CONDENSADO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_ID_TIPO_CONDENSADO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_ID_TIPO_CONDENSADO_FK;
          }
        case WOS_RESERVATORIO_ID_TIPO_RESERVATORIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_ID_TIPO_RESERVATORIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_ID_TIPO_RESERVATORIO_FK;
          }
        case WOS_RESERVATORIO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_RESERVATORIO_TURNO_ID_RESERVATORIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_TURNO_ID_RESERVATORIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_TURNO_ID_RESERVATORIO_FK;
          }
        case WOS_RESERVATORIO_TURNO_ID_TURNO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_RESERVATORIO_TURNO_ID_TURNO_FK;
          } else {
            return EXCECAO_DELETE_WOS_RESERVATORIO_TURNO_ID_TURNO_FK;
          }
        case WOS_ROTA_ID_DESTINATARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_ID_DESTINATARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_ID_DESTINATARIO_FK;
          }
        case WOS_ROTA_ID_REMETENTE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_ID_REMETENTE_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_ID_REMETENTE_FK;
          }
        case WOS_ROTA_PONTO_ID_PONTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_PONTO_ID_PONTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_PONTO_ID_PONTO_FK;
          }
        case WOS_ROTA_PONTO_ID_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_PONTO_ID_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_PONTO_ID_ROTA_FK;
          }
        case WOS_PONTO_MENSAGEM_ID_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_PONTO_MENSAGEM_ID_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_PONTO_MENSAGEM_ID_ROTA_FK;
          }
        case WOS_ROTA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_PONTO_MENSAGEM_ID_PONTO_MENSAGEM_FK;
          }
        case WOS_ROTA_SEGMENTO_ID_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_SEGMENTO_ID_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_SEGMENTO_ID_ROTA_FK;
          }
        case WOS_ROTA_SEGMENTO_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_SEGMENTO_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_SEGMENTO_ID_SEGMENTO_FK;
          }
        case WOS_ROTA_WAYPOINT_ID_ROTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_ROTA_WAYPOINT_ID_ROTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_ROTA_WAYPOINT_ID_ROTA_FK;
          }
        case WOS_SEGMENTO_ID_PLACA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEGMENTO_ID_PLACA_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEGMENTO_ID_PLACA_FK;
          }
        case WOS_SEGMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_SEGMENTO_PLANO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_SEGMENTO_REGISTRADO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEGMENTO_REGISTRADO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEGMENTO_REGISTRADO_ID_FUNCIONARIO_FK;
          }
        case WOS_SEGMENTO_REGISTRADO_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEGMENTO_REGISTRADO_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEGMENTO_REGISTRADO_ID_JORNADA_FK;
          }
        case WOS_SEGMENTO_REGISTRADO_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEGMENTO_REGISTRADO_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEGMENTO_REGISTRADO_ID_SEGMENTO_FK;
          }
        case WOS_SEGMENTO_WAYPOINT_ID_SEGMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEGMENTO_WAYPOINT_ID_SEGMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEGMENTO_WAYPOINT_ID_SEGMENTO_FK;
          }
        case WOS_SEMIRREBOQUE_ID_MATERIAL_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEMIRREBOQUE_ID_MATERIAL_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEMIRREBOQUE_ID_MATERIAL_FK;
          }
        case WOS_SEMIRREBOQUE_ID_MODELO_SEMIRREBOQUE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEMIRREBOQUE_ID_MODELO_SEMIRREBOQUE_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEMIRREBOQUE_ID_MODELO_SEMIRREBOQUE_FK;
          }
        case WOS_SEMIRREBOQUE_ID_TIPO_CARGA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEMIRREBOQUE_ID_TIPO_CARGA_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEMIRREBOQUE_ID_TIPO_CARGA_FK;
          }
        case WOS_SEMIRREBOQUE_ID_TIPO_CARROCERIA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_SEMIRREBOQUE_ID_TIPO_CARROCERIA_FK;
          } else {
            return EXCECAO_DELETE_WOS_SEMIRREBOQUE_ID_TIPO_CARROCERIA_FK;
          }
        case WOS_SEMIRREBOQUE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_STATUS_LIGACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_STATUS_ORDEM_SERVICO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TABELA_SERVICO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TABELA_SERVICO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TABELA_SERVICO_ID_CONTRATO_FK;
          }
        case WOS_TABELA_SERVICO_CONTRATO_ID_TABELA_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TABELA_SERVICO_CONTRATO_ID_TABELA_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TABELA_SERVICO_CONTRATO_ID_TABELA_SERVICO_FK;
          }
        case WOS_TABELA_SERVICO_CONTRATO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TABELA_SERVICO_CONTRATO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TABELA_SERVICO_CONTRATO_ID_TIPO_SERVICO_FK;
          }
        case WOS_TIMELINE_ID_FASE_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIMELINE_ID_FASE_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIMELINE_ID_FASE_FK;
          }
        case WOS_TIMELINE_ID_LIDER_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIMELINE_ID_LIDER_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIMELINE_ID_LIDER_FK;
          }
        case WOS_TIMELINE_ID_ORDEM_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIMELINE_ID_ORDEM_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIMELINE_ID_ORDEM_SERVICO_FK;
          }
        case WOS_TIMELINE_ID_STATUS_ANTERIOR_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIMELINE_ID_STATUS_ANTERIOR_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIMELINE_ID_STATUS_ANTERIOR_FK;
          }
        case WOS_TIMELINE_ID_STATUS_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIMELINE_ID_STATUS_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIMELINE_ID_STATUS_FK;
          }
        case WOS_TIMELINE_COLUMNS_UC:
          return EXCECAO_WOS_TIMELINE_COLUMNS_UC;
        case WOS_TIPO_ABASTECIMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ATUACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ATUACAO_NOME_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_ATUACAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_ATUACAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_ATUACAO_FK;
          }
        case WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_SERVICO_FK;
          }
        case WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_SERVICO_ID_TIPO_ATUACAO_UC:
          return EXCECAO_WOS_TIPO_ATUACAO_SERVICO_ID_TIPO_SERVICO_ID_TIPO_ATUACAO_UC;
        case WOS_TIPO_CARGA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_CARROCERIA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_CONDENSADO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_CORTE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ENTIDADE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ESCORAMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_HIDROMETRO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_IMOVEL_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_IMPEDIMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_IMPEDIMENTO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_IMPEDIMENTO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_IMPEDIMENTO_FK;
          }
        case WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_SERVICO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_SERVICO_FK;
          } else {
            return EXCECAO_DELETE_WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_SERVICO_FK;
          }
        case WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_IMPEDIMENTO_ID_TIPO_SERVICO_UC:
          return EXCECAO_WOS_TIPO_IMPEDIMENTO_SERVICO_ID_TIPO_IMPEDIMENTO_ID_TIPO_SERVICO_UC;
        case WOS_TIPO_LACRE_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_LIGACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_LOGRADOURO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_PAVIMENTACAO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_RESERVATORIO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_SERVICO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TREINAMENTO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_CONTRATO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO_CONTRATO;
        case WOS_EPI_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TIPO_ASO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_ESCRITORIOS_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_EPI_CA_UC:
          return EXCECAO_DUPLICATED_KEY_CA;
        case WOS_EPI_NOME_UC:
          return EXCECAO_DUPLICATED_KEY_NOME;
        case WOS_TIPO_ASO_NOME_UC:
          return EXCECAO_DUPLICATED_KEY_NOME;
        case WOS_ESCRITORIOS_NOME_UC:
          return EXCECAO_DUPLICATED_KEY_NOME;
        case WOS_TURNO_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_TREINAMENTO_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_EMPREGADO_TREINAMENTO_ID_EMPRESA_FK;
          } else {
            return EXCECAO_DELETE_WOS_EMPREGADO_TREINAMENTO_ID_EMPRESA_FK;
          }
        case WOS_DIALOGO_DIARIO_SEGURANCA_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_VEICULO_ID_CATEGORIA_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ID_CATEGORIA_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ID_CATEGORIA_VEICULO_FK;
          }
        case WOS_VEICULO_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ID_CONTRATO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ID_CONTRATO_FK;
          }
        case WOS_VEICULO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ID_FUNCIONARIO_FK;
          }
        case WOS_VEICULO_ID_MOTORISTA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ID_MOTORISTA_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ID_MOTORISTA_FK;
          }
        case WOS_VEICULO_CHECKLIST_ID_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_CHECKLIST_ID_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_CHECKLIST_ID_VEICULO_FK;
          }
        case WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FK;
          }
        case WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_CHECKLIST_FOTO_ID_VEICULO_CHECKLIST_FOTO_BLOB_FK;
          }
        case WOS_VEICULO_CHECKLIST_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_CHECKLIST_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_CHECKLIST_ITEM_CHECKLIST_ID_ITEM_CHECKLIST_FK;
          }
        case WOS_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ITEM_MANUTENCAO_PREVENTIVA_ID_VEICULO_FK;
          }
        case WOS_VEICULO_ODOMETRO_ID_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ODOMETRO_ID_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ODOMETRO_ID_VEICULO_FK;
          }
        case WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FK;
          }
        case WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FOTO_BLOB_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FOTO_BLOB_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_ODOMETRO_FOTO_ID_VEICULO_ODOMETRO_FOTO_BLOB_FK;
          }
        case WOS_VEICULO_PARADO_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_PARADO_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_PARADO_ID_FUNCIONARIO_FK;
          }
        case WOS_VEICULO_PARADO_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VEICULO_PARADO_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_VEICULO_PARADO_ID_JORNADA_FK;
          }
        case WOS_VELOCIDADE_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VELOCIDADE_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VELOCIDADE_ID_FUNCIONARIO_FK;
          }
        case WOS_VELOCIDADE_ID_JORNADA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VELOCIDADE_ID_JORNADA_FK;
          } else {
            return EXCECAO_DELETE_WOS_VELOCIDADE_ID_JORNADA_FK;
          }
        case WOS_VOUCHER_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VOUCHER_ID_EMPRESA_FK;
          } else {
            return EXCECAO_DELETE_WOS_VOUCHER_ID_EMPRESA_FK;
          }
        case WOS_VOUCHER_ID_FUNCIONARIO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VOUCHER_ID_FUNCIONARIO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VOUCHER_ID_FUNCIONARIO_FK;
          }
        case WOS_VOUCHER_ID_VEICULO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_VOUCHER_ID_VEICULO_FK;
          } else {
            return EXCECAO_DELETE_WOS_VOUCHER_ID_VEICULO_FK;
          }
        case WOS_VOUCHER_CODIGO_UC:
          return EXCECAO_DUPLICATED_KEY_CODIGO;
        case WOS_USUARIO_FUNCAO_ID_USUARIO_ID_FUNCAO_UC:
          return EXCECAO_WOS_USUARIO_FUNCAO_ID_USUARIO_ID_FUNCAO_UC;
        case WOS_USUARIO_LOGIN_UC:
          return EXCECAO_WOS_USUARIO_LOGIN_UC;
        case WOS_USUARIO_PERMISSAO_ID_PERMISSAO_ID_USUARIO_UC:
          return EXCECAO_WOS_USUARIO_PERMISSAO_ID_PERMISSAO_ID_USUARIO_UC;
        case WOS_USUARIO_USUARIO_PERMISSAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_USUARIO_USUARIO_PERMISSAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_USUARIO_USUARIO_PERMISSAO_FK;
          }
        case WOS_USUARIO_USUARIO_FUNCAO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_USUARIO_USUARIO_FUNCAO_FK;
          } else {
            return EXCECAO_DELETE_WOS_USUARIO_USUARIO_FUNCAO_FK;
          }
        case WOS_EQUIPE_ID_EMPRESA_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_EQUIPE_ID_EMPRESA_FK;
          }
        case WOS_EQUIPE_ID_CONTRATO_FK:
          if (action.equals(Action.SAVE)) {
            return EXCECAO_SAVE_WOS_EQUIPE_ID_CONTRATO_FK;
          }
        case WOS_VEICULO_PLACA_UC:
          return EXCECAO_WOS_BAIRRO_NOME_CIDADE_UC;
        case WOS_REGISTRO_JORNADA_LOGIN_FLAG_REGISTRO_JORNADA_FINALIZADO_IDX:
          return EXCECAO_DUPLICATED_KEY_REGISTRO_JORNADA_ABERTA;
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
