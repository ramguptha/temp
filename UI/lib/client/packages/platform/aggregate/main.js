define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // Aggregate
  // =========
  //
  // Class for handling aggregate fields
  // There are two types of aggregate data: OneToOne and OneToMany
  var Base = Em.Object.extend({ Spec: null });

  return {
    Base: Base,
    OneToOne: Base.extend(),
    OneToMany: Base.extend()
  }
});
