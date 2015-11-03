'use strict';

test('setPath', function() {
  var obj = {
    foo: {
      bar: null
    },
    baz: null
  };

  localeUtil.setPath(obj, 'foo.bar', true);
  strictEqual(true, obj.foo.bar, 'Set foo.bar');

  localeUtil.setPath(obj, 'baz', true);
  strictEqual(true, obj.baz, 'Set baz');
});

test('TAG_OPEN', function() {
  var tests = [
    ['<strong>', true, 'Simple tag'],
    ['<div class="foo">', true, 'Tag with attrs double quoted'],
    ['<div class=\'foo\'>', true, 'Tag with attrs single quoted'],
    ['<option selected>', true, 'Tag with unquoted attr'],
    ['</div>', false, 'Closing tag'],
    ['<<div>', false, 'Double-opened tag'],
    ['<div<>', false, 'Stray <']
  ];

  tests.forEach(function(spec, idx) {
    var match = spec[0].match(localeUtil.TAG_OPEN);
    ok(spec[1] ? match instanceof Array : (match === null), spec[2]);
  });
});

test('TAG_CLOSE', function() {
  var tests = [
    ['</strong>', true, 'Simple tag'],
    ['</div class="foo">', false, 'Tag with attrs double quoted'],
    ['</div class=\'foo\'>', false, 'Tag with attrs single quoted'],
    ['</option selected>', false, 'Tag with unquoted attr'],
    ['<</div>', false, 'Double-opened tag'],
    ['</div<>', false, 'Stray <']
  ];

  tests.forEach(function(spec, idx) {
    var match = spec[0].match(localeUtil.TAG_CLOSE);
    ok(spec[1] ? match instanceof Array : (match === null), spec[2]);
  });
});

test('validateHandlebars', function() {
  var tests = [
    ['', true, 'Empty string'],
    ['<b>foo</b>', true, 'Valid HTML with no Handlebars'],
    ['<b><em>foo</em></b>', true, 'Valid HTML with nested tags, no Handlebars'],
    ['<b><em>{{foo}}</em></b>', true, 'Valid HTML with nested tags and Handlebars'],
    ['<b><em>foo</b></em>', false, 'Invalid HTML with improper nesting'],
    ['foo</b>', false, 'Invalid HTML (close of un-opened)'],
    ['<b>foo', false, 'Invalid HTML (un-closed tag)'],
    ['{{{foo', false, 'Invalid Handlebars (unclosed triple)'],
    ['{{foo', false, 'Invalid Handlebars (unclosed)'],
    ['foo}}}', false, 'Invalid Handlebars (close of un-opened triple)'],
    ['foo}}', false, 'Invalid Handlebars (close of un-opened)'],
    ['<p>Use the predefined Hardware and Software reports to get started. You can edit these reports and save them to create your own custom reports.</p><p>For more information, see <a href="javascript:sendEmberAction(\'showIntro\')">Getting Started</a> or click <span class="btn-small btn-help inline-icon">?</span> to view the Help.</p>', true, 'Valid Handlebars that still fails']
  ];
  
  tests.forEach(function(spec, idx) {
    strictEqual(localeUtil.validateHandlebars(spec[0]), spec[1], spec[2]);
  });
});
