define([
  'formatter'
], function(
  Formatter
) {
  'use strict';

  // Formats
  // =======
  //
  // The Formats package specifies how general types should be displayed. It really should be deprecated for a 
  // more robust type system.

  return {
    ID: { name: 'ID', width: 60, formatter: Formatter.toString },
    IDString: { name: 'IDSTRING', width: 20 },
    Guid: { name: 'GUID', width: 150 },
    Bytes: { name: 'BYTES', width: 100, formatter: Formatter.formatBytes },
    Megabytes: { name: 'MEGABYTES', width: 100, formatter: Formatter.formatMegabytes },
    BytesOrNA: { name: 'BYTES_OR_NA', width: 100, formatter: Formatter.formatBytesOrNA },
    HashedPassword: { name: 'HASHED_PASSWORD' },
    OSVersion: { name: 'OS_VERSION', width: 100, formatter: Formatter.formatOSVersion },
    OSVersionPlus: { name: 'OS_VERSION_PLUS', width: 100, formatter: Formatter.formatOSVersionPlus },
    OSVersionComputer: { name: 'OS_VERSION_COMPUTER', width: 100, formatter: Formatter.formatOSVersionComputer },
    AgentVersion: { name: 'OS_VERSION', width: 100, formatter: Formatter.formatAgentVersion },
    IPv4Address: { name: 'IPV4_ADDRESS', width: 150, formatter: Formatter.formatIPv4Address },
    ClockSpeed: { name: 'CLOCK_SPEED', width: 100, formatter: Formatter.formatClockSpeed },
    Percent: {name: 'PERCENT', width: 60, formatter: Formatter.formatPercent },
    IntervalInMinutesHoursOrDays: { name: 'INTERVAL_MIN_HOUR_OR_DAY', width: 150, formatter: Formatter.formatIntervalInDaysHoursOrMinutesToString },
    IntervalInSeconds: { name: 'INTERVAL_SECS', width: 150, formatter: Formatter.formatIntervalInSecs },
    Ownership: { name: 'OWNERSHIP', width: 150, formatter: Formatter.formatOwnership },
    NetworkSpeed: { name: 'NETWORK_SPEED', width: 100, formatter: Formatter.formatSpeedBitOrNA },

    LinkIdentifier: { name: 'LINK_ID', formatter: Formatter.showIdentifierLink, searchable: true },

    Date: { name: 'DATE', width: 175, formatter: Formatter.formatDate },
    DateLocal: { name: 'DATE', width: 175, formatter: Formatter.formatDateLocal },
    ShortDate: { name: 'SHORTDATE', width: 150, formatter: Formatter.formatShortDate },
    ShortDateTime: { name: 'SHORTDATE', width: 150, formatter: Formatter.formatShortDateTime },
    Time: { name: 'TIME', width: 175, formatter: Formatter.formatTime },
    TimeLocal: { name: 'TIME', width: 175, formatter: Formatter.formatTimeLocal },
    Boolean: { name: 'BOOLEAN', width: 150, formatter: Formatter.formatBoolean },
    BooleanOrNA: { name: 'BOOLEAN_OR_NA', width: 150, formatter: Formatter.formatBooleanOrNA },
    Number: { name: 'NUMBER', width: 100 },
    NumberOrNA: { name: 'NUMBER_OR_NA', width: 100, formatter: Formatter.formatNumberOrNA },
    NumberToBooleanOrNA: { name: 'NUMBER_TO_BOOLEAN_OR_NA', width: 150, formatter: Formatter.formatNumberToBooleanOrNA },

    ShortString: { name: 'SHORTSTRING', width: 120, searchable: true },
    String: { name: 'STRING', width: 175, searchable: true },
    MediumString: { name: 'MEDIUMSTRING', width: 250, searchable: true },
    LongString: { name: 'LONGSTRING', width: 325, searchable: true },
    Text: { name: 'TEXT', searchable: true },
    ShortStringOrNA: { name: 'SHORTSTRING_OR_NA', width: 120, formatter: Formatter.toStringOrNA, searchable: true },
    StringOrNA: { name: 'STRING_OR_NA', width: 200, formatter: Formatter.toStringOrNA, searchable: true },
    MediumStringOrNA: { name: 'MEDIUMSTRING_OR_NA', width: 250, formatter: Formatter.toStringOrNA, searchable: true },
    MediumStringOrUnknownError: { name: 'MEDIUMSTRING_OR_UNKNOWN_ERROR', width: 250, formatter: Formatter.toStringOrUnknownError, searchable: true },
    LongStringOrNA: { name: 'LONGSTRING_OR_NA', width: 400, formatter: Formatter.toStringOrNA, searchable: true },

    EnumMediaFileAssignmentType: {name: 'ENUM_MEDIA_FILE_ASSIGNMENT_TYPE', width: 200, formatter: Formatter.formatFileAssignmentType },
    EnumMediaFileAvailabilitySelector: {name: 'ENUM_MEDIA_FILE_AVAILABILITY_SELECTOR', width: 200, formatter: Formatter.formatFileAvailabilitySelector },
    MediaFileAssignmentTime: {name: 'MEDIA_FILE_ASSIGNMENT_TIME', width: 200, formatter: Formatter.formatFileAssignmentTime },

    ReportCount: { name: 'REPORT_COUNT', width: 100, formatter: Formatter.formatReportCount },

    TitleDbType: { name: 'TITLE_DB_TYPE', width: 20, formatter: Formatter.formatTitleDbType, searchable: true },
    TItleDbIsEnabled: { name: 'TITLE_DB_IS_ENABLED', width: 7, formatter: Formatter.formatTitleDbIsEnabled},

    FpSuiteName: { name: 'FP_SUITE_NAME', width: 150, formatter: Formatter.formatSuiteName, searchable: true },
    FpPublisherName: { name: 'FP_PUBLISHER_NAME', width: 150, formatter: Formatter.formatPublisherName, searchable: true },
    FpTitleDbName: { name: 'FP_TITLE_DB_TYPE', width: 150, formatter: Formatter.formatTitleDbName, searchable: true },
    FpFileVersion: { name: 'FP_FILE_VERSION', width: 150, formatter: Formatter.formatFpFileVersion },

    LongHyperlinkToNewPage: { name: 'LONG_HYPERLINK_NP', width: 400, formatter: Formatter.formatHyperlinkToNewPage, searchable: true },
    BuildNumber: { name: 'BUILD_NUMBER', width: 100, formatter: Formatter.toStringOrNA, searchable: true },
    IconLabel: { name: 'ICON_LABEL', width: 50 },
    Icon: { name: 'ICON', width: 40, formatter: Formatter.toStringOrNA },
    // Some icons may represent data that we want to allow the user to search ( ex. OS = Android is portrayed as an Android icon )
    SearchableIcon: { name: 'SEARCHABLE_ICON', width: 60, formatter: Formatter.toStringOrNA, searchable: true },
    UUIDOrNA: { name: 'UUID_OR_NA', width: 270, formatter: Formatter.toStringOrNA, searchable: true },
    ReportType: {name: 'REPORT_TYPE', width: 20, formatter: Formatter.formatReportType, searchable: false },
    ChangeType: {name: 'CHANGE_TYPE', width: 70, formatter: Formatter.formatChangeType, searchable: true },
    ChangeOrigin: {name: 'CHANGE_ORIGIN', width: 100, formatter: Formatter.camelCaseToTitleCase, searchable: true },
    ChangeSystemName: {name: 'CHANGE_SYSTEM_NAME', width: 100, formatter: Formatter.camelCaseToTitleCase, searchable: true }
  };
});
