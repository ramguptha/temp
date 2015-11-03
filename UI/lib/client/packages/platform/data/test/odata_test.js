define([
  'testing',
  'packages/platform/advanced-filter',
  '../lib/odata_uri_builder'
], function(
  Testing,
  AdvancedFilter,
  OdataUriBuilder
) {
  'use strict';

  return Testing.module('odata_uri_builder', [
    Testing.test('serialize', function(assert) {

      var odataUriBuilder = OdataUriBuilder.create({});
      var searchableAttrs = ['fieldA', 'fieldB'];
      var attrToEndpointNameMap = { fieldA: 'FieldA', fieldB: 'FieldB', aggregate1ToN: 'Aggregate1ToN', 'aggregate1ToN.child': 'Aggregate1ToN.Child', 'aggregate1To1.child': 'Aggregate1To1.Child'  };

      //------ String type ATTRs ------//
      ///////////////////////////////////
      var json = {
        name: 'EQUALS',
        type: Boolean,
        params: [
          { name: 'ATTR', type: String, params: ['fieldA'] },
          { name: 'STRING', type: String, params: ['test'] }
        ]
      };
      var serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "FieldA eq 'test'", "FieldA equals 'test': FieldA eq 'test'");

      json.name = 'NOT_EQUALS';
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "FieldA ne 'test'", "FieldA not equals 'test': FieldA ne 'test'");

      json = {
        name: 'SUBSTRING_OF',
        type: String,
        params: [
          { name: 'ATTR', type: String, params: ['fieldA'] },
          { name: 'STRING', type: String, params: ['test'] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "substringof('test', FieldA)", "FieldA contains 'test': substringof('test', FieldA)");

      json.name = 'NOT_SUBSTRING_OF';
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "(not substringof('test', FieldA))", "FieldA does not contain 'test': (not substringof('test', FieldA))");

      json = {
        name: 'IS_EMPTY',
        type: Boolean,
        params: [
          { name: 'ATTR', type: String, params: ['fieldA'] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "(FieldA eq null) or (FieldA eq '')", "FieldA is empty: (FieldA eq null) or (FieldA eq '')");

      json.name = 'IS_NOT_EMPTY';
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "(FieldA ne null) and (FieldA ne '')", "FieldA is not empty: (FieldA ne null) and (FieldA ne '')");


      //------ Date type ATTRs ------//
      //////////////////////////////////////
      json = {
        name: 'EQUALS',
        type: Boolean,
        params: [
          { name: 'ATTR', type: Date, params: ['fieldA'] },
          { name: 'DATE', type: Date, params: ['Tue Oct 07 2014 17:00:00 GMT-0700 (Pacific Daylight Time)'] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "FieldA ge datetime'2014-10-08T00:00:00Z' and FieldA lt datetime'2014-10-09T00:00:00Z'", "FieldA equals SOME_DATE: FieldA ge SOME_DATE and FieldA lt SOME_DATE");

      json = {
        name: 'NOT_EQUALS',
        type: Boolean,
        params: [
          { name: 'ATTR', type: Date, params: ['fieldA'] },
          { name: 'DATE', type: Date, params: ['Tue Oct 07 2014 17:00:00 GMT-0700 (Pacific Daylight Time)'] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      assert.strictEqual(serializedJson, "FieldA lt datetime'2014-10-08T00:00:00Z' or FieldA ge datetime'2014-10-09T00:00:00Z'", "FieldA not equals SOME_DATE: FieldA lt SOME_DATE or FieldA ge SOME_DATE");

      json = {
        name: 'WITHIN',
        type: Boolean,
        params: [
          { name: 'ATTR', type: Date, params: ['fieldA'] },
          { name: 'INTEGER', type: 'INTEGER', params: [3] },
          { name: 'STRING', type: String, params: ['LastNDays'] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      // This test will never pass since the result keeps changing based on the real time
      // assert.strictEqual(serializedJson, "FieldA gt datetime'2014-10-07T17:06:51Z'", "FieldA within last 3 days: FieldA gt datetime'2014-10-07T17:06:51Z'");

      var date = new Date('2014-10-12 11:13:00.000Z');
      json = {
        name: 'GREATER_THAN',
        type: Boolean,
        params: [
          { name: 'ATTR', type: Date, params: ['fieldA'] },
          { name: 'DATETIME', type: Date, params: [date] }
        ]
      };
      serializedJson = odataUriBuilder.serialize(json, searchableAttrs, attrToEndpointNameMap);
      //assert.strictEqual(serializedJson, "FieldA gt datetime'2014-10-12T11:13:00Z'", "FieldA Greater than SOME_DATE: FieldA gt datetime'2014-10-12T11:13:00Z'");

      // Test cases of keys of 'values'
      // STRING
      var serialize = function(val) {
        return odataUriBuilder.values.STRING.serializer(val);
      }
      assert.strictEqual(serialize("test"), "'test'", "test (STRING) 'test'");
      assert.strictEqual(serialize("'test"), "'''test'", "'test (STRING) ''test'");
      assert.strictEqual(serialize("test/,.<1>ode"), "'test/,.<1>ode'", "test/,.<1>ode (STRING) 'test/,.<1>ode'");

      // NUMBER
      var serialize = function(val) {
        return odataUriBuilder.values.NUMBER.serializer(val);
      }
      assert.strictEqual(serialize("10"), "10", "10 (NUMBER) 10");
      assert.strictEqual(serialize("10.2"), "10.2", "10.2 (NUMBER) 10.2");

      // INTEGER
      serialize = function(val) {
        return odataUriBuilder.values.INTEGER.serializer(val);
      }
      assert.strictEqual(serialize("10"), "10", "10 (INTEGER) 10");
      assert.strictEqual(serialize("10.2"), "10.2", "10.2 (INTEGER) 10.2");

      // DATE
      serialize = function(val) {
        return odataUriBuilder.values.DATE.serializer(val);
      }
      // TODO Talk to Dave about the expected behaviour for DATE serializer
//      assert.strictEqual(serialize("Tue Oct 07 2014 17:00:00 GMT-0700 (Pacific Daylight Time)"), "datetime'Tue Oct 07 2014 17:00:00 GMT-0700 (Pacific Daylight Time)'", "10 (INTEGER) 10");

      // DATETIME
      serialize = function(val) {
        return odataUriBuilder.values.DATETIME.serializer(val);
      }
      //assert.strictEqual(serialize(date), "datetime'2014-10-12T11:13:00Z'", "Mon Oct 12 2014 11:13:00 GMT (DATE) datetime'2014-10-12T11:13:00Z'");

      // GUID
      serialize = function(val) {
        return odataUriBuilder.values.GUID.serializer(val);
      }
      assert.strictEqual(serialize("3341398a-a113-4188"), "guid'3341398a-a113-4188'", "3341398a-a113-4188 (GUID) guid'3341398a-a113-4188'");

      // ATTR
      serialize = function(val, type, attrToEndpointNameMap) {
        return odataUriBuilder.values.ATTR.serializer(val, type, attrToEndpointNameMap);
      }
      assert.strictEqual(serialize("fieldA", String, attrToEndpointNameMap), "FieldA", "fieldA (ATTR) FieldA");
      assert.strictEqual(serialize("fieldA", Number, attrToEndpointNameMap), "FieldA", "fieldA (ATTR) FieldA");
      assert.strictEqual(serialize("aggregate1ToN", 'AGGREGATE_1_TO_N', attrToEndpointNameMap), "Aggregate1ToN", "aggregate1ToN (ATTR) Aggregate1ToN");
      assert.strictEqual(serialize("child", String, attrToEndpointNameMap), "Child/Child", "child (ATTR) Child/Child");
      assert.strictEqual(serialize("aggregate1To1.child", String, attrToEndpointNameMap), "Aggregate1To1/Child", "aggregate1To1.child (ATTR) Aggregate1To1/Child");
//      assert.throws(serialize(2, Number, attrToEndpointNameMap), ['Unknown attr queried', 2]);
    })
  ]);
});
