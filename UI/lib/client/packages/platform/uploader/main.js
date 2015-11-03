define([
  'ember',
  'jquery',
  'am-data',
  'logger'
], function(
  Em,
  $,
  AmData,
  logger
) {
    /**
     * Uploads file to server in chunks (currently 100 kB). Supports start, pause, resume, cancel.
     * 
     * Arguments:
     * 
     *   file:             File to be uploaded
     *   options:          Optional parameters to be appended to the url
     *   hdlrContext:      Context that will be returned to callback handlers.
     *   doneHdlr:         Callback handler for notification when the file upload is complete, with context and file
     *                     argument.
     *   errHandler:       Callback handler for notification of an error uploading the file, with context, file,
     *                     jqXHR, textStatus and errorThrown arguments.
     *   progressHdlr:     Callback handler for notification of progress, with context, file, bytesUploaded and
     *                     estimatedSecsLeft.  
     * 
     */
    function ChunkedFileUploader(file, options, hdlrContext, doneHdlr, errHandler, progressHdlr) {
      if (!this instanceof ChunkedFileUploader) {
        return new ChunkedFileUploader(file, options);
      }

      this.state = 'Not started';
      this.timeStarted = 0;
      this.file = file;

      this.options = $.extend({
        url: AmData.get('urlRoot') + '/api/content/upload/' + file.name
      }, options);

      this.hdlrContext = hdlrContext;
      this.doneHdlr = doneHdlr;
      this.errHandler = errHandler;
      this.progressHdlr = progressHdlr;

      this.chunkSize = (1024 * 100); // 100KB. May need to tweak or allow override?
      this.rangeStart = 0;
      this.rangeEnd = this.chunkSize;
      this.estimatedSecsLeft = Infinity; 

      if ('mozSlice' in this.file) {
        this.slice_method = 'mozSlice';
      }
      else if ('webkitSlice' in this.file) {
        this.slice_method = 'webkitSlice';
      }
      else {
        this.slice_method = 'slice';
      }

    }

    ChunkedFileUploader.prototype = {

      // Public Methods ____________________________________________________

      start: function() {
        this.state = 'In progress';  
        this.timeStarted = (new Date()).getTime();
        this._upload();
      },

      cancel: function() {
      	this.state = 'Cancelled'; 
        this.isCancelled = true;
        this.estimatedSecsLeft = Infinity; 
      },

      pause: function() {
        this.state = 'Paused'; 
        this.isPaused = true;
        this.estimatedSecsLeft = Infinity; 
      },

      resume: function() {
      	this.state = 'In progress';  
        this.isPaused = false;
        this._upload();
      },

      // Internal Methods __________________________________________________

      _upload: function() {
        var self = this,
          chunk;

        // Slight timeout needed here (File read / AJAX ready state conflict?)
        setTimeout(function() {
          // Prevent range overflow
          if (self.rangeEnd > self.file.size) {
            self.rangeEnd = self.file.size;
          }

          chunk = self.file[self.slice_method](self.rangeStart, self.rangeEnd);

          //logger.log('UPLOADER: CHUNKED_FILE_UPLOADER: _upload file:', self.file.name, ' range:', self.rangeStart, self.rangeEnd);

          var request = $.ajax(self.options.url, {
            data: chunk,
            type: 'POST',
            contentType: 'application/octet-stream',
            processData: false,
            headers: {'Content-Range': ('bytes ' + self.rangeStart + '-' + (self.rangeEnd -1) + '/' + self.file.size)}
          });

          request.done(function(rsp, textStatus, jqXHR) {
            self._onChunkComplete(self, jqXHR.status);
          }); 
 
          request.fail(function(jqXHR, textStatus, errorThrown) {
    	    self._onChunkError(self, jqXHR, textStatus, errorThrown);
          });
        }, 20);
      },

      // Event Handlers ____________________________________________________

      _onChunkComplete: function(self, statusCode) {
        //logger.log('UPLOADER: CHUNKED_FILE_UPLOADER: _onChunkComplete:', self.file.name, ' range:', self.rangeStart, self.rangeEnd);

        // If the end range is already the same size as our file, we
        // can assume that our last chunk has been processed and exit
        // out of the function.
        if (self.rangeEnd === self.file.size) {
          self._onUploadComplete(self, statusCode);
          return;
        }

        var timeNow = new Date();
        var rate = (self.rangeEnd / (timeNow.getTime() - self.timeStarted)) * 1000; // bytes/sec
        var updatedSecsLeft = (self.file.size - self.rangeEnd) / rate;

        if (self.estimatedSecsLeft - updatedSecsLeft > 0.25) { // update progress at most every 250 msec
          self.estimatedSecsLeft = updatedSecsLeft;
          if(typeof self.progressHdlr === 'function') {
            self.progressHdlr(self.hdlrContext, self.file, self.rangeEnd, self.estimatedSecsLeft);
          }
        }
        // Update our ranges
        self.rangeStart = self.rangeEnd;
        self.rangeEnd = self.rangeStart + self.chunkSize;

        // Continue as long as we aren't cancelled or paused
        if (!self.isCancelled && !self.isPaused) {
          self._upload();
        }
      },
 
      _onChunkError: function(self, jqXHR, textStatus, errorThrown) {
        logger.log('UPLOADER: CHUNKED_FILE_UPLOADER: _onChunkError:', self.file.name, jqXHR, textStatus, errorThrown);

        self.state = 'Failed';
        self.estimatedSecsLeft = Infinity; 

        if (typeof self.errHandler === 'function')
          self.errHandler(self.hdlrContext, self.file, jqXHR, textStatus, errorThrown);
      },

      _onUploadComplete: function(self, statusCode) {
        logger.log('UPLOADER: CHUNKED_FILE_UPLOADER: _onUploadComplete:', self.file.name, ', status code: ', statusCode);

    	self.state = 'Completed';
        self.estimatedSecsLeft = 0;

        if (typeof self.doneHdlr === 'function') 
          self.doneHdlr(self.hdlrContext, self.file, statusCode);
      }
    };

    return Em.Object.create({
      ChunkedFileUploader: ChunkedFileUploader
    });

  }  
);
