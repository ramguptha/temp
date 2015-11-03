define([
  'formats'
], function(
  format
) {
  'use strict';

  if( 'Boolean' in format ) {
    format['Boolean'].searchable = true;
  }

  if( 'BooleanOrNA' in format ) {
    format['BooleanOrNA'].searchable = true;
  }
  
  if( 'OSVersion' in format ) {
    format['OSVersion'].searchable = true;
  }

  if( 'OSVersionPlus' in format ) {
    format['OSVersionPlus'].searchable = true;
  }

  if( 'Bytes' in format ) {
    format['Bytes'].searchable = true;
  }

  return format;
});