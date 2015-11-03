define([
  'jquery',
  'text!./source_files.txt'
], function(
  $,
  sourceFilesSerialized
) {
  'use strict';

  // For invocation from the Chrome console. Prints strings / text node count for all specified html and handlebars files.
  // 1) at the Terminal: cd client; find . -name '*.handlebars' -or -name '*.html' > packages/analysis/source_files.txt
  // 2) at the Chrome console: require(['packages/analysis/analyze'], function(analyser) { analyser.run() });

  var h1 = function(str) {
    console.log('================================================================================');
    console.log(str.toUpperCase());
    console.log('================================================================================');
  };

  var br = function() { console.log(''); };

  return {
    run: function() {
      var html = [];
      var packages = {};
      var sourceFiles = sourceFilesSerialized.split(/\s+/);

      sourceFiles.forEach(function(path) {
        if (path.match(/\.html$/)) {
          var pkg = 'html';
        } else {
          var matches = path.match(/packages\/([^\/]+)/);
          if (matches) {
            var pkg = matches[1];
          } else {
            var pkg = 'app';
          }
        }
        packages[pkg] = packages[pkg] || [];
        packages[pkg].push(path);
      });

      var jobQueue = [];
      var completedJobs = [];

      for (var pkg in packages) {
        if (packages.hasOwnProperty(pkg)) {
          jobQueue.push({ type: 'package', name: pkg });
          packages[pkg].forEach(function(path) {
            jobQueue.push({ type: 'file', path: path });
          });
        }
      }

      var analyzeTemplateOrHtmlFile = function(job, content) {
        // Remove Handlebars content
        content = content.replace(/{{[^}]+}}/gm, '');
        var dom = $(content);

        job.wordCount = dom.text().split(/\s+/gm).length;

        // jQuery 1.8+: use addBack() instead of andSelf()
        var textNodes = dom.find(':not(iframe)').andSelf().contents().filter(function() {
          return this.nodeType == 3 && this.textContent.replace(/\s+/gm, '') !== '';
        });
        job.textNodeCount = textNodes.length;
      };

      var printResults = function() {
        var totalWordCount = 0;
        var totalTextNodeCount = 0;

        var packageWordCount = 0;
        var packageTextNodeCount = 0;

        var currentPackage = null;

        $.each(completedJobs, function(idx, job) {
          switch (job.type) {
          case 'package':
            if (currentPackage) {
              br();
              console.log('Package total: ' + packageWordCount + ' words, ' + packageTextNodeCount + ' text nodes');
            }

            h1(job.name);

            currentPackage = job.name;
            packageWordCount = packageTextNodeCount = 0;
            break;
          case 'file':
            var wordCount = job.wordCount;
            var textNodeCount = job.textNodeCount;

            console.log(job.path + ': ' + wordCount + ' words, ' + textNodeCount + ' text nodes');

            totalWordCount += wordCount;
            totalTextNodeCount += textNodeCount;

            packageWordCount += wordCount;
            packageTextNodeCount += textNodeCount;
            break;
          }
        });

        h1('Total');
        console.log(totalWordCount + ' words, ' + totalTextNodeCount + ' text nodes');
      };

      var nextJob = function() {
        var job = jobQueue.shift();

        if (!job) {
          printResults();
          return;
        }

        switch (job.type) {
        case 'package':
          completedJobs.push(job);
          setTimeout(nextJob, 0);
          break;
        case 'file':
          require(['text!' + job.path], function(content) {
            analyzeTemplateOrHtmlFile(job, content);
            completedJobs.push(job);
            setTimeout(nextJob, 0);
          });
          break;
        }
      };

      nextJob();
    }
  };
});
