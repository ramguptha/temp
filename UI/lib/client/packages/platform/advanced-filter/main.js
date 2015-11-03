define([
  'ember',

  'packages/platform/guid',
  'packages/platform/date-type',
  'packages/platform/enum-type',

  'logger'
], function(
  Em,

  Guid,
  DateType,
  EnumType,

  logger
  ) {
  'use strict';

  // Advanced Filters
  // ================
  //
  // An Advanced Filter unambiguously represents a set of query criteria, to be performed against a data set. It is
  // a nested data structure, a tree.

  // Validity tests, by type
  // -----------------------

  var stringIsValid = function(value) {
    return typeof(value) === 'string' && !Em.isEmpty(value);
  };

  var numberIsValid = function(value) {
    return typeof(value) === 'number' && !isNaN(value);
  };

  var integerIsValid = function(value) {
    return numberIsValid(value) && (value === Math.floor(value));
  };

  var dateIsValid = DateType.isValid;

  var guidIsValid = Guid.isValid;

  // Casts to string for serialization
  // ---------------------------------

  var stringToString = function(value) {
    return "'" + value.replace("'", "''") + "'";
  };

  var numberToString = function(value) {
    return value.toString(10);
  };

  var dateToString = function(value) {
    return value.toISOString();
  };

  // Casts to JSON
  // -------------

  var numberToJSON = function(value) {
    return Number(value);
  };

  var dateToJSON = function(value) {
    return value.toISOString();
  };

  // Stubs, abstract support
  // -----------------------

  var requiredProperty = function() { throw 'Implement me'; }.property();

  var notImplemented = function() { throw 'Implement me'; };

  // Types
  // -----
  //
  // Every node in an advanced filter has a type.

  var TYPE_BOOLEAN = Boolean;
  var TYPE_STRING = String;
  var TYPE_NUMBER = Number;
  var TYPE_FLOAT = 'FLOAT';
  var TYPE_INTEGER = 'INTEGER';
  var TYPE_DATETIME = Date;
  var TYPE_DATE = 'DATE';
  var TYPE_NULL = 'NULL';
  var TYPE_ANY = 'ANY';
  var TYPE_GUID = 'GUID';
  var TYPE_AGGREGATE_1_TO_N = 'AGGREGATE_1_TO_N';

  // FilterNode
  // ----------
  //
  // Base class for all advanced filter nodes
  var FilterNode = Em.Object.extend(Em.Copyable, {

    // Each kind of filter has a _constant_ name, used during serialization and deserialization.
    name: requiredProperty,

    // Each kind of filter has a type, used to determine compatibility with other kinds of nodes.
    type: requiredProperty,

    // Each filter has zero or more parameters, which uniquely describe it. The params array is often a writable
    // property, proxying other properties in the filter. This mechanism is used by the default copy implementation.
    params: Em.A(),

    // A filter is valid, or not.
    isValid: requiredProperty,

    // A filter is complete, or not.
    isComplete: Em.computed.oneWay('isValid'),

    // Copyable
    copy: function() {
      var copied = this.get('copyPrototype').create();

      copied.set('params', this.get('params').map(function(param) { return Em.copy(param); }));

      return copied;
    },

    copyPrototype: requiredProperty,

    // Returns an optimized copy of this.
    optimize: function() {
      return this.copy();
    },

    // Returns a copy of this containing only valid nodes.
    validate: function() {
      if (!this.get('isValid')) {
        return undefined;
      } else {
        return this;
      }
    },

    filterValid: function(params) {
      return params.filter(function(param) {
        return 'instance' === Em.typeOf(param) ? param.get('isValid') : true;
      }, this)
    },

    // Returns a string representation of this.
    toString: function(self) {
      return (self || this).get('asString');
    },

    // @each as a property dependency makes this implementation very expensive. Subclasses should declare
    // more specific dependencies for better performance.
    asString: function() {
      return this.makeString(this.get('name'), this.get('isValid'), this.get('params'));
    }.property('name', 'isValid', 'params.@each.asString'),

    makeString: function(name, isValid, params) {
      if (!isValid) {
        return undefined;
      }

      var validParams = this.filterValid(params);
      return name + '(' + validParams.map(function(param) { return param.get('asString'); }).join(', ') + ')';
    },

    asDebugString: function() {
      return this.makeDebugString(this.get('asString'), this.get('name'), this.get('params'));
    }.property('asString', 'name', 'params.@each.asDebugString'),

    makeDebugString: function(asString, name, params) {
      if (asString) {
        return asString;
      }

      var invalidString = 'INVALID()';

      return name + '(' + params.map(function(param) {
        return FilterNode.detectInstance(param) ? param.get('asDebugString') : invalidString;
      }).join(', ') + ')';
    },

    // Returns a representation of this that conforms to the standard toJSON() interface.
    toJSON: function() {
      return this.transformToJSON(this.get('name'), this.get('type'), this.get('params'));
    },

    transformToJSON: function(name, type, params) {
      if (!this.get('isValid')) {
        return undefined;
      }

      var operand = {
        name: name,
        type: type,
        params: this.filterValid(params).map(function(param) {
          if (null === param) {
            return param;
          } else if (param.toJSON) {
            return param.toJSON();
          } else return param;
        }, this)
      };

      return operand;
    },

    // Walk the filter heirarchy
    depthFirstWalk: function(visitor, accumulator, depth) {
      if (Em.isNone(depth)) {
        depth = 0;
      }

      this.get('params').forEach(function(param) {
        if (FilterNode.detectInstance(param)) {
          param.depthFirstWalk(visitor, accumulator, depth + 1);
        }
      });

      return visitor(this, accumulator, depth);
    }
  });

  // Conjunctions
  // ------------
  //
  // Conjunctions are typically binary ops ("a AND b"), but we allow conjunctions to have an arbitrary number
  // of parameters, in order to more closely match both the UI representation and middle tier representations,
  // which combine the notions of conjunction and grouping.
  var ConjunctionFilter = FilterNode.extend({

    // As logical operators, conjunctions are of type boolean.
    type: TYPE_BOOLEAN,

    // Params is just a read / write alias for the operands array
    params: Em.computed.alias('operands'),

    // Every conjunction has it's own array of operands.
    operands: function() { return Em.A(); }.property(),

    // Marker, for handlebars.
    isConjunctionFilter: true,

    // A conjunction is valid if each of its operands is valid.
    // Validation fails for empty AND() or AND(OR())
    isValid: function() {
      var operands = this.get('operands');
      if (operands.length) {
        return this.get('operands').some(function(operand) {
          return operand.get('isValid');
        });
      } else {
        return false;
      }
    }.property('operands.@each.isValid'),

    // A conjunction is valid if each of its operands is complete which is bound to isValid property.
    // Validation fails for empty AND() or AND(OR())
    isComplete: function() {
      var operands = this.get('operands');
      if (operands.length) {
        return this.get('operands').every(function(operand) {
          return operand.get('isComplete');
        });
      } else {
        return false;
      }
    }.property('operands.@each.isComplete'),

    asString: function() {
      return this.makeString(this.get('name'), this.get('isValid'), this.get('operands'));
    }.property('name', 'isValid', 'operands.@each.asString'),

    asDebugString: function() {
      return this.makeDebugString(undefined, this.get('name'), this.get('params'));
    }.property('name', 'params.@each.asDebugString'),

    // Returns a copy of this containing only valid nodes.
    validate: function() {
      if (!this.get('isValid')) {
        return undefined;
      }

      return this.get('copyPrototype').create({
        operands: this.filterValid(this.get('operands'))
      });
    },

    optimize: function() {
      var validOperands = this.get('operands').filterBy('isValid');
      if (validOperands.get('length') === 1) {
        return validOperands.objectAt(0).optimize();
      } else if (validOperands.get('length') === 0) {
        return null;
      }

      return this.get('copyPrototype').create({
        name: this.get('name'),
        operands: validOperands.map(function(operand) {
          var result = operand.optimize();
          return result ? result.copy() : null;
        })
      });
    }
  });

  var AndFilter = ConjunctionFilter.extend({
    name: 'AND',
    label: 'shared.and'.tr(),
    copyPrototype: function() { return AndFilter; }.property(),
    isAndFilter: true
  });

  var OrFilter = ConjunctionFilter.extend({
    name: 'OR',
    label: 'shared.or'.tr(),
    copyPrototype: function() { return OrFilter; }.property(),
    isOrFilter: true
  });

  // Constants
  // ---------
  //
  // Constants are always valid.
  var ConstantNode = FilterNode.extend({
    isValid: true,

    asString: function() {
      // All parameters are constant.
      return this.makeString(this.get('name'), this.get('isValid'), Em.A());
    }.property()
  });

  // AnyAttr represents "any searchable attribute". It's used to represent full text search and the like.
  var AnyAttrConstant = ConstantNode.extend({
    name: 'ANY_ATTR',
    type: TYPE_ANY,
    copyPrototype: function() { return AnyAttrConstant; }.property()
  });

  // Null has its own filter type, for those cases where null is valid (usually it is not).
  var NullConstant = ConstantNode.extend({
    name: 'NULL',
    type: TYPE_NULL,
    copyPrototype: function() { return NullConstant; }.property()
  });

  // Empty string is a special constant, for when an empty string is to be used on purpose. A StringLiteral is not
  // valid when it contains an empty string, but EmptyStringConstant, like all constants, is valid.
  var EmptyStringConstant = ConstantNode.extend({
    name: 'EMPTY_STRING',
    type: TYPE_STRING,
    copyPrototype: function() { return EmptyStringConstant; }.property()
  });

  // Value Wrapper
  // -------------
  //
  // Wraps some kind of variable or constant.
  var ValueNode = FilterNode.extend({
    params: Em.computed('value', {
      set: function(key, value) {
        this.set('value', value.objectAt(0));
      },
      get: function() {
        return Em.A([this.get('value')]);
      }
    }),

    // The wrapped value.
    value: null,

    asString: function() {
      return this.makeValueString(this.get('name'), this.get('isValid'), this.get('value'));
    }.property('name', 'isValid', 'value'),

    asDebugString: function() {
      return this.makeValueString(this.get('name'), true, this.get('value'));
    }.property('name', 'value'),

    makeValueString: function(name, isValid, value) {
      if (!isValid) {
        return undefined;
      }

      var serializedValue = null;

      if (value instanceof Date) {
        serializedValue = dateToString(value);
      } else if ('number' === typeof(value)) {
        serializedValue = value.toString(10);
      } else if ('string' === typeof(value)) {
        serializedValue = stringToString(value);
      } else if (null === value) {
        serializedValue = 'null';
      } else if (undefined === value) {
        serializedValue = 'undefined';
      } else {
        throw ["Don't know how to serialize this kind of value", this, value];
      }

      return this.get('name') + '(' + serializedValue + ')';
    },

    copy: function() {
      return this.get('copyPrototype').create({
        name: this.get('name'),
        type: this.get('type'),
        value: this.get('value')
      });
    },

    transformToJSON: function(name, type, params) {
      if (!this.get('isValid')) {
        return undefined;
      }

      return {
        name: name,
        type: type,
        params: params
      };
    }
  });

  // Literals
  // --------
  //
  // A Literal wraps a value.
  var LiteralNode = ValueNode.extend({
  });

  var StringLiteral = LiteralNode.extend({
    name: 'STRING',
    type: TYPE_STRING,

    isValid: function() {
      return stringIsValid(this.get('value'));
    }.property('value'),

    valueAsJSON: function() { return String(this.get('value')); }.property('value'),

    copyPrototype: function() { return StringLiteral; }.property()
  });

  var NumberLiteral = LiteralNode.extend({
    name: 'NUMBER',
    type: TYPE_NUMBER,

    valueAsJSON: function() { return Number(this.get('value')); }.property('value'),

    isValid: function() {
      return numberIsValid(this.get('value'));
    }.property('value'),

    copyPrototype: function() { return NumberLiteral; }.property()
  });

  var IntegerLiteral = LiteralNode.extend({
    name: 'INTEGER',
    type: TYPE_INTEGER,

    valueAsJSON: function() { return Number(this.get('value')); }.property('value'),

    isValid: function() {
      return integerIsValid(this.get('value'));
    }.property('value'),

    copyPrototype: function() { return NumberLiteral; }.property()
  });

  var DateLiteral = LiteralNode.extend({
    name: 'DATE',
    type: TYPE_DATE,

    valueAsJSON: function() { return this.get('value').toISOString(); }.property('value'),

    isValid: function() {
      return dateIsValid(this.get('value'));
    }.property('value'),

    copyPrototype: function() { return DateLiteral; }.property()
  });

  var DateTimeLiteral = LiteralNode.extend({
    name: 'DATETIME',
    type: TYPE_DATETIME,

    valueAsJSON: function() { return this.get('value').toISOString(); }.property('value'),

    isValid: function() {
      return dateIsValid(this.get('value'));
    }.property('value'),

    copyPrototype: function() { return DateTimeLiteral; }.property()
  });

  var GuidLiteral = LiteralNode.extend({
    name: 'GUID',
    type: TYPE_GUID,

    valueAsJSON: function() { return String(this.get('value')); }.property('value'),

    isValid: function() {
      return guidIsValid(this.get('value'));
    }.property('value'),

    copyPrototype: function() { return GuidLiteral; }.property()
  });

  // Attributes
  // ----------
  //
  // An Attr node wraps a model attribute.
  var Attr = ValueNode.extend({
    name: 'ATTR',

    // The name of the attribute is stored in the value.
    value: null,

    // The type of the Attr is the type of the wrapped attribute.
    type: null,

    isValid: function() {
      return !Em.isEmpty(this.get('value')) && !Em.isNone(this.get('type'));
    }.property('type', 'value'),

    toJSON: function() {
      return this._super();
    },

    copyPrototype: function() { return Attr; }.property()
  });

  // Filter Ops
  // ----------
  //
  // A filter op provides hooks for named parameters. Most ops extend this. A filter op accepts some range of types
  // for its operands, and may return a transformed type. All ops take at least one parameter.
  var OpNode = FilterNode.extend({

    // All implemented ops return a boolean. As more interested ops are implemented, this property will be pushed
    // downwards in the inheritance hierarchy.
    type: TYPE_BOOLEAN,

    // An array of the types accepted by this Op.
    acceptableTypes: requiredProperty,

    // An Op is valid if the type of all of its params match, and are members of acceptableTypes. As more interesting
    // ops are added, this will likely change.
    isValid: function() {
      var params = this.get('params');
      var firstOperand = params.objectAt(0);

      // No Op accepts naked values, all ops have an lval (so why isn't it declared???).
      if (!FilterNode.detectInstance(firstOperand)) {
        return false;
      }

      var firstOperandType = firstOperand.get('type');
      return this.get('acceptableTypes').contains(firstOperandType) && params.every(function(operand) {
        return FilterNode.detectInstance(operand) &&
          operand.get('isValid') &&
          (operand.get('type') === firstOperandType);
      });
    }.property('params.@each.isValid', 'acceptableTypes.[]'),
    // A certain word may sometimes need to be appended to the end of the filter line to better portray the functionality of the operator
    filterLineAppendWord: null
  });

  //Quaternary Op
  //----------
  //Used for operands that takes 4 parameters.
  var QuaternaryOp = OpNode.extend({
    lval: null,
    rval1: null,
    rval2: null,
    rval3: null,

    params: Em.computed('lval', 'rval1', 'rval2', 'rval3', {
      set: function(key, value) {
        var lval, rval1, rval2, rval3;
        lval = value.objectAt(0);
        rval1 = value.objectAt(1);
        rval2 = value.objectAt(2);
        rval3 = value.objectAt(3);

        this.setProperties({
          lval: lval,
          rval1: rval1,
          rval2: rval2,
          rval3: rval3
        });
        return Em.A([lval, rval1, rval2, rval3]);
      },
      get: function() {
        var lval, rval1, rval2, rval3;
        lval = this.get('lval');
        rval1 = this.get('rval1');
        rval2 = this.get('rval2');
        rval3 = this.get('rval3')
        return Em.A([lval, rval1, rval2, rval3]);
      }
    }),

    transformToJSON: function(name, type, params) {
      if (!this.get('isValid')) {
        return undefined;
      }

      //FIXME: There is some recursive trickiness going on with the binaryop which puts the param values in
      //params[i].params[0]. Need to replicate that here to maintain consistency.
      return {
        name: name,
        params: params.map(function(item){return {params: [item.value]}})
      };
    },

    asString: function () {
      return this.makeString(this.get('name'), this.get('isValid'), Em.A([this.get('lval'), this.get('rval1'), this.get('rval2'), this.get('rval3')]));
    }.property('isValid', 'lval.asString', 'rval1.asString', 'rval2.asString', 'rval3.asString'),

    isValid: function () {
      return !Em.isEmpty(this.get('rval1.value')) && !Em.isEmpty(this.get('rval2.value')) && !Em.isEmpty(this.get('rval3.value'));
    }.property('rval1.value', 'rval2.value', 'rval3.value'),

    // An array of acceptable types for the rval, often dependent on the type of the lval.
    acceptableRvalTypes: requiredProperty
  });

  // Between Op
  // ----------
  // Op that accept three string operands and an operator.
  var BetweenOp = QuaternaryOp.extend({
    name: 'BETWEEN',
    acceptableTypes: Em.A([TYPE_STRING]),
    acceptableRvalTypes: function() {
      return Em.A([this.get('lval.type'), TYPE_NULL]);
    }.property('lval.type'),
    copyPrototype: function() { return BetweenOp; }.property()
  });

  //Trinary Op
  //----------
  //Used for operands that takes 3 parameters. The type of the lval and the "to, from" must be compatible.
  var TrinaryOp = OpNode.extend({
    lval: null,
    rval1: null,
    rval2: null,

    params: Em.computed('lval', 'rval1', 'rval2', {
      set: function (key, value) {
        var lval, rval1, rval2;
        lval = value.objectAt(0);
        rval1 = value.objectAt(1);
        rval2 = value.objectAt(2);

        this.setProperties({
          lval: lval,
          rval1: rval1,
          rval2: rval2
        });
        return Em.A([lval, rval1, rval2]);
      },
      get: function() {
        var lval, rval1, rval2;
        lval = this.get('lval');
        rval1 = this.get('rval1');
        rval2 = this.get('rval2');
        return Em.A([lval, rval1, rval2]);
      }
    }),

    asString: function() {
      return this.makeString(this.get('name'), this.get('isValid'), Em.A([this.get('lval'), this.get('rval1'), this.get('rval2')]));
    }.property('isValid', 'lval.asString', 'rval1.asString', 'rval2.asString'),

    isValid: function() { throw 'required'; }.property(),
    acceptableRvalTypes: function() { throw 'required'; }.property()     // An array of acceptable types for the rval, often dependent on the type of the lval.
  });

  // Between Op
  // ----------
  // Op that accept two dates as values.
  var BetweenDatesOp = TrinaryOp.extend({
    name: 'BETWEEN_DATE',
    acceptableTypes: Em.A([TYPE_DATE,TYPE_DATETIME]),

    acceptableRvalTypes: function() {
      return Em.A([this.get('lval.type'), TYPE_NULL]);
    }.property('lval.type'),

    isValid: function() {
      return dateIsValid(this.get('rval1.value')) && dateIsValid(this.get('rval2.value'));
    }.property('rval1.value', 'rval2.value'),

    copyPrototype: function() { return BetweenDatesOp; }.property()
  });

  // IPv4 Range Ops
  // ----------
  // Ops that accept two IPv4 addresses as values
  var InRangeOp = TrinaryOp.extend({
    name: 'IN_RANGE',
    acceptableTypes: Em.A([TYPE_STRING]),
    acceptableRvalTypes: function() {
      return Em.A([this.get('lval.type'), TYPE_NULL]);
    }.property('lval.type'),
    isValid: function() {
      return !Em.isEmpty(this.get('rval1.value')) && !Em.isEmpty(this.get('rval2.value'));
    }.property('rval1.value', 'rval2.value'),
    copyPrototype: function() { return InRangeOp; }.property()
  });

  var NotInRangeOp = InRangeOp.extend({
    name: 'NOT_IN_RANGE',
    copyPrototype: function() { return NotInRangeOp; }.property()
  });

  // Binary Ops
  // ----------
  //
  // Binary ops are ops that take two parameters. The type of the lval and the rval must be compatible.
  var BinaryOp = OpNode.extend({
    lval: null,
    rval: null,

    params: Em.computed('lval', 'rval', {
      set: function(key, value) {
        var lval, rval;
        lval = value.objectAt(0);
        rval = value.objectAt(1);

        this.setProperties({
          lval: lval,
          rval: rval
        });
        return Em.A([lval, rval]);
      },
      get: function() {
        var lval, rval;
        lval = this.get('lval');
        rval = this.get('rval');
        return Em.A([lval, rval]);
      }
    }),

    asString: function() {
      return this.makeString(this.get('name'), this.get('isValid'), Em.A([this.get('lval'), this.get('rval')]));
    }.property('isValid', 'lval.asString', 'rval.asString'),

    // An array of acceptable types for the from, rval2, often dependent on the type of the lval.
    acceptableRvalTypes: requiredProperty
  });

  // String Ops
  // ----------
  //
  // Ops that accept strings.
  var StringOp = BinaryOp.extend({
    acceptableTypes: Em.A([TYPE_STRING, TYPE_ANY]),
    acceptableRvalTypes: Em.A([TYPE_STRING]),

    isValid: function() {
      return this.get('acceptableTypes').contains(this.get('lval.type')) &&
        this.get('acceptableRvalTypes').contains(this.get('rval.type')) &&
        this.get('lval.isValid') && this.get('rval.isValid');
    }.property('acceptableTypes.[]', 'acceptableRvalTypes.[]', 'lval.type', 'lval.isValid', 'rval.type', 'rval.isValid')
  });

  var StartsWithOp = StringOp.extend({
    isStartsWithOp: true,
    name: 'STARTS_WITH',
    copyPrototype: function() { return StartsWithOp; }.property()
  });

  var NotStartsWithOp = StringOp.extend({
    isStartsWithOp: true,
    name: 'NOT_STARTS_WITH',
    copyPrototype: function() { return NotStartsWithOp; }.property()
  });

  var EndsWithOp = StringOp.extend({
    isEndsWithOp: true,
    name: 'ENDS_WITH',
    copyPrototype: function() { return EndsWithOp; }.property()
  });

  var NotEndsWithOp = StringOp.extend({
    isEndsWithOp: true,
    name: 'NOT_ENDS_WITH',
    copyPrototype: function() { return NotEndsWithOp; }.property()
  });

  var SubstringOfOp = StringOp.extend({
    isSubstringOfOp: true,
    name: 'SUBSTRING_OF',
    copyPrototype: function() { return SubstringOfOp; }.property()
  });

  var NotSubstringOfOp = StringOp.extend({
    isNotSubstringOfOp: true,
    name: 'NOT_SUBSTRING_OF',
    copyPrototype: function() { return NotSubstringOfOp; }.property()
  });

  // DateTime Ops
  // --------
  //
  // Ops that accept datetime.
  var DateOp = StringOp.extend({
    name: 'DATE',
    isValid: function() {
      return dateIsValid(new Date(this.get('rval.value')));
    }.property('rval.value'),
    acceptableTypes: Em.A([TYPE_DATETIME]),
    copyPrototype: function() { return DateOp; }.property()
  });

  var BeforeDateOp = DateOp.extend({
    name: 'BEFORE_DATE',
    copyPrototype: function() { return BeforeDateOp; }.property()
  });

  var BeforeDateTimeOp = DateOp.extend({
    name: 'BEFORE_DATE_TIME',
    copyPrototype: function() { return BeforeDateTimeOp; }.property()
  });

  var AfterDateOp = DateOp.extend({
    name: 'AFTER_DATE',
    copyPrototype: function() { return AfterDateOp; }.property()
  });

  var AfterDateTimeOp = DateOp.extend({
    name: 'AFTER_DATE_TIME',
    copyPrototype: function() { return AfterDateTimeOp; }.property()
  });

  // Date Ops
  // --------
  //
  // Ops that accept date.
  var DayOfOp = BinaryOp.extend({
    name: 'DAY_OF',
    copyPrototype: function() { return DayOfOp; }.property(),
    acceptableRvalTypes: Em.A([TYPE_DATE])
  });

  // Aggregate Ops
  // ----------
  //
  // Ops that accept Aggregate OneToMany data.
  var AggregateOp = BinaryOp.extend({
    acceptableTypes: Em.A([TYPE_BOOLEAN]),

    isValid: function() {
      return this.get('lval.isValid') &&
        this.get('acceptableTypes').contains(this.get('rval.type')) &&
        this.get('rval.isValid');
    }.property('lval.isValid', 'acceptableTypes.[]', 'rval.type', 'rval.isValid')
  });

  var AnyAggregateOp = AggregateOp.extend({
    isAnyAggregateOp: true,
    name: 'ANY_AGGREGATE',
    copyPrototype: function() { return AnyAggregateOp; }.property()
  });

  var AllAggregateOp = AggregateOp.extend({
    isAllAggregateOp: true,
    name: 'ALL_AGGREGATE',
    copyPrototype: function() { return AllAggregateOp; }.property()
  });

  // Comparison Ops
  // --------------
  var ComparisonOp = BinaryOp.extend({
    acceptableTypes: Em.A([
      TYPE_BOOLEAN,
      TYPE_STRING,
      TYPE_NUMBER,
      TYPE_FLOAT,
      TYPE_INTEGER,
      TYPE_DATE,
      TYPE_DATETIME,
      TYPE_NULL,
      TYPE_ANY,
      TYPE_GUID
    ]),

    // Unlike most ops, equals accepts NULL as a type for any parameter
    isValid: function() {
      var params = this.get('params');
      var firstOperand = params.objectAt(0);

      // No Op accepts naked values
      if (!FilterNode.detectInstance(firstOperand)) {
        return false;
      }

      var firstOperandType = firstOperand.get('type');
      return this.get('acceptableTypes').contains(firstOperandType) && params.every(function(operand) {
        if (!FilterNode.detectInstance(operand)) {
          return false;
        }

        var type = operand.get('type') === TYPE_DATE ? TYPE_DATETIME : operand.get('type');

        return FilterNode.detectInstance(operand) &&
          operand.get('isValid') &&
          ((type === firstOperandType) || (type === TYPE_NULL));
      });
    }.property('params.@each.isValid', 'acceptableTypes.[]'),

    acceptableRvalTypes: function() {
      var type = this.get('lval.type');
      return Em.A([(type === TYPE_DATETIME ? TYPE_DATE : type), TYPE_NULL]);
    }.property('lval.type')
  });

  var EqualsOp = ComparisonOp.extend({
    name: 'EQUALS',
    copyPrototype: function() { return EqualsOp; }.property()
  });

  var NotEqualsOp = ComparisonOp.extend({
    name: 'NOT_EQUALS',
    copyPrototype: function() { return NotEqualsOp; }.property()
  });

  // Ordered Comparison Ops
  // ----------------------
  var OrderedComparisonOp = BinaryOp.extend({
    acceptableTypes: Em.A([
      TYPE_STRING,
      TYPE_NUMBER,
      TYPE_FLOAT,
      TYPE_INTEGER,
      TYPE_DATE,
      TYPE_DATETIME
    ]),

    acceptableRvalTypes: function() {
      return Em.A([this.get('lval.type')]);
    }.property('lval.type')
  });

  var GreaterThanOp = OrderedComparisonOp.extend({
    name: 'GREATER_THAN',
    copyPrototype: function() { return GreaterThanOp; }.property()
  });

  var LessThanOp = OrderedComparisonOp.extend({
    name: 'LESS_THAN',
    copyPrototype: function() { return LessThanOp; }.property()
  });

  var GreaterEqualsOp = OrderedComparisonOp.extend({
    name: 'GREATER_EQUALS',
    copyPrototype: function() { return GreaterEqualsOp; }.property()
  });

  var LessEqualsOp = OrderedComparisonOp.extend({
    name: 'LESS_EQUALS',
    copyPrototype: function() { return LessEqualsOp; }.property()
  });

  // LastNOps
  // --------
  //
  // DATE was (NOT) within last N UNITS
  var LastNOpBase = TrinaryOp.extend({
    acceptableTypes: Em.A([TYPE_DATE,TYPE_DATETIME]),

    isValid: function() {
      return !Em.isEmpty(this.get('rval1.value')) && !Em.isEmpty(this.get('rval2.value'));
    }.property('rval1.value', 'rval2', 'rval2.value'),

    acceptableRvalTypes: Em.A([TYPE_INTEGER]),
    acceptableRval2Types: Em.A([TYPE_STRING]),
    copyPrototype: function() { return LastNOpBase; }.property()
  });

  var LastNOp = LastNOpBase.extend({
    name: 'WITHIN',
    copyPrototype: function() { return LastNOp; }.property()
  });

  var NotLastNOp = LastNOpBase.extend({
    name: 'NOT_WITHIN',
    copyPrototype: function() { return NotLastNOp; }.property()
  });

  var IsExactly = LastNOpBase.extend({
    name: 'IS_EXACTLY',
    copyPrototype: function() { return IsExactly; }.property()
  });

  var InNext = LastNOpBase.extend({
    name: 'IN_NEXT',
    copyPrototype: function() { return InNext; }.property()
  });

  var NotInNext = LastNOpBase.extend({
    name: 'NOT_IN_NEXT',
    copyPrototype: function() { return NotInNext; }.property()
  });

  // Unary Ops
  // ---------
  //
  // Ops that accept a single parameter.
  var UnaryOp = OpNode.extend({
    lval: null,

    params: Em.computed('lval', {
      set: function(key, value) {
        var lval;
        lval = value.objectAt(0);
        this.set('lval', lval);
        return Em.A([lval]);
      },
      get: function() {
        var lval;
        lval = this.get('lval');
        return Em.A([lval]);
      }
    }),

    isValid: function(){
      return  this.get('params').every(function(operand) {
        return FilterNode.detectInstance(operand) &&
          operand.get('isValid');
      });
    }.property(),

    asString: function() {
      return this.makeString(this.get('name'), this.get('isValid'), Em.A([this.get('lval')]));
    }.property('isValid', 'lval.asString')
  });

  // The WrapOp is a special "pass through" op which exists to facilitate editor support. It can be thought of
  // as a less crappy version of the previous incarnation's FilterOpWrapper.
  var WrapOp = UnaryOp.extend({
    name: 'WRAP_OP',

    // All types.
    acceptableTypes: Em.A([
      TYPE_BOOLEAN,
      TYPE_STRING,
      TYPE_NUMBER,
      TYPE_FLOAT,
      TYPE_INTEGER,
      TYPE_DATETIME,
      TYPE_DATE,
      TYPE_NULL,
      TYPE_ANY,
      TYPE_GUID
    ]),

    copyPrototype: function() { return WrapOp; }.property(),

    isValid: function() {
      var lval = this.get('lval');
      return OpNode.detectInstance(lval) && lval.get('isValid');
    }.property('lval.isValid'),

    type: function() {
      return this.get('lval.type');
    }.property('lval.type'),

    // A WrapOp optimizes out.
    optimize: function() {
      var lval = this.get('lval');
      return lval ? lval.copy() : undefined;
    },

    // A WrapOp does not appear in the JSON.
    transformToJSON: function(name, type, params) {
      if (!this.get('isValid')) {
        return undefined;
      }

      return this.get('lval').toJSON();
    }
  });

  var TrueOp = UnaryOp.extend({
    name: 'TRUE_OP',
    copyPrototype:  function() { return TrueOp; }.property(),
    value: true
  });

  var FalseOp = UnaryOp.extend({
    name: 'FALSE_OP',
    copyPrototype:  function() { return FalseOp; }.property(),
    value: false
  });

  var IsEmptyOp = UnaryOp.extend({
    name: 'IS_EMPTY',
    copyPrototype:  function() { return IsEmptyOp; }.property(),
    value: 1
  });

  var IsNotEmptyOp = UnaryOp.extend({
    name: 'IS_NOT_EMPTY',
    copyPrototype:  function() { return IsNotEmptyOp; }.property(),
    value: 1
  });

  var IsNAOp = UnaryOp.extend({
    name: 'IS_NA',
    copyPrototype:  function() { return IsNAOp; }.property(),
    value: true
  });

  var IsNotNAOp = UnaryOp.extend({
    name: 'IS_NOT_NA',
    copyPrototype:  function() { return IsNotNAOp; }.property(),
    value: true
  });

  // Exports
  // -------

  return {
    TYPE_BOOLEAN: TYPE_BOOLEAN,
    TYPE_STRING: TYPE_STRING,
    TYPE_NUMBER: TYPE_NUMBER,
    TYPE_FLOAT: TYPE_FLOAT,
    TYPE_INTEGER: TYPE_INTEGER,
    TYPE_DATE: TYPE_DATE,
    TYPE_DATETIME: TYPE_DATETIME,
    TYPE_NULL: TYPE_NULL,
    TYPE_ANY: TYPE_ANY,
    TYPE_GUID: TYPE_GUID,
    TYPE_AGGREGATE_1_TO_N: TYPE_AGGREGATE_1_TO_N,

    FilterNode: FilterNode,

    ConjunctionFilter: ConjunctionFilter,
    AndFilter: AndFilter,
    OrFilter: OrFilter,

    ValueNode: ValueNode,

    ConstantNode: ConstantNode,
    AnyAttrConstant: AnyAttrConstant,
    NullConstant: NullConstant,
    EmptyStringConstant: EmptyStringConstant,

    LiteralNode: LiteralNode,
    StringLiteral: StringLiteral,
    NumberLiteral: NumberLiteral,
    IntegerLiteral: IntegerLiteral,
    DateLiteral: DateLiteral,
    DateTimeLiteral: DateTimeLiteral,
    GuidLiteral: GuidLiteral,

    Attr: Attr,

    OpNode: OpNode,

    BinaryOp: BinaryOp,

    StringOp: StringOp,
    StartsWithOp: StartsWithOp,
    NotStartsWithOp: NotStartsWithOp,
    EndsWithOp: EndsWithOp,
    NotEndsWithOp: NotEndsWithOp,
    SubstringOfOp: SubstringOfOp,
    NotSubstringOfOp: NotSubstringOfOp,
    DateOp: DateOp,
    BeforeDateOp: BeforeDateOp,
    BeforeDateTimeOp: BeforeDateTimeOp,
    AfterDateOp: AfterDateOp,
    AfterDateTimeOp: AfterDateTimeOp,

    UnaryOp: UnaryOp,
    WrapOp: WrapOp,
    IsEmptyOp: IsEmptyOp,
    IsNotEmptyOp: IsNotEmptyOp,
    IsNAOp: IsNAOp,
    IsNotNAOp: IsNotNAOp,
    TrueOp: TrueOp,
    FalseOp: FalseOp,

    AggregateOp: AggregateOp,
    AnyAggregateOp: AnyAggregateOp,
    AllAggregateOp: AllAggregateOp,

    TrinaryOp: TrinaryOp,
    BetweenDatesOp: BetweenDatesOp,
    InRangeOp: InRangeOp,
    NotInRangeOp: NotInRangeOp,

    QuaternaryOp: QuaternaryOp,
    BetweenOp: BetweenOp,

    ComparisonOp: ComparisonOp,
    EqualsOp: EqualsOp,
    NotEqualsOp: NotEqualsOp,

    OrderComparisonOp: OrderedComparisonOp,
    GreaterEqualsOp: GreaterEqualsOp,
    LessEqualsOp: LessEqualsOp,
    GreaterThanOp: GreaterThanOp,
    LessThanOp: LessThanOp,

    LastNOpBase: LastNOpBase,
    LastNOp: LastNOp,
    NotLastNOp: NotLastNOp,
    IsExactly: IsExactly,
    InNext: InNext,
    NotInNext: NotInNext,

    DayOfOp: DayOfOp
  };
});
