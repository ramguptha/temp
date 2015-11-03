Troubleshooting
===============

Put troubleshooting tips here.

Node Fails to Start
-------------------

**Another instance of node (or something else) may be listening on the same TCP/IP port**

*Symptom*

You get an error in cmd console such as:

    > node app

    Http server listening on port 3000

    events.js:71
            throw arguments[1]; // Unhandled 'error' event
                           ^
    Error: listen EADDRINUSE
        at errnoException (net.js:770:11)
        at Server._listen2 (net.js:910:14)
        at listen (net.js:932:10)
        at Server.listen (net.js:998:5)
        at Function.app.listen (c:\Users\dsimonov\WebstormProjects\Learning\node_modules\express\lib\application.js:532:24)
        at c:\Users\dsimonov\WebstormProjects\Learning\app.js:163:7
        at Object.context.execCb (c:\Users\dsimonov\WebstormProjects\Learning\node_modules\requirejs\bin\r.js:1777:33)
        at Object.Module.check (c:\Users\dsimonov\WebstormProjects\Learning\node_modules\requirejs\bin\r.js:1078:51)
        at Object.Module.enable (c:\Users\dsimonov\WebstormProjects\Learning\node_modules\requirejs\bin\r.js:1319:22)
        at Object.Module.init (c:\Users\dsimonov\WebstormProjects\Learning\node_modules\requirejs\bin\r.js:977:26)

*Solution*

Check to make sure all other node servers are stopped and not using port 3000.
Stop the servers or simply launch your app on a different port. --p=3001
