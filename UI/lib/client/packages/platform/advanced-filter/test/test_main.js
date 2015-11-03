define([
  'testing',
  'ember',
  'packages/platform/advanced-filter'
], function(
  Testing,
  Ember,
  AdvancedFilter
) {
  'use strict';

  return Testing.package('advanced-filter', [
    Testing.module('main', [
      Testing.test('String ops', function(assert) {
        var AF = AdvancedFilter;

        var testPlan = [
          AF.StartsWithOp,
          AF.NotStartsWithOp,
          AF.EndsWithOp,
          AF.NotEndsWithOp,
          AF.SubstringOfOp,
          AF.NotSubstringOfOp
        ];

        Em.A(testPlan).forEach(function(stringOp) {
          // Should not throw!
          var op = stringOp.create();

          // Has no parameters, should not be valid
          assert.strictEqual(op.get('isValid'), false, op.get('name') + ' should not be valid if empty');
        });
      }),

      Testing.test('Equals, string literals, empty string and null constants, copy, isValid and strings.', function(assert) {
        var attr = AdvancedFilter.Attr.create({
          value: 'testAttr',
          type: AdvancedFilter.TYPE_STRING
        });

        assert.strictEqual(attr.copy().get('asString'), attr.get('asString'), 'copied attr');
        assert.strictEqual(attr.get('isValid'), true, 'attr is valid');
        assert.strictEqual(attr.get('asString'), "ATTR('testAttr')", 'serialized attr');

        var string = AdvancedFilter.StringLiteral.create({ value: 'testRVal' });

        assert.strictEqual(string.copy().get('asString'), string.get('asString'), 'copied string');
        assert.strictEqual(string.get('isValid'), true, 'non empty string literal is valid');
        assert.strictEqual(string.get('asString'), "STRING('testRVal')", 'serialized non empty string');

        var equals = AdvancedFilter.EqualsOp.create({
          lval: attr,
          rval: string
        });

        assert.strictEqual(equals.copy().get('asString'), equals.get('asString'), 'copied equals');
        assert.strictEqual(equals.get('isValid'), true, 'rval with not (null / "") value and String type is valid.');
        assert.strictEqual(equals.get('asString'), "EQUALS(ATTR('testAttr'), STRING('testRVal'))", 'serialized equals');

        // Various invalid incarnations of StringLiteral
        string.set('value', '');
        assert.strictEqual(string.get('isValid'), false, 'string literal with empty string is not valid');
        assert.strictEqual(equals.get('isValid'), false, 'equals with invalid rval is not valid');

        string.set('value', null);
        assert.strictEqual(string.get('isValid'), false, 'string literal with null value is not valid');
        assert.strictEqual(equals.get('isValid'), false, 'equals with invalid rval is not valid');
        assert.strictEqual(equals.get('asString'), undefined, 'equals with invalid rval is not valid. asString returns undefined');

        // Cases that rval is empty string ""
        var emptyStringConstant = AdvancedFilter.EmptyStringConstant.create();
        equals.set('rval', emptyStringConstant);
        assert.strictEqual(emptyStringConstant.get('isValid'), true, 'empty string constant is valid');
        assert.strictEqual(equals.get('isValid'), true, 'equals with valid rval is valid');

        assert.strictEqual(equals.get('asString'), "EQUALS(ATTR('testAttr'), EMPTY_STRING())", 'serialized equals with empty string parameter');

        // Cases that rval is null
        var nullConstant = AdvancedFilter.NullConstant.create();
        equals.set('rval', nullConstant)
        assert.strictEqual(nullConstant.get('isValid'), true, 'null constant is valid');
        assert.strictEqual(equals.get('isValid'), true, 'equals with valid rval is valid');

        assert.strictEqual(equals.get('asString'), "EQUALS(ATTR('testAttr'), NULL())", 'serialized equals with empty string parameter');
      }),

      Testing.test('asDebugString', function(assert) {
        var validFilter = AdvancedFilter.EqualsOp.create({
          lval: AdvancedFilter.Attr.create({
            value: 'testAttr',
            type: AdvancedFilter.TYPE_STRING
          }),

          rval: AdvancedFilter.StringLiteral.create({
            value: 'testRval'
          })
        });

        assert.strictEqual(validFilter.get('asString'), "EQUALS(ATTR('testAttr'), STRING('testRval'))", 'Valid filter transforms to a valid string for asString.');
        assert.strictEqual(validFilter.get('asDebugString'), "EQUALS(ATTR('testAttr'), STRING('testRval'))", 'Valid filter transforms to a valid string for asDebugString');

        var invalidRvalFilter = AdvancedFilter.EqualsOp.create({
          lval: AdvancedFilter.Attr.create({ }),

          rval: AdvancedFilter.StringLiteral.create({
            value: 'testRval'
          })
        });
        assert.strictEqual(invalidRvalFilter.get('asString'), undefined, 'asString returns undefined for invalid lval');
        assert.strictEqual(invalidRvalFilter.get('asDebugString'), "EQUALS(ATTR(null), STRING('testRval'))", 'asDebugString returns null for invalid lval');

        var invalidNodeFilter = AdvancedFilter.EqualsOp.create({ });
        assert.strictEqual(invalidNodeFilter.get('asString'), undefined, 'asString returns undefined for invalid filterNode (EqualsOp)');
        assert.strictEqual(invalidNodeFilter.get('asDebugString'), "EQUALS(INVALID(), INVALID())", 'asDebugString returns INVALID() for invalid filterNode (EqualsOp)');
      }),

      Testing.test('toJSON', function(assert) {
        var stingLval = AdvancedFilter.Attr.create({
          value: 'testAttr',
          type: AdvancedFilter.TYPE_STRING
        });

        assert.deepEqual(stingLval.toJSON(), { name: 'ATTR', params: ['testAttr'], type: String }, 'Testing the result of string lval toJSON');

        var invalidLval = AdvancedFilter.Attr.create({
          value: 'testAttr'
        });
        assert.deepEqual(invalidLval.toJSON(), undefined, 'Testing the result of invalid lval toJSON');
      }),

      Testing.test('isComplete', function(assert) {
        var completeFilter = AdvancedFilter.AndFilter.create({
          operands: Em.A([
            AdvancedFilter.WrapOp.create({
              lval: AdvancedFilter.EqualsOp.create({
                lval: AdvancedFilter.Attr.create({ value: 'attr1', type: AdvancedFilter.TYPE_STRING }),
                rval: AdvancedFilter.StringLiteral.create({ value: 'test' })
              })
            }),
            AdvancedFilter.WrapOp.create({
              lval: AdvancedFilter.EqualsOp.create({
                lval: AdvancedFilter.Attr.create({ value: 'attr2', type: AdvancedFilter.TYPE_STRING }),
                rval: AdvancedFilter.StringLiteral.create({ value: 'test' })
              })
            })
          ])
        });
        assert.strictEqual(completeFilter.get('isValid'), true, 'completeFilter is valid');
        assert.strictEqual(completeFilter.get('isComplete'), true, 'completeFilter is complete');

        var inCompleteFilter = AdvancedFilter.AndFilter.create({
          operands: Em.A([
            AdvancedFilter.OrFilter.create({
              operands: Em.A([
                AdvancedFilter.WrapOp.create({
                  lval: AdvancedFilter.EqualsOp.create({
                    lval: AdvancedFilter.Attr.create({ value: 'testAttr', type: AdvancedFilter.TYPE_STRING }),
                    rval: AdvancedFilter.StringLiteral.create({ value: 'testRval' })
                  })
                }),

                AdvancedFilter.WrapOp.create({
                  lval: AdvancedFilter.EqualsOp.create({})
                })
              ])
            })
          ])
        });
        assert.strictEqual(inCompleteFilter.get('isValid'), true, 'inCompleteFilter is valid');
        assert.strictEqual(inCompleteFilter.get('isComplete'), false, 'inCompleteFilter is not complete');
      }),


      Testing.test('IsEmpty', function(assert) {
        var attr = AdvancedFilter.Attr.create({
          value: 'testAttr',
          type: AdvancedFilter.TYPE_STRING
        });

        assert.equal(attr.get('isValid'), true, 'attr is valid');

        var isEmpty = AdvancedFilter.IsEmptyOp.create({ lval: attr });

        assert.equal(isEmpty.get('isValid'), true, 'is empty with attr as parameter is valid');
      }),

      Testing.test('Behaviour of deeper filters', function(assert) {
        // This is the structure of filters used by Customer Center.
        var deepFilter = AdvancedFilter.AndFilter.create({
          operands: Em.A([

            AdvancedFilter.OrFilter.create({
              operands: Em.A([
                
                AdvancedFilter.WrapOp.create({

                  lval: AdvancedFilter.EqualsOp.create({
                    lval: AdvancedFilter.Attr.create({
                      value: 'testAttr',
                      type: AdvancedFilter.TYPE_STRING
                    }),

                    rval: AdvancedFilter.StringLiteral.create({
                      value: 'testRval'
                    })
                  })
                })
              ])
            })
          ])
        });

        assert.strictEqual(deepFilter.get('isValid'), true, 'deepFilter is valid');
        assert.strictEqual(deepFilter.get('asString'), "AND(OR(WRAP_OP(EQUALS(ATTR('testAttr'), STRING('testRval')))))", 'deepFilter as string');

        var validateUniqueness = function(source, copy) {
          if ('object' === typeof(source)) {
            var context = AdvancedFilter.FilterNode.detectInstance(source) ? ' (' + source.get('asString') + ')' : String(source);
            assert.notEqual(source, copy, 'instances should be copies' + context);
          }

          if (AdvancedFilter.FilterNode.detectInstance(source)) {
            for (var i = 0; i < source.get('params.length'); i++) {
              validateUniqueness(source.get('params').objectAt(i), copy.get('params').objectAt(i));
            }
          }
        }

        var copied = deepFilter.copy();

        assert.strictEqual(copied.get('asString'), deepFilter.get('asString'), 'copy of deepFilter as string');
        assert.strictEqual(copied.get('isValid'), true, 'copy of deepFilter is valid');
        validateUniqueness(deepFilter, copied);

        var optimized = deepFilter.optimize();

        assert.strictEqual(optimized.get('asString'), "EQUALS(ATTR('testAttr'), STRING('testRval'))", 'optimized deepFilter as string');
        assert.strictEqual(optimized.get('isValid'), true, 'optimized copy of deepFilter is valid');
        assert.strictEqual(optimized.copy().get('asString'), "EQUALS(ATTR('testAttr'), STRING('testRval'))", 'optimized copy as string');
        optimized.depthFirstWalk(function(optimizedNode) {
          var found = false;
          deepFilter.depthFirstWalk(function(sourceNode) {
            if (optimizedNode === sourceNode) {
              found = true;
            }
          });
          assert.ok(!found, 'no nodes in optimized should also be part of original');
        });
      })
    ])
  ]);
});


