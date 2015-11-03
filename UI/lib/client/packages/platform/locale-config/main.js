define([
  'moment',
  'jsworld'
], function(
  Moment,
  JsWorld
) {
  'use strict';

  // LocaleConfiguration
  // ===================
  //
  // The bootstrapper for localization functionality. Mostly this involves downloading and configuring localization
  // resources for dependent libraries.

  // The locale. Set once per page-load.
  var localeCode = null;

  // Locale-specific configuration. Set once per page-load.
  var localeConfig = null;

  // Shared Moment.js locale code.
  var momentLocale = null;

  // Shared Moment.js locale instance.
  var momentLocaleConfig = null;

  // Shared JsWorld locale instance.
  var jsWorldLocaleConfig = null;

  return {

    // nearestSupportedLocaleCode(localeCode, candidateLocaleCodes, transform)
    // -----------------------------------------------------------------------
    //
    // Given a list of candidate locales and an optional transform function for them, finds the nearest match to
    // the provided localeCode.
    nearestSupportedLocale: function(localeCode, candidateLocaleCodes, transform) {
      var nearestLocaleCode = null;

      var candidateLocaleCode = localeCode;
      while (null === nearestLocaleCode) {
        for (var i = 0; i < candidateLocaleCodes.length && null === nearestLocaleCode; i++) {
          var rawSupportedLocaleCode = candidateLocaleCodes[i];
          var supportedLocaleCode = transform ? transform(rawSupportedLocaleCode) : rawSupportedLocaleCode;

          var matched = false;
          if (candidateLocaleCode === supportedLocaleCode) {
            matched = true;
          } else if (supportedLocaleCode.slice(0, candidateLocaleCode.length) === candidateLocaleCode && 
              supportedLocaleCode.slice(candidateLocaleCode.length, 1).match(/\W|$/)) {
            matched = true;
          }

          if (matched) {
            nearestLocaleCode = rawSupportedLocaleCode;
          }
        }

        if (-1 !== candidateLocaleCode.indexOf('-')) {
          candidateLocaleCode = candidateLocaleCode.replace(/-[^-]+$/, '');
        } else {
          break;
        }
      }

      return nearestLocaleCode;
    },

    // configure(locale, callback)
    // ---------------------------
    //
    // Set the locale, downloading resources as necessary. Invoke the callback with the configured locale when done.
    setLocale: function(locale, callback) {
      var self = this;

      if (localeCode) {
        throw ['Locale can only be set once per page-load', localeCode, locale];
      }

      if (!locale) {
        locale = navigator.language || navigator.userLanguage;
      }

      // Remember the configuration
      localeCode = locale.toLowerCase();

      // Configure Require.js's i18n plugin
      var config = window.requirejs.s.contexts._.config;

      config.config = config.config || {};
      config.config.i18n = config.config.i18n || {};
      config.config.i18n.locale = localeCode;

      // Async queue for loading locale configurations
      var loaders = [];

      // Executes the next load on the queue, or the final callback if the queue is drained.
      var iterateLoading = function() {
        if (0 < loaders.length) {

          // Next load
          var loader = loaders.shift();
          loader(iterateLoading);
        } else {

          // Loading is complete - notify caller
          callback(localeCode);
        }
      };

      // en-us is the Moment.js default, there is nothing to do if that's the locale.
      if ('en-us' !== localeCode) {

        // Otherwise, configure Moment.js with the best matching locale available.
        var supportedMomentLocales = 'af bo de-at et he ja ml pl sr-cyrl tzm ar-ma br de eu hi ka mr pt-br sr uk ar-sa bs el fa hr km ms-my pt sv uz ar ca en-au fi hu ko my ro ta vi az cs en-ca fo hy-am lb nb ru th zh-cn be cv en-gb fr-ca id lt ne sk tl-ph zh-tw bg cy eo fr is lv nl sl tr bn da es gl it mk nn sq tzm-latn'.split(' ');

        // Module scope, for use during construction of localized Moment.js instances.
        momentLocale = this.nearestSupportedLocale(localeCode, supportedMomentLocales);

        if (null === momentLocale) {
          throw [
            'Unable to find a Moment.js locale to match configured one', localeCode, supportedMomentLocales
          ];
        }

        loaders.push(function(next) {
          // Load and configure the Moment Locale
          require(['moment-locales/' + momentLocale], function(loadedMomentLocaleConfig) {
            momentLocaleConfig = loadedMomentLocaleConfig;
            
            // Done
            next();
          });
        });
      }

      var supportedJsWorldLocales = 'POSIX_LC aa_DJ aa_ER aa_ET af_NA af_ZA agq_CM ak_GH am_ET ar_AE ar_BH ar_DJ ar_DZ ar_EG ar_EH ar_ER ar_IL ar_IQ ar_JO ar_KM ar_KW ar_LB ar_LY ar_MA ar_MR ar_OM ar_PS ar_QA ar_SA ar_SD ar_SO ar_SS ar_SY ar_TD ar_TN ar_YE as_IN asa_TZ ast_ES az_Cyrl_AZ az_Latn_AZ bas_CM be_BY bem_ZM bez_TZ bg_BG bm_ML bn_BD bn_IN bo_CN bo_IN br_FR brx_IN bs_Cyrl_BA bs_Latn_BA byn_ER ca_AD ca_ES ca_ES_VALENCIA ca_FR ca_IT cgg_UG chr_US cs_CZ cy_GB da_DK da_GL dav_KE de_AT de_BE de_CH de_DE de_LI de_LU dje_NE dua_CM dyo_SN dz_BT ebu_KE ee_GH ee_TG el_CY el_GR en_150 en_AG en_AI en_AS en_AU en_BB en_BE en_BM en_BS en_BW en_BZ en_CA en_CC en_CK en_CM en_CX en_DG en_DM en_ER en_FJ en_FK en_FM en_GB en_GD en_GG en_GH en_GI en_GM en_GU en_GY en_HK en_IE en_IM en_IN en_IO en_JE en_JM en_KE en_KI en_KN en_KY en_LC en_LR en_LS en_MG en_MH en_MO en_MP en_MS en_MT en_MU en_MW en_NA en_NF en_NG en_NR en_NU en_NZ en_PG en_PH en_PK en_PN en_PR en_PW en_RW en_SB en_SC en_SD en_SG en_SH en_SL en_SS en_SX en_SZ en_TC en_TK en_TO en_TT en_TV en_TZ en_UG en_UM en_US en_VC en_VG en_VI en_VU en_WS en_ZA en_ZM en_ZW es_AR es_BO es_CL es_CO es_CR es_CU es_DO es_EA es_EC es_ES es_GQ es_GT es_HN es_IC es_MX es_NI es_PA es_PE es_PH es_PR es_PY es_SV es_US es_UY es_VE et_EE eu_ES ewo_CM fa_AF fa_IR ff_CM ff_GN ff_MR ff_SN fi_FI fil_PH fo_FO fr_BE fr_BF fr_BI fr_BJ fr_BL fr_CA fr_CD fr_CF fr_CG fr_CH fr_CI fr_CM fr_DJ fr_DZ fr_FR fr_GA fr_GF fr_GN fr_GP fr_GQ fr_HT fr_KM fr_LU fr_MA fr_MC fr_MF fr_MG fr_ML fr_MQ fr_MR fr_MU fr_NC fr_NE fr_PF fr_PM fr_RE fr_RW fr_SC fr_SN fr_SY fr_TD fr_TG fr_TN fr_VU fr_WF fr_YT fur_IT fy_NL ga_IE gd_GB gl_ES gsw_CH gsw_LI gu_IN guz_KE gv_IM ha_Latn_GH ha_Latn_NE ha_Latn_NG haw_US he_IL hi_IN hr_BA hr_HR hu_HU hy_AM ia_FR id_ID ig_NG ii_CN is_IS it_CH it_IT it_SM ja_JP jgo_CM jmc_TZ ka_GE kab_DZ kam_KE kde_TZ kea_CV khq_ML ki_KE kk_Cyrl_KZ kkj_CM kl_GL kln_KE km_KH kn_IN ko_KP ko_KR kok_IN ks_Arab_IN ksb_TZ ksf_CM ksh_DE kw_GB ky_Cyrl_KG lag_TZ lg_UG lkt_US ln_AO ln_CD ln_CF ln_CG lo_LA lt_LT lu_CD luo_KE luy_KE lv_LV mas_KE mas_TZ mer_KE mfe_MU mg_MG mgh_MZ mgo_CM mk_MK ml_IN mn_Cyrl_MN mr_IN ms_Latn_BN ms_Latn_MY ms_Latn_SG mt_MT mua_CM my_MM naq_NA nd_ZW ne_IN ne_NP nl_AW nl_BE nl_BQ nl_CW nl_NL nl_SR nl_SX nmg_CM nnh_CM nr_ZA nso_ZA nus_SD nyn_UG om_ET om_KE or_IN os_GE os_RU pa_Arab_PK pa_Guru_IN pl_PL ps_AF pt_AO pt_BR pt_CV pt_GW pt_MO pt_MZ pt_PT pt_ST pt_TL rm_CH rn_BI ro_MD ro_RO rof_TZ ru_BY ru_KG ru_KZ ru_MD ru_RU ru_UA rw_RW rwk_TZ sah_RU saq_KE sbp_TZ se_FI se_NO seh_MZ ses_ML sg_CF shi_Latn_MA shi_Tfng_MA si_LK sk_SK sl_SI sn_ZW so_DJ so_ET so_KE so_SO sq_AL sq_MK sq_XK sr_Cyrl_BA sr_Cyrl_ME sr_Cyrl_RS sr_Cyrl_XK sr_Latn_BA sr_Latn_ME sr_Latn_RS sr_Latn_XK ss_SZ ss_ZA ssy_ER st_LS st_ZA sv_AX sv_FI sv_SE sw_KE sw_TZ sw_UG swc_CD ta_IN ta_LK ta_MY ta_SG te_IN teo_KE teo_UG tg_Cyrl_TJ th_TH ti_ER ti_ET tig_ER tn_BW tn_ZA to_TO tr_CY ts_ZA twq_NE tzm_Latn_MA ug_Arab_CN uk_UA ur_IN ur_PK uz_Arab_AF uz_Cyrl_UZ uz_Latn_UZ vai_Latn_LR vai_Vaii_LR ve_ZA vi_VN vun_TZ wae_CH wal_ET xh_ZA xog_UG yav_CM yo_BJ yo_NG zgh_MA zh_Hans_CN zh_Hans_HK zh_Hans_MO zh_Hans_SG zh_Hant_HK zh_Hant_MO zh_Hant_TW zu_ZA'.split(' ');

      var jsWorldLocale = this.nearestSupportedLocale(localeCode, supportedJsWorldLocales, function(localeName) {
        return localeName.replace('_', '-').toLowerCase();
      });

      if (null === jsWorldLocale) {
        throw [
          'Unable to find a JsWorld locale to match configured one', localeCode, supportedJsWorldLocales
        ];
      }

      loaders.push(function(next) {

        // JsWorld locales are loaded into window.POSIX_LC. We leave the locales un-shimmed due to the large number
        // of them.
        require(['jsworld-locales/' + jsWorldLocale], function() {
          jsWorldLocaleConfig = new jsworld.Locale(window.POSIX_LC[jsWorldLocale]);

          self.jsWorldLocaleConfig = jsWorldLocaleConfig;

          self.jsWorldMonetaryParser = new JsWorld.MonetaryParser(jsWorldLocaleConfig);
          self.jsWorldNumericFormatter = new JsWorld.NumericFormatter(jsWorldLocaleConfig);

          self.jsWorldNumericParser = new JsWorld.NumericParser(jsWorldLocaleConfig);
          self.jsWorldNumericFormatter = new JsWorld.NumericFormatter(jsWorldLocaleConfig);

          self.jsWorldDateTimeParser = new JsWorld.DateTimeParser(jsWorldLocaleConfig);
          self.jsWorldDateTimeFormatter = new JsWorld.DateTimeFormatter(jsWorldLocaleConfig);

          next();
        });
      });

      loaders.push(function(next) {
        require(['i18n!packages/platform/locale-config/nls/config'], function(config) {
          localeConfig = config;
          next();
        });
      });

      // Kick off async loading
      iterateLoading();
    },

    // locale()
    // --------
    //
    // Getter for locale code.
    locale: function() {
      return localeCode;
    },

    // isRightToLeft()
    // ---------------
    //
    // Getter: is the current locale read from right to left?
    isRightToLeft: function() {
      return localeConfig.isRightToLeft;
    },

    // momentUtc(*args)
    // -------------
    //
    // Generator for localized Moment.js instances that think in UTC.
    momentUtc: function() {
      var moment = Moment.utc.apply(window, arguments);

      if (momentLocale) {
        moment.locale(momentLocale, momentLocaleConfig);
      }

      return moment;
    },

    // momentLocal(*args)
    // -------------
    //
    // Generator for localized Moment.js instances that think in local time.
    momentLocal: function() {
      var moment = Moment.apply(window, arguments);

      if (momentLocale) {
        moment.locale(momentLocale, momentLocaleConfig);
      }

      return moment;
    },

    // jsWorldLocale
    // -------------
    //
    // The configured JsWorld locale, exposed for its utility methods.
    jsWorldLocaleConfig: null,

    // jsWorldNumericParser
    // --------------------
    //
    // JsWorld NumericParser, with the configured locale.
    jsWorldNumericParser: null,

    // jsWorldNumericFormatter
    // -----------------------
    //
    // JsWorld NumericFormatter, with the configured locale.
    jsWorldNumericFormatter: null,

    // jsWorldMonetaryParser
    // ---------------------
    //
    // JsWorld MonetaryParser, with the configured locale.
    jsWorldMonetaryParser: null,

    // jsWorldMonetaryFormatter
    // ------------------------
    //
    // JsWorld MonetaryFormatter, with the configured locale.
    jsWorldMonetaryFormatter: null,

    // jsWorldDateTimeParser
    // ---------------------
    //
    // JsWorld DateTimeParser, with the configured locale.
    jsWorldDateTimeParser: null,

    // jsWorldDateTimeFormatter
    // ------------------------
    //
    // JsWorld DateTimeFormatter, with the configured locale.
    jsWorldDateTimeFormatter: null
  };
});
